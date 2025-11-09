package service;

import model.Image;
import model.Label;

import java.util.Random;

import model.DetectionResult;

/**
 * A mock implementation of the ImageAnalyzerService interface.
 * It randomly generates labels and confidence values to simulate an AI image recognition service.
 */
public class MockRekognitionService implements ImageAnalyzerService {
    private static final String[] SAMPLE_LABELS =
            {"Otter", "Animal", "Sea", "Flower", "Plant", "Food", "Person"};
    private static final Random random = new Random();

    @Override
    public DetectionResult detect(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        DetectionResult result = new DetectionResult(image);

        int labelCount = 1 + random.nextInt(4); // 1-4 random labels
        for (int i = 0; i < labelCount; i++) {
            String labelName = SAMPLE_LABELS[random.nextInt(SAMPLE_LABELS.length)];
            double confidence = 55.0 + random.nextDouble() * 45.0; // 55-100% confidence
            result.addLabel(new Label(labelName, confidence));
        }

        return result;
    }
}
