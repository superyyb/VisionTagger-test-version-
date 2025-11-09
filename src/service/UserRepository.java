package service;

import model.User;
import java.util.Optional;

/**
 * Repository interface for managing User persistence and retrieval.
 * Only registered users are stored in the repository.
 */
public interface UserRepository {
  /**
   * Checks if a username already exists for a registered user.
   *
   * @param username the username to check
   * @return true if the username exists for a registered user, false otherwise
   */
  boolean existsByUsername(String username);

  /**
   * Finds a user by username.
   *
   * @param username the username to search for
   * @return an Optional containing the user if found, empty otherwise
   */
  Optional<User> findByUsername(String username);

  /**
   * Saves a user to the repository.
   *
   * @param user the user to save
   * @return the saved user
   */
  User save(User user);
}

