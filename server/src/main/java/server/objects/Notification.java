package server.objects;

/**
 * Class representing a notification for a user.
 * These are generated implicitly by the server whenever a new comment
 * is registered on a user's photo or comment.
 */
public final class Notification {
    // The id of either the comment or photo which was commented on.
    private final long referenceId;

    // The id of the new comment which generated the notification
    private final long commentId;

    // The type of comment this is. This indicates to the client whether or not
    // the reference id is in reference to a photo or a comment by the given user.
    private final CommentType commentType;

    // Who posted the comment and who is receiving the notification
    private final String commentAuthor;
    private final String notifiedUser;

    public Notification(Comment comment, String notifiedUser) {
        this.referenceId = comment.getReferenceId();
        this.commentId = comment.getId();
        this.commentType = comment.getCommentType();
        this.commentAuthor = comment.getAuthor();
        this.notifiedUser = notifiedUser;
    }

    public Notification(long commentId, long referenceId, String notifiedUser,
                        String commentAuthor, CommentType type) {
        this.commentId = commentId;
        this.referenceId = referenceId;
        this.notifiedUser = notifiedUser;
        this.commentAuthor = commentAuthor;
        commentType = type;
    }

    /**
     * @return The id of the new comment which generated the notification
     */
    public long getCommentId() {
        return commentId;
    }

    /**
     * @return  The id of either the comment or photo which was commented on.
     */
    public long getReferenceId() {
        return referenceId;
    }

    /**
     * @return Who posted the comment
     */
    public String getCommentAuthor() {
        return commentAuthor;
    }

    /**
     * The type of comment this is. This indicates to the client whether or not
     * the reference id is in reference to a photo or a comment by the given user.
     * @return the type of comment
     */
    public CommentType getCommentType() {
        return commentType;
    }

    /**
     * @return the user receiving the notification.
     */
    public String getNotifiedUser() {
        return notifiedUser;
    }
}
