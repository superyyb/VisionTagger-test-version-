package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Represents the recognition results of an image by the VisionTaggerApp. It contains the recognized labels and other metadata. */
public class DetectionResult {
    private final Image image;
    private final List<Label> labels;
    private final LocalDate detectedAt;

    /**
     * Constructs a DetectionResult object.
     * @param image the image that was analyzed
     */
    public DetectionResult(Image image) {
        this.image = image;
        this.labels = new ArrayList<>();
        this.detectedAt = LocalDate.now();
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
    public LocalDate getDetectedAt() {
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
}
