package view;

import java.util.List;
import model.DetectionResult;
import model.Label;

/**
 * JSON-based implementation of the View interface.
 * 
 * <p>This view renders detection results in JSON format, suitable for API responses
 * or programmatic consumption. Outputs a formatted JSON object containing the image
 * path and an array of detected labels with their confidence scores.
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
