package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import model.User;
import java.util.Optional;

/**
 * Unit tests for the UserService class.
 */
public class UserServiceTest {

    private UserService userService;
    private InMemoryUserRepository userRepository;
    private String username;
    private String email;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        userService = new UserService(userRepository);
        username = "testuser";
        email = "test@example.com";
    }

    // ==================== CreateGuestUser Tests ====================

    @Test
    void testCreateGuestUser() {
        User guest = userService.createGuestUser(username);
        
        assertNotNull(guest);
        assertEquals(username, guest.getUsername());
        assertFalse(guest.isRegistered());
        assertEquals("", guest.getEmail());
    }

    @Test
    void testCreateGuestUserNullUsernameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createGuestUser(null);
        });
    }

    @Test
    void testCreateGuestUserEmptyUsernameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createGuestUser("");
        });
    }

    @Test
    void testCreateGuestUserTrimsWhitespace() {
        User guest = userService.createGuestUser("  testuser  ");
        assertEquals("testuser", guest.getUsername());
    }

    @Test
    void testCreateGuestUserDoesNotCheckDuplicates() {
        User guest1 = userService.createGuestUser("guest");
        User guest2 = userService.createGuestUser("guest");
        
        assertNotNull(guest1);
        assertNotNull(guest2);
        assertNotEquals(guest1.getId(), guest2.getId());
    }

    @Test
    void testCreateGuestUserDoesNotStore() {
        User guest = userService.createGuestUser(username);
        
        assertFalse(userRepository.existsByUsername(username));
    }

    // ==================== CreateRegisteredUser Tests ====================

    @Test
    void testCreateRegisteredUser() {
        User registered = userService.createRegisteredUser(username, email);
        
        assertNotNull(registered);
        assertEquals(username, registered.getUsername());
        assertEquals(email, registered.getEmail());
        assertTrue(registered.isRegistered());
    }

    @Test
    void testCreateRegisteredUserStoresInRepository() {
        User registered = userService.createRegisteredUser(username, email);
        
        assertTrue(userRepository.existsByUsername(username));
        Optional<User> found = userRepository.findByUsername(username);
        assertTrue(found.isPresent());
        assertEquals(registered.getId(), found.get().getId());
    }

    @Test
    void testCreateRegisteredUserNullUsernameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser(null, email);
        });
    }

    @Test
    void testCreateRegisteredUserEmptyUsernameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser("", email);
        });
    }

    @Test
    void testCreateRegisteredUserNullEmailThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser(username, null);
        });
    }

    @Test
    void testCreateRegisteredUserEmptyEmailThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser(username, "");
        });
    }

    @Test
    void testCreateRegisteredUserTrimsInputs() {
        User registered = userService.createRegisteredUser("  testuser  ", "  test@example.com  ");
        
        assertEquals("testuser", registered.getUsername());
        assertEquals("test@example.com", registered.getEmail());
    }

    @Test
    void testCreateRegisteredUserDuplicateUsernameThrowsException() {
        userService.createRegisteredUser(username, email);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser(username, "different@example.com");
        });
    }

    @Test
    void testCreateRegisteredUserDuplicateCaseInsensitiveThrowsException() {
        userService.createRegisteredUser("TestUser", email);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser("testuser", "different@example.com");
        });
    }

    @Test
    void testCreateRegisteredUserAfterGuestDoesNotConflict() {
        userService.createGuestUser(username);
        
        User registered = userService.createRegisteredUser(username, email);
        assertNotNull(registered);
        assertTrue(registered.isRegistered());
    }

    // ==================== IsUsernameAvailable Tests ====================

    @Test
    void testIsUsernameAvailableForNewUsername() {
        assertTrue(userService.isUsernameAvailable(username));
    }

    @Test
    void testIsUsernameAvailableForTakenUsername() {
        userService.createRegisteredUser(username, email);
        
        assertFalse(userService.isUsernameAvailable(username));
    }

    @Test
    void testIsUsernameAvailableCaseInsensitive() {
        userService.createRegisteredUser("TestUser", email);
        
        assertFalse(userService.isUsernameAvailable("testuser"));
        assertFalse(userService.isUsernameAvailable("TESTUSER"));
    }

    @Test
    void testIsUsernameAvailableNullReturnsFalse() {
        assertFalse(userService.isUsernameAvailable(null));
    }

    @Test
    void testIsUsernameAvailableEmptyReturnsFalse() {
        assertFalse(userService.isUsernameAvailable(""));
    }

    @Test
    void testIsUsernameAvailableAfterGuestCreation() {
        userService.createGuestUser(username);
        
        assertTrue(userService.isUsernameAvailable(username));
    }

    // ==================== FindByUsername Tests ====================

    @Test
    void testFindByUsername() {
        User registered = userService.createRegisteredUser(username, email);
        
        Optional<User> found = userService.findByUsername(username);
        assertTrue(found.isPresent());
        assertEquals(registered.getId(), found.get().getId());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> found = userService.findByUsername("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUsernameNull() {
        Optional<User> found = userService.findByUsername(null);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUsernameEmpty() {
        Optional<User> found = userService.findByUsername("");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUsernameForGuestUser() {
        userService.createGuestUser(username);
        
        Optional<User> found = userService.findByUsername(username);
        assertFalse(found.isPresent());
    }

    // ==================== Integration Tests ====================

    @Test
    void testCompleteUserWorkflow() {
        // Check availability
        assertTrue(userService.isUsernameAvailable(username));
        
        // Register
        User user = userService.createRegisteredUser(username, email);
        assertNotNull(user);
        assertTrue(user.isRegistered());
        
        // Username now unavailable
        assertFalse(userService.isUsernameAvailable(username));
        
        // Can find user
        Optional<User> found = userService.findByUsername(username);
        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getId());
        
        // Cannot create duplicate
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createRegisteredUser(username, "another@example.com");
        });
    }

    @Test
    void testGuestAndRegisteredUserWorkflow() {
        // Create guest
        User guest = userService.createGuestUser(username);
        assertFalse(guest.isRegistered());
        
        // Username still available for registration
        assertTrue(userService.isUsernameAvailable(username));
        
        // Register with same username
        User registered = userService.createRegisteredUser(username, email);
        assertTrue(registered.isRegistered());
        
        // Now username unavailable
        assertFalse(userService.isUsernameAvailable(username));
        
        // Can find registered user
        Optional<User> found = userService.findByUsername(username);
        assertTrue(found.isPresent());
        assertEquals(registered.getId(), found.get().getId());
    }

    @Test
    void testMultipleUsers() {
        User user1 = userService.createRegisteredUser("user1", "user1@example.com");
        User user2 = userService.createRegisteredUser("user2", "user2@example.com");
        User guest1 = userService.createGuestUser("guest1");
        User guest2 = userService.createGuestUser("guest2");
        
        // Registered users findable
        assertTrue(userService.findByUsername("user1").isPresent());
        assertTrue(userService.findByUsername("user2").isPresent());
        
        // Guest users not findable
        assertFalse(userService.findByUsername("guest1").isPresent());
        assertFalse(userService.findByUsername("guest2").isPresent());
        
        // Registered usernames unavailable
        assertFalse(userService.isUsernameAvailable("user1"));
        assertFalse(userService.isUsernameAvailable("user2"));
        
        // Guest usernames available
        assertTrue(userService.isUsernameAvailable("guest1"));
        assertTrue(userService.isUsernameAvailable("guest2"));
    }

    // ==================== Edge Cases ====================

    @Test
    void testCreateUserWithSpecialCharacters() {
        String specialUsername = "user_123-test";
        String specialEmail = "user+test@example.com";
        
        User user = userService.createRegisteredUser(specialUsername, specialEmail);
        assertNotNull(user);
        assertEquals(specialUsername, user.getUsername());
        assertEquals(specialEmail, user.getEmail());
        
        Optional<User> found = userService.findByUsername(specialUsername);
        assertTrue(found.isPresent());
    }

    @Test
    void testCreateMultipleGuestsWithSameUsername() {
        User guest1 = userService.createGuestUser("guest");
        User guest2 = userService.createGuestUser("guest");
        User guest3 = userService.createGuestUser("guest");
        
        assertNotNull(guest1);
        assertNotNull(guest2);
        assertNotNull(guest3);
        
        assertNotEquals(guest1.getId(), guest2.getId());
        assertNotEquals(guest2.getId(), guest3.getId());
        assertNotEquals(guest1.getId(), guest3.getId());
        
        // Username still available for registration
        assertTrue(userService.isUsernameAvailable("guest"));
    }

    @Test
    void testRegistrationPreservesUsernameCase() {
        User user = userService.createRegisteredUser("TestUser", email);
        
        Optional<User> found = userService.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("TestUser", found.get().getUsername()); // Original case preserved
    }
}
