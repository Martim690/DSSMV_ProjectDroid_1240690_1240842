package mrgym.models;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class WorkoutPlanTest {

    @Test
    public void testDefaultConstructor() {
        WorkoutPlan plan = new WorkoutPlan();
        assertNull(plan.getId());
        assertNull(plan.getName());
        assertNull(plan.getExercises());
        assertNull(plan.getUserEmail());
    }

    @Test
    public void testTwoParameterConstructor() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise(1, "Bench Press", "Chest"));
        WorkoutPlan plan = new WorkoutPlan("Chest Day", exercises);

        assertNull(plan.getId());
        assertEquals("Chest Day", plan.getName());
        assertEquals(1, plan.getExercises().size());
        assertNull(plan.getUserEmail());
    }

    @Test
    public void testThreeParameterConstructor() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise(1, "Squat", "Legs", "https://img.com/sq.png", 4, 10));
        exercises.add(new Exercise(2, "Lunge", "Legs", "https://img.com/lu.png", 3, 12));
        WorkoutPlan plan = new WorkoutPlan("Leg Routine", exercises, "user@test.com");

        assertNull(plan.getId());
        assertEquals("Leg Routine", plan.getName());
        assertEquals(2, plan.getExercises().size());
        assertEquals("user@test.com", plan.getUserEmail());
    }

    @Test
    public void testSettersAndGetters() {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setId("plan_123");
        plan.setName("Back & Biceps");
        plan.setUserEmail("ricardo@mrgym.com");

        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise(10, "Row", "Back", "https://img.com/row.png", 4, 8));
        plan.setExercises(exercises);

        assertEquals("plan_123", plan.getId());
        assertEquals("Back & Biceps", plan.getName());
        assertEquals("ricardo@mrgym.com", plan.getUserEmail());
        assertEquals(1, plan.getExercises().size());
        assertEquals(4, plan.getExercises().get(0).getSets());
        assertEquals(8, plan.getExercises().get(0).getReps());
    }

    @Test
    public void testToString() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise(1, "Plank", "Core"));
        WorkoutPlan plan = new WorkoutPlan("Core Day", exercises);

        String str = plan.toString();
        assertTrue(str.contains("Core Day"));
        assertTrue(str.contains("exercises=1"));
    }

    @Test
    public void testJsonSerialization() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise(101, "Overhead Press", "Shoulders", "https://img.com/ohp.png", 3, 10));
        exercises.add(new Exercise(102, "Lateral Raise", "Shoulders", "https://img.com/lat.png", 3, 15));
        WorkoutPlan plan = new WorkoutPlan("Shoulder Blast", exercises, "athlete@test.com");
        plan.setId("plan_abc_999");

        Gson gson = new Gson();
        String json = gson.toJson(plan);
        WorkoutPlan deserialized = gson.fromJson(json, WorkoutPlan.class);

        assertEquals(plan.getId(), deserialized.getId());
        assertEquals(plan.getName(), deserialized.getName());
        assertEquals(plan.getUserEmail(), deserialized.getUserEmail());
        assertNotNull(deserialized.getExercises());
        assertEquals(2, deserialized.getExercises().size());
        assertEquals(10, deserialized.getExercises().get(0).getReps());
        assertEquals(15, deserialized.getExercises().get(1).getReps());
    }
}
