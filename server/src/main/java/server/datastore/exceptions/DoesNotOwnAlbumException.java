package server.datastore.exceptions;

/**
 * Exception thrown when a user tries to manipulate an album which is not theirs
 */
public class DoesNotOwnAlbumException extends Exception {
    private final String message;

    public DoesNotOwnAlbumException(long albumId, String user) {
        message = String.format("Album %s does not belong to user %s", albumId, user);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
