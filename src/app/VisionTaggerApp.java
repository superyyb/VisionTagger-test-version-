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
   * <p>Command-line usage:
   * <ul>
   *   <li>{@code java VisionTaggerApp --json &lt;filepath&gt;} - Output results as JSON</li>
   *   <li>{@code java VisionTaggerApp --gui &lt;filepath&gt;} - Display results in GUI window</li>
   *   <li>{@code java VisionTaggerApp &lt;filepath&gt;} - Display results in console (default)</li>
   *   <li>{@code java VisionTaggerApp} - Prompts for file path interactively</li>
   * </ul>
   * 
   * @param args command-line arguments: optional view flag (--json or --gui) and file path
   */
  public static void main(String[] args) {
    ImageAnalyzerService analyzer = new MockRekognitionService();
    View view = new ConsoleView(); // default
    String filePath = null;

    // Parse command-line arguments
    if (args.length > 0) {
      switch (args[0]) {
        case "--json":
          view = new JsonView();
          if (args.length > 1) filePath = args[1];
          break;
        case "--gui":
          // SwingView needs analyzer if you’re using the enhanced version
          view = new SwingView();
          if (args.length > 1) filePath = args[1];
          break;
        default:
          // First argument is the file path
          filePath = args[0];
      }
    }

    // Only ask for input if still null
    if (filePath == null) {
      @SuppressWarnings("resource")
      Scanner input = new Scanner(System.in);
      System.out.print("Enter image file path: ");
      filePath = input.nextLine();
    }

    ImageController controller = new ImageController(analyzer, view);
    controller.process(filePath);
  }
}