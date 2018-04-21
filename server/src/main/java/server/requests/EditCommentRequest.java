package server.requests;

import server.objects.Auth;
import server.objects.CommentType;

/**
 * Request wrapper for edit comment common
 */
public class EditCommentRequest extends AuthRequest {

    private final String commentContents;
    private final long referenceId;
    private final long timestamp;

    public EditCommentRequest(Auth auth, String commentContents, long referenceId) {
        super(auth);
        this.commentContents = commentContents;
        this.referenceId = referenceId;
        timestamp = System.nanoTime();
    }

    /**
     * @return the comment's new contents
     */
    public String getCommentContents() {
        return commentContents;
    }

    /**
     * @return id of comment
     */
    public long getReferenceId() {
        return referenceId;
    }
}
