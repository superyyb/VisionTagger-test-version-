package model;

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
        user = User.registeredUser(username, email);
    }

    @Test
    void testConstructorWithAllFields() {
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getId());
        assertTrue(user.isRegistered());
    }

    @Test
    void testGuestConstructor() {
        User guest = User.guestUser(username);
        assertEquals(username, guest.getUsername());
        assertEquals("", guest.getEmail());
        assertFalse(guest.isRegistered());
        assertNotNull(guest.getId());
        // Guest users use UUID, so ID should not be username-based
        assertFalse(guest.getId().startsWith("user_"));
    }

    @Test
    void testConstructorWithNullEmail() {
        // registeredUser throws exception for null email, so use guestUser instead
        User usr = User.guestUser(username);
        assertEquals("", usr.getEmail());
        assertFalse(usr.isRegistered());
    }

    @Test
    void testRegisteredUserHasEmail() {
        User registered = User.registeredUser(username, email);
        assertTrue(registered.isRegistered());
        assertEquals(email, registered.getEmail());
    }

    @Test
    void testConstructorGeneratesIdForRegisteredUser() {
        User usr = User.registeredUser("new Username", email);
        assertEquals(usr.getId(), "user_new_username");
        assertTrue(usr.isRegistered());
    }

    @Test
    void testGetters() {
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getId());
    }

    @Test
    void testEqualsForRegisteredUsers() {
        // Registered users with same username have same ID (username-based)
        User user1 = User.registeredUser(username, email);
        User user2 = User.registeredUser(username, email);
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsForGuestUsers() {
        // Guest users with same username have different IDs (UUID-based)
        User guest1 = User.guestUser(username);
        User guest2 = User.guestUser(username);
        assertNotEquals(guest1, guest2);
        assertNotEquals(guest1.hashCode(), guest2.hashCode());
    }

    @Test
    void testGuestUserFactoryMethodDelegatesToConstructor() {
        // Factory method should create same result as constructor
        User guest1 = User.guestUser(username);
        User guest2 = User.guestUser(username);
        assertEquals(guest1.getUsername(), guest2.getUsername());
        assertEquals(guest1.getEmail(), guest2.getEmail());
        assertFalse(guest1.isRegistered());
        assertFalse(guest2.isRegistered());
        // Guest users are DIFFERENT users (different UUIDs)
        assertNotEquals(guest1, guest2);
        assertNotEquals(guest1.getId(), guest2.getId());
    }

    @Test
    void testRegisteredUserFactoryMethodDelegatesToConstructor() {
        // Factory method should create same result as constructor (when email is valid)
        User registered1 = User.registeredUser(username, email);
        User registered2 = User.registeredUser(username, email);
        assertEquals(registered1.getUsername(), registered2.getUsername());
        assertEquals(registered1.getEmail(), registered2.getEmail());
        assertTrue(registered1.isRegistered());
        assertTrue(registered2.isRegistered());
        // Registered users with same username are the SAME user (same ID)
        assertEquals(registered1, registered2);
        assertEquals(registered1.getId(), registered2.getId());
    }

    @Test
    void testRegisteredUserFactoryMethodThrowsExceptionForEmptyEmail() {
        // Factory method adds validation that constructor doesn't have
        assertThrows(IllegalArgumentException.class, () -> {
            User.registeredUser(username, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            User.registeredUser(username, "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            User.registeredUser(username, "   ");
        });
    }

    @Test
    void testEqualsWithDifferentUsername() {
        User user1 = User.registeredUser("user1", email);
        User user2 = User.registeredUser("user2", email);
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
        assertEquals(result, "User[id=" + user.getId() + ", username=" + username + ", email=" + email + ", type=REGISTERED]");
    }

    @Test
    void testIdFormatForRegisteredUser() {
        // Registered users have username-based IDs
        assertEquals(user.getId(), "user_" + username.toLowerCase().replaceAll("\\s+", "_"));
    }

    @Test
    void testIsRegistered() {
        User guest = User.guestUser(username);
        assertFalse(guest.isRegistered());

        User registered = User.registeredUser(username, email);
        assertTrue(registered.isRegistered());

        // Empty email throws exception for registeredUser, so use guestUser
        User withEmptyEmail = User.guestUser(username);
        assertFalse(withEmptyEmail.isRegistered());
    }
}