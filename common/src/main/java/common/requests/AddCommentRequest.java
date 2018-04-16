package common.requests;

import common.Auth;
import common.CommentType;

/**
 * Request wrapper for add comment common
 */
public class AddCommentRequest extends AuthRequest {

    private final String commentContents;
    private final long referenceId;
    private final CommentType type;
    private final long timestamp;

    public AddCommentRequest(Auth auth, String commentContents, long referenceId, CommentType type) {
        super(auth);
        this.commentContents = commentContents;
        this.referenceId = referenceId;
        this.type = type;
        timestamp = System.nanoTime();
    }

    /**
     * @return the comment's contents
     */
    public String getCommentContents() {
        return commentContents;
    }

    /**
     * @return the time at which this request was made.
     */
    public long getTimestamp() {
        return timestamp;
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
    public CommentType getType() {
        return type;
    }
}
