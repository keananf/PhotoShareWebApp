package server.requests;

import server.objects.EventType;
import server.objects.EventType;

/**
 * Request wrapper for add comment common
 */
public class AddCommentRequest {

    private final String commentContents;
    private final long referenceId;
    private final EventType eventType;

    public AddCommentRequest(String commentContents, long referenceId, EventType eventType) {
        this.commentContents = commentContents;
        this.referenceId = referenceId;
        this.eventType = eventType;
    }

    /**
     * @return the comment's contents
     */
    public String getCommentContents() {
        return commentContents;
    }

    /**
     * @return id of parent comment / photo
     */
    public long getReferenceId() {
        return referenceId;
    }

    /**
     * @return whether this is a reply or a comment
     */
    public EventType getEventType() {
        return eventType;
    }
}
