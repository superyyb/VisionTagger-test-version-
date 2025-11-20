package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import model.User;
import java.util.Optional;

/**
 * Unit tests for the InMemoryUserRepository class.
 */
public class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;
    private User registeredUser;
    private User guestUser;
    private String username;
    private String email;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        username = "testuser";
        email = "test@example.com";
        registeredUser = User.registeredUser(username, email);
        guestUser = User.guestUser("guestuser");
    }

    // ==================== Save Tests ====================

    @Test
    void testSaveRegisteredUser() {
        User saved = repository.save(registeredUser);
        assertNotNull(saved);
        assertEquals(registeredUser.getUsername(), saved.getUsername());
        assertEquals(registeredUser.getEmail(), saved.getEmail());
    }

    @Test
    void testSaveGuestUserDoesNotStore() {
        User saved = repository.save(guestUser);
        assertNotNull(saved);
        
        // Guest users should not be stored
        assertFalse(repository.existsByUsername(guestUser.getUsername()));
        assertFalse(repository.findByUsername(guestUser.getUsername()).isPresent());
    }

    @Test
    void testSaveNullUserThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(null);
        });
    }

    @Test
    void testSaveUpdatesExistingUser() {
        repository.save(registeredUser);
        
        User updatedUser = User.registeredUser(username, "newemail@example.com");
        repository.save(updatedUser);
        
        Optional<User> found = repository.findByUsername(username);
        assertTrue(found.isPresent());
        assertEquals("newemail@example.com", found.get().getEmail());
    }

    // ==================== ExistsByUsername Tests ====================

    @Test
    void testExistsByUsernameForRegisteredUser() {
        assertFalse(repository.existsByUsername(username));
        
        repository.save(registeredUser);
        
        assertTrue(repository.existsByUsername(username));
    }

    @Test
    void testExistsByUsernameForGuestUser() {
        repository.save(guestUser);
        
        assertFalse(repository.existsByUsername(guestUser.getUsername()));
    }

    @Test
    void testExistsByUsernameNullReturnsFalse() {
        assertFalse(repository.existsByUsername(null));
    }

    @Test
    void testExistsByUsernameCaseInsensitive() {
        repository.save(registeredUser);
        
        assertTrue(repository.existsByUsername("TESTUSER"));
        assertTrue(repository.existsByUsername("TestUser"));
        assertTrue(repository.existsByUsername("testuser"));
    }

    @Test
    void testExistsByUsernameNotFound() {
        assertFalse(repository.existsByUsername("nonexistent"));
    }

    // ==================== FindByUsername Tests ====================

    @Test
    void testFindByUsername() {
        repository.save(registeredUser);
        
        Optional<User> found = repository.findByUsername(username);
        assertTrue(found.isPresent());
        assertEquals(registeredUser.getId(), found.get().getId());
        assertEquals(registeredUser.getUsername(), found.get().getUsername());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> found = repository.findByUsername("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUsernameNull() {
        Optional<User> found = repository.findByUsername(null);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUsernameCaseInsensitive() {
        repository.save(registeredUser);
        
        Optional<User> found1 = repository.findByUsername("TESTUSER");
        Optional<User> found2 = repository.findByUsername("TestUser");
        
        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertEquals(registeredUser.getId(), found1.get().getId());
        assertEquals(registeredUser.getId(), found2.get().getId());
    }

    @Test
    void testFindByUsernameForGuestUser() {
        repository.save(guestUser);
        
        Optional<User> found = repository.findByUsername(guestUser.getUsername());
        assertFalse(found.isPresent());
    }

    // ==================== Clear Tests ====================

    @Test
    void testClear() {
        repository.save(registeredUser);
        repository.save(User.registeredUser("user2", "user2@example.com"));
        
        assertTrue(repository.existsByUsername(username));
        assertTrue(repository.existsByUsername("user2"));
        
        repository.clear();
        
        assertFalse(repository.existsByUsername(username));
        assertFalse(repository.existsByUsername("user2"));
    }

    @Test
    void testClearEmptyRepository() {
        repository.clear();
        assertFalse(repository.existsByUsername(username));
    }

    // ==================== Integration Tests ====================

    @Test
    void testSaveMultipleRegisteredUsers() {
        User user1 = User.registeredUser("user1", "user1@example.com");
        User user2 = User.registeredUser("user2", "user2@example.com");
        User user3 = User.registeredUser("user3", "user3@example.com");
        
        repository.save(user1);
        repository.save(user2);
        repository.save(user3);
        
        assertTrue(repository.existsByUsername("user1"));
        assertTrue(repository.existsByUsername("user2"));
        assertTrue(repository.existsByUsername("user3"));
    }

    @Test
    void testSaveMixedGuestAndRegisteredUsers() {
        User registered = User.registeredUser("registered", "reg@example.com");
        User guest1 = User.guestUser("guest1");
        User guest2 = User.guestUser("guest2");
        
        repository.save(registered);
        repository.save(guest1);
        repository.save(guest2);
        
        // Only registered user should be stored
        assertTrue(repository.existsByUsername("registered"));
        assertFalse(repository.existsByUsername("guest1"));
        assertFalse(repository.existsByUsername("guest2"));
    }

    @Test
    void testCaseInsensitiveOverwrite() {
        User user1 = User.registeredUser("TestUser", "test1@example.com");
        User user2 = User.registeredUser("testuser", "test2@example.com");
        
        repository.save(user1);
        repository.save(user2);
        
        // Second save should overwrite first (case-insensitive)
        Optional<User> found = repository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("test2@example.com", found.get().getEmail());
    }

    @Test
    void testRepositoryPersistsAcrossOperations() {
        repository.save(User.registeredUser("user1", "user1@example.com"));
        repository.save(User.registeredUser("user2", "user2@example.com"));
        
        assertTrue(repository.existsByUsername("user1"));
        Optional<User> found = repository.findByUsername("user2");
        assertTrue(found.isPresent());
        
        repository.save(User.registeredUser("user3", "user3@example.com"));
        
        assertTrue(repository.existsByUsername("user1"));
        assertTrue(repository.existsByUsername("user2"));
        assertTrue(repository.existsByUsername("user3"));
    }
}
