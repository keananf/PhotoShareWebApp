package server.requests;

import server.objects.Auth;
import server.objects.CommentType;

/**
 * Request wrapper for add comment common
 */
public class AddCommentRequest extends AuthRequest {

    private final String commentContents;
    private final long referenceId;
    private final CommentType commentType;

    public AddCommentRequest(Auth auth, String commentContents, long referenceId, CommentType commentType) {
        super(auth);
        this.commentContents = commentContents;
        this.referenceId = referenceId;
        this.commentType = commentType;
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
    public CommentType getCommentType() {
        return commentType;
    }
}
