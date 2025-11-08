package model;

import java.util.Objects;

/**
 * Represents a user of the VisionTaggerApp. Each user has a unique ID, username, and an optional
 * email address.
 */
public class User {
  private final String id;
  private final String username;
  private final String email;

  /**
   * Constructs a User object with all fields initialized.
   *
   * @param username the username of the user
   * @param email    the email address of the user; empty string by default
   */
  public User(String username, String email) {
    this.id = generateId(username);
    this.username = username;
    this.email = email == null ? "" : email;
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
