package mrgym.models;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;

public class JournalEntryTest {

    @Test
    public void testDefaultConstructor() {
        JournalEntry entry = new JournalEntry();
        assertNull(entry.getId());
        assertNull(entry.getPhotoPath());
        assertEquals(0, entry.getTimestamp());
        assertEquals(0.0, entry.getWeight(), 0.001);
        assertEquals(0.0, entry.getHeight(), 0.001);
        assertNull(entry.getUserEmail());
    }

    @Test
    public void testFiveParameterConstructor() {
        JournalEntry entry = new JournalEntry("legacy_1", "/legacy/path.jpg", 1000L, 72.0, 178.0);
        assertEquals("legacy_1", entry.getId());
        assertEquals("/legacy/path.jpg", entry.getPhotoPath());
        assertEquals(1000L, entry.getTimestamp());
        assertEquals(72.0, entry.getWeight(), 0.001);
        assertEquals(178.0, entry.getHeight(), 0.001);
        assertNull(entry.getUserEmail());
    }

    @Test
    public void testSixParameterConstructor() {
        JournalEntry entry = new JournalEntry("entry_1", "/path/photo.jpg", 123456789L, 78.5, 182.0, "user@test.com");
        assertEquals("entry_1", entry.getId());
        assertEquals("/path/photo.jpg", entry.getPhotoPath());
        assertEquals(123456789L, entry.getTimestamp());
        assertEquals(78.5, entry.getWeight(), 0.001);
        assertEquals(182.0, entry.getHeight(), 0.001);
        assertEquals("user@test.com", entry.getUserEmail());
    }

    @Test
    public void testSettersAndGetters() {
        JournalEntry entry = new JournalEntry();
        entry.setId("entry_2");
        entry.setPhotoPath("/new/path.jpg");
        entry.setTimestamp(987654321L);
        entry.setWeight(82.0);
        entry.setHeight(175.5);
        entry.setUserEmail("athlete@test.com");

        assertEquals("entry_2", entry.getId());
        assertEquals("/new/path.jpg", entry.getPhotoPath());
        assertEquals(987654321L, entry.getTimestamp());
        assertEquals(82.0, entry.getWeight(), 0.001);
        assertEquals(175.5, entry.getHeight(), 0.001);
        assertEquals("athlete@test.com", entry.getUserEmail());
    }

    @Test
    public void testRestDbJsonDeserialization() {
        String json = "{\"_id\":\"6ae8a23139d99e3300002657\",\"height\":160.0,\"id\":\"1793630763843\",\"photoPath\":\"/data/user/0/journal.jpg\",\"timestamp\":1793630763843,\"userEmail\":\"teste2@gmail.com\",\"weight\":65.0}";
        Gson gson = new Gson();
        JournalEntry entry = gson.fromJson(json, JournalEntry.class);

        assertNotNull(entry);
        assertEquals("6ae8a23139d99e3300002657", entry.getId());
        assertEquals(160.0, entry.getHeight(), 0.001);
        assertEquals(65.0, entry.getWeight(), 0.001);
        assertEquals("teste2@gmail.com", entry.getUserEmail());
        assertEquals("/data/user/0/journal.jpg", entry.getPhotoPath());
        assertEquals(1793630763843L, entry.getTimestamp());
    }

    @Test
    public void testGsonSerializationAndDeserialization() {
        JournalEntry original = new JournalEntry("entry_3", "/img.jpg", 1500L, 80.0, 185.0, "teste@gmail.com");
        Gson gson = new Gson();
        String json = gson.toJson(original);
        JournalEntry deserialized = gson.fromJson(json, JournalEntry.class);

        assertEquals(original.getId(), deserialized.getId());
        assertEquals(original.getPhotoPath(), deserialized.getPhotoPath());
        assertEquals(original.getTimestamp(), deserialized.getTimestamp());
        assertEquals(original.getWeight(), deserialized.getWeight(), 0.001);
        assertEquals(original.getHeight(), deserialized.getHeight(), 0.001);
        assertEquals(original.getUserEmail(), deserialized.getUserEmail());
    }

    @Test
    public void testToString() {
        JournalEntry entry = new JournalEntry("entry_4", "/img4.jpg", 2000L, 70.0, 170.0, "john@test.com");
        String result = entry.toString();
        assertTrue(result.contains("entry_4"));
        assertTrue(result.contains("john@test.com"));
    }
}
