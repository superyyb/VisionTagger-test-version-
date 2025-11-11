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
 * <p>This implementation simulates DynamoDB's structure:
 * <ul>
 *   <li>Primary Key: imageId (partition key) - O(1) lookup</li>
 *   <li>Global Secondary Index (GSI): userId - O(1) lookup via index</li>
 *   <li>Upsert behavior: save() overwrites existing items with same imageId</li>
 * </ul>
 * 
 * <p>This implementation is not thread-safe and should only be used in single-threaded
 * environments or with external synchronization. For production, use DynamoDBFileStorageService.
 */
public class InMemoryFileStorageService implements FileStorageService {
    // Primary table: imageId -> DetectionResult (simulates DynamoDB primary key)
    private final Map<String, DetectionResult> primaryTable = new HashMap<>();
    
    // Global Secondary Index (GSI): userId -> List<imageId> (simulates DynamoDB GSI)
    // This allows O(1) lookup for userId queries instead of O(n) scan
    private final Map<String, List<String>> userIdIndex = new HashMap<>();
    
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
        
        String imageId = result.getImage().getId().trim();
        String userId = result.getImage().getUploaderId().trim();
        
        // Check if this is an update (DynamoDB upsert behavior)
        DetectionResult existingResult = primaryTable.get(imageId);
        boolean isUpdate = existingResult != null;
        
        // Update primary table (DynamoDB put item - overwrites if exists)
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
            } else {
                // Same userId, no index change needed (item already in index)
                return;
            }
        }
        
        // Add to new userId's index (or create if doesn't exist)
        userIdIndex.computeIfAbsent(userId, _key -> new ArrayList<>()).add(imageId);
    }

    @Override
    public Optional<DetectionResult> getResultByImageId(String imageId) {
        if (imageId == null || imageId.trim().isEmpty()) {
            throw new IllegalArgumentException("Image ID cannot be null or empty");
        }
        // Primary key lookup - O(1) like DynamoDB
        return Optional.ofNullable(primaryTable.get(imageId.trim()));
    }

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
     * Check if a detection result exists for the given image ID.
     * Mimics DynamoDB's conditional check behavior.
     * 
     * @param imageId the image ID to check
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
     * Delete a detection result by image ID.
     * Mimics DynamoDB's delete item operation.
     * 
     * @param imageId the image ID of the result to delete
     * @return true if the item was deleted, false if it didn't exist
     * @throws IllegalArgumentException if imageId is null or empty
     */
    public boolean deleteByImageId(String imageId) {
        if (imageId == null || imageId.trim().isEmpty()) {
            throw new IllegalArgumentException("Image ID cannot be null or empty");
        }
        String trimmedImageId = imageId.trim();
        
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
     * Get the count of detection results for a user.
     * Mimics DynamoDB's count operation on GSI.
     * 
     * @param userId the user ID to count results for
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
     * Clears all detection results from storage (useful for testing).
     * This mimics truncating a DynamoDB table.
     */
    public void clear() {
        primaryTable.clear();
        userIdIndex.clear();
    }
}