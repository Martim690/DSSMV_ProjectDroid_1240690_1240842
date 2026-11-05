package mrgym.models;

public class Exercise {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private int sets = 3;
    private int reps = 10;
    private double weight = 0.0;

    public Exercise() {
    }

    public Exercise(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Exercise(int id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Exercise(int id, String name, String description, String imageUrl, int sets, int reps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sets = sets;
        this.reps = reps;
    }

    public Exercise(int id, String name, String description, String imageUrl, int sets, int reps, double weight) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    @Override
    public String toString() {
        return "Exercise{id=" + id + ", name='" + name + "', sets=" + sets + ", reps=" + reps + ", weight=" + weight + "}";
    }
}
