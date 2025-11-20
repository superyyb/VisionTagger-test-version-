package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import model.DetectionResult;
import model.Image;
import model.Label;
import model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit tests for the MockRekognitionService class.
 */
public class MockRekognitionServiceTest {

    private MockRekognitionService service;
    private User user;
    private Image image;

    @BeforeEach
    void setUp() {
        service = new MockRekognitionService();
        user = User.guestUser("testuser");
        image = new Image(user.getId(), "/path/to/test.jpg", "Test image");
    }

    // ==================== Basic Functionality Tests ====================

    @Test
    void testDetectReturnsResult() {
        DetectionResult result = service.detect(image);
        
        assertNotNull(result);
        assertNotNull(result.getLabels());
        assertNotNull(result.getImage());
        assertEquals(image.getId(), result.getImage().getId());
    }

    @Test
    void testDetectNullImageThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.detect(null);
        });
    }

    @Test
    void testDetectGeneratesLabels() {
        DetectionResult result = service.detect(image);
        
        List<Label> labels = result.getLabels();
        assertFalse(labels.isEmpty());
        assertTrue(labels.size() >= 3, "Should generate at least 3 labels");
        assertTrue(labels.size() <= 15, "Should generate at most 15 labels");
    }

    // ==================== Label Generation Tests ====================

    @Test
    void testLabelsHaveValidNames() {
        DetectionResult result = service.detect(image);
        
        for (Label label : result.getLabels()) {
            assertNotNull(label.getName());
            assertFalse(label.getName().trim().isEmpty());
        }
    }

    @Test
    void testLabelsHaveValidConfidence() {
        DetectionResult result = service.detect(image);
        
        for (Label label : result.getLabels()) {
            assertTrue(label.getConfidence() >= 30.0, 
                "Confidence should be at least 30%: " + label.getConfidence());
            assertTrue(label.getConfidence() <= 100.0, 
                "Confidence should be at most 100%: " + label.getConfidence());
        }
    }

    @Test
    void testLabelsAreUnique() {
        DetectionResult result = service.detect(image);
        
        Set<String> labelNames = new HashSet<>();
        for (Label label : result.getLabels()) {
            assertTrue(labelNames.add(label.getName()), 
                "Duplicate label found: " + label.getName());
        }
    }

    // ==================== Randomness Tests ====================

    @Test
    void testMultipleDetectionsGenerateDifferentResults() {
        DetectionResult result1 = service.detect(image);
        DetectionResult result2 = service.detect(image);
        
        boolean hasDifferentLabels = false;
        List<Label> labels1 = result1.getLabels();
        List<Label> labels2 = result2.getLabels();
        
        if (labels1.size() != labels2.size()) {
            hasDifferentLabels = true;
        } else {
            for (int i = 0; i < labels1.size(); i++) {
                if (!labels1.get(i).getName().equals(labels2.get(i).getName())) {
                    hasDifferentLabels = true;
                    break;
                }
            }
        }
        
        assertTrue(hasDifferentLabels, 
            "Multiple detections should generate different results");
    }

    @Test
    void testDetectionConsistencyAcrossMultipleRuns() {
        for (int i = 0; i < 10; i++) {
            DetectionResult result = service.detect(image);
            
            assertNotNull(result);
            assertFalse(result.getLabels().isEmpty());
            assertTrue(result.getLabels().size() >= 3);
            assertTrue(result.getLabels().size() <= 15);
            
            for (Label label : result.getLabels()) {
                assertNotNull(label.getName());
                assertTrue(label.getConfidence() >= 30.0);
                assertTrue(label.getConfidence() <= 100.0);
            }
        }
    }

    // ==================== Label Content Tests ====================

    @Test
    void testLabelsFromDefinedSet() {
        DetectionResult result = service.detect(image);
        
        Set<String> expectedLabels = Set.of(
            "Animal", "Dog", "Cat", "Bird", "Horse", "Otter", "Fish", "Elephant", "Lion", "Bear",
            "Plant", "Flower", "Tree", "Sea", "Ocean", "Beach", "Mountain", "Forest", "Sky", "Water",
            "Food", "Pizza", "Coffee", "Fruit", "Vegetable", "Dessert", "Restaurant", "Dining",
            "Person", "People", "Child", "Adult", "Sports", "Exercise", "Dancing", "Running",
            "Vehicle", "Car", "Bicycle", "Phone", "Computer", "Book", "Furniture", "Clothing",
            "Indoor", "Outdoor", "Urban", "Nature", "Building", "Room", "Street", "Park"
        );
        
        for (Label label : result.getLabels()) {
            assertTrue(expectedLabels.contains(label.getName()), 
                "Label should be from defined set: " + label.getName());
        }
    }

    // ==================== Different Images Tests ====================

    @Test
    void testDetectWorksWithDifferentImages() {
        Image image1 = new Image(user.getId(), "/path/image1.jpg", "Image 1");
        Image image2 = new Image(user.getId(), "/path/image2.png", "Image 2");
        Image image3 = new Image(user.getId(), "/path/image3.gif", "Image 3");
        
        DetectionResult result1 = service.detect(image1);
        DetectionResult result2 = service.detect(image2);
        DetectionResult result3 = service.detect(image3);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        
        assertFalse(result1.getLabels().isEmpty());
        assertFalse(result2.getLabels().isEmpty());
        assertFalse(result3.getLabels().isEmpty());
    }

    @Test
    void testDetectPreservesImageReference() {
        DetectionResult result = service.detect(image);
        
        assertEquals(image.getId(), result.getImage().getId());
        assertEquals(image.getStoragePath(), result.getImage().getStoragePath());
        assertEquals(image.getUploaderId(), result.getImage().getUploaderId());
    }

    // ==================== Edge Cases ====================

    @Test
    void testDetectWithImageWithSpecialCharacters() {
        Image specialImage = new Image(user.getId(), 
            "/path/with spaces/image-1_test.jpg", 
            "Description with 特殊字符");
        
        DetectionResult result = service.detect(specialImage);
        
        assertNotNull(result);
        assertFalse(result.getLabels().isEmpty());
        assertEquals(specialImage.getId(), result.getImage().getId());
    }

    @Test
    void testDetectWithImageWithNoDescription() {
        Image noDescImage = new Image(user.getId(), "/path/test.jpg");
        
        DetectionResult result = service.detect(noDescImage);
        
        assertNotNull(result);
        assertFalse(result.getLabels().isEmpty());
    }

    @Test
    void testDetectWithVeryLongPath() {
        String longPath = "/very/long/path/".repeat(10) + "image.jpg";
        Image longPathImage = new Image(user.getId(), longPath, "Long path");
        
        DetectionResult result = service.detect(longPathImage);
        
        assertNotNull(result);
        assertFalse(result.getLabels().isEmpty());
    }

    // ==================== Service Behavior Tests ====================

    @Test
    void testServiceIsStateless() {
        DetectionResult result1 = service.detect(image);
        DetectionResult result2 = service.detect(image);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1, result2); // Different timestamps
    }

    @Test
    void testMultipleServicesIndependent() {
        MockRekognitionService service1 = new MockRekognitionService();
        MockRekognitionService service2 = new MockRekognitionService();
        
        DetectionResult result1 = service1.detect(image);
        DetectionResult result2 = service2.detect(image);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertFalse(result1.getLabels().isEmpty());
        assertFalse(result2.getLabels().isEmpty());
    }

    @Test
    void testLabelDiversityAcrossMultipleDetections() {
        Set<String> allLabels = new HashSet<>();
        
        for (int i = 0; i < 10; i++) {
            DetectionResult result = service.detect(image);
            for (Label label : result.getLabels()) {
                allLabels.add(label.getName());
            }
        }
        
        // Should see multiple different labels across 10 runs
        assertTrue(allLabels.size() > 5, 
            "Should generate diverse labels across multiple runs, got: " + allLabels.size());
    }
}
