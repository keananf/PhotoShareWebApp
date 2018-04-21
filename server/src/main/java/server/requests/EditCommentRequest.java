package server.requests;

import server.objects.Auth;
import server.objects.CommentType;

/**
 * Request wrapper for edit comment common
 */
public class EditCommentRequest extends AuthRequest {

    private final String commentContents;

    public EditCommentRequest(Auth auth, String commentContents) {
        super(auth);
        this.commentContents = commentContents;
    }

    /**
     * @return the comment's new contents
     */
    public String getCommentContents() {
        return commentContents;
    }
}
