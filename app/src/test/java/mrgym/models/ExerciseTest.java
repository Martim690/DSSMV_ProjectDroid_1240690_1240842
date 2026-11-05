package mrgym.models;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExerciseTest {

    @Test
    public void testDefaultConstructor() {
        Exercise exercise = new Exercise();
        assertEquals(0, exercise.getId());
        assertNull(exercise.getName());
        assertNull(exercise.getDescription());
        assertNull(exercise.getImageUrl());
        assertEquals(3, exercise.getSets());
        assertEquals(10, exercise.getReps());
        assertEquals(0.0, exercise.getWeight(), 0.001);
    }

    @Test
    public void testThreeParameterConstructor() {
        Exercise exercise = new Exercise(101, "Push Up", "Bodyweight upper body exercise");
        assertEquals(101, exercise.getId());
        assertEquals("Push Up", exercise.getName());
        assertEquals("Bodyweight upper body exercise", exercise.getDescription());
        assertNull(exercise.getImageUrl());
        assertEquals(3, exercise.getSets());
        assertEquals(10, exercise.getReps());
        assertEquals(0.0, exercise.getWeight(), 0.001);
    }

    @Test
    public void testFourParameterConstructor() {
        Exercise exercise = new Exercise(102, "Squat", "Leg compound movement", "https://example.com/squat.png");
        assertEquals(102, exercise.getId());
        assertEquals("Squat", exercise.getName());
        assertEquals("Leg compound movement", exercise.getDescription());
        assertEquals("https://example.com/squat.png", exercise.getImageUrl());
        assertEquals(3, exercise.getSets());
        assertEquals(10, exercise.getReps());
        assertEquals(0.0, exercise.getWeight(), 0.001);
    }

    @Test
    public void testSixParameterConstructor() {
        Exercise exercise = new Exercise(103, "Deadlift", "Posterior chain compound", "https://example.com/deadlift.png", 5, 5);
        assertEquals(103, exercise.getId());
        assertEquals("Deadlift", exercise.getName());
        assertEquals("Posterior chain compound", exercise.getDescription());
        assertEquals("https://example.com/deadlift.png", exercise.getImageUrl());
        assertEquals(5, exercise.getSets());
        assertEquals(5, exercise.getReps());
        assertEquals(0.0, exercise.getWeight(), 0.001);
    }

    @Test
    public void testSevenParameterConstructor() {
        Exercise exercise = new Exercise(104, "Bench Press", "Chest barbell press", "https://example.com/bench.png", 4, 8, 80.0);
        assertEquals(104, exercise.getId());
        assertEquals("Bench Press", exercise.getName());
        assertEquals("Chest barbell press", exercise.getDescription());
        assertEquals("https://example.com/bench.png", exercise.getImageUrl());
        assertEquals(4, exercise.getSets());
        assertEquals(8, exercise.getReps());
        assertEquals(80.0, exercise.getWeight(), 0.001);
    }

    @Test
    public void testSettersAndGetters() {
        Exercise exercise = new Exercise();
        exercise.setId(201);
        exercise.setName("Bench Press");
        exercise.setDescription("Barbell chest press");
        exercise.setImageUrl("https://example.com/bench.png");
        exercise.setSets(4);
        exercise.setReps(8);
        exercise.setWeight(75.5);

        assertEquals(201, exercise.getId());
        assertEquals("Bench Press", exercise.getName());
        assertEquals("Barbell chest press", exercise.getDescription());
        assertEquals("https://example.com/bench.png", exercise.getImageUrl());
        assertEquals(4, exercise.getSets());
        assertEquals(8, exercise.getReps());
        assertEquals(75.5, exercise.getWeight(), 0.001);
    }

    @Test
    public void testToString() {
        Exercise exercise = new Exercise(301, "Pull Up", "Back exercise", "https://example.com/pullup.png", 3, 12, 10.0);
        String str = exercise.toString();
        assertTrue(str.contains("301"));
        assertTrue(str.contains("Pull Up"));
        assertTrue(str.contains("sets=3"));
        assertTrue(str.contains("reps=12"));
        assertTrue(str.contains("weight=10.0"));
    }

    @Test
    public void testJsonSerialization() {
        Exercise exercise = new Exercise(401, "Bicep Curl", "Isolation arm exercise", "https://example.com/curl.png", 4, 15, 14.0);
        Gson gson = new Gson();
        String json = gson.toJson(exercise);
        Exercise deserialized = gson.fromJson(json, Exercise.class);

        assertEquals(exercise.getId(), deserialized.getId());
        assertEquals(exercise.getName(), deserialized.getName());
        assertEquals(exercise.getDescription(), deserialized.getDescription());
        assertEquals(exercise.getImageUrl(), deserialized.getImageUrl());
        assertEquals(exercise.getSets(), deserialized.getSets());
        assertEquals(exercise.getReps(), deserialized.getReps());
        assertEquals(exercise.getWeight(), deserialized.getWeight(), 0.001);
    }
}
