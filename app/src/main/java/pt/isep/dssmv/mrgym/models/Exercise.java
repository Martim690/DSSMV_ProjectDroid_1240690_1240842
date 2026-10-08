package pt.isep.dssmv.mrgym.models;

public class Exercise {
    private int id;
    private String name;
    private String description;

    public Exercise(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}
