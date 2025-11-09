package model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user of the VisionTaggerApp. Each user has a unique ID, username, and an optional
 * email address.
 */
public class User {
  private final String id;
  private final String username;
  private final String email;
  private final boolean isRegistered;

  /**
   * Constructs a guest User object without an email address.
   *
   * @param username the username of the user
   */
  public User(String username) {
    this.id = UUID.randomUUID().toString();
    this.username = username;
    this.email = "";
    this.isRegistered = false;
  }

  /**
   * Constructs a User object with all fields initialized.
   *
   * @param username the username of the user
   * @param email    the email address of the user
   */
  public User(String username, String email) {
    this.id = generateId(username);
    this.username = username;
    this.email = email;
    this.isRegistered = (email != null && !email.isEmpty());
  }

  /**
   * Creates a guest user with a unique ID. Guest users can have duplicate usernames.
   *
   * @param username the username of the guest user
   * @return a new guest User object
   */
  public static User guestUser(String username) {
    return new User(username);
  }

  /**
   * Creates a registered user with an email address. Registered users must have unique usernames.
   * Note: Username uniqueness should be validated by UserService before calling this method.
   *
   * @param username the username of the registered user
   * @param email    the email address of the registered user
   * @return a new registered User object
   */
  public static User registeredUser(String username, String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Registered users must provide an email address");
    }
    return new User(username, email);
  }

  /**
   * Generates a unique ID for the user based on the username.
   *
   * @param username the username of the user
   * @return a formatted string of the form "user_<username>" with whitespace replaced by
   *         underscores and all letters in lowercase
   */
  private String generateId(String username) {
    return "user_<" + username.trim().toLowerCase().replaceAll("\\s", "_") + ">";
  }

  /**
   * Returns the unique identifier for this user.
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the username of this user.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the email address of this user.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Returns whether this user is registered (has an email address).
   *
   * @return true if the user is registered, false if guest
   */
  public boolean isRegistered() {
    return isRegistered;
  }

  /**
   * Returns a string representation of this user.
   *
   * @return User[id, username, email]
   */
  @Override
  public String toString() {
    return String.format("User[id=%s, username=%s, email=%s]", id, username, email);
  }

  /**
   * Compares this user to another user for equality. Two users are considered equal if they have
   * the same ID.
   *
   * @param o the object to be compared
   * @return true if two users are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User other = (User) o;
    return id.equals(other.id);
  }

  /**
   * Returns the hash code of this user. The hash code is based on the user ID, consistent with
   * equals().
   *
   * @return the hash code based on the user ID
   */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
