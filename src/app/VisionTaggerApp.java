package app;

import java.util.Scanner;

import controller.ImageController;
import model.User;
import service.*;
import view.ConsoleView;
import view.JsonView;
import view.SwingView;
import view.SwingViewPro;
import view.View;
import view.UserManagementView;

/**
 * Main application entry point for VisionTagger.
 * 
 * <p>VisionTagger is an image recognition application that allows users to upload images
 * and receive AI-powered label detection results. The application supports both guest
 * and registered users, with registered users having persistent storage of their results.
 * 
 * <p>Features:
 * <ul>
 *   <li>Guest users: Quick image analysis without registration</li>
 *   <li>Registered users: Persistent storage of analysis results</li>
 *   <li>User history: View all saved results for registered users</li>
 *   <li>Multiple view modes: Console, JSON, and GUI</li>
 * </ul>
 * 
 * <p>Command-line usage:
 * <ul>
 *   <li>{@code java VisionTaggerApp --json &lt;filepath&gt;} - Output results as JSON</li>
 *   <li>{@code java VisionTaggerApp --gui &lt;filepath&gt;} - Display results in simple GUI window</li>
 *   <li>{@code java VisionTaggerApp --gui-pro &lt;filepath&gt;} - Display results in enhanced Pro GUI window</li>
 *   <li>{@code java VisionTaggerApp &lt;filepath&gt;} - Display results in console (default)</li>
 *   <li>{@code java VisionTaggerApp} - Interactive mode with user management</li>
 * </ul>
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
//--gui-pro /Users/you/Desktop/test.jpg
public class VisionTaggerApp {
  /**
   * Main entry point for the VisionTagger application.
   * 
   * <p>Initializes the application with in-memory implementations for development/testing.
   * In production, these would be replaced with real implementations (e.g., DynamoDB,
   * S3, AWS Rekognition).
   * 
   * @param args command-line arguments: optional view flag (--json, --gui, or --gui-pro) and file path
   */
  public static void main(String[] args) {
    // Initialize services
    ImageAnalyzerService analyzer = new MockRekognitionService();
    UserRepository userRepo = new InMemoryUserRepository();
    UserService userService = new UserService(userRepo);
    FileStorageService storage = new InMemoryFileStorageService();
    
    View view = new ConsoleView(); // default
    String filePath = null;
    boolean interactiveMode = false;

    // Parse command-line arguments
    if (args.length > 0) {
      String firstArg = args[0];
      switch (firstArg) {
        case "--json":
          view = new JsonView();
          if (args.length > 1) filePath = args[1];
          break;
        case "--gui":
          view = new SwingView();
          if (args.length > 1) filePath = args[1];
          break;
        case "--gui-pro":
          view = new SwingViewPro();
          if (args.length > 1) filePath = args[1];
          break;
        default:
          // First argument is the file path
          filePath = args[0];
      }
    } else {
      // No arguments - use interactive mode
      interactiveMode = true;
    }

    // Create controller with storage for registered users
    ImageController controller = new ImageController(analyzer, storage, view);

    if (interactiveMode) {
      // Interactive mode with user management
      runInteractiveMode(controller, userService, view);
    } else {
      // Quick mode: process file directly (guest user, no storage)
      if (filePath == null) {
        System.err.println("Error: No file path provided.");
        System.err.println("Usage: java VisionTaggerApp [--json|--gui|--gui-pro] <filepath>");
        System.err.println("   or: java VisionTaggerApp <filepath>  (for console view)");
        System.exit(1);
      }
      // Use simple process for quick mode (no user management)
      ImageController quickController = new ImageController(analyzer, view);
      quickController.process(filePath);
    }
  }

  /**
   * Runs the interactive mode with user management.
   * 
   * @param controller the image controller with storage support
   * @param userService the user service for authentication
   * @param view the view for displaying results
   */
  private static void runInteractiveMode(ImageController controller, UserService userService, View view) {
    @SuppressWarnings("resource")
    Scanner scanner = new Scanner(System.in);
    UserManagementView userView = new UserManagementView(userService);
    
    // Show user management menu
    User currentUser = userView.showMainMenu();
    userView.displayUserInfo(currentUser);
    
    boolean running = true;
    while (running) {
      System.out.println("\n=== Main Menu ===");
      System.out.println("1. Analyze Image");
      System.out.println("2. View My Results" + (currentUser.isRegistered() ? "" : " (Registered users only)"));
      System.out.println("3. Switch User");
      System.out.println("4. Exit");
      System.out.print("Choose an option (1-4): ");
      
      String choice = scanner.nextLine().trim();
      
      switch (choice) {
        case "1":
          handleAnalyzeImage(controller, currentUser, scanner);
          break;
        case "2":
          if (currentUser.isRegistered()) {
            controller.displayUserHistory(currentUser);
          } else {
            System.out.println("\n⚠ This feature is only available for registered users.");
            System.out.println("Please register to save and view your results.");
          }
          break;
        case "3":
          currentUser = userView.showMainMenu();
          userView.displayUserInfo(currentUser);
          break;
        case "4":
          System.out.println("\nThank you for using VisionTagger!");
          running = false;
          break;
        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }

  /**
   * Handles image analysis for a user.
   * 
   * @param controller the image controller
   * @param user the current user
   * @param scanner the scanner for input
   */
  private static void handleAnalyzeImage(ImageController controller, User user, Scanner scanner) {
    System.out.print("\nEnter image file path: ");
    String filePath = scanner.nextLine().trim();
    
    if (filePath.isEmpty()) {
      System.out.println("✗ File path cannot be empty.");
      return;
    }
    
    System.out.print("Enter description (optional, press Enter to skip): ");
    String description = scanner.nextLine().trim();
    if (description.isEmpty()) {
      description = null;
    }
    
    try {
      controller.processForUser(user, filePath, description);
    } catch (IllegalArgumentException e) {
      System.out.println("✗ Error: " + e.getMessage());
    }
  }
}