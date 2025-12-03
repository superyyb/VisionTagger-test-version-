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
    username = "testUser";
    email = "test@example.com";
  }


  //Tests that createGuestUser creates a guest user with correct properties.
  @Test
  void testCreateGuestUser() {
    User guest = userService.createGuestUser(username);

    assertNotNull(guest);
    assertEquals(username, guest.getUsername());
    assertFalse(guest.isRegistered());
    assertEquals("", guest.getEmail());
  }


  //Tests that createGuestUser throws an IllegalArgumentException
  //when called with null/empty username.
  @Test
  void testCreateGuestUserInvalidUsernameThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createGuestUser(null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createGuestUser("");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createGuestUser("   ");
    });
  }

  //Tests that createGuestUser trims whitespace from the username.
  @Test
  void testCreateGuestUserTrimsWhitespace() {
    User guest = userService.createGuestUser("  testUser  ");
    assertEquals("testUser", guest.getUsername());
  }


  //Tests that createGuestUser does not check for duplicate usernames
  //and allows multiple guests with same username.
  @Test
  void testCreateGuestUserDoesNotCheckDuplicates() {
    User guest1 = userService.createGuestUser("guest");
    User guest2 = userService.createGuestUser("guest");

    assertNotNull(guest1);
    assertNotNull(guest2);
    assertNotEquals(guest1.getId(), guest2.getId());
  }


  //Tests that createGuestUser does not store the guest user in the repository.
  @Test
  void testCreateGuestUserDoesNotStore() {
    User guest = userService.createGuestUser(username);

    assertFalse(userRepository.existsByUsername(username));
  }


  //Tests that createRegisteredUser creates a registered user
  //with correct properties and stores it in the repository.
  @Test
  void testCreateRegisteredUser() {
    User registered = userService.createRegisteredUser(username, email);

    assertNotNull(registered);
    assertEquals(username, registered.getUsername());
    assertEquals(email, registered.getEmail());
    assertTrue(registered.isRegistered());
    assertTrue(userRepository.existsByUsername(username));
    Optional<User> found = userRepository.findByUsername(username);
    assertTrue(found.isPresent());
    assertEquals(registered.getId(), found.get().getId());
  }


  //Tests that createRegisteredUser throws an IllegalArgumentException
  //when called with null/empty username.
  @Test
  void testCreateRegisteredUserInvalidUsernameThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createRegisteredUser(null, email);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createRegisteredUser("", email);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createRegisteredUser("   ", email);
    });
  }


  //Tests that createRegisteredUser throws an IllegalArgumentException
  //when called with null/empty email.
  @Test
  void testCreateRegisteredUserInvalidEmailThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createRegisteredUser(username, null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createRegisteredUser(username, "");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createRegisteredUser(username, "   ");
    });
  }


  //Tests that createRegisteredUser trims whitespace from both username and email inputs.
  @Test
  void testCreateRegisteredUserTrimsInputs() {
    User registered = userService.createRegisteredUser("  testUser  ", "  test@example.com  ");

    assertEquals("testUser", registered.getUsername());
    assertEquals("test@example.com", registered.getEmail());
  }


  //Tests that createRegisteredUser throws an IllegalArgumentException
//when attempting to create a user with a duplicate username (case-insensitive).
  @Test
  void testCreateRegisteredUserDuplicateUsernameThrowsException() {
    userService.createRegisteredUser(username, email);

    // Test exact duplicate
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createRegisteredUser(username, "different@example.com");
    });

    // Test case-insensitive duplicate
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createRegisteredUser("TestUser", "another@example.com");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      userService.createRegisteredUser("testuser", "yetanother@example.com");
    });
  }


  //Tests that creating a registered user after creating a guest user
  //with the same username does not conflict.
  @Test
  void testCreateRegisteredUserAfterGuestDoesNotConflict() {
    userService.createGuestUser(username);

    User registered = userService.createRegisteredUser(username, email);
    assertNotNull(registered);
    assertTrue(registered.isRegistered());
  }


  //Tests that isUsernameAvailable returns true for a new username
  //that has not been registered.
  @Test
  void testIsUsernameAvailableForNewUsername() {
    assertTrue(userService.isUsernameAvailable(username));
  }


  //Tests that isUsernameAvailable is case-insensitive and
  //returns false for usernames that differ only in case.
  @Test
  void testIsUsernameAvailableCaseInsensitive() {
    userService.createRegisteredUser("TestUser", email);

    assertFalse(userService.isUsernameAvailable("testuser"));
    assertFalse(userService.isUsernameAvailable("TESTUSER"));
  }


  //Tests that isUsernameAvailable returns false when called with null/empty string.
  @Test
  void testIsUsernameAvailableInvalidInputReturnsFalse() {
    assertFalse(userService.isUsernameAvailable(null));
    assertFalse(userService.isUsernameAvailable(""));
    assertFalse(userService.isUsernameAvailable("   "));
  }


  //Tests that isUsernameAvailable returns true after creating a guest user,
  //since guest users are not stored.
  @Test
  void testIsUsernameAvailableAfterGuestCreation() {
    userService.createGuestUser(username);

    assertTrue(userService.isUsernameAvailable(username));
  }


  //Tests that findByUsername returns the correct registered user after creation.
  @Test
  void testFindByUsername() {
    User registered = userService.createRegisteredUser(username, email);

    Optional<User> found = userService.findByUsername(username);
    assertTrue(found.isPresent());
    assertEquals(registered.getId(), found.get().getId());
  }


  //Tests that findByUsername returns an empty Optional for a non-existent username.
  @Test
  void testFindByUsernameNotFound() {
    Optional<User> found = userService.findByUsername("nonexistent");
    assertFalse(found.isPresent());
  }


  //Tests that findByUsername returns an empty Optional when called with null/empty.
  @Test
  void testFindByUsernameInvalidInputReturnsEmpty() {
    Optional<User> found1 = userService.findByUsername(null);
    Optional<User> found2 = userService.findByUsername("");
    Optional<User> found3 = userService.findByUsername("   ");

    assertFalse(found1.isPresent());
    assertFalse(found2.isPresent());
    assertFalse(found3.isPresent());
  }


  //Tests that createRegisteredUser works correctly with special characters in username and email.
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
}