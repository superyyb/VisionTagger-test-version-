package controller;

import model.Image;
import model.DetectionResult;
import model.User;
import service.ImageAnalyzerService;
import service.FileStorageService;
import view.View;

import java.util.List;
import java.util.Optional;

/**
 * Controller that coordinates image uploads, analysis, and result retrieval.
 * 
 * <p>This controller acts as the main orchestrator for image-related operations,
 * coordinating between the image analyzer service, storage service, and view.
 * It handles the workflow of uploading an image, analyzing it for labels, and
 * storing/retrieving/displaying the results.
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public class ImageController {

  /** Service for analyzing images and detecting labels. */
  private final ImageAnalyzerService analyzerService;
  
  /** Service for persisting and retrieving detection results. */
  private final FileStorageService storageService;
  
  /** View for displaying detection results. */
  private final View view;

  /**
   * Constructs an ImageController with the specified services and view.
   * 
   * @param analyzerService the service for analyzing images
   * @param view the view for displaying results
   * @throws IllegalArgumentException if either parameter is null
   */
  public ImageController(ImageAnalyzerService analyzerService, View view) {
    if (analyzerService == null) {
      throw new IllegalArgumentException("Analyzer service cannot be null");
    }
    if (view == null) {
      throw new IllegalArgumentException("View cannot be null");
    }
    this.analyzerService = analyzerService;
    this.storageService = null; // Optional for simple processing
    this.view = view;
  }

  /**
   * Constructs an ImageController with the specified services (backward compatibility).
   * 
   * @param analyzerService the service for analyzing images
   * @param storageService the service for storing and retrieving results
   * @throws IllegalArgumentException if either service is null
   */
  public ImageController(ImageAnalyzerService analyzerService, FileStorageService storageService) {
    if (analyzerService == null) {
      throw new IllegalArgumentException("Analyzer service cannot be null");
    }
    if (storageService == null) {
      throw new IllegalArgumentException("Storage service cannot be null");
    }
    this.analyzerService = analyzerService;
    this.storageService = storageService;
    this.view = null;
  }

  /**
   * Handles the image upload and triggers detection analysis.
   * 
   * <p>This method performs the complete workflow:
   * <ol>
   *   <li>Creates an Image object from the provided parameters</li>
   *   <li>Analyzes the image using the analyzer service</li>
   *   <li>Saves the detection result to storage (if storage service is available)</li>
   *   <li>Returns the result</li>
   * </ol>
   * 
   * @param user the user uploading the image (must not be null)
   * @param filePath the path or URI of the image file (must not be null or empty)
   * @param description optional description of the image (can be null)
   * @return the DetectionResult containing detected labels
   * @throws IllegalArgumentException if user is null, or filePath is null/empty
   */
  public DetectionResult uploadAndAnalyzeImage(User user, String filePath, String description) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    if (filePath == null || filePath.trim().isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }
    // Create image object with user ID, file path, and optional description
    Image image = new Image(user.getId(), filePath, description);
    // Analyze image to detect labels
    DetectionResult result = analyzerService.detect(image);
    // Persist the result to storage if storage service is available
    if (storageService != null) {
      storageService.save(result);
    }
    return result;
  }

  /**
   * Retrieves a detection result by image ID.
   * 
   * @param imageId the unique ID of the image (must not be null or empty)
   * @return an Optional containing the DetectionResult if found, empty otherwise
   * @throws IllegalArgumentException if imageId is null or empty
   * @throws IllegalStateException if storage service is not available
   */
  public Optional<DetectionResult> getDetectionResult(String imageId) {
    if (imageId == null || imageId.trim().isEmpty()) {
      throw new IllegalArgumentException("Image ID cannot be null or empty");
    }
    if (storageService == null) {
      throw new IllegalStateException("Storage service is not available");
    }
    return storageService.getResultByImageId(imageId);
  }

  /**
   * Lists all detection results for a user.
   * 
   * @param user the user to retrieve results for (must not be null)
   * @return a list of DetectionResult objects for the user, empty list if none found
   * @throws IllegalArgumentException if user is null
   * @throws IllegalStateException if storage service is not available
   */
  public List<DetectionResult> listUserResults(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    if (storageService == null) {
      throw new IllegalStateException("Storage service is not available");
    }
    return storageService.getResultsByUserId(user.getId());
  }

  /**
   * Processes an image file: analyzes it and displays the results using the view.
   * 
   * <p>This is a simplified workflow that:
   * <ol>
   *   <li>Creates a guest user for the operation</li>
   *   <li>Analyzes the image</li>
   *   <li>Displays the result using the configured view</li>
   * </ol>
   * 
   * <p>Note: Results are not persisted when using this method. Use
   * uploadAndAnalyzeImage() with a registered user and storage service for persistence.
   * 
   * @param filePath the path to the image file (must not be null or empty)
   * @throws IllegalArgumentException if filePath is null or empty
   * @throws IllegalStateException if view is not available
   */
  public void process(String filePath) {
    if (filePath == null || filePath.trim().isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }
    if (view == null) {
      throw new IllegalStateException("View is not available");
    }
    // Create a guest user for this operation
    User guestUser = User.guestUser("guest");
    // Create image object
    Image image = new Image(guestUser.getId(), filePath.trim(), "");
    // Analyze image
    DetectionResult result = analyzerService.detect(image);
    // Display result using the view
    view.display(result);
  }
}