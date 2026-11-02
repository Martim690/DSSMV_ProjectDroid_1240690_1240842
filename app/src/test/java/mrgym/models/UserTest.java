package mrgym.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testDefaultConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getName());
    }

    @Test
    public void testParameterizedConstructor() {
        User user = new User("user@test.com", "pass123", "Test User");
        assertEquals("user@test.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals("Test User", user.getName());
        assertNull(user.getId());
    }

    @Test
    public void testSettersAndGetters() {
        User user = new User();
        user.setId("user_123");
        user.setEmail("athlete@test.com");
        user.setPassword("secret456");
        user.setName("Athlete");

        assertEquals("user_123", user.getId());
        assertEquals("athlete@test.com", user.getEmail());
        assertEquals("secret456", user.getPassword());
        assertEquals("Athlete", user.getName());
    }

    @Test
    public void testToString() {
        User user = new User("john@test.com", "123456", "John Doe");
        String result = user.toString();
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("john@test.com"));
    }
}
