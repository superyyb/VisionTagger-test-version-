package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents recognition results of an image.
 * 
 * <p>A DetectionResult contains the image that was analyzed, a list of detected labels
 * with their confidence scores, and the timestamp when the detection was performed.
 * Labels can be added to the result after creation, but the result itself is immutable
 * once fully constructed (except for label additions).
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public final class DetectionResult {

  /** The image that was analyzed. */
  private final Image image;
  
  /** List of labels detected in the image with their confidence scores. */
  private final List<Label> labels;
  
  /** Timestamp when the detection was performed. */
  private final LocalDateTime detectedAt;

  /**
   * Constructs a DetectionResult for the given image.
   * 
   * <p>The detection timestamp is automatically set to the current time, and the
   * labels list is initialized as empty. Labels should be added using addLabel().
   * 
   * @param image the image that was analyzed (must not be null)
   * @throws IllegalArgumentException if image is null
   */
  public DetectionResult(Image image) {
    if (image == null) {
      throw new IllegalArgumentException("Image cannot be null");
    }
    this.image = image;
    this.labels = new ArrayList<>();
    this.detectedAt = LocalDateTime.now();
  }

  /**
   * Gets the image that was analyzed.
   * 
   * @return the Image object
   */
  public Image getImage() { return image; }

  /**
   * Returns an unmodifiable list of labels to preserve immutability.
   * 
   * <p>The returned list is a read-only view of the internal labels list.
   * To add labels, use the addLabel() method.
   * 
   * @return an unmodifiable list of Label objects
   */
  public List<Label> getLabels() {
    return Collections.unmodifiableList(labels);
  }

  /**
   * Gets the timestamp when the detection was performed.
   * 
   * @return the LocalDateTime when detection occurred
   */
  public LocalDateTime getDetectedAt() { return detectedAt; }

  /**
   * Safely adds a label to this detection result.
   * 
   * @param label the label to add (must not be null)
   * @throws IllegalArgumentException if label is null
   */
  public void addLabel(Label label) {
    if (label == null) {
      throw new IllegalArgumentException("Label cannot be null");
    }
    labels.add(label);
  }

  /**
   * Returns the label with the highest confidence score.
   * 
   * <p>If multiple labels have the same highest confidence, one of them is returned
   * (the choice is non-deterministic). If no labels exist, returns null.
   * 
   * @return the Label with the highest confidence, or null if the labels list is empty
   */
  public Label getTopLabel() {
    return labels.stream()
                 .max((a, b) -> Double.compare(a.getConfidence(), b.getConfidence()))
                 .orElse(null);
  }

  @Override
  public String toString() {
    return String.format("DetectionResult[image=%s, labels=%s, detectedAt=%s]",
        image.getStoragePath(), labels, detectedAt);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DetectionResult)) return false;
    DetectionResult other = (DetectionResult) o;
    return Objects.equals(image, other.image)
        && Objects.equals(labels, other.labels)
        && Objects.equals(detectedAt, other.detectedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(image, labels, detectedAt);
  }
}