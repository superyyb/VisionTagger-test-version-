package service;

import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserService class.
 */
public class UserServiceTest {

    private UserService userService;
    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        userService = new UserService(repository);
    }

    @Test
    void testCreateGuestUser() {
        User guest = userService.createGuestUser("guestuser");
        assertNotNull(guest);
        assertFalse(guest.isRegistered());
        assertEquals("guestuser", guest.getUsername());
        assertEquals("", guest.getEmail());
    }

    @Test
    void testCreateGuestUserTrimsWhitespace() {
        User guest = userService.createGuestUser("  guestuser  ");
        assertEquals("guestuser", guest.getUsername());
    }

    @Test
    void testCreateGuestUserAllowsDuplicateUsernames() {
        User guest1 = userService.createGuestUser("guestuser");
        User guest2 = userService.createGuestUser("guestuser");
        
        // Guest users can have same username (different UUIDs)
        assertEquals(guest1.getUsername(), guest2.getUsername());
        assertNotEquals(guest1.getId(), guest2.getId());
    }

    @Test
    void testCreateGuestUserThrowsExceptionForNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createGuestUser(null);
        });
    }

    @Test
    void testCreateGuestUserThrowsExceptionForEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createGuestUser("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createGuestUser("   ");
        });
    }

    @Test
    void testCreateRegisteredUser() {
        User registered = userService.createRegisteredUser("newuser", "newuser@example.com");
        assertNotNull(registered);
        assertTrue(registered.isRegistered());
        assertEquals("newuser", registered.getUsername());
        assertEquals("newuser@example.com", registered.getEmail());
    }

    @Test
    void testCreateRegisteredUserTrimsWhitespace() {
        User registered = userService.createRegisteredUser("  newuser  ", "  email@example.com  ");
        assertEquals("newuser", registered.getUsername());
        assertEquals("email@example.com", registered.getEmail());
    }

    @Test
    void testCreateRegisteredUserThrowsExceptionForNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser(null, "email@example.com");
        });
    }

    @Test
    void testCreateRegisteredUserThrowsExceptionForEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser("", "email@example.com");
        });
    }

    @Test
    void testCreateRegisteredUserThrowsExceptionForNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser("username", null);
        });
    }

    @Test
    void testCreateRegisteredUserThrowsExceptionForEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser("username", "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser("username", "   ");
        });
    }

    @Test
    void testCreateRegisteredUserPreventsDuplicateUsernames() {
        userService.createRegisteredUser("testuser", "test1@example.com");
        
        // Attempting to create another registered user with same username should fail
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser("testuser", "test2@example.com");
        });
    }

    @Test
    void testCreateRegisteredUserAllowsSameUsernameForGuest() {
        // Create a guest user (doesn't check duplicates)
        userService.createGuestUser("testuser");
        
        // Should be able to create registered user with same username
        // (guest users don't count as duplicates)
        User registered = userService.createRegisteredUser("testuser", "test@example.com");
        assertNotNull(registered);
        assertTrue(registered.isRegistered());
    }

    @Test
    void testIsUsernameAvailable() {
        assertTrue(userService.isUsernameAvailable("available"));
        
        userService.createRegisteredUser("taken", "taken@example.com");
        assertFalse(userService.isUsernameAvailable("taken"));
    }

    @Test
    void testIsUsernameAvailableIgnoresGuestUsers() {
        userService.createGuestUser("guestuser");
        // Guest users don't block username availability
        assertTrue(userService.isUsernameAvailable("guestuser"));
    }

    @Test
    void testIsUsernameAvailableReturnsFalseForNull() {
        assertFalse(userService.isUsernameAvailable(null));
    }

    @Test
    void testIsUsernameAvailableReturnsFalseForEmpty() {
        assertFalse(userService.isUsernameAvailable(""));
        assertFalse(userService.isUsernameAvailable("   "));
    }

    @Test
    void testCaseInsensitiveUsernameCheck() {
        userService.createRegisteredUser("TestUser", "test@example.com");
        
        // Should detect duplicate regardless of case
        assertFalse(userService.isUsernameAvailable("testuser"));
        assertFalse(userService.isUsernameAvailable("TESTUSER"));
        assertFalse(userService.isUsernameAvailable("TestUser"));
    }
}

