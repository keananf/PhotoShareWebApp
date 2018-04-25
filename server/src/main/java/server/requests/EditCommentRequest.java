package server.requests;

/**
 * Request wrapper for edit comment common
 */
public class EditCommentRequest {

    private final String commentContents;

    public EditCommentRequest(String commentContents) {
        this.commentContents = commentContents;
    }

    /**
     * @return the comment's new contents
     */
    public String getCommentContents() {
        return commentContents;
    }
}