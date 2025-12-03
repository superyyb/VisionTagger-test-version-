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


  //Tests that multiple DetectionResults can be saved independently
  //and each one can be retrieved by its own image ID.
  @Test
  void testSaveMultipleResults() {
    storageService.save(result1);
    storageService.save(result2);
    storageService.save(result3);

    assertTrue(storageService.getResultByImageId(image1.getId()).isPresent());
    assertTrue(storageService.getResultByImageId(image2.getId()).isPresent());
    assertTrue(storageService.getResultByImageId(image3.getId()).isPresent());
  }


  //Tests that saving with a null DetectionResult throws an IllegalArgumentException.
  @Test
  void testSaveNullResultThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.save(null);
    });
  }


  //Tests that saving a DetectionResult with null image
  //throws an IllegalArgumentException.
  @Test
  void testSaveWithNullImageThrowsException() throws Exception {
    DetectionResult resultWithNullImage = createDetectionResultWithNullImage();
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.save(resultWithNullImage);
    });
  }


  //Tests that saving a DetectionResult with null/empty/whitespace imageId
  //throws an IllegalArgumentException.
  @Test
  void testSaveWithInvalidImageIdThrowsException() throws Exception {
    // Test null imageId
    testSaveWithInvalidField(null, "id");
    
    // Test empty imageId
    testSaveWithInvalidField("", "id");
    
    // Test whitespace-only imageId
    testSaveWithInvalidField("   ", "id");
  }


  //Tests that saving a DetectionResult with null/empty/whitespace uploaderId
  //throws an IllegalArgumentException.
  @Test
  void testSaveWithInvalidUploaderIdThrowsException() throws Exception {
    // Test null uploaderId
    testSaveWithInvalidField(null, "uploaderId");
    
    // Test empty uploaderId
    testSaveWithInvalidField("", "uploaderId");
    
    // Test whitespace-only uploaderId
    testSaveWithInvalidField("   ", "uploaderId");
  }


  //Tests that saving a DetectionResult with the same image ID and overwrites the previous entry.
  @Test
  void testSaveUpdatesExistingResult() {
    storageService.save(result1);

    DetectionResult updatedResult = new DetectionResult(image1);
    updatedResult.addLabel(new Label("UpdatedLabel", 88.8));

    storageService.save(updatedResult);
    //The new result completely replaces the old one, rather than appending to it.
    Optional<DetectionResult> retrieved = storageService.getResultByImageId(image1.getId());
    assertTrue(retrieved.isPresent());
    assertEquals(1, retrieved.get().getLabels().size());
    assertEquals("UpdatedLabel", retrieved.get().getLabels().get(0).getName());
  }


  //Tests that updating a DetectionResult with a different userId correctly updates the GSI index.
  @Test
  void testSaveUpdatesUserIdChangesGSI() throws Exception {
    // Save initial result for user1
    storageService.save(result1);
    String imageId = image1.getId();

    // Verify initial state: result belongs to user1
    assertEquals(1, storageService.getResultsByUserId(user1.getId()).size());
    assertEquals(0, storageService.getResultsByUserId(user2.getId()).size());
    assertTrue(storageService.getResultsByUserId(user1.getId()).stream()
        .anyMatch(r -> r.getImage().getId().equals(imageId)));

    // Create a new Image with the same imageId but different uploaderId (user2)
    // Use reflection to set the same imageId and different uploaderId
    Image imageWithSameId = createImageWithSameId(imageId, user2.getId(), 
                                                   "/path/test.jpg", "Updated image");

    // Create new DetectionResult with updated image (same id, different userId)
    DetectionResult updatedResult = new DetectionResult(imageWithSameId);
    updatedResult.addLabel(new Label("UpdatedLabel", 88.8));

    // Save the updated result (this should update the existing entry)
    storageService.save(updatedResult);

    // Verify the result was updated in primary table
    Optional<DetectionResult> retrieved = storageService.getResultByImageId(imageId);
    assertTrue(retrieved.isPresent());
    assertEquals(user2.getId(), retrieved.get().getImage().getUploaderId());
    assertEquals(1, retrieved.get().getLabels().size());

    // Verify GSI index was updated: removed from user1, added to user2
    assertEquals(0, storageService.getResultsByUserId(user1.getId()).size());
    assertEquals(1, storageService.getResultsByUserId(user2.getId()).size());
    assertTrue(storageService.getResultsByUserId(user2.getId()).stream()
        .anyMatch(r -> r.getImage().getId().equals(imageId)));

    // Verify countByUserId reflects the change
    assertEquals(0, storageService.countByUserId(user1.getId()));
    assertEquals(1, storageService.countByUserId(user2.getId()));
  }


  //Tests that getResultByImageId successfully retrieves a DetectionResult
  //for an existing image ID with correct content.
  @Test
  void testGetResultByImageId() {
    storageService.save(result1);

    Optional<DetectionResult> retrieved = storageService.getResultByImageId(image1.getId());

    assertTrue(retrieved.isPresent());
    assertEquals(image1.getId(), retrieved.get().getImage().getId());
    assertEquals(2, retrieved.get().getLabels().size());
    assertEquals("Otter", retrieved.get().getLabels().get(0).getName());
    assertEquals(96.3, retrieved.get().getLabels().get(0).getConfidence(), 0.001);
    assertEquals("Animal", retrieved.get().getLabels().get(1).getName());
    assertEquals(99.0, retrieved.get().getLabels().get(1).getConfidence(), 0.001);
  }


  //Tests that requesting a DetectionResult for a non-existent image ID returns an empty Optional.
  @Test
  void testGetResultByImageIdNotFound() {
    Optional<DetectionResult> retrieved = storageService.getResultByImageId("nonexistent-id");
    assertFalse(retrieved.isPresent());
  }


  //Tests that getResultByImageId throws an IllegalArgumentException
  //when called with a null/empty image ID.
  @Test
  void testGetResultByImageIdInvalidInputThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.getResultByImageId(null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.getResultByImageId("");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.getResultByImageId("   ");
    });
  }


   //Verifies that getResultByImageId trims whitespace from the provided image ID
   //and can still successfully retrieve the stored DetectionResult.
  @Test
  void testGetResultByImageIdTrimsWhitespace() {
    storageService.save(result1);

    Optional<DetectionResult> retrieved = storageService.getResultByImageId("  " + image1.getId() + "  ");
    assertTrue(retrieved.isPresent());
  }


  //Tests that getResultsByUserId returns all DetectionResults belonging to a user
  //with correct counts and no data leakage between users.
  @Test
  void testGetResultsByUserIdIsolation() {
    storageService.save(result1);
    storageService.save(result2);
    storageService.save(result3);

    List<DetectionResult> user1Results = storageService.getResultsByUserId(user1.getId());
    List<DetectionResult> user2Results = storageService.getResultsByUserId(user2.getId());

    assertEquals(2, user1Results.size());
    assertEquals(1, user2Results.size());
    assertEquals(2, storageService.countByUserId(user1.getId()));
    assertEquals(1, storageService.countByUserId(user2.getId()));

    // Verify no data leakage
    assertTrue(user1Results.stream().allMatch(r -> r.getImage().getUploaderId().equals(user1.getId())));
    assertTrue(user2Results.stream().allMatch(r -> r.getImage().getUploaderId().equals(user2.getId())));
  }


  //Tests that getResultsByUserId for a non-existent user ID returns a non-null but empty list.
  @Test
  void testGetResultsByUserIdEmptyList() {
    List<DetectionResult> results = storageService.getResultsByUserId("nonexistent-user");
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }


  //Tests that getResultsByUserId throws an IllegalArgumentException
  //when called with a null/empty user ID string.
  @Test
  void testGetResultsByUserIdInvalidInputThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.getResultsByUserId(null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.getResultsByUserId("");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.getResultsByUserId("   ");
    });
  }


  //Tests that existsByImageId returns false before saving
  //and returns true after saving a DetectionResult with that image ID.
  @Test
  void testExistsByImageId() {
    assertFalse(storageService.existsByImageId(image1.getId()));

    storageService.save(result1);

    assertTrue(storageService.existsByImageId(image1.getId()));
  }


  //Tests that existsByImageId throws an IllegalArgumentException
  //when called with a null/empty image ID.
  @Test
  void testExistsByImageIdInvalidInputThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.existsByImageId(null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.existsByImageId("");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.existsByImageId("   ");
    });
  }


  //Tests that deleteByImageId successfully removes an existing DetectionResult from storage
  //returns true, and existsByImageId returns false afterward.
  @Test
  void testDeleteByImageId() {
    storageService.save(result1);
    assertTrue(storageService.existsByImageId(image1.getId()));

    boolean deleted = storageService.deleteByImageId(image1.getId());
    assertTrue(deleted);
    assertFalse(storageService.existsByImageId(image1.getId()));
  }


  //Tests that deleteByImageId returns false when attempting to delete a non-existent image ID.
  @Test
  void testDeleteByImageIdNotFound() {
    boolean deleted = storageService.deleteByImageId("nonexistent-id");
    assertFalse(deleted);
  }


  //Tests that deleteByImageId throws an IllegalArgumentException
  //when called with a null/empty image ID.
  @Test
  void testDeleteByImageIdInvalidInputThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.deleteByImageId(null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.deleteByImageId("");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.deleteByImageId("   ");
    });
  }


  //Tests that deleting a DetectionResult also updates the GSI
  //by decreasing the number of results for that user.
  @Test
  void testDeleteByImageIdRemovesFromGSI() {
    storageService.save(result1);
    storageService.save(result2);
    assertEquals(2, storageService.getResultsByUserId(user1.getId()).size());

    storageService.deleteByImageId(image1.getId());

    assertEquals(1, storageService.getResultsByUserId(user1.getId()).size());
  }


  //Tests that deleting the only DetectionResult for a user
  //removes that user's entry from the GSI, resulting in zero results.
  @Test
  void testDeleteByImageIdCleansEmptyGSIEntry() {
    storageService.save(result3);
    assertEquals(1, storageService.getResultsByUserId(user2.getId()).size());

    storageService.deleteByImageId(image3.getId());

    assertEquals(0, storageService.getResultsByUserId(user2.getId()).size());
  }


  //Tests that countByUserId returns the correct count of DetectionResults for a user.
  @Test
  void testCountByUserIdReturnsCorrectCount() {
    // Initially zero
    assertEquals(0, storageService.countByUserId(user1.getId()));

    // After saving one result
    storageService.save(result1);
    assertEquals(1, storageService.countByUserId(user1.getId()));

    // After saving multiple results
    storageService.save(result2);
    assertEquals(2, storageService.countByUserId(user1.getId()));

    // Different user has different count
    storageService.save(result3);
    assertEquals(2, storageService.countByUserId(user1.getId()));
    assertEquals(1, storageService.countByUserId(user2.getId()));
  }


  //Tests that countByUserId throws an IllegalArgumentException
  //when called with a null/empty user ID.
  @Test
  void testCountByUserIdInvalidInputThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.countByUserId(null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.countByUserId("");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.countByUserId("   ");
    });
  }


  //Tests that clear removes all DetectionResults from both the primary table and the GSI index,
  //leaving no data for any user or image ID.
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

  // ========== Helper Methods for Reflection-based Tests ==========

  /**
   * Creates an Image with an invalid id field using reflection.
   * Used to test validation logic that checks for null/empty/whitespace imageId.
   * 
   * @param uploaderId the uploader ID (must be valid)
   * @param invalidId the invalid id value (null, empty, or whitespace)
   * @return Image with invalid id field
   * @throws Exception if reflection fails
   */
  private Image createImageWithInvalidId(String uploaderId, String invalidId) throws Exception {
    Image image = new Image(uploaderId, "/path/test.jpg", "Test");
    java.lang.reflect.Field idField = Image.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(image, invalidId);
    return image;
  }

  /**
   * Creates an Image with an invalid uploaderId field using reflection.
   * Used to test validation logic that checks for null/empty/whitespace uploaderId.
   * 
   * @param invalidUploaderId the invalid uploaderId value (null, empty, or whitespace)
   * @return Image with invalid uploaderId field
   * @throws Exception if reflection fails
   */
  private Image createImageWithInvalidUploaderId(String invalidUploaderId) throws Exception {
    // Create Image with valid uploaderId first, then modify it
    Image image = new Image(user1.getId(), "/path/test.jpg", "Test");
    java.lang.reflect.Field uploaderIdField = Image.class.getDeclaredField("uploaderId");
    uploaderIdField.setAccessible(true);
    uploaderIdField.set(image, invalidUploaderId);
    return image;
  }

  /**
   * Creates a DetectionResult with null image field using reflection.
   * Used to test validation logic that checks for null image.
   * 
   * @return DetectionResult with null image field
   * @throws Exception if reflection fails
   */
  private DetectionResult createDetectionResultWithNullImage() throws Exception {
    DetectionResult result = new DetectionResult(image1);
    java.lang.reflect.Field imageField = DetectionResult.class.getDeclaredField("image");
    imageField.setAccessible(true);
    imageField.set(result, null);
    return result;
  }

  /**
   * Creates an Image with a specified id and uploaderId using reflection.
   * Used to test scenarios where imageId remains the same but userId changes.
   * 
   * @param imageId the image ID to set (must match existing image)
   * @param uploaderId the uploader ID (can be different from original)
   * @param storagePath the storage path
   * @param description the description
   * @return Image with specified id and uploaderId
   * @throws Exception if reflection fails
   */
  private Image createImageWithSameId(String imageId, String uploaderId, 
                                      String storagePath, String description) throws Exception {
    Image image = new Image(uploaderId, storagePath, description);
    java.lang.reflect.Field idField = Image.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(image, imageId);
    return image;
  }

  /**
   * Helper method to test saving DetectionResult with invalid field values.
   * Creates an Image with invalid field, wraps it in DetectionResult, and verifies exception.
   * 
   * @param invalidValue the invalid value to test (null, empty, or whitespace)
   * @param fieldType "id" or "uploaderId"
   * @throws Exception if reflection fails
   */
  private void testSaveWithInvalidField(String invalidValue, String fieldType) throws Exception {
    Image image;
    if ("id".equals(fieldType)) {
      image = createImageWithInvalidId(user1.getId(), invalidValue);
    } else if ("uploaderId".equals(fieldType)) {
      image = createImageWithInvalidUploaderId(invalidValue);
    } else {
      throw new IllegalArgumentException("fieldType must be 'id' or 'uploaderId'");
    }
    
    DetectionResult result = new DetectionResult(image);
    assertThrows(IllegalArgumentException.class, () -> {
      storageService.save(result);
    });
  }

}
