package service;

import model.Image;
import model.Label;

import java.util.*;

import model.DetectionResult;

/**
 * A mock implementation of the ImageAnalyzerService interface.
 * It randomly generates labels and confidence values to simulate an AI image recognition service.
 * Uses a diverse set of labels covering various categories similar to AWS Rekognition.
 */
public class MockRekognitionService implements ImageAnalyzerService {
    // Expanded label set covering multiple categories (animals, objects, scenes, activities, etc.)
    private static final String[] SAMPLE_LABELS = {
        // Animals
        "Animal", "Dog", "Cat", "Bird", "Horse", "Otter", "Fish", "Elephant", "Lion", "Bear",
        // Nature & Environment
        "Plant", "Flower", "Tree", "Sea", "Ocean", "Beach", "Mountain", "Forest", "Sky", "Water",
        // Food & Dining
        "Food", "Pizza", "Coffee", "Fruit", "Vegetable", "Dessert", "Restaurant", "Dining",
        // People & Activities
        "Person", "People", "Child", "Adult", "Sports", "Exercise", "Dancing", "Running",
        // Objects & Technology
        "Vehicle", "Car", "Bicycle", "Phone", "Computer", "Book", "Furniture", "Clothing",
        // Scenes & Settings
        "Indoor", "Outdoor", "Urban", "Nature", "Building", "Room", "Street", "Park"
    };
    
    private static final Random random = new Random();
    private static final int MIN_LABELS = 3;
    private static final int MAX_LABELS = 15;
    private static final double MIN_CONFIDENCE = 30.0;
    private static final double MAX_CONFIDENCE = 100.0;

    @Override
    public DetectionResult detect(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        DetectionResult result = new DetectionResult(image);

        // Generate 3-15 labels (more realistic range for real image recognition services)
        int labelCount = MIN_LABELS + random.nextInt(MAX_LABELS - MIN_LABELS + 1);
        
        // Use a set to ensure unique label names in a single detection
        Set<String> usedLabels = new HashSet<>();
        
        for (int i = 0; i < labelCount; i++) {
            String labelName;
            int attempts = 0;
            // Try to get a unique label (with a safety limit to avoid infinite loops)
            do {
                labelName = SAMPLE_LABELS[random.nextInt(SAMPLE_LABELS.length)];
                attempts++;
            } while (usedLabels.contains(labelName) && attempts < SAMPLE_LABELS.length);
            
            // If we couldn't find a unique label, skip this iteration
            if (usedLabels.contains(labelName)) {
                continue;
            }
            
            usedLabels.add(labelName);
            
            // Generate confidence between 30-100% (more realistic distribution)
            double confidence = MIN_CONFIDENCE + random.nextDouble() * (MAX_CONFIDENCE - MIN_CONFIDENCE);
            result.addLabel(new Label(labelName, confidence));
        }

        return result;
    }
}
