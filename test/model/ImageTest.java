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
    private String uploader;
    private String filePath;
    private String description;

    @BeforeEach
    void setUp() {
        uploader = "testuser";
        filePath = "/path/to/image.jpg";
        description = "Test image description";
        image = new Image(uploader, filePath, description);
    }

    @Test
    void testConstructorWithAllFields() {
        assertNotNull(image);
        assertEquals(uploader, image.getUploader());
        assertEquals(filePath, image.getFilePath());
        assertEquals(description, image.getDescription());
        assertNotNull(image.getId());
        assertNotNull(image.getUploadedAt());
    }

    @Test
    void testConstructorWithoutDescription() {
        Image img = new Image(uploader, filePath);
        assertEquals("", img.getDescription());
        assertEquals(uploader, img.getUploader());
        assertEquals(filePath, img.getFilePath());
    }

    @Test
    void testConstructorWithNullDescription() {
        Image img = new Image(uploader, filePath, null);
        assertEquals("", img.getDescription());
    }

    @Test
    void testConstructorGeneratesUniqueId() {
        Image img1 = new Image(uploader, filePath, description);
        Image img2 = new Image(uploader, filePath, description);
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
        assertEquals(uploader, image.getUploader());
        assertEquals(filePath, image.getFilePath());
        assertEquals(description, image.getDescription());
        assertNotNull(image.getId());
        assertNotNull(image.getUploadedAt());
    }

    /* Test equality and hash code consistency */
    @Test
    void testEquals() {
        Image img1 = new Image(uploader, filePath, description);
        Image img2 = new Image(uploader, filePath, description);
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
        assertEquals(result, "Image[id=" + image.getId() + ", uploader=" + uploader + ", filePath=" + filePath + ", uploadedAt=" + image.getUploadedAt() + "]");
    }
}