package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents recognition results of an image.
 */
public final class DetectionResult {

  private final Image image;
  private final List<Label> labels;
  private final LocalDateTime detectedAt;

  public DetectionResult(Image image) {
    if (image == null) {
      throw new IllegalArgumentException("Image cannot be null");
    }
    this.image = image;
    this.labels = new ArrayList<>();
    this.detectedAt = LocalDateTime.now();
  }

  public Image getImage() { return image; }

  /** Returns an unmodifiable list of labels to preserve immutability. */
  public List<Label> getLabels() {
    return Collections.unmodifiableList(labels);
  }

  public LocalDateTime getDetectedAt() { return detectedAt; }

  /** Safely add a label. */
  public void addLabel(Label label) {
    if (label == null) throw new IllegalArgumentException("Label cannot be null");
    labels.add(label);
  }

  /** Returns the label with the highest confidence, or null if empty. */
  public Label getTopLabel() {
    return labels.stream()
                 .max((a, b) -> Double.compare(a.getConfidence(), b.getConfidence()))
                 .orElse(null);
  }

  @Override
  public String toString() {
    return String.format("DetectionResult[image=%s, labels=%s, detectedAt=%s]",
        image.getFilePath(), labels, detectedAt);
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