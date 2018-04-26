package server.datastore.exceptions;


import server.objects.Comment;
import server.objects.EventType;

/**
 * Thrown when a resource request has incorrect information (e.g. parent, user, comment, etc.)
 */
public class InvalidResourceRequestException extends Exception {
    private final String message;

    public InvalidResourceRequestException(Comment comment) {
        boolean reply = comment.getEventType().equals(EventType.REPLY);
        String parent = reply ? "parent comment" : "photo";

        this.message = String.format("Cannot find %s with id %d", parent, comment.getReferenceId());
    }

    public InvalidResourceRequestException(long id) {
        message = String.format("Invalid reference id passed in: %d", id);
    }

    public InvalidResourceRequestException(String user) {
        message = String.format("Invalid user passed in: %s", user);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
