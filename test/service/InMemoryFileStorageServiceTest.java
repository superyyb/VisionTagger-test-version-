package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import model.DetectionResult;
import model.Image;
import model.Label;
import model.User;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for the InMemoryFileStorageService class.
 */
public class InMemoryFileStorageServiceTest {

    private InMemoryFileStorageService storageService;
    private User user1;
    private User user2;
    private Image image1;
    private Image image2;
    private Image image3;
    private DetectionResult result1;
    private DetectionResult result2;
    private DetectionResult result3;

    @BeforeEach
    void setUp() {
        storageService = new InMemoryFileStorageService();
        
        user1 = User.registeredUser("user1", "user1@example.com");
        user2 = User.registeredUser("user2", "user2@example.com");
        
        image1 = new Image(user1.getId(), "/path/image1.jpg", "Test image 1");
        image2 = new Image(user1.getId(), "/path/image2.jpg", "Test image 2");
        image3 = new Image(user2.getId(), "/path/image3.jpg", "Test image 3");
        
        result1 = new DetectionResult(image1);
        result1.addLabel(new Label("Otter", 96.3));
        result1.addLabel(new Label("Animal", 99.0));
        
        result2 = new DetectionResult(image2);
        result2.addLabel(new Label("Dog", 95.5));
        
        result3 = new DetectionResult(image3);
        result3.addLabel(new Label("Cat", 98.8));
    }

    // ==================== Save Tests ====================

