package service;

import model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implementation of UserRepository for testing and development.
 */
public class InMemoryUserRepository implements UserRepository {
  private final Map<String, User> registeredUsers; // username -> User (only for registered users)

  public InMemoryUserRepository() {
    this.registeredUsers = new HashMap<>();
  }

  @Override
  public boolean existsByUsername(String username) {
    if (username == null) {
      return false;
    }
    String normalizedUsername = normalizeUsername(username);
    User existingUser = registeredUsers.get(normalizedUsername);
    // Only check registered users (those with email)
    return existingUser != null && existingUser.isRegistered();
  }

  @Override
  public Optional<User> findByUsername(String username) {
    if (username == null) {
      return Optional.empty();
    }
    String normalizedUsername = normalizeUsername(username);
    return Optional.ofNullable(registeredUsers.get(normalizedUsername));
  }

  @Override
  public User save(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    // Only store registered users in the map
    if (user.isRegistered()) {
      String normalizedUsername = normalizeUsername(user.getUsername());
      registeredUsers.put(normalizedUsername, user);
    }
    return user;
  }

  /**
   * Normalizes username for consistent lookup (lowercase, trimmed).
   *
   * @param username the username to normalize
   * @return the normalized username
   */
  private String normalizeUsername(String username) {
    return username.trim().toLowerCase();
  }

  /**
   * Clears all registered users from the repository (useful for testing).
   */
  public void clear() {
    registeredUsers.clear();
  }
}

