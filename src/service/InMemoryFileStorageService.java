package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import model.DetectionResult;

/**
 * In-memory implementation of FileStorageService that mimics AWS DynamoDB behavior.
 * 
 * This implementation simulates DynamoDB's structure:
 * Primary Key: imageId (partition key)
 * Global Secondary Index (GSI): userId
 */
public class InMemoryFileStorageService implements FileStorageService {
    // Primary table: imageId -> DetectionResult
    private final Map<String, DetectionResult> primaryTable = new HashMap<>();
    
    // Global Secondary Index (GSI): userId -> List<imageId>
    private final Map<String, List<String>> userIdIndex = new HashMap<>();
    
    /**
     * Saves a detection result to storage.
     * 
     * <p>This method implements DynamoDB-like upsert behavior:
     * <ul>
     *   <li>If the image ID already exists, the result is overwritten</li>
     *   <li>If the userId changes, the index is updated accordingly</li>
     *   <li>New results are added to both the primary table and GSI index</li>
     * </ul>
     * 
     * @param result the detection result to save (must not be null)
     * @throws IllegalArgumentException if result is null, or if result's image or IDs are invalid
     */
    @Override
    public void save(DetectionResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null");
        }
        if (result.getImage() == null) {
            throw new IllegalArgumentException("Result must contain a non-null image");
        }
        if (result.getImage().getId() == null || result.getImage().getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Image ID cannot be null or empty");
        }
        if (result.getImage().getUploaderId() == null || result.getImage().getUploaderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Uploader ID cannot be null or empty");
        }
        
        String imageId = result.getImage().getId().trim();//new result
        String userId = result.getImage().getUploaderId().trim();
        
        // Check if this is an update
        DetectionResult existingResult = primaryTable.get(imageId);//old result
        boolean isUpdate = existingResult != null;
        
        // Update primary table
        primaryTable.put(imageId, result);
        
        // Update GSI index
        if (isUpdate) {
            // If updating, remove from old userId's index if userId changed
            String oldUserId = existingResult.getImage().getUploaderId();
            if (!oldUserId.equals(userId)) {
                // User changed - remove from old index
                userIdIndex.computeIfPresent(oldUserId, (_key, imageIds) -> {
                    imageIds.remove(imageId);
                    return imageIds.isEmpty() ? null : imageIds;
                });
                // Add to new userId's index (or create if doesn't exist)
                userIdIndex.computeIfAbsent(userId, _key -> new ArrayList<>()).add(imageId);
            }
            // If same userId, item is already in the index, no change needed
        } else {
            // New item - add to userId's index (or create if doesn't exist)
            userIdIndex.computeIfAbsent(userId, _key -> new ArrayList<>()).add(imageId);
        }
    }

    /**
     * Retrieves a detection result by image ID.
     * 
     * <p>Uses the primary key lookup for O(1) performance, mimicking DynamoDB's
     * primary key query behavior.
     * 
     * @param imageId the ID of the image to retrieve the detection result for (must not be null or empty)
     * @return an Optional containing the DetectionResult if found, empty otherwise
     * @throws IllegalArgumentException if imageId is null or empty
     */
    @Override
    public Optional<DetectionResult> getResultByImageId(String imageId) {
        if (imageId == null || imageId.trim().isEmpty()) {
            throw new IllegalArgumentException("Image ID cannot be null or empty");
        }
        // Primary key lookup - O(1) like DynamoDB
        return Optional.ofNullable(primaryTable.get(imageId.trim()));
    }

    /**
     * Retrieves all detection results for a user by user ID.
     * 
     * <p>Uses the GSI (Global Secondary Index) for efficient lookup:
     * <ul>
     *   <li>O(1) to get the list of image IDs from the index</li>
     *   <li>O(k) to fetch results where k = number of results for the user</li>
     * </ul>
     * This mimics DynamoDB GSI query behavior.
     * 
     * @param userId the unique ID of the user to retrieve detection results for (must not be null or empty)
     * @return a list of DetectionResult objects for the user, empty list if none found
     * @throws IllegalArgumentException if userId is null or empty
     */
    @Override
    public List<DetectionResult> getResultsByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        String trimmedUserId = userId.trim();
        
        // GSI lookup - O(1) to get imageIds, then O(k) to fetch results where k = number of results
        // This mimics DynamoDB GSI query behavior
        List<String> imageIds = userIdIndex.getOrDefault(trimmedUserId, Collections.emptyList());
        
        return imageIds.stream()
            .map(primaryTable::get)
            .filter(Objects::nonNull) // Filter out any nulls (shouldn't happen, but defensive)
            .collect(Collectors.toList());
    }
    
    /**
     * Checks if a detection result exists for the given image ID.
     * Mimics DynamoDB's conditional check behavior.
     * 
     * @param imageId the image ID to check (must not be null or empty)
     * @return true if a result exists, false otherwise
     * @throws IllegalArgumentException if imageId is null or empty
     */
    public boolean existsByImageId(String imageId) {
        if (imageId == null || imageId.trim().isEmpty()) {
            throw new IllegalArgumentException("Image ID cannot be null or empty");
        }
        return primaryTable.containsKey(imageId.trim());
    }
    
    /**
     * Deletes a detection result by image ID.
     * 
     * <p>Mimics DynamoDB's delete item operation. Removes the result from both
     * the primary table and the GSI index.
     * 
     * @param imageId the image ID of the result to delete (must not be null or empty)
     * @return true if the item was deleted, false if it didn't exist
     * @throws IllegalArgumentException if imageId is null or empty
     */
    public boolean deleteByImageId(String imageId) {
        if (imageId == null || imageId.trim().isEmpty()) {
            throw new IllegalArgumentException("Image ID cannot be null or empty");
        }
        String trimmedImageId = imageId.trim();
        
        // Remove from primary table
        DetectionResult result = primaryTable.remove(trimmedImageId);
        if (result == null) {
            return false;
        }
        
        // Remove from GSI index
        String userId = result.getImage().getUploaderId();
        userIdIndex.computeIfPresent(userId, (_key, imageIds) -> {
            imageIds.remove(trimmedImageId);
            return imageIds.isEmpty() ? null : imageIds;
        });
        
        return true;
    }
    
    /**
     * Gets the count of detection results for a user.
     * 
     * <p>Mimics DynamoDB's count operation on GSI. Uses the userId index for
     * efficient counting without scanning all results.
     * 
     * @param userId the user ID to count results for (must not be null or empty)
     * @return the number of results for the user
     * @throws IllegalArgumentException if userId is null or empty
     */
    public long countByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        List<String> imageIds = userIdIndex.get(userId.trim());
        return imageIds != null ? imageIds.size() : 0;
    }

    /**
     * Clears all detection results from storage.
     * 
     * <p>This method is useful for testing and mimics truncating a DynamoDB table.
     * Removes all entries from both the primary table and the GSI index.
     */
    public void clear() {
        primaryTable.clear();
        userIdIndex.clear();
    }
}