package service;

import model.Image;
import model.DetectionResult;

/**
 * Interface for analyzing images and returning detected labels.
 * Implementations can be mock (for testing) or real (AWS Rekognition, Google Vision API, etc.).
 */
public interface ImageAnalyzerService {

    /**
     * Perform detection on the given image.
     *
     * @param image  the image to detect
     * @return a DetectionResult containing detected labels
     */
    DetectionResult detect(Image image);
}
