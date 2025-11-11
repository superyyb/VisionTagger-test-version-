package service;

/**
 * Exception thrown when a requested resource is not found in the storage system.
 * This is a checked exception to force callers to handle the "not found" case explicitly.
 */
public class NotFoundException extends Exception {
    
    /**
     * Constructs a NotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public NotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a NotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

