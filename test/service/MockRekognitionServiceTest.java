package service;

import model.Image;
import model.DetectionResult;
import model.Label;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MockRekognitionService class.
 * Tests the service layer in isolation without external dependencies.
 */
public class MockRekognitionServiceTest {

    private MockRekognitionService service;
    private User testUser;
    private static final Set<String> VALID_LABELS = new HashSet<>(Arrays.asList(
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
    ));

    @BeforeEach
    void setUp() {
        service = new MockRekognitionService();
        testUser = User.guestUser("testuser");
    }

    @Test
    void testDetectReturnsDetectionResult() {
        Image image = new Image(testUser.getId(), "/path/to/image.jpg");
        DetectionResult result = service.detect(image);

        assertNotNull(result);
        assertTrue(result instanceof DetectionResult);
    }

    @Test
    void testDetectReturnsResultWithCorrectImage() {
        Image image = new Image(testUser.getId(), "/path/to/image.jpg", "Test description");
        DetectionResult result = service.detect(image);

        assertEquals(image, result.getImage(), "Result should contain the input image");
        assertEquals(image.getId(), result.getImage().getId());
        assertEquals(image.getStoragePath(), result.getImage().getStoragePath());
    }

    @Test
    void testDetectThrowsExceptionForNullImage() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.detect(null);
        }, "Should throw IllegalArgumentException for null image");
    }

    @RepeatedTest(20)
    void testDetectGeneratesCorrectNumberOfLabels() {
        Image image = new Image(testUser.getId(), "/path/to/image.jpg");
        DetectionResult result = service.detect(image);
        List<Label> labels = result.getLabels();

        assertNotNull(labels, "Labels list should not be null");
        assertTrue(labels.size() >= 3 && labels.size() <= 15,
                "Should generate between 3 and 15 labels, but got: " + labels.size());
    }

    @RepeatedTest(20)
    void testDetectGeneratesLabelsWithValidConfidence() {
        Image image = new Image(testUser.getId(), "/path/to/image.jpg");
        DetectionResult result = service.detect(image);
        List<Label> labels = result.getLabels();

        for (Label label : labels) {
            double confidence = label.getConfidence();
            assertTrue(confidence >= 30.0 && confidence <= 100.0,
                    String.format("Confidence should be between 30.0 and 100.0, but got: %.2f", confidence));
        }
    }

    @RepeatedTest(20)
    void testDetectGeneratesLabelsFromSampleSet() {
        Image image = new Image(testUser.getId(), "/path/to/image.jpg");
        DetectionResult result = service.detect(image);
        List<Label> labels = result.getLabels();

        for (Label label : labels) {
            assertTrue(VALID_LABELS.contains(label.getName()),
                    String.format("Label '%s' should be from the sample set", label.getName()));
        }
    }

    @RepeatedTest(20)
    void testDetectGeneratesUniqueLabelsInSingleCall() {
        Image image = new Image(testUser.getId(), "/path/to/image.jpg");
        DetectionResult result = service.detect(image);
        List<Label> labels = result.getLabels();

        // Check that label names are unique (no duplicate label names in a single detection)
        Set<String> uniqueLabelNames = new HashSet<>();
        for (Label label : labels) {
            assertFalse(uniqueLabelNames.contains(label.getName()),
                    String.format("Label name '%s' should be unique in a single detection", label.getName()));
            uniqueLabelNames.add(label.getName());
        }
        assertEquals(labels.size(), uniqueLabelNames.size(),
                "All label names should be unique in a single detection");
    }

    @Test
    void testDetectResultHasDetectedAtTimestamp() {
        Image image = new Image(testUser.getId(), "/path/to/image.jpg");
        DetectionResult result = service.detect(image);

        assertNotNull(result.getDetectedAt(), "detectedAt timestamp should not be null");
    }

    @RepeatedTest(10)
    void testDetectProducesDifferentResultsForSameImage() {
        Image image = new Image(testUser.getId(), "/path/to/image.jpg");
        DetectionResult result1 = service.detect(image);
        DetectionResult result2 = service.detect(image);

        // Results should potentially differ due to randomness
        // At least verify both are valid
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(image, result1.getImage());
        assertEquals(image, result2.getImage());
        
        // The labels or their order might differ due to randomness
        assertTrue(result1.getLabels().size() >= 3 && result1.getLabels().size() <= 15);
        assertTrue(result2.getLabels().size() >= 3 && result2.getLabels().size() <= 15);
    }

    @Test
    void testDetectWithDifferentImages() {
        User user1 = User.guestUser("user1");
        User user2 = User.guestUser("user2");
        Image image1 = new Image(user1.getId(), "/path/to/image1.jpg");
        Image image2 = new Image(user2.getId(), "/path/to/image2.jpg", "Description");
        
        DetectionResult result1 = service.detect(image1);
        DetectionResult result2 = service.detect(image2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(image1, result1.getImage());
        assertEquals(image2, result2.getImage());
        assertNotEquals(image1.getId(), image2.getId());
    }

    @RepeatedTest(50)
    void testDetectCoversAllSampleLabels() {
        Image image = new Image(testUser.getId(), "/path/to/image.jpg");
        Set<String> generatedLabels = new HashSet<>();
        
        // Run multiple times to increase chance of covering all labels
        for (int i = 0; i < 50; i++) {
            DetectionResult result = service.detect(image);
            for (Label label : result.getLabels()) {
                generatedLabels.add(label.getName());
            }
        }
        
        // With 50 runs, we should have generated a diverse set of labels
        assertFalse(generatedLabels.isEmpty(), "Should generate at least some labels");
        assertTrue(generatedLabels.size() >= 10, 
                String.format("Should generate at least 10 different labels across 50 runs, but got: %d", 
                        generatedLabels.size()));
    }
}
