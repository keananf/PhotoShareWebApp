package server.requests;

import server.objects.CommentType;

/**
 * Request wrapper for add comment common
 */
public class AddCommentRequest {

    private final String commentContents;
    private final long referenceId;
    private final CommentType commentType;

    public AddCommentRequest(String commentContents, long referenceId, CommentType commentType) {
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
