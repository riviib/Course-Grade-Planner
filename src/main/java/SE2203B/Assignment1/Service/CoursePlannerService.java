package SE2203B.Assignment1.Service;

import SE2203B.Assignment1.Domain.Assessment;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoursePlannerService {

    private final List<Assessment> assessments = new ArrayList<>();

    public List<Assessment> getAllAssessments() {
        return new ArrayList<>(assessments);
    }

    public void addAssessment(Assessment assessment) {
        if (isNameDuplicate(assessment.getName(), null)) {
            throw new IllegalArgumentException("Assessment name must be unique");
        }
        assessments.add(assessment);
    }

    public void updateAssessment(Assessment oldAssessment, Assessment newAssessment) {
        if (isNameDuplicate(newAssessment.getName(), oldAssessment)) {
            throw new IllegalArgumentException("Assessment name must be unique");
        }

        int index = assessments.indexOf(oldAssessment);
        if (index >= 0) {
            assessments.set(index, newAssessment);
        }
    }

    public void deleteAssessment(Assessment assessment) {
        assessments.remove(assessment);
    }

    public boolean isNameDuplicate(String name, Assessment currentAssessment) {
        return assessments.stream()
                .filter(a -> a != currentAssessment)
                .anyMatch(a -> a.getName().equals(name));
    }

    public void clearAll() {
        assessments.clear();
    }
}