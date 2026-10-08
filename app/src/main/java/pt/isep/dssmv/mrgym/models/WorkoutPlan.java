package pt.isep.dssmv.mrgym.models;

import java.util.List;

public class WorkoutPlan {
    private String _id;
    private String name;
    private List<Exercise> exercises;

    public WorkoutPlan(String name, List<Exercise> exercises) {
        this.name = name;
        this.exercises = exercises;
    }

    public String getName() { return name; }
    public List<Exercise> getExercises() { return exercises; }
}
