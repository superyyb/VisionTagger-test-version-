package view;

import model.User;
import service.UserService;
import java.util.Scanner;
import java.util.Optional;

/**
 * View for managing user authentication and registration in console mode.
 * 
 * <p>This view handles the user interaction flow for:
 * <ul>
 *   <li>Guest user creation</li>
 *   <li>User registration</li>
 *   <li>User login</li>
 *   <li>Displaying user information</li>
 * </ul>
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public class UserManagementView {
  private final UserService userService;
  @SuppressWarnings("resource") // Intentionally not closing Scanner(System.in)
  private final Scanner scanner = new Scanner(System.in);

  /**
   * Constructs a UserManagementView with the specified user service.
   * 
   * @param userService the service for user management operations
   */
  public UserManagementView(UserService userService) {
    this.userService = userService;
  }

  /**
   * Displays the main menu and returns the selected user.
   * 
   * @return the User object (guest or registered)
   */
  public User showMainMenu() {
    System.out.println("\n=== VisionTagger ===");
    System.out.println("1. Continue as Guest");
    System.out.println("2. Register New User");
    System.out.println("3. Login");
    System.out.println("4. Exit");
    System.out.print("Choose an option (1-4): ");
    
    String choice = scanner.nextLine().trim();
    
    switch (choice) {
      case "1":
        return handleGuestUser();
      case "2":
        return handleRegistration();
      case "3":
        return handleLogin();
      case "4":
        System.out.println("Exiting...");
        System.exit(0);
        return null;
      default:
        System.out.println("Invalid choice. Continuing as guest...");
        return handleGuestUser();
    }
  }

  /**
   * Handles guest user creation.
   * 
   * @return a guest User object
   */
  private User handleGuestUser() {
    System.out.print("\nEnter guest username (or press Enter for 'Guest'): ");
    String username = scanner.nextLine().trim();
    if (username.isEmpty()) {
      username = "Guest";
    }
    User guest = userService.createGuestUser(username);
    System.out.println("✓ Logged in as guest: " + guest.getUsername());
    System.out.println("Note: Guest results are not saved.");
    return guest;
  }

  /**
   * Handles user registration.
   * 
   * @return a registered User object, or guest if registration fails
   */
  private User handleRegistration() {
    System.out.println("\n=== User Registration ===");
    System.out.print("Enter username: ");
    String username = scanner.nextLine().trim();
    
    if (username.isEmpty()) {
      System.out.println("✗ Username cannot be empty. Continuing as guest...");
      return handleGuestUser();
    }

    // Check if username is available
    if (!userService.isUsernameAvailable(username)) {
      System.out.println("✗ Username already exists. Please choose another.");
      System.out.print("Try again? (y/n): ");
      if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
        return handleRegistration();
      }
      return handleGuestUser();
    }

    System.out.print("Enter email: ");
    String email = scanner.nextLine().trim();
    
    if (email.isEmpty()) {
      System.out.println("✗ Email is required for registration. Continuing as guest...");
      return handleGuestUser();
    }

    try {
      User user = userService.createRegisteredUser(username, email);
      System.out.println("✓ Registration successful!");
      System.out.println("✓ Logged in as: " + user.getUsername());
      System.out.println("Note: Your results will be saved.");
      return user;
    } catch (IllegalArgumentException e) {
      System.out.println("✗ Registration failed: " + e.getMessage());
      System.out.println("Continuing as guest...");
      return handleGuestUser();
    }
  }

  /**
   * Handles user login.
   * 
   * @return a registered User object, or guest if login fails
   */
  private User handleLogin() {
    System.out.println("\n=== User Login ===");
    System.out.print("Enter username: ");
    String username = scanner.nextLine().trim();
    
    if (username.isEmpty()) {
      System.out.println("✗ Username cannot be empty. Continuing as guest...");
      return handleGuestUser();
    }

    Optional<User> userOpt = userService.findByUsername(username);
    
    if (userOpt.isPresent() && userOpt.get().isRegistered()) {
      User user = userOpt.get();
      System.out.println("✓ Login successful!");
      System.out.println("✓ Welcome back, " + user.getUsername() + "!");
      System.out.println("Note: Your results will be saved.");
      return user;
    } else {
      System.out.println("✗ User not found. Please register first.");
      System.out.print("Register now? (y/n): ");
      if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
        return handleRegistration();
      }
      return handleGuestUser();
    }
  }

  /**
   * Displays user information.
   * 
   * @param user the user to display information for
   */
  public void displayUserInfo(User user) {
    System.out.println("\n=== User Information ===");
    System.out.println("Username: " + user.getUsername());
    System.out.println("Type: " + (user.isRegistered() ? "Registered" : "Guest"));
    if (user.isRegistered()) {
      System.out.println("Email: " + user.getEmail());
    }
    System.out.println("User ID: " + user.getId());
  }
}

