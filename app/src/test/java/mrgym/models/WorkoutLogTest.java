package mrgym.models;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;

public class WorkoutLogTest {

    @Test
    public void testDefaultConstructor() {
        WorkoutLog log = new WorkoutLog();
        assertNull(log.getId());
        assertNull(log.getUserId());
        assertNull(log.getDate());
        assertEquals(0, log.getDurationMinutes());
    }

    @Test
    public void testParameterizedConstructor() {
        WorkoutLog log = new WorkoutLog("user_abc", "2026-11-07", 45);
        assertNull(log.getId());
        assertEquals("user_abc", log.getUserId());
        assertEquals("2026-11-07", log.getDate());
        assertEquals(45, log.getDurationMinutes());
    }

    @Test
    public void testSettersAndGetters() {
        WorkoutLog log = new WorkoutLog();
        log.setId("log_999");
        log.setUserId("athlete_1");
        log.setDate("2026-11-08");
        log.setDurationMinutes(60);

        assertEquals("log_999", log.getId());
        assertEquals("athlete_1", log.getUserId());
        assertEquals("2026-11-08", log.getDate());
        assertEquals(60, log.getDurationMinutes());
    }

    @Test
    public void testToString() {
        WorkoutLog log = new WorkoutLog("user_123", "2026-11-07", 50);
        String result = log.toString();
        assertTrue(result.contains("2026-11-07"));
        assertTrue(result.contains("50min"));
    }

    @Test
    public void testJsonSerializationAndDeserialization() {
        WorkoutLog original = new WorkoutLog("user_xyz", "2026-11-09", 75);
        original.setId("log_restdb_123");

        Gson gson = new Gson();
        String json = gson.toJson(original);
        WorkoutLog deserialized = gson.fromJson(json, WorkoutLog.class);

        assertEquals(original.getId(), deserialized.getId());
        assertEquals(original.getUserId(), deserialized.getUserId());
        assertEquals(original.getDate(), deserialized.getDate());
        assertEquals(original.getDurationMinutes(), deserialized.getDurationMinutes());
    }
}
