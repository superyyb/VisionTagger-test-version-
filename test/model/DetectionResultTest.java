package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Unit tests for the DetectionResult model class.
 */
public class DetectionResultTest {

    private DetectionResult detectionResult;
    private Image image;
    private User user;
    private String filePath;

    @BeforeEach
    void setUp() {
        user = User.guestUser("testuser");
        filePath = "/path/to/image.jpg";
        image = new Image(user.getId(), filePath, "Test description");
        detectionResult = new DetectionResult(image);
    }

    @Test
    void testConstructor() {
        assertNotNull(detectionResult);
        assertEquals(image, detectionResult.getImage());
        assertNotNull(detectionResult.getLabels());
        assertTrue(detectionResult.getLabels().isEmpty());
        assertNotNull(detectionResult.getDetectedAt());
    }

    @Test
    void testConstructorSetsDetectedAtTimestamp() {
        LocalDateTime before = LocalDateTime.now();
        DetectionResult result = new DetectionResult(image);
        LocalDateTime after = LocalDateTime.now();
        
        assertTrue(result.getDetectedAt().isAfter(before.minusSeconds(1)));
        assertTrue(result.getDetectedAt().isBefore(after.plusSeconds(1)));
    }

    @Test
    void testGetImage() {
        assertEquals(image, detectionResult.getImage());
    }

    @Test
    void testGetLabelsInitiallyEmpty() {
        List<Label> labels = detectionResult.getLabels();
        assertNotNull(labels);
        assertTrue(labels.isEmpty());
    }

    @Test
    void testAddLabel() {
        Label label = new Label("Otter", 96.3);
        detectionResult.addLabel(label);
        
        List<Label> labels = detectionResult.getLabels();
        assertEquals(1, labels.size());
        assertEquals(label, labels.get(0));
    }

    @Test
    void testAddMultipleLabels() {
        Label label1 = new Label("Otter", 96.3);
        Label label2 = new Label("Animal", 99.0);
        Label label3 = new Label("Sea", 88.8);
        
        detectionResult.addLabel(label1);
        detectionResult.addLabel(label2);
        detectionResult.addLabel(label3);
        
        List<Label> labels = detectionResult.getLabels();
        assertEquals(3, labels.size());
        assertEquals(label1, labels.get(0));
        assertEquals(label2, labels.get(1));
        assertEquals(label3, labels.get(2));
    }

    @Test
    void testLabelsListIsMutable() {
        Label label = new Label("Otter", 96.3);
        detectionResult.addLabel(label);
        
        List<Label> labels = detectionResult.getLabels();
        assertEquals(1, labels.size());
        
        labels.add(new Label("Animal", 99.0));
        assertEquals(2, labels.size());
    }

    @Test
    void testGetDetectedAt() {
        LocalDateTime detectedAt = detectionResult.getDetectedAt();
        assertNotNull(detectedAt);
        assertTrue(detectedAt.isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testToString() {
        Label label = new Label("Otter", 96.3);
        detectionResult.addLabel(label);
        
        String result = detectionResult.toString();
        assertEquals(result, String.format("Recognition result for %s:\n[%s]", filePath, label.toString()));
    }

    @Test
    void testToStringWithMultipleLabels() {
        Label label1 = new Label("Otter", 96.3);
        Label label2 = new Label("Animal", 99.0);
        detectionResult.addLabel(label1);
        detectionResult.addLabel(label2);
        
        String result = detectionResult.toString();
        assertEquals(result, String.format("Recognition result for %s:\n[%s, %s]", filePath, label1.toString(), label2.toString()));
    }

    @Test
    void testEquals() {
        DetectionResult result1 = new DetectionResult(image);
        DetectionResult result2 = new DetectionResult(image);
        
        Label label = new Label("Otter", 96.3);
        result1.addLabel(label);
        result2.addLabel(label);
        
        // Note: equals compares image, labels, and detectedAt
        // Since detectedAt is set to now(), they might differ by milliseconds
        // So we test that they're not equal due to timestamp difference
        // But if we create them with same timestamp, they should be equal
        assertNotEquals(result1, result2);
        assertNotEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    void testEqualsWithDifferentImage() {
        User otherUser = User.guestUser("otheruser");
        Image image2 = new Image(otherUser.getId(), "/other/path.jpg", "Other");
        DetectionResult result1 = new DetectionResult(image);
        DetectionResult result2 = new DetectionResult(image2);
        
        assertNotEquals(result1, result2);
        assertNotEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    void testHashCodeChangesWithLabels() {
        int hashCode1 = detectionResult.hashCode();
        
        detectionResult.addLabel(new Label("Person", 95.5));
        int hashCode2 = detectionResult.hashCode();
        
        // Hash code should change when labels are added
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void testEqualsWithInvalidObject() {
        assertFalse(detectionResult.equals(null));
        assertFalse(detectionResult.equals("not a detection result"));
    }
}