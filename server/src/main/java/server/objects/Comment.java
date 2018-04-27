package server.objects;

import server.requests.AddCommentRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a comment on either a photo / another comment
 */
public class Comment implements NotifiableEvent{
    private final String author;
    private final String commentTime;
    private String commentContents;
    private long id;

    // Indicates if this is a reply or not
    // as well as notes the id of the 'parent' photo or comment
    private final long referenceId;
    private final EventType eventType;

    private List<String> votes;

    public Comment(long id, String author, String commentContents, long referenceId, EventType eventType,
                   List<String> commentVotes, String time) {
        // Comment information
        this.author = author;
        this.commentContents = commentContents;
        this.commentTime = time;
        this.id = id;
        this.votes = commentVotes;

        // Reference information
        this.referenceId = referenceId;
        this.eventType = eventType;
    }

    public Comment(long id, String author, AddCommentRequest request, String date) {
        this(id, author, request.getCommentContents(), request.getReferenceId(),
                request.getEventType(), new ArrayList<>(), date);
    }

    /**
     * @return the name of the user who posted this
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return the time this photo was created
     */
    public String getCommentTime() {
        return commentTime;
    }

    /**
     * @return the actual parent
     */
    public String getCommentContents() {
        return commentContents;
    }

    /**
     * @return the id of this comment.
     */
    public long getId() {
        return id;
    }

    /**
     * @return the id of the photo / comment this is commenting on / replying to.
     */

    public long getReferenceId() {

        return referenceId;
    }

    @Override
    public long getContentId() {
        return id;
    }

    /**
     * @return the commentType of comment (photo comment or reply)
     */
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public String getParentName() {
        return author;
    }

    /**
     * @return the users who upvoted  this comment
     */
    public List<String> getLikes() {
        return votes;
    }
}
