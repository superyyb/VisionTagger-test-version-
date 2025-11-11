package view;

import model.DetectionResult;
import model.Label;

/**
 * JSON-based implementation of the View interface.
 * 
 * <p>This view renders detection results in JSON format, suitable for API responses
 * or programmatic consumption. Currently not implemented.
 * 
 * <p>TODO: Implement JSON rendering:
 * <ul>
 *   <li>Serialize DetectionResult to JSON</li>
 *   <li>Include all metadata (image info, labels, timestamps)</li>
 *   <li>Handle pretty-printing options</li>
 *   <li>Support JSON array output for multiple results</li>
 * </ul>
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public class JsonView implements View {
  @Override
  public void display(DetectionResult result) {
    StringBuilder json = new StringBuilder();
    json.append("{\n");
    json.append("  \"image\": \"").append(result.getImage().getStoragePath()).append("\",\n");
    json.append("  \"labels\": [\n");
    
    List<Label> labels = result.getLabels();
    for (int i = 0; i < labels.size(); i++) {
      Label l = labels.get(i);
        json.append("    { \"name\": \"").append(l.getName())
          .append("\", \"confidence\": ").append(String.format("%.2f", l.getConfidence()))
          .append(" }");
        if (i < labels.size() - 1) json.append(",");
        json.append("\n");
    }

    json.append("  ]\n");
    json.append("}");
    System.out.println(json.toString());
  }
}
