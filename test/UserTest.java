import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User model class.
 */
public class UserTest {

    private User user;
    private String username;
    private String email;

    @BeforeEach
    void setUp() {
        username = "testuser";
        email = "test@example.com";
        user = new User(username, email);
    }

    @Test
    void testConstructorWithAllFields() {
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getId());
    }

    @Test
    void testConstructorWithNullEmail() {
        User usr = new User(username, null);
        assertEquals("", usr.getEmail());
    }

    @Test
    void testConstructorGeneratesId() {
        User usr = new User("new Username", email);
        assertEquals(usr.getId(), "user_<new_username>");
    }

    @Test
    void testGetters() {
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getId());
    }

    @Test
    void testEquals() {
        User user1 = new User(username, email);
        User user2 = new User(username, email);
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsWithDifferentUsername() {
        User user1 = new User("user1", email);
        User user2 = new User("user2", email);
        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsWithInvalidObject() {
        assertFalse(user.equals(null));
        assertFalse(user.equals("not a user"));
    }

    @Test
    void testToString() {
        String result = user.toString();
        assertEquals(result, "User[id=" + user.getId() + ", username=" + username + ", email=" + email + "]");
    }

    @Test
    void testIdFormat() {
        assertEquals(user.getId(), "user_<" + username.toLowerCase().replaceAll("\\s", "_") + ">");
    }
}

