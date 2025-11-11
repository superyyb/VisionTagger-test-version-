package app;

import controller.ImageController;
import model.User;
import service.*;

/**
 * Main application entry point for VisionTagger.
 * 
 * <p>VisionTagger is an image recognition application that allows users to upload images
 * and receive AI-powered label detection results. The application supports both guest
 * and registered users, with registered users having persistent storage of their results.
 * 
 * <p>This main method demonstrates the basic workflow:
 * <ol>
 *   <li>Initialize service dependencies (user repository, storage, analyzer)</li>
 *   <li>Create a registered user</li>
 *   <li>Upload and analyze an image</li>
 *   <li>Retrieve and display results for the user</li>
 * </ol>
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public class VisionTaggerApp {
  /**
   * Main entry point for the VisionTagger application.
   * 
   * <p>Initializes the application with in-memory implementations for development/testing.
   * In production, these would be replaced with real implementations (e.g., DynamoDB,
   * S3, AWS Rekognition).
   * 
   * @param args command-line arguments (currently unused)
   */
  public static void main(String[] args) {
    // Initialize dependencies with in-memory implementations for development
    UserRepository userRepo = new InMemoryUserRepository();
    UserService userService = new UserService(userRepo);
    FileStorageService storage = new InMemoryFileStorageService();
    ImageAnalyzerService analyzer = new MockRekognitionService();
    ImageController controller = new ImageController(analyzer, storage);

    // Create a registered user
    User anna = userService.createRegisteredUser("Anna", "anna@example.com");

    // Upload image and analyze it
    var result = controller.uploadAndAnalyzeImage(anna, "cat.jpg", "A cute cat photo");
    System.out.println(result);

    // Retrieve and display all results for the user
    System.out.println("All results for " + anna.getUsername() + ":");
    controller.listUserResults(anna).forEach(System.out::println);
  }
}