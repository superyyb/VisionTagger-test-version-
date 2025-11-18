package service;

import model.User;
import java.util.Optional;

/**
 * Service for managing user creation and validation.
 */
public class UserService {
  private final UserRepository userRepository;

  /**
   * Constructs a UserService with the specified repository.
   *
   * @param userRepository the repository for user persistence
   */
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Creates a guest user without email validation. Guest users can have duplicate usernames.
   *
   * @param username the username for the guest user
   * @return a new guest User object
   */
  public User createGuestUser(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    return User.guestUser(username.trim());
  }

  /**
   * Creates a registered user with email validation. Registered users must have unique usernames.
   *
   * @param username the username for the registered user
   * @param email    the email address for the registered user
   * @return a new registered User object
   * @throws IllegalArgumentException if username is null/empty, email is null/empty,
   *                                  or username already exists for a registered user
   */
  public User createRegisteredUser(String username, String email) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be null or empty for registered users");
    }

    String trimmedUsername = username.trim();
    
    // Check for duplicate username (only for registered users)
    if (!isUsernameAvailable(trimmedUsername)) {
      throw new IllegalArgumentException("Username already exists: " + trimmedUsername);
    }

    User user = User.registeredUser(trimmedUsername, email.trim());
    return userRepository.save(user);
  }

  /**
   * Checks if a username is available for registration.
   *
   * @param username the username to check
   * @return true if the username is available, false otherwise
   */
  public boolean isUsernameAvailable(String username) {
    if (username == null || username.trim().isEmpty()) {
      return false;
    }
    return !userRepository.existsByUsername(username.trim());
  }

  /**
   * Finds a user by username.
   *
   * @param username the username to search for
   * @return an Optional containing the user if found, empty otherwise
   */
  public Optional<User> findByUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      return Optional.empty();
    }
    return userRepository.findByUsername(username.trim());
  }
}

