package controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import model.DetectionResult;
import model.Image;
import model.Label;
import model.User;
import service.ImageAnalyzerService;
import service.FileStorageService;
import service.MockRekognitionService;
import service.InMemoryFileStorageService;
import view.View;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for the ImageController class.
 */
public class ImageControllerTest {

  private ImageAnalyzerService mockAnalyzerService;
  private FileStorageService mockStorageService;
  private View mockView;
  private User registeredUser;
  private User guestUser;
  private Image testImage;
  private DetectionResult testResult;
  private String testFilePath;

  @BeforeEach
  void setUp() {
    // Create real service implementations (fresh instances for each test)
    mockAnalyzerService = new MockRekognitionService();
    mockStorageService = new InMemoryFileStorageService();
    mockView = new TestView();

    // Create test users
    registeredUser = User.registeredUser("testUser", "test@example.com");
    guestUser = User.guestUser("guest");

    // Create test image and result
    testFilePath = "/path/to/test.jpg";
    testImage = new Image(registeredUser.getId(), testFilePath, "Test image");
    testResult = new DetectionResult(testImage);
    testResult.addLabel(new Label("TestLabel", 95.0));
  }


  //Tests that constructors throw IllegalArgumentException when parameters are null.
  @Test
  void testConstructorsNullParametersThrowsException() {

    // Test null analyzer service throws exception (all 3 constructors)
    // Constructor 1: ImageController(analyzer, view)
    assertThrows(IllegalArgumentException.class, () -> {
      new ImageController(null, (View) mockView);
    });
    // Constructor 2: ImageController(analyzer, storage, view)
    assertThrows(IllegalArgumentException.class, () -> {
      new ImageController(null, mockStorageService, mockView);
    });
    // Constructor 3: ImageController(analyzer, storage)
    assertThrows(IllegalArgumentException.class, () -> {
      new ImageController(null, (FileStorageService) mockStorageService);
    });

    // Test null view throws exception (constructors that require view)
    // Constructor 1: ImageController(analyzer, view)
    assertThrows(IllegalArgumentException.class, () -> {
      new ImageController(mockAnalyzerService, (View) null);
    });
    // Constructor 2: ImageController(analyzer, storage, view)
    assertThrows(IllegalArgumentException.class, () -> {
      new ImageController(mockAnalyzerService, mockStorageService, null);
    });

    // Test null storage service throws exception (constructors that require storage)
    // Constructor 2: ImageController(analyzer, storage, view)
    assertThrows(IllegalArgumentException.class, () -> {
      new ImageController(mockAnalyzerService, (FileStorageService) null, mockView);
    });
    // Constructor 3: ImageController(analyzer, storage)
    assertThrows(IllegalArgumentException.class, () -> {
      new ImageController(mockAnalyzerService, (FileStorageService) null);
    });
  }


  //Tests that ImageController can be constructed with all three constructor variants.
  @Test
  void testConstructorsSuccess() {
    // Test constructor1 with analyzer and view only
    ImageController controller1 = new ImageController(mockAnalyzerService, (View) mockView);
    assertNotNull(controller1);

    // Test constructor2 with analyzer, storage, and view
    ImageController controller2 = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);
    assertNotNull(controller2);

