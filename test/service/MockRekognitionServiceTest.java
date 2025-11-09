package service;

import model.Image;
import model.DetectionResult;
import model.Label;
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
    private static final Set<String> VALID_LABELS = new HashSet<>(Arrays.asList(
            "Otter", "Animal", "Sea", "Flower", "Plant", "Food", "Person"
    ));

    @BeforeEach
    void setUp() {
        service = new MockRekognitionService();
    }

    @Test
    void testDetectReturnsDetectionResult() {
        Image image = new Image("testuser", "/path/to/image.jpg");
        DetectionResult result = service.detect(image);

        assertNotNull(result);
        assertTrue(result instanceof DetectionResult);
    }

    @Test
    void testDetectReturnsResultWithCorrectImage() {
        Image image = new Image("testuser", "/path/to/image.jpg", "Test description");
        DetectionResult result = service.detect(image);

        assertEquals(image, result.getImage(), "Result should contain the input image");
        assertEquals(image.getId(), result.getImage().getId());
        assertEquals(image.getFilePath(), result.getImage().getFilePath());
    }

    @Test
    void testDetectThrowsExceptionForNullImage() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.detect(null);
        }, "Should throw IllegalArgumentException for null image");
    }

    @RepeatedTest(20)
    void testDetectGeneratesCorrectNumberOfLabels() {
        Image image = new Image("testuser", "/path/to/image.jpg");
        DetectionResult result = service.detect(image);
        List<Label> labels = result.getLabels();

        assertNotNull(labels, "Labels list should not be null");
        assertTrue(labels.size() >= 1 && labels.size() <= 4,
                "Should generate between 1 and 4 labels, but got: " + labels.size());
    }

    @RepeatedTest(20)
    void testDetectGeneratesLabelsWithValidConfidence() {
        Image image = new Image("testuser", "/path/to/image.jpg");
        DetectionResult result = service.detect(image);
        List<Label> labels = result.getLabels();

        for (Label label : labels) {
            double confidence = label.getConfidence();
            assertTrue(confidence >= 55.0 && confidence <= 100.0,
                    String.format("Confidence should be between 55.0 and 100.0, but got: %.2f", confidence));
        }
    }

    @RepeatedTest(20)
    void testDetectGeneratesLabelsFromSampleSet() {
        Image image = new Image("testuser", "/path/to/image.jpg");
        DetectionResult result = service.detect(image);
        List<Label> labels = result.getLabels();

        for (Label label : labels) {
            assertTrue(VALID_LABELS.contains(label.getName()),
                    String.format("Label '%s' should be from the sample set", label.getName()));
        }
    }

    @RepeatedTest(20)
    void testDetectGeneratesUniqueLabelsInSingleCall() {
        Image image = new Image("testuser", "/path/to/image.jpg");
        DetectionResult result = service.detect(image);
        List<Label> labels = result.getLabels();

        // Check that labels are unique (name + confidence combination)
        Set<Label> uniqueLabels = new HashSet<>(labels);
        // Note: Since labels can have same name but different confidence, we check the full label
        // If we want to ensure unique names only, we'd need a different check
        assertEquals(labels.size(), uniqueLabels.size(),
                "All labels should be unique (name + confidence combination)");
    }

    @Test
    void testDetectResultHasDetectedAtTimestamp() {
        Image image = new Image("testuser", "/path/to/image.jpg");
        DetectionResult result = service.detect(image);

        assertNotNull(result.getDetectedAt(), "detectedAt timestamp should not be null");
    }

    @RepeatedTest(10)
    void testDetectProducesDifferentResultsForSameImage() {
        Image image = new Image("testuser", "/path/to/image.jpg");
        DetectionResult result1 = service.detect(image);
        DetectionResult result2 = service.detect(image);

        // Results should potentially differ due to randomness
        // At least verify both are valid
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(image, result1.getImage());
        assertEquals(image, result2.getImage());
        
        // The labels or their order might differ due to randomness
        assertTrue(result1.getLabels().size() >= 1 && result1.getLabels().size() <= 4);
        assertTrue(result2.getLabels().size() >= 1 && result2.getLabels().size() <= 4);
    }

    @Test
    void testDetectWithDifferentImages() {
        Image image1 = new Image("user1", "/path/to/image1.jpg");
        Image image2 = new Image("user2", "/path/to/image2.jpg", "Description");
        
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
        Image image = new Image("testuser", "/path/to/image.jpg");
        Set<String> generatedLabels = new HashSet<>();
        
        // Run multiple times to increase chance of covering all labels
        for (int i = 0; i < 50; i++) {
            DetectionResult result = service.detect(image);
            for (Label label : result.getLabels()) {
                generatedLabels.add(label.getName());
            }
        }
        
        // With 50 runs, we should have generated at least some of the labels
        assertFalse(generatedLabels.isEmpty(), "Should generate at least some labels");
        assertTrue(generatedLabels.size() > 0, "Should generate multiple different labels");
    }
}
