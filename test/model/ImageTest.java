package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Unit tests for the Image model class.
 */
public class ImageTest {

    private Image image;
    private User user;
    private String filePath;
    private String description;

    @BeforeEach
    void setUp() {
        user = User.guestUser("testuser");
        filePath = "/path/to/image.jpg";
        description = "Test image description";
        image = new Image(user.getId(), filePath, description);
    }

    @Test
    void testConstructorWithAllFields() {
        assertNotNull(image);
        assertEquals(user.getId(), image.getUploaderId());
        assertEquals(filePath, image.getStoragePath());
        assertEquals(description, image.getDescription());
        assertNotNull(image.getId());
        assertNotNull(image.getUploadedAt());
    }

    @Test
    void testConstructorWithoutDescription() {
        Image img = new Image(user.getId(), filePath);
        assertEquals("", img.getDescription());
        assertEquals(user.getId(), img.getUploaderId());
        assertEquals(filePath, img.getStoragePath());
    }

    @Test
    void testConstructorWithNullDescription() {
        Image img = new Image(user.getId(), filePath, null);
        assertEquals("", img.getDescription());
    }

    @Test
    void testConstructorThrowsExceptionForNullUploaderId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Image(null, filePath, description);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Image("", filePath, description);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Image("   ", filePath, description);
        });
    }

    @Test
    void testConstructorGeneratesUniqueId() {
        Image img1 = new Image(user.getId(), filePath, description);
        Image img2 = new Image(user.getId(), filePath, description);
        assertNotEquals(img1.getId(), img2.getId());
    }

    @Test
    void testConstructorSetsUploadedAtTimestamp() {
        LocalDateTime uploadedAt = image.getUploadedAt();
        assertNotNull(uploadedAt);
        assertTrue(uploadedAt.isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testGetters() {
        assertEquals(user.getId(), image.getUploaderId());
        assertEquals(filePath, image.getStoragePath());
        assertEquals(description, image.getDescription());
        assertNotNull(image.getId());
        assertNotNull(image.getUploadedAt());
    }

    /* Test equality and hash code consistency */
    @Test
    void testEquals() {
        Image img1 = new Image(user.getId(), filePath, description);
        Image img2 = new Image(user.getId(), filePath, description);
        assertNotEquals(img1, img2);
        assertNotEquals(img1.hashCode(), img2.hashCode());

        assertEquals(img1, img1);
        assertEquals(img1.hashCode(), img1.hashCode());
    }

    @Test
    void testEqualsWithInvalidObject() {
        assertFalse(image.equals(null));
        assertFalse(image.equals("not an image"));
    }
    
    @Test
    void testToString() {
        String result = image.toString();
        assertTrue(result.contains("uploaderId=" + user.getId()));
        assertTrue(result.contains("storagePath=" + filePath));
    }
}