    // Test constructor3 with analyzer and storage only
    ImageController controller3 = new ImageController(mockAnalyzerService,
        (FileStorageService) mockStorageService);
    assertNotNull(controller3);
  }


  //Tests that uploadAndAnalyzeImage successfully creates image,
  //analyzes it, saves to storage, and returns result.
  @Test
  void testUploadAndAnalyzeImageSuccess() {
    InMemoryFileStorageService storage = new InMemoryFileStorageService();
    ImageController controller = new ImageController(mockAnalyzerService, storage, mockView);

    DetectionResult result = controller.uploadAndAnalyzeImage(registeredUser, testFilePath,
        "Description");

    assertNotNull(result);
    assertNotNull(result.getImage());
    assertEquals(registeredUser.getId(), result.getImage().getUploaderId());
    assertEquals(testFilePath, result.getImage().getStoragePath());
    assertEquals("Description", result.getImage().getDescription());

    // Verify result is saved to storage
    Optional<DetectionResult> saved = storage.getResultByImageId(result.getImage().getId());
    assertTrue(saved.isPresent());
    assertEquals(result.getImage().getId(), saved.get().getImage().getId());
  }


  //Tests that uploadAndAnalyzeImage does not save when storage service is not available.
  @Test
  void testUploadAndAnalyzeImageWithoutStorage() {
    ImageController controller = new ImageController(mockAnalyzerService, (View) mockView);

    DetectionResult result = controller.uploadAndAnalyzeImage(guestUser, testFilePath, null);

    assertNotNull(result);
    // Should not throw exception even without storage
  }


  //Tests that uploadAndAnalyzeImage throws IllegalArgumentException
  //when user is null.
  @Test
  void testUploadAndAnalyzeImageNullUserThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    assertThrows(IllegalArgumentException.class, () -> {
      controller.uploadAndAnalyzeImage(null, testFilePath, null);
    });
  }


  //Tests that uploadAndAnalyzeImage throws IllegalArgumentException
  //when filePath is null or empty.
  @Test
  void testUploadAndAnalyzeImageNullOrEmptyFilePathThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    assertThrows(IllegalArgumentException.class, () -> {
      controller.uploadAndAnalyzeImage(registeredUser, null, null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      controller.uploadAndAnalyzeImage(registeredUser, "", null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      controller.uploadAndAnalyzeImage(registeredUser, "   ", null);
    });
  }


  //Tests that uploadAndAnalyzeImage accepts null description
  //and converts it to empty string.
  @Test
  void testUploadAndAnalyzeImageWithNullDescription() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    DetectionResult result = controller.uploadAndAnalyzeImage(registeredUser, testFilePath, null);

    assertNotNull(result);
    assertEquals("", result.getImage().getDescription());
  }


  //Tests that getDetectionResult successfully retrieves a saved result by image ID.
  @Test
  void testGetDetectionResultSuccess() {
    InMemoryFileStorageService storage = new InMemoryFileStorageService();
    ImageController controller = new ImageController(mockAnalyzerService, storage, mockView);

    // Save a result first
    DetectionResult saved = controller.uploadAndAnalyzeImage(registeredUser, testFilePath, null);
    String imageId = saved.getImage().getId();

    // Retrieve the saved result
    Optional<DetectionResult> retrieved = controller.getDetectionResult(imageId);

    assertTrue(retrieved.isPresent());
    assertEquals(imageId, retrieved.get().getImage().getId());
    assertEquals(saved.getImage().getStoragePath(), retrieved.get().getImage().getStoragePath());
  }


  //Tests that getDetectionResult returns empty Optional when result is not found.
  @Test
  void testGetDetectionResultNotFound() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    Optional<DetectionResult> result = controller.getDetectionResult("nonexistent-id");

    assertFalse(result.isPresent());
  }


  //Tests that getDetectionResult throws IllegalArgumentException when imageId is null or empty.
  @Test
  void testGetDetectionResultNullOrEmptyImageIdThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    assertThrows(IllegalArgumentException.class, () -> {
      controller.getDetectionResult(null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      controller.getDetectionResult("");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      controller.getDetectionResult("   ");
    });
  }


  //Tests that getDetectionResult throws IllegalStateException when storage service is not available.
  @Test
  void testGetDetectionResultWithoutStorageThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, (View) mockView);

    assertThrows(IllegalStateException.class, () -> {
      controller.getDetectionResult("some-id");
    });
  }


  //Tests that listUserResults successfully retrieves all results for a user.
  @Test
  void testListUserResultsSuccess() {
    InMemoryFileStorageService storage = new InMemoryFileStorageService();
    ImageController controller = new ImageController(mockAnalyzerService, storage, mockView);

    DetectionResult result1 = controller.uploadAndAnalyzeImage(registeredUser, testFilePath, null);
    DetectionResult result2 = controller.uploadAndAnalyzeImage(registeredUser, "/path/to/test2.jpg",
        null);

    List<DetectionResult> results = controller.listUserResults(registeredUser);

    assertEquals(2, results.size());
    assertTrue(
        results.stream().anyMatch(r -> r.getImage().getId().equals(result1.getImage().getId())));
    assertTrue(
        results.stream().anyMatch(r -> r.getImage().getId().equals(result2.getImage().getId())));
  }


  //Tests that listUserResults returns empty list when user has no results.
  @Test
  void testListUserResultsEmpty() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    List<DetectionResult> results = controller.listUserResults(registeredUser);

    assertNotNull(results);
    assertTrue(results.isEmpty());
  }


  //Tests that listUserResults throws IllegalArgumentException when user is null.
  @Test
  void testListUserResultsNullUserThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    assertThrows(IllegalArgumentException.class, () -> {
      controller.listUserResults(null);
    });
  }


  //Tests that listUserResults throws IllegalStateException when storage service is not available.
  @Test
  void testListUserResultsWithoutStorageThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, (View) mockView);

    assertThrows(IllegalStateException.class, () -> {
      controller.listUserResults(registeredUser);
    });
  }


  //Tests that process successfully analyzes image and displays result using view.
  @Test
  void testProcessSuccess() {
    TestView view = new TestView();
    ImageController controller = new ImageController(mockAnalyzerService, (View) view);

    controller.process(testFilePath);

    assertTrue(view.wasDisplayCalled());
    assertNotNull(view.getLastDisplayedResult());
  }


  //Tests that process throws IllegalArgumentException when filePath is null or empty.
  @Test
  void testProcessNullOrEmptyFilePathThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, (View) mockView);

    assertThrows(IllegalArgumentException.class, () -> {
      controller.process(null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      controller.process("");
    });
    assertThrows(IllegalArgumentException.class, () -> {
      controller.process("   ");
    });
  }


  //Tests that process trims filePath with leading and trailing whitespace.
  @Test
  void testProcessTrimsFilePath() {
    TestView view = new TestView();
    ImageController controller = new ImageController(mockAnalyzerService, (View) view);

    String filePathWithSpaces = "  /path/to/test.jpg  ";
    String expectedTrimmedPath = "/path/to/test.jpg";

    controller.process(filePathWithSpaces);

    assertTrue(view.wasDisplayCalled());
    assertNotNull(view.getLastDisplayedResult());
    // Verify that the filePath was trimmed before creating the Image
    assertEquals(expectedTrimmedPath, view.getLastDisplayedResult().getImage().getStoragePath());
  }


  //Tests that process throws IllegalStateException when view is not available.
  @Test
  void testProcessWithoutViewThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService,
        (FileStorageService) mockStorageService);

    assertThrows(IllegalStateException.class, () -> {
      controller.process(testFilePath);
    });
  }


  //Tests that processForUser successfully processes image for registered user and saves result.
  @Test
  void testProcessForUserRegisteredUserSaves() {
    InMemoryFileStorageService storage = new InMemoryFileStorageService();
    TestView view = new TestView();
    ImageController controller = new ImageController(mockAnalyzerService, storage, view);

    DetectionResult result = controller.processForUser(registeredUser, testFilePath, "Description");

    assertNotNull(result);
    assertTrue(view.wasDisplayCalled());
    Optional<DetectionResult> saved = storage.getResultByImageId(result.getImage().getId());
    assertTrue(saved.isPresent());
  }


  //Tests that processForUser processes image for guest user without saving.
  @Test
  void testProcessForUserGuestUserDoesNotSave() {
    InMemoryFileStorageService storage = new InMemoryFileStorageService();
    TestView view = new TestView();
    ImageController controller = new ImageController(mockAnalyzerService, storage, view);

    DetectionResult result = controller.processForUser(guestUser, testFilePath, null);

    assertNotNull(result);
    assertTrue(view.wasDisplayCalled());

    // The result belongs to guest user
    assertEquals(guestUser.getId(), result.getImage().getUploaderId());

    //Guest user results should not be saved
    Optional<DetectionResult> saved = storage.getResultByImageId(result.getImage().getId());
    assertFalse(saved.isPresent(), "Guest user results should not be saved");
  }

  //Tests that processForUser throws IllegalArgumentException when user is null.
  @Test
  void testProcessForUserNullUserThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    assertThrows(IllegalArgumentException.class, () -> {
      controller.processForUser(null, testFilePath, null);
    });
  }


  //Tests that processForUser throws IllegalArgumentException when filePath is null or empty.
  @Test
  void testProcessForUserNullOrEmptyFilePathThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    assertThrows(IllegalArgumentException.class, () -> {
      controller.processForUser(registeredUser, null, null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      controller.processForUser(registeredUser, "", null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      controller.processForUser(registeredUser, "   ", null);
    });
  }


  //Tests that processForUser trims filePath with leading and trailing whitespace.
  @Test
  void testProcessForUserTrimsFilePath() {
    InMemoryFileStorageService storage = new InMemoryFileStorageService();
    TestView view = new TestView();
    ImageController controller = new ImageController(mockAnalyzerService, storage, view);

    String filePathWithSpaces = "  /path/to/test.jpg  ";
    String expectedTrimmedPath = "/path/to/test.jpg";

    DetectionResult result = controller.processForUser(registeredUser, filePathWithSpaces, null);

    assertNotNull(result);
    assertTrue(view.wasDisplayCalled());
    // Verify that the filePath was trimmed before creating the Image
    assertEquals(expectedTrimmedPath, result.getImage().getStoragePath());
  }


  //Tests that processForUser throws IllegalStateException when view is not available.
  @Test
  void testProcessForUserWithoutViewThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService,
        (FileStorageService) mockStorageService);

    assertThrows(IllegalStateException.class, () -> {
      controller.processForUser(registeredUser, testFilePath, null);
    });
  }


  //Tests that displayUserHistory successfully displays user's saved results (with and without data).
  @Test
  void testDisplayUserHistory() {
    InMemoryFileStorageService storage = new InMemoryFileStorageService();
    ImageController controller = new ImageController(mockAnalyzerService, storage, mockView);

    // Test case 1: User with saved results
    controller.uploadAndAnalyzeImage(registeredUser, testFilePath, null);
    controller.uploadAndAnalyzeImage(registeredUser, "/path/to/test2.jpg", null);

    List<DetectionResult> results = controller.listUserResults(registeredUser);
    assertEquals(2, results.size());

    // Should not throw exception and should display results
    assertDoesNotThrow(() -> {
      controller.displayUserHistory(registeredUser);
    });

    // Test case 2: User with no saved results
    User newUser = User.registeredUser("newuser", "new@example.com");
    List<DetectionResult> emptyResults = controller.listUserResults(newUser);
    assertTrue(emptyResults.isEmpty());

    // Should not throw exception for empty history
    assertDoesNotThrow(() -> {
      controller.displayUserHistory(newUser);
    });
  }


  //Tests that displayUserHistory throws IllegalArgumentException when user is null.
  @Test
  void testDisplayUserHistoryNullUserThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, mockStorageService,
        mockView);

    assertThrows(IllegalArgumentException.class, () -> {
      controller.displayUserHistory(null);
    });
  }


  //Tests that displayUserHistory throws IllegalStateException when storage service is not available.
  @Test
  void testDisplayUserHistoryWithoutStorageThrowsException() {
    ImageController controller = new ImageController(mockAnalyzerService, (View) mockView);

    assertThrows(IllegalStateException.class, () -> {
      controller.displayUserHistory(registeredUser);
    });
  }


  // Test helper class for View testing

  private static class TestView implements View {
    private boolean displayCalled = false;
    private DetectionResult lastDisplayedResult;

    @Override
    public void display(DetectionResult result) {
      displayCalled = true;
      lastDisplayedResult = result;
    }

    public boolean wasDisplayCalled() {
      return displayCalled;
    }

    public DetectionResult getLastDisplayedResult() {
      return lastDisplayedResult;
    }
  }
}