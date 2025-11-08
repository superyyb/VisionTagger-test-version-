package model;

import java.util.Objects;

/**
 * Represents a label recognized from an image by the VisionTaggerApp. Each label has a name and a
 * confidence score. The confidence score is a value between 0.0 and 100.0 that indicates the level
 * of certainty in the label assignment.
 */
public class Label {

  private final String name;
  private final double confidence;

  /**
   * Constructs a Label object.
   *
   * @param name       the name of the detected label
   * @param confidence the level of confidence in the label assigned to the detected image
   *                   (0.0-100.0)
   */
  public Label(String name, double confidence) {
    this.name = name;
    this.confidence = confidence;
  }

  /**
   * Returns the name of this detected label.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the confidence score for this label.
   */
  public double getConfidence() {
    return confidence;
  }

  /**
   * Returns a string representation of this label.
   *
   * @return name (confidence%)
   */
  @Override
  public String toString() {
    return String.format("%s (%.2f%%)", name, confidence);
  }

  /**
   * Compares this label to another label for equality. Two labels are considered equal if they have
   * the same name and confidence score.
   *
   * @param o the object to be compared
   * @return true if two labels are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Label other = (Label) o;
    return Double.compare(other.confidence, confidence) == 0 && name.equals(other.name);
  }

  /**
   * Returns the hash code of this label. The hash code is based on the label name and confidence
   * score, consistent with equals().
   *
   * @return the hash code based on the label name and confidence score
   */
  @Override
  public int hashCode() {
    return Objects.hash(name, confidence);
  }
}
