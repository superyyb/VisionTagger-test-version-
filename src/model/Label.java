package model;

import java.util.Objects;

/**
 * Represents a label recognized from an image.
 * Immutable value object.
 */
public final class Label {

  private final String name;
  private final double confidence; // percentage 0.0–100.0

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

  public String getName() { return name; }
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