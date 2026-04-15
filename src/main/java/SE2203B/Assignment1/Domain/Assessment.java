package SE2203B.Assignment1.Domain;

public class Assessment {
    private String name;
    private String type;
    private double weight;
    private boolean marked;
    private double mark;

    public Assessment() {
    }

    public Assessment(String name, String type, double weight, boolean marked, double mark) {
        this.name = name;
        this.type = type;
        this.weight = weight;
        this.marked = marked;
        this.mark = mark;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }
}
