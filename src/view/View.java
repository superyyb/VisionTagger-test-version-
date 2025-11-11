package view;

import model.DetectionResult;

/**
 * Interface for rendering detection results in different formats.
 * 
 * <p>Implementations of this interface provide different ways to display
 * detection results to users, such as console output, JSON format, HTML, etc.
 * 
 * <p>TODO: Define view methods:
 * <ul>
 *   <li>render(DetectionResult) - render a single result</li>
 *   <li>render(List&lt;DetectionResult&gt;) - render multiple results</li>
 *   <li>renderError(String) - render error messages</li>
 * </ul>
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public interface View {
  void display(DetectionResult result);
}
