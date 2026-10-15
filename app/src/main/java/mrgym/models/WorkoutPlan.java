package mrgym.models;

import java.util.List;

public class WorkoutPlan {
    private String _id;
    private String name;
    private List<Exercise> exercises;

    public WorkoutPlan() {
    }

    public WorkoutPlan(String name, List<Exercise> exercises) {
        this.name = name;
        this.exercises = exercises;
    }

    public String getId() { return _id; }
    public void setId(String _id) { this._id = _id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    @Override
    public String toString() {
        return "WorkoutPlan{name='" + name + "', exercises=" + (exercises != null ? exercises.size() : 0) + "}";
    }
}
