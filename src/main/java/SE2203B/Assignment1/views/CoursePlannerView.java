package SE2203B.Assignment1.views;

import SE2203B.Assignment1.Domain.Assessment;
import SE2203B.Assignment1.Service.CoursePlannerService;
import SE2203B.Assignment1.Service.GradeCalculator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class CoursePlannerView extends VerticalLayout {

    private final CoursePlannerService service;

    // Navigation
    private final HorizontalLayout menuBar = new HorizontalLayout();
    private final H2 sectionTitle = new H2("Course Planner");
    private final Paragraph sectionIndicator = new Paragraph("Current Section: Planner");

    // Planner section
    private final VerticalLayout plannerSection = new VerticalLayout();
    private final Grid<Assessment> grid = new Grid<>(Assessment.class, false);
    private final Binder<Assessment> binder = new Binder<>(Assessment.class);

    // Form fields
    private final TextField nameField = new TextField("Assessment Name");
    private final ComboBox<String> typeField = new ComboBox<>("Assessment Type");
    private final NumberField weightField = new NumberField("Weight (%)");
    private final Checkbox markedCheckbox = new Checkbox("Marked?");
    private final NumberField markField = new NumberField("Mark (%)");

    // Buttons
    private final Button saveButton = new Button("Save");
    private final Button clearButton = new Button("Clear");
    private final Button deleteButton = new Button("Delete");

    // Summary section
    private final VerticalLayout summarySection = new VerticalLayout();
    private final NumberField targetGradeField = new NumberField();

    // Summary display labels
    private Paragraph totalMarkedLabel;
    private Paragraph weightedGradeLabel;
    private Paragraph remainingWeightLabel;
    private Paragraph requiredAverageLabel;

    private Assessment selectedAssessment = null;

    @Autowired
    public CoursePlannerView(CoursePlannerService service) {
        this.service = service;

        setSizeFull();
        setPadding(true);

        createMenuBar();
        createPlannerSection();
        createSummarySection();

        add(menuBar, sectionIndicator, sectionTitle, plannerSection);
        showPlannerSection();
    }

    private void createMenuBar() {
        Button plannerButton = new Button("Planner", e -> showPlannerSection());
        Button summaryButton = new Button("Summary", e -> showSummarySection());
        Button helpButton = new Button("Help", e -> showHelpDialog());

        plannerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        summaryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        helpButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        menuBar.add(plannerButton, summaryButton, helpButton);
        menuBar.setSpacing(true);
    }

    private void createPlannerSection() {
        plannerSection.setPadding(true);
        plannerSection.setSpacing(true);

        FormLayout formLayout = createForm();
        configureGrid();

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, clearButton, deleteButton);
        buttonLayout.setSpacing(true);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.setEnabled(false);

        saveButton.addClickListener(e -> saveAssessment());
        clearButton.addClickListener(e -> clearForm());
        deleteButton.addClickListener(e -> deleteAssessment());

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setSizeFull();

        VerticalLayout formContainer = new VerticalLayout(new H3("Assessment Details"), formLayout, buttonLayout);
        formContainer.setWidth("400px");

        VerticalLayout gridContainer = new VerticalLayout(new H3("Assessments"), grid);
        gridContainer.setSizeFull();

        contentLayout.add(formContainer, gridContainer);
        contentLayout.setFlexGrow(1, gridContainer);

        plannerSection.add(contentLayout);
    }

    private FormLayout createForm() {
        FormLayout formLayout = new FormLayout();

        typeField.setItems("Lab", "Quiz", "Assignment", "Midterm", "Final", "Project", "Other");
        typeField.setValue("Assignment");

        weightField.setStep(0.1);
        weightField.setMin(0.1);
        weightField.setMax(100.0);
        weightField.setValue(0.0);

        markField.setStep(0.1);
        markField.setMin(0.0);
        markField.setMax(100.0);
        markField.setValue(0.0);
        markField.setEnabled(false);

        markedCheckbox.addValueChangeListener(e -> {
            markField.setEnabled(e.getValue());
            if (!e.getValue()) {
                markField.clear();
            }
        });

        formLayout.add(nameField, typeField, weightField, markedCheckbox, markField);
        configureBinder();

        return formLayout;
    }

    private void configureBinder() {
        binder.forField(nameField)
                .asRequired("Name is required")
                .withValidator(name -> !name.trim().isEmpty(), "Name cannot be empty")
                .withValidator(name -> !service.isNameDuplicate(name, selectedAssessment),
                        "Assessment name must be unique")
                .bind(Assessment::getName, Assessment::setName);

        binder.forField(typeField)
                .asRequired("Type is required")
                .bind(Assessment::getType, Assessment::setType);

        binder.forField(weightField)
                .asRequired("Weight is required")
                .withValidator(weight -> weight > 0 && weight <= 100,
                        "Weight must be between 0 and 100")
                .bind(Assessment::getWeight, Assessment::setWeight);

        binder.forField(markedCheckbox)
                .bind(Assessment::isMarked, Assessment::setMarked);

        binder.forField(markField)
                .withValidator(mark -> !markedCheckbox.getValue() || mark != null,
                        "Mark is required when assessment is marked")
                .withValidator(mark -> !markedCheckbox.getValue() || (mark >= 0 && mark <= 100),
                        "Mark must be between 0 and 100")
                .bind(
                        assessment -> assessment.isMarked() ? assessment.getMark() : 0.0,
                        (assessment, mark) -> {
                            if (assessment.isMarked()) {
                                assessment.setMark(mark != null ? mark : 0.0);
                            }
                        }
                );
    }

    private void configureGrid() {
        grid.addColumn(Assessment::getName).setHeader("Name").setFlexGrow(1);
        grid.addColumn(Assessment::getType).setHeader("Type").setWidth("120px");
        grid.addColumn(a -> String.format("%.1f", a.getWeight())).setHeader("Weight (%)").setWidth("120px");
        grid.addColumn(a -> a.isMarked() ? "Yes" : "No").setHeader("Marked?").setWidth("100px");
        grid.addColumn(a -> a.isMarked() ? String.format("%.1f", a.getMark()) : "-")
                .setHeader("Mark (%)").setWidth("120px");

        grid.asSingleSelect().addValueChangeListener(e -> {
            selectedAssessment = e.getValue();
            if (selectedAssessment != null) {
                binder.readBean(selectedAssessment);
                deleteButton.setEnabled(true);
            } else {
                deleteButton.setEnabled(false);
            }
        });

        grid.setItems(service.getAllAssessments());
        grid.setHeight("400px");
    }

    private void createSummarySection() {
        summarySection.setPadding(true);
        summarySection.setSpacing(true);
        summarySection.setVisible(false);
        summarySection.setMaxWidth("600px");

        VerticalLayout statsContainer = new VerticalLayout();
        statsContainer.setSpacing(true);
        statsContainer.setPadding(false);

        // Initialize labels
        totalMarkedLabel = new Paragraph("Total marked weight: 0.0%");
        totalMarkedLabel.getStyle().set("margin", "5px 0");

        weightedGradeLabel = new Paragraph("Weighted grade so far (marked only): 0.0%");
        weightedGradeLabel.getStyle().set("margin", "5px 0");

        remainingWeightLabel = new Paragraph("Remaining weight to reach 100%: 100.0%");
        remainingWeightLabel.getStyle().set("margin", "5px 0");

        // Target input
        HorizontalLayout targetLayout = new HorizontalLayout();
        targetLayout.setAlignItems(Alignment.CENTER);
        targetLayout.setSpacing(false);

        Paragraph targetLabel = new Paragraph("Target overall (%)");
        targetLabel.getStyle().set("margin", "5px 10px 5px 0");

        targetGradeField.setValue(75.0);
        targetGradeField.setStep(0.1);
        targetGradeField.setMin(0.0);
        targetGradeField.setMax(100.0);
        targetGradeField.setWidth("150px");
        targetGradeField.addValueChangeListener(e -> updateSummaryDisplay());

        targetLayout.add(targetLabel, targetGradeField);

        requiredAverageLabel = new Paragraph("Required average on remaining: 0.0%");
        requiredAverageLabel.getStyle().set("margin", "5px 0");

        statsContainer.add(
                totalMarkedLabel,
                weightedGradeLabel,
                remainingWeightLabel,
                targetLayout,
                requiredAverageLabel
        );

        summarySection.add(statsContainer);
    }

    private void saveAssessment() {
        try {
            Assessment assessment = new Assessment();
            binder.writeBean(assessment);

            if (selectedAssessment == null) {
                service.addAssessment(assessment);
                showNotification("Assessment added successfully", NotificationVariant.LUMO_SUCCESS);
            } else {
                service.updateAssessment(selectedAssessment, assessment);
                showNotification("Assessment updated successfully", NotificationVariant.LUMO_SUCCESS);
            }

            clearForm();
            refreshGrid();
            updateSummary();

        } catch (ValidationException e) {
            showNotification("Please fix validation errors", NotificationVariant.LUMO_ERROR);
        } catch (IllegalArgumentException e) {
            showNotification(e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteAssessment() {
        if (selectedAssessment != null) {
            service.deleteAssessment(selectedAssessment);
            showNotification("Assessment deleted successfully", NotificationVariant.LUMO_SUCCESS);
            clearForm();
            refreshGrid();
            updateSummary();
        }
    }

    private void clearForm() {
        binder.readBean(new Assessment());
        nameField.clear();
        typeField.setValue("Assignment");
        weightField.setValue(0.0);
        markedCheckbox.setValue(false);
        markField.setValue(0.0);
        markField.setEnabled(false);

        grid.asSingleSelect().clear();
        selectedAssessment = null;
        deleteButton.setEnabled(false);
    }

    private void refreshGrid() {
        grid.setItems(service.getAllAssessments());
    }

    private void updateSummary() {
        updateSummaryDisplay();
    }

    private void updateSummaryDisplay() {
        var assessments = service.getAllAssessments();
        double targetGrade = targetGradeField.getValue() != null ? targetGradeField.getValue() : 75.0;

        double totalMarkedWeight = GradeCalculator.roundToOneDecimal(
                GradeCalculator.calculateTotalMarkedWeight(assessments));
        double weightedGrade = GradeCalculator.roundToOneDecimal(
                GradeCalculator.calculateWeightedGrade(assessments));
        double remainingWeight = GradeCalculator.roundToOneDecimal(
                GradeCalculator.calculateRemainingWeight(assessments));
        double requiredAverage = GradeCalculator.roundToOneDecimal(
                GradeCalculator.calculateRequiredAverage(assessments, targetGrade));

        totalMarkedLabel.setText("Total marked weight: " + totalMarkedWeight + "%");
        weightedGradeLabel.setText("Weighted grade so far (marked only): " + weightedGrade + "%");
        remainingWeightLabel.setText("Remaining weight to reach 100%: " + remainingWeight + "%");
        requiredAverageLabel.setText("Required average on remaining: " + requiredAverage + "%");
    }

    private void showPlannerSection() {
        plannerSection.setVisible(true);
        summarySection.setVisible(false);
        sectionTitle.setText("Course Planner");
        sectionIndicator.setText("Current Section: Planner");
    }

    private void showSummarySection() {
        plannerSection.setVisible(false);
        summarySection.setVisible(true);
        sectionTitle.setText("Grade Summary");
        sectionIndicator.setText("Current Section: Summary");
        updateSummaryDisplay();

        add(summarySection);
    }

    private void showHelpDialog() {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(false);

        H3 title = new H3("Help");
        Paragraph planner = new Paragraph("Planner: add assessments with weights and marks.");
        Paragraph summary = new Paragraph("Summary: see totals and required average for a target grade.");
        Paragraph rules = new Paragraph("Rules: total planner weight cannot exceed 100%; names must be unique. ");


        Button closeButton = new Button("Close", e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        content.add(title, planner, summary, rules, closeButton);

        dialog.add(content);
        dialog.open();
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}