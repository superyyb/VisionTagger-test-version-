package model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an image uploaded by a user. 
 * Immutable once created.
 */
public final class Image {

  private final String id;
  private final String uploaderId;
  private final String storagePath;
  private final LocalDateTime uploadedAt;
  private final String description;

  /**
   * Constructs an Image object with optional description.
   *
   * @param uploaderId the unique ID of the user who uploaded the image
   * @param storagePath the path or URI of the image file
   * @param description optional description of this image
   */
  public Image(String uploaderId, String storagePath, String description) {
    if (uploaderId == null || uploaderId.trim().isEmpty()) {
      throw new IllegalArgumentException("Uploader ID cannot be null or empty");
    }
    if (storagePath == null || storagePath.trim().isEmpty()) {
      throw new IllegalArgumentException("Storage path cannot be null or empty");
    }
    this.id = UUID.randomUUID().toString();
    this.uploaderId = uploaderId.trim();
    this.storagePath = storagePath.trim();
    this.uploadedAt = LocalDateTime.now();
    this.description = description == null ? "" : description.trim();
  }

  /** Convenience constructor without description. */
  public Image(String uploaderId, String storagePath) {
    this(uploaderId, storagePath, "");
  }

  // Getters
  
  /**
   * Gets the unique identifier for this image.
   * 
   * @return the image ID (UUID string)
   */
  public String getId() { return id; }
  
  /**
   * Gets the ID of the user who uploaded this image.
   * 
   * @return the uploader's user ID
   */
  public String getUploaderId() { return uploaderId; }
  
  /**
   * Gets the storage path or URI where the image file is stored.
   * 
   * @return the storage path/URI
   */
  public String getStoragePath() { return storagePath; }
  
  /**
   * Gets the timestamp when the image was uploaded.
   * 
   * @return the upload timestamp
   */
  public LocalDateTime getUploadedAt() { return uploadedAt; }
  
  /**
   * Gets the optional description of the image.
   * 
   * @return the description (empty string if none was provided)
   */
  public String getDescription() { return description; }

  @Override
  public String toString() {
    return String.format(
        "Image[id=%s, uploaderId=%s, storagePath=%s, uploadedAt=%s, description=%s]",
        id, uploaderId, storagePath, uploadedAt, description);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Image)) return false;
    Image other = (Image) o;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}