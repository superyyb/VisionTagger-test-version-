package view;

import model.DetectionResult;
import model.Label;

/**
 * Console-based implementation of the View interface.
 * 
 * <p>This view renders detection results in a human-readable format suitable
 * for console/terminal output. Displays the image path and all detected labels
 * with their confidence scores in a formatted text layout.
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public class ConsoleView implements View {
  @Override
  public void display(DetectionResult result) {
    System.out.println("VisionTagger Detection Result");
    System.out.println("Image: " + result.getImage().getStoragePath());
    System.out.println("Detected Labels:");

    for (Label label : result.getLabels()) {
      System.out.printf(" - %s (%.2f%%)\n", label.getName(), label.getConfidence());
    }
  }
}
