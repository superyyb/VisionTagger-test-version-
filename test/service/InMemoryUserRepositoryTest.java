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
    private User registeredUser2;
    private User guestUser;
    private String username;
    private String email;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        username = "testUser";
        email = "testUser@example.com";
        registeredUser = User.registeredUser(username, email);
        registeredUser2 = User.registeredUser("testUser2", "testUser2@example.com");
        guestUser = User.guestUser("guestUser");
    }


    //Tests that saving a registered user stores it in the repository,
    //and it can be found by existsByUsername.
    @Test
    void testSaveRegisteredUser() {
        assertFalse(repository.existsByUsername(username));
        
        User saved = repository.save(registeredUser);
        assertNotNull(saved);
        assertEquals(registeredUser.getUsername(), saved.getUsername());
        assertEquals(registeredUser.getEmail(), saved.getEmail());
        assertTrue(repository.existsByUsername(username));
    }


    //Tests that saving a guest user does not store it in the repository.
    @Test
    void testSaveGuestUserDoesNotStore() {
        User saved = repository.save(guestUser);
        assertNotNull(saved);
        
        // Guest users should not be stored
        assertFalse(repository.existsByUsername(guestUser.getUsername()));
        assertFalse(repository.findByUsername(guestUser.getUsername()).isPresent());
    }


    //Tests that saving a null user throws an IllegalArgumentException.
    @Test
    void testSaveNullUserThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(null);
        });
    }

    //Tests that saving an existing user updates it with new information.
    @Test
    void testSaveUpdatesExistingUser() {
        repository.save(registeredUser);
        
        User updatedUser = User.registeredUser(username, "newemail@example.com");
        repository.save(updatedUser);
        
        Optional<User> found = repository.findByUsername(username);
        assertTrue(found.isPresent());
        assertEquals("newemail@example.com", found.get().getEmail());
    }


    //Tests that existsByUsername returns false when called with null/empty usernames(Invalid input).
    @Test
    void testExistsByUsernameInvalidInputReturnsFalse() {
      assertFalse(repository.existsByUsername(null));
      assertFalse(repository.existsByUsername(""));
      assertFalse(repository.existsByUsername("   "));
    }


    //Tests that existsByUsername is case-insensitive and works with different case variations.
    @Test
    void testExistsByUsernameCaseInsensitive() {
        repository.save(registeredUser);
        
        assertTrue(repository.existsByUsername("TESTUSER"));
        assertTrue(repository.existsByUsername("TestUser"));
        assertTrue(repository.existsByUsername("testuser"));
    }

    //Tests that existsByUsername returns false for a non-existent username.
    //User may not have registered yet
    @Test
    void testExistsByUsernameNotFound() {
        assertFalse(repository.existsByUsername("nonexistent"));
    }


    //Tests that findByUsername returns the correct registered user after saving.
    @Test
    void testFindByUsername() {
        repository.save(registeredUser);
        
        Optional<User> found = repository.findByUsername(username);
        assertTrue(found.isPresent());
        assertEquals(registeredUser.getId(), found.get().getId());
        assertEquals(registeredUser.getUsername(), found.get().getUsername());
    }


    //Tests that findByUsername returns an empty Optional for a non-existent username.
    @Test
    void testFindByUsernameNotFound() {
        Optional<User> found = repository.findByUsername("nonexistent");
        assertFalse(found.isPresent());
    }


    //Tests that findByUsername returns an empty Optional when called with null/empty.
    @Test
    void testFindByUsernameInvalidInputReturnsEmpty() {
      Optional<User> found1 = repository.findByUsername(null);
      Optional<User> found2 = repository.findByUsername("");
      Optional<User> found3 = repository.findByUsername("   ");

      assertFalse(found1.isPresent());
      assertFalse(found2.isPresent());
      assertFalse(found3.isPresent());
    }


    //Tests that findByUsername is case-insensitive and works with different case variations.
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


    //Tests that clear removes all registered users from the repository.
    @Test
    void testClear() {
        repository.save(registeredUser);
        repository.save(registeredUser2);
        
        assertTrue(repository.existsByUsername(username));
        assertTrue(repository.existsByUsername(registeredUser2.getUsername()));
        
        repository.clear();
        
        assertFalse(repository.existsByUsername(username));
        assertFalse(repository.existsByUsername(registeredUser2.getUsername()));
    }
}
