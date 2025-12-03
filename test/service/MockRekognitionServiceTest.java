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

  // Predefined set of allowed labels (must match MockRekognitionService.SAMPLE_LABELS)
  private static final Set<String> ALLOWED_LABELS = Set.of(
      "Animal", "Dog", "Cat", "Bird", "Horse", "Otter", "Fish", "Elephant", "Lion", "Bear",
      "Plant", "Flower", "Tree", "Sea", "Ocean", "Beach", "Mountain", "Forest", "Sky", "Water",
      "Food", "Pizza", "Coffee", "Fruit", "Vegetable", "Dessert", "Restaurant", "Dining",
      "Person", "People", "Child", "Adult", "Sports", "Exercise", "Dancing", "Running",
      "Vehicle", "Car", "Bicycle", "Phone", "Computer", "Book", "Furniture", "Clothing",
      "Indoor", "Outdoor", "Urban", "Nature", "Building", "Room", "Street", "Park"
  );

  private MockRekognitionService service;
  private User user;
  private Image image;

  @BeforeEach
  void setUp() {
    service = new MockRekognitionService();
    user = User.guestUser("testUser");
    image = new Image(user.getId(), "/path/to/test.jpg", "Test image");
  }


  //Tests that detect() returns a valid DetectionResult with correct image reference
  @Test
  void testDetectReturnsResult() {
    DetectionResult result = service.detect(image);

    assertNotNull(result);
    assertNotNull(result.getLabels());
    assertEquals(image.getId(), result.getImage().getId());
  }


  //Tests that detect() throws IllegalArgumentException when given a null image
  @Test
  void testDetectNullImageThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> service.detect(null));
  }


  //Tests that detect() generates 3–15 labels
  @Test
  void testLabelCountRange() {
    DetectionResult result = service.detect(image);

    assertFalse(result.getLabels().isEmpty());
    assertTrue(result.getLabels().size() >= 3);
    assertTrue(result.getLabels().size() <= 15);
  }


  //Tests that label confidence values fall within 30–100%
  @Test
  void testConfidenceRange() {
    DetectionResult result = service.detect(image);

    for (Label label : result.getLabels()) {
      assertTrue(label.getConfidence() >= 30.0);
      assertTrue(label.getConfidence() <= 100.0);
    }
  }


  //Tests that labels generated in one single run are unique
  @Test
  void testLabelsAreUnique() {
    DetectionResult result = service.detect(image);

    Set<String> set = new HashSet<>();
    for (Label label : result.getLabels()) {
      assertTrue(set.add(label.getName()), "Duplicate label detected: " + label.getName());
    }
  }


  //Tests that all generated labels have valid non-empty names
  //and come from the predefined SAMPLE_LABELS set.
  @Test
  void testLabelsHaveValidNamesFromDefinedSet() {
    DetectionResult result = service.detect(image);

    for (Label label : result.getLabels()) {
      // Verify name is not null and not empty
      assertNotNull(label.getName(), "Label name should not be null");
      assertFalse(label.getName().trim().isEmpty(), "Label name should not be empty");
      
      // Verify name comes from the predefined set
      assertTrue(ALLOWED_LABELS.contains(label.getName()), 
          "Label name '" + label.getName() + "' is not in the allowed set");
    }
  }


  //Tests that repeated detect() calls on the same image produce different random results.
  @Test
  void testRandomnessAcrossCalls() {
    DetectionResult res1 = service.detect(image);
    DetectionResult res2 = service.detect(image);

    // Verify statelessness: results should be different.
    assertNotEquals(res1, res2, "Service should be stateless and produce different results");

    // Verify randomness: should differ in either size or order or content.
    boolean different = false;

    List<Label> l1 = res1.getLabels();
    List<Label> l2 = res2.getLabels();

    if (l1.size() != l2.size()) {
      different = true;
    } else {
      for (int i = 0; i < l1.size(); i++) {
        if (!l1.get(i).getName().equals(l2.get(i).getName())) {
          different = true;
          break;
        }
      }
    }
    assertTrue(different, "Randomness test failed: two results should not be identical.");
  }


  // Tests that detect() works with special characters in file path and description
  @Test
  void testSpecialCharactersImage() {
    Image img = new Image(user.getId(),
        "/path/with spaces/image-1_test.jpg",
        "Describe with special-characters.");

    DetectionResult result = service.detect(img);
    assertNotNull(result);
    assertFalse(result.getLabels().isEmpty());
  }


  // Tests that detect() works for image without description
  @Test
  void testImageWithoutDescription() {
    Image img = new Image(user.getId(), "/path/test.jpg");

    DetectionResult result = service.detect(img);
    assertNotNull(result);
    assertFalse(result.getLabels().isEmpty());
  }


  // Tests detect() on very long path strings
  @Test
  void testVeryLongPath() {
    String longPath = "/very/long/path/".repeat(10) + "image.jpg";
    Image img = new Image(user.getId(), longPath, "Long path");

    DetectionResult result = service.detect(img);
    assertNotNull(result);
    assertFalse(result.getLabels().isEmpty());
  }
}
