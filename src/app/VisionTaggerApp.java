package app;

import java.util.Scanner;

import controller.ImageController;
import service.*;
import view.ConsoleView;
import view.JsonView;
import view.SwingView;
import view.View;

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
    
    ImageAnalyzerService analyzer = new MockRekognitionService();
    View view;

    // Choose view mode
    if (args.length > 0 && args[0].equals("--json")) {
      view = new JsonView();
    } else if (args.length > 0 && args[0].equals("--gui")) {
      view = new SwingView();
    } else {
      view = new ConsoleView();
    }

    ImageController controller = new ImageController(analyzer, view);

    // Choose input method
    String filePath;
    if (args.length > 1) {
      filePath = args[1];
    } else {
      @SuppressWarnings("resource") // Intentionally not closing Scanner(System.in)
      Scanner input = new Scanner(System.in);
      System.out.print("Enter image file path: ");
      filePath = input.nextLine();
    }

    controller.process(filePath);
  }
}