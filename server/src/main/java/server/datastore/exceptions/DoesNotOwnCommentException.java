package server.datastore.exceptions;

/**
 * Exception thrown when a user tries to manipulate a comment which is not theirs
 */
public class DoesNotOwnCommentException extends Exception {
    private final String message;

    public DoesNotOwnCommentException(long commentId, String user) {
        message = String.format("Comment %s does not belong to user %s", commentId, user);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
