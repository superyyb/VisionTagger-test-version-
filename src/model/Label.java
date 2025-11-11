package model;

import java.util.Objects;

/**
 * Represents a label recognized from an image.
 * 
 * <p>A Label is an immutable value object containing a name (e.g., "Cat", "Person")
 * and a confidence score representing how certain the recognition system is about
 * this label (0.0 to 100.0 percent).
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public final class Label {

  /** The name of the detected label (e.g., "Cat", "Person", "Building"). */
  private final String name;
  
  /** The confidence score as a percentage (0.0 to 100.0). */
  private final double confidence;

  /**
   * Constructs a Label with the specified name and confidence score.
   * 
   * @param name the name of the label (must not be null or empty)
   * @param confidence the confidence score as a percentage (0.0 to 100.0)
   * @throws IllegalArgumentException if name is null/empty or confidence is out of range
   */
  public Label(String name, double confidence) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Label name cannot be null or empty");
    }
    if (confidence < 0.0 || confidence > 100.0) {
      throw new IllegalArgumentException("Confidence must be between 0.0 and 100.0");
    }
    this.name = name.trim();
    this.confidence = confidence;
  }

  /**
   * Gets the name of this label.
   * 
   * @return the label name
   */
  public String getName() { return name; }
  
  /**
   * Gets the confidence score for this label.
   * 
   * @return the confidence as a percentage (0.0 to 100.0)
   */
  public double getConfidence() { return confidence; }

  @Override
  public String toString() {
    return String.format("%s (%.2f%%)", name, confidence);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Label)) return false;
    Label other = (Label) o;
    return Double.compare(confidence, other.confidence) == 0 && name.equals(other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, confidence);
  }
}