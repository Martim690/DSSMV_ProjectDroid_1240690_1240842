package mrgym.models;

import java.util.List;

public class WorkoutPlan {
    private String _id;
    private String name;
    private List<Exercise> exercises;
    private String userEmail;

    public WorkoutPlan() {
    }

    public WorkoutPlan(String name, List<Exercise> exercises) {
        this.name = name;
        this.exercises = exercises;
    }

    public WorkoutPlan(String name, List<Exercise> exercises, String userEmail) {
        this.name = name;
        this.exercises = exercises;
        this.userEmail = userEmail;
    }

    public String getId() { return _id; }
    public void setId(String _id) { this._id = _id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    @Override
    public String toString() {
        return "WorkoutPlan{name='" + name + "', exercises=" + (exercises != null ? exercises.size() : 0) + "}";
    }
}
