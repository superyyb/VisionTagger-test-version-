package app;

import model.DetectionResult;
import model.Image;
import model.User;
import service.ImageAnalyzerService;
import service.MockRekognitionService;

public class VisionTaggerApp {
  public static void main(String[] args) {
    System.out.println("Starting VisionTagger Demo...\n");
    
    try {
      // Create image analyzer service
      ImageAnalyzerService analyzer = new MockRekognitionService();
      
      // Create a user (guest user for demo)
      User user = User.guestUser("testuser");
      
      // Create an image to analyze (using user ID)
      Image image = new Image(user.getId(), "/path/to/image.jpg", "Test description");
      
      // Analyze the image
      DetectionResult result = analyzer.detect(image);
      
      // Display results
      System.out.println(result);
      
    } catch (Exception e) {
      System.err.println("Demo failed");
      e.printStackTrace();
    }
  }

  // TODO: Abstract input and output streams to allow for different input sources and output sinks.
}
