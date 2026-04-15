package SE2203B.Assignment1.Service;

import SE2203B.Assignment1.Domain.Assessment;
import java.util.List;

public class GradeCalculator {

    public static double calculateTotalMarkedWeight(List<Assessment> assessments) {
        return assessments.stream()
                .filter(Assessment::isMarked)
                .mapToDouble(Assessment::getWeight)
                .sum();
    }

    public static double calculateWeightedGrade(List<Assessment> assessments) {
        return assessments.stream()
                .filter(Assessment::isMarked)
                .mapToDouble(a -> a.getWeight() * a.getMark() / 100.0)
                .sum();
    }

    public static double calculateRemainingWeight(List<Assessment> assessments) {
        double totalMarked = calculateTotalMarkedWeight(assessments);
        return Math.max(0, 100.0 - totalMarked);
    }

    public static double calculateRequiredAverage(List<Assessment> assessments, double targetGrade) {
        double weightedGrade = calculateWeightedGrade(assessments);
        double remainingWeight = calculateRemainingWeight(assessments);

        if (remainingWeight <= 0) {
            return 0.0;
        }

        return (targetGrade - weightedGrade) / (remainingWeight / 100.0);
    }

    public static double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}