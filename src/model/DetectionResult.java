package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Represents the recognition results of an image by the VisionTaggerApp. It contains the recognized labels and other metadata. */
public class DetectionResult {
    private final Image image;
    private final List<Label> labels;
    private final LocalDateTime detectedAt;

    /**
     * Constructs a DetectionResult object.
     * @param image the image that was analyzed
     */
    public DetectionResult(Image image) {
        this.image = image;
        this.labels = new ArrayList<>();
        this.detectedAt = LocalDateTime.now();
    }

    /** Returns the analyzed image. */
    public Image getImage() {
        return image;
    }

    /** Returns the list of labels detected from the image. */
    public List<Label> getLabels() {
        return labels;
    }

    /** Returns the date and time when the image was analyzed. */
    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    /** Adds a label to the list of labels detected from the image. */
    public void addLabel(Label label) {
        labels.add(label);
    }

    @Override
    public String toString() {
        return String.format("Recognition result for %s:\n%s", image.getFilePath(), labels.toString());
    }

    /**
     * Compares this detection result to another detection result for equality.
     * Two detection results are considered equal if they have the same image, labels, and detection timestamp.
     *
     * @param o the object to be compared
     * @return true if two detection results are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DetectionResult other = (DetectionResult) o;
        return Objects.equals(image, other.image) &&
               Objects.equals(labels, other.labels) &&
               Objects.equals(detectedAt, other.detectedAt);
    }

    /**
     * Returns the hash code of this detection result.
     * The hash code is based on the image, labels, and detection timestamp, consistent with equals().
     *
     * @return the hash code based on the image, labels, and detection timestamp
     */
    @Override
    public int hashCode() {
        return Objects.hash(image, labels, detectedAt);
    }
}