    @Test
    void testSaveValidResult() {
        storageService.save(result1);
        
        Optional<DetectionResult> retrieved = storageService.getResultByImageId(image1.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(result1.getImage().getId(), retrieved.get().getImage().getId());
    }

    @Test
    void testSaveMultipleResults() {
        storageService.save(result1);
        storageService.save(result2);
        storageService.save(result3);
        
        assertTrue(storageService.getResultByImageId(image1.getId()).isPresent());
        assertTrue(storageService.getResultByImageId(image2.getId()).isPresent());
        assertTrue(storageService.getResultByImageId(image3.getId()).isPresent());
    }

    @Test
    void testSaveNullResultThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.save(null);
        });
    }

    @Test
    void testSaveResultWithNullImageThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.save(new DetectionResult(null));
        });
    }

    @Test
    void testSaveUpdatesExistingResult() {
        storageService.save(result1);
        
        DetectionResult updatedResult = new DetectionResult(image1);
        updatedResult.addLabel(new Label("UpdatedLabel", 88.8));
        
        storageService.save(updatedResult);
        
        Optional<DetectionResult> retrieved = storageService.getResultByImageId(image1.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(1, retrieved.get().getLabels().size());
        assertEquals("UpdatedLabel", retrieved.get().getLabels().get(0).getName());
    }

    // ==================== Get by Image ID Tests ====================

    @Test
    void testGetResultByImageId() {
        storageService.save(result1);
        
        Optional<DetectionResult> retrieved = storageService.getResultByImageId(image1.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(image1.getId(), retrieved.get().getImage().getId());
    }

    @Test
    void testGetResultByImageIdNotFound() {
        Optional<DetectionResult> retrieved = storageService.getResultByImageId("nonexistent-id");
        assertFalse(retrieved.isPresent());
    }

    @Test
    void testGetResultByImageIdNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.getResultByImageId(null);
        });
    }

    @Test
    void testGetResultByImageIdEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.getResultByImageId("");
        });
    }

    @Test
    void testGetResultByImageIdTrimsWhitespace() {
        storageService.save(result1);
        
        Optional<DetectionResult> retrieved = storageService.getResultByImageId("  " + image1.getId() + "  ");
        assertTrue(retrieved.isPresent());
    }

    // ==================== Get by User ID Tests ====================

    @Test
    void testGetResultsByUserId() {
        storageService.save(result1);
        storageService.save(result2);
        storageService.save(result3);
        
        List<DetectionResult> user1Results = storageService.getResultsByUserId(user1.getId());
        assertEquals(2, user1Results.size());
        
        List<DetectionResult> user2Results = storageService.getResultsByUserId(user2.getId());
        assertEquals(1, user2Results.size());
    }

    @Test
    void testGetResultsByUserIdEmptyList() {
        List<DetectionResult> results = storageService.getResultsByUserId("nonexistent-user");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetResultsByUserIdNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.getResultsByUserId(null);
        });
    }

    @Test
    void testGetResultsByUserIdEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.getResultsByUserId("");
        });
    }

    @Test
    void testGetResultsByUserIdIsolation() {
        storageService.save(result1);
        storageService.save(result2);
        storageService.save(result3);
        
        List<DetectionResult> user1Results = storageService.getResultsByUserId(user1.getId());
        List<DetectionResult> user2Results = storageService.getResultsByUserId(user2.getId());
        
        assertEquals(2, user1Results.size());
        assertEquals(1, user2Results.size());
        
        // Verify no data leakage
        assertTrue(user1Results.stream().allMatch(r -> r.getImage().getUploaderId().equals(user1.getId())));
        assertTrue(user2Results.stream().allMatch(r -> r.getImage().getUploaderId().equals(user2.getId())));
    }

    // ==================== GSI Update Tests ====================

    @Test
    void testGSIUpdatesWhenUserChanges() {
        storageService.save(result1);
        assertEquals(1, storageService.getResultsByUserId(user1.getId()).size());
        assertEquals(0, storageService.getResultsByUserId(user2.getId()).size());
        
        // Update with different user
        Image updatedImage = new Image(user2.getId(), image1.getStoragePath(), "Updated");
        DetectionResult updatedResult = new DetectionResult(updatedImage);
        updatedResult.addLabel(new Label("NewLabel", 90.0));
        
        storageService.save(updatedResult);
        
        // Verify GSI update
        assertEquals(0, storageService.getResultsByUserId(user1.getId()).size());
        assertEquals(1, storageService.getResultsByUserId(user2.getId()).size());
    }

    // ==================== ExistsById Tests ====================

    @Test
    void testExistsByImageId() {
        assertFalse(storageService.existsByImageId(image1.getId()));
        
        storageService.save(result1);
        
        assertTrue(storageService.existsByImageId(image1.getId()));
    }

    @Test
    void testExistsByImageIdNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.existsByImageId(null);
        });
    }

    // ==================== Delete Tests ====================

    @Test
    void testDeleteByImageId() {
        storageService.save(result1);
        assertTrue(storageService.existsByImageId(image1.getId()));
        
        boolean deleted = storageService.deleteByImageId(image1.getId());
        assertTrue(deleted);
        assertFalse(storageService.existsByImageId(image1.getId()));
    }

    @Test
    void testDeleteByImageIdNotFound() {
        boolean deleted = storageService.deleteByImageId("nonexistent-id");
        assertFalse(deleted);
    }

    @Test
    void testDeleteByImageIdNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.deleteByImageId(null);
        });
    }

    @Test
    void testDeleteByImageIdRemovesFromGSI() {
        storageService.save(result1);
        storageService.save(result2);
        assertEquals(2, storageService.getResultsByUserId(user1.getId()).size());
        
        storageService.deleteByImageId(image1.getId());
        
        assertEquals(1, storageService.getResultsByUserId(user1.getId()).size());
    }

    @Test
    void testDeleteByImageIdCleansEmptyGSIEntry() {
        storageService.save(result3);
        assertEquals(1, storageService.getResultsByUserId(user2.getId()).size());
        
        storageService.deleteByImageId(image3.getId());
        
        assertEquals(0, storageService.getResultsByUserId(user2.getId()).size());
    }

    // ==================== Count Tests ====================

    @Test
    void testCountByUserId() {
        storageService.save(result1);
        storageService.save(result2);
        storageService.save(result3);
        
        assertEquals(2, storageService.countByUserId(user1.getId()));
        assertEquals(1, storageService.countByUserId(user2.getId()));
    }

    @Test
    void testCountByUserIdZero() {
        assertEquals(0, storageService.countByUserId("nonexistent-user"));
    }

    @Test
    void testCountByUserIdNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.countByUserId(null);
        });
    }

    // ==================== Clear Tests ====================

    @Test
    void testClear() {
        storageService.save(result1);
        storageService.save(result2);
        storageService.save(result3);
        
        assertTrue(storageService.existsByImageId(image1.getId()));
        
        storageService.clear();
        
        assertFalse(storageService.existsByImageId(image1.getId()));
        assertFalse(storageService.existsByImageId(image2.getId()));
        assertFalse(storageService.existsByImageId(image3.getId()));
        assertEquals(0, storageService.getResultsByUserId(user1.getId()).size());
        assertEquals(0, storageService.getResultsByUserId(user2.getId()).size());
    }

    @Test
    void testClearEmptyStorage() {
        storageService.clear();
        assertEquals(0, storageService.getResultsByUserId(user1.getId()).size());
    }

    // ==================== Integration Tests ====================

    @Test
    void testCompleteWorkflow() {
        // Save
        storageService.save(result1);
        
        // Retrieve by image ID
        Optional<DetectionResult> retrieved = storageService.getResultByImageId(image1.getId());
        assertTrue(retrieved.isPresent());
        
        // Retrieve by user ID
        List<DetectionResult> userResults = storageService.getResultsByUserId(user1.getId());
        assertEquals(1, userResults.size());
        
        // Delete
        storageService.deleteByImageId(image1.getId());
        assertFalse(storageService.existsByImageId(image1.getId()));
    }

    @Test
    void testMultipleUsersWorkflow() {
        // Upload for different users
        storageService.save(result1);
        storageService.save(result2);
        storageService.save(result3);
        
        // Verify isolation
        assertEquals(2, storageService.getResultsByUserId(user1.getId()).size());
        assertEquals(1, storageService.getResultsByUserId(user2.getId()).size());
        
        // Delete one user's result
        storageService.deleteByImageId(image1.getId());
        
        // Verify other user unaffected
        assertEquals(1, storageService.getResultsByUserId(user1.getId()).size());
        assertEquals(1, storageService.getResultsByUserId(user2.getId()).size());
    }

    @Test
    void testBatchOperations() {
        // Batch save
        for (int i = 0; i < 10; i++) {
            Image img = new Image(user1.getId(), "/path/image" + i + ".jpg", "Image " + i);
            DetectionResult result = new DetectionResult(img);
            result.addLabel(new Label("Label" + i, 90.0 + i));
            storageService.save(result);
        }
        
        // Verify count
        assertEquals(10, storageService.countByUserId(user1.getId()));
        
        // Verify retrieval
        List<DetectionResult> results = storageService.getResultsByUserId(user1.getId());
        assertEquals(10, results.size());
    }
}
