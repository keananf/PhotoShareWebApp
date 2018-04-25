package server.objects;

/**
 * Class representing a notification for a user.
 * These are generated implicitly by the server whenever a new comment
 * is registered on a user's photo or comment.
 */
public final class Notification {

    // The id of the new comment or follow event which generated the notification
    private final long contentId;

    // The type of noification this is. This indicates to the client whether or not
    // the reference id is in reference to a photo, follow or comment by the given user.
    private final EventType eventType;

    // Who posted the comment and who is receiving the notification
    private final String author;
    private final String notifiedUser;

    public Notification(long contentId, String notifiedUser,
                        String author, EventType type) {
        this.contentId = contentId;
        this.notifiedUser = notifiedUser;
        this.author = author;
        eventType = type;
    }

    /**
     * @return The id of the new comment which generated the notification
     */
    public long getContentId() {
        return contentId;
    }


    /**
     * @return Who posted the comment
     */
    public String getAuthor() {
        return author;
    }

    /**
     * The type of comment this is. This indicates to the client whether or not
     * the reference id is in reference to a photo or a comment by the given user.
     * @return the type of comment
     */
    public EventType getCommentType() {
        return eventType;
    }

    /**
     * @return the user receiving the notification.
     */
    public String getNotifiedUser() {
        return notifiedUser;
    }
}
