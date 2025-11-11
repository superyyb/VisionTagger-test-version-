package service;

import java.util.List;
import java.util.Optional;
import model.DetectionResult;

/**
 * AWS S3 implementation of FileStorageService.
 * 
 * <p>This service provides persistent storage for detection results using Amazon S3.
 * Currently not implemented. When implemented, it should:
 * <ul>
 *   <li>Store DetectionResult objects as JSON in S3 buckets</li>
 *   <li>Retrieve results by image ID and user ID</li>
 *   <li>Manage S3 bucket organization and naming</li>
 *   <li>Handle AWS credentials and error cases</li>
 * </ul>
 * 
 * <p>TODO: Implement S3 storage operations:
 * <ul>
 *   <li>Configure AWS SDK and S3 client</li>
 *   <li>Implement save() to upload JSON to S3</li>
 *   <li>Implement getResultByImageId() to retrieve from S3</li>
 *   <li>Implement getResultsByUserId() using S3 listing/querying</li>
 *   <li>Handle S3 errors and retries</li>
 * </ul>
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public class S3StorageService implements FileStorageService {
  // TODO: Implement S3 storage service
  
  /**
   * Saves a detection result to S3 storage.
   * 
   * @param result the detection result to save
   * @throws IllegalArgumentException if result is null or invalid
   */
  @Override
  public void save(DetectionResult result) {
    // TODO: Implement S3 save operation
    throw new UnsupportedOperationException("S3StorageService not yet implemented");
  }
  
  /**
   * Retrieves a detection result by image ID from S3.
   * 
   * @param imageId the ID of the image to retrieve the detection result for
   * @return an Optional containing the detection result if found, empty otherwise
   * @throws IllegalArgumentException if imageId is null or empty
   */
  @Override
  public Optional<DetectionResult> getResultByImageId(String imageId) {
    // TODO: Implement S3 retrieval by image ID
    throw new UnsupportedOperationException("S3StorageService not yet implemented");
  }
  
  /**
   * Retrieves all detection results for a user by user ID from S3.
   * 
   * @param userId the unique ID of the user to retrieve detection results for
   * @return a list of detection results for the user, empty list if none found
   * @throws IllegalArgumentException if userId is null or empty
   */
  @Override
  public List<DetectionResult> getResultsByUserId(String userId) {
    // TODO: Implement S3 retrieval by user ID
    throw new UnsupportedOperationException("S3StorageService not yet implemented");
  }
}
