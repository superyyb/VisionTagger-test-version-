package model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user of the VisionTaggerApp.
 * A user can be either a guest (temporary) or registered (persistent).
 */
public final class User {

  public enum Type { GUEST, REGISTERED }

  private final String id;
  private final String username;
  private final String email;
  private final Type type;

  private User(String id, String username, String email, Type type) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.type = type;
  }

  /**
   * Creates a guest user. Guest users can share usernames.
   *
   * @param username the username of the guest user
   * @return a new guest User
   */
  public static User guestUser(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    return new User(UUID.randomUUID().toString(), username.trim(), "", Type.GUEST);
  }

  /**
   * Creates a registered user. Registered users must have an email and unique usernames.
   *
   * @param username the username of the registered user
   * @param email the email of the registered user
   * @return a new registered User
   */
  public static User registeredUser(String username, String email) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be null or empty for registered users");
    }
    String id = "user_" + username.trim().toLowerCase().replaceAll("\\s+", "_");
    return new User(id, username.trim(), email.trim(), Type.REGISTERED);
  }

  public String getId() { return id; }
  public String getUsername() { return username; }
  public String getEmail() { return email; }
  public Type getType() { return type; }
  public boolean isRegistered() { return type == Type.REGISTERED; }

  @Override
  public String toString() {
    return String.format("User[id=%s, username=%s, email=%s, type=%s]", id, username, email, type);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User other = (User) o;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}