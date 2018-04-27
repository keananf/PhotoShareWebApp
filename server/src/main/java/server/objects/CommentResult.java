package server.objects;

import java.util.List;

/**
 * Class representing a comment result.
 */
public class CommentResult {
    private final Comment comment;
    private final List<Comment> childComments;

    public CommentResult(Comment comment, List<Comment> replies) {
        this.comment = comment;
        childComments = replies;
    }

    /**
     * @return the parent comment
     */
    public Comment getComment() {
        return comment;
    }

    /**
     * @return top-level replies to the parent comment
     */
    public List<Comment> getChildComments() {
        return childComments;
    }
}
