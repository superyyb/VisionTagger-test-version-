package service;

import java.util.List;
import java.util.Optional;

import model.DetectionResult;

/**
 * Interface for storing and retrieving detection results.
 * This service manages persistence of image recognition results.
 */
public interface FileStorageService {

    /**
     * Save a detection result to storage.
     * 
     * @param result the detection result to save
     * @throws IllegalArgumentException if result is null or invalid
     */
    void save(DetectionResult result);

    /**
     * Retrieve a detection result by image ID.
     * 
     * @param imageId the ID of the image to retrieve the detection result for
     * @return an Optional containing the detection result if found, empty otherwise
     * @throws IllegalArgumentException if imageId is null or empty
     */
    Optional<DetectionResult> getResultByImageId(String imageId);

    /**
     * Retrieve all detection results for a user by user ID.
     * 
     * @param userId the unique ID of the user to retrieve detection results for
     * @return a list of detection results for the user, empty list if none found
     * @throws IllegalArgumentException if userId is null or empty
     */
    List<DetectionResult> getResultsByUserId(String userId);
}
