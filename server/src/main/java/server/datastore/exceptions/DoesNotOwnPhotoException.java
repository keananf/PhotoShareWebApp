package server.datastore.exceptions;

/**
 * Exception thrown when a user tries to manipulate a photo which is not theirs
 */
public class DoesNotOwnPhotoException extends Exception {
    private final String message;

    public DoesNotOwnPhotoException(long photoId, String user) {
        message = String.format("Photo %s does not belong to user %s", photoId, user);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
