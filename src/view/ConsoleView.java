package view;

import model.DetectionResult;
import model.Label;

/**
 * Console-based implementation of the View interface.
 * 
 * <p>This view renders detection results in a human-readable format suitable
 * for console/terminal output. Currently not implemented.
 * 
 * <p>TODO: Implement console rendering:
 * <ul>
 *   <li>Format DetectionResult for console display</li>
 *   <li>Display labels with confidence scores</li>
 *   <li>Format timestamps and metadata</li>
 *   <li>Handle multi-result displays</li>
 * </ul>
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public class ConsoleView implements View {
  @Override
  public void display(DetectionResult result) {
    System.out.println("VisionTagger Detction Result");
    System.out.println("Image: " + result.getImage().getStoragePath());
    System.out.println("Detected Labels:");

    for (Label lebel : result.getLabels()) {
      System.out.printf(" - %s (%.2f%%)", lebel.getName(), lebel.getConfidence());
    }
    System.out.println();
  }
}
