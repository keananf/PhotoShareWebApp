package server.objects;

import server.requests.AddCommentRequest;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a comment on either a photo / another comment
 */
public class Comment implements NotifiableEvent{
    private final String author;
    private final long commentTime;
    private String commentContents;
    private long id;

    // Indicates if this is a reply or not
    // as well as notes the id of the 'parent' photo or comment
    private final long referenceId;
    private final EventType eventType;

    private HashMap<String, Boolean> votes;

    public Comment(long id, String author, String commentContents, long referenceId, EventType eventType,
                   HashMap<String, Boolean> commentVotes, long time) {
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

    public Comment(long id, String author, AddCommentRequest request) {
        this(id, author, request.getCommentContents(), request.getReferenceId(),
                request.getEventType(), new HashMap<>(), System.nanoTime());
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
    public long getCommentTime() {
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
    public List<String> getUpvotes() {
        // Filter out all upvotes, and then create a list of the names of the users who cast them
        return votes.entrySet().stream().filter(kv -> kv.getValue())
                .map(kv -> kv.getKey()).collect(Collectors.toList());
    }

    /**
     * @return the users who downvoted  this comment
     */
    public List<String> getDownvotes() {
        // Filter out all downvotes, and then create a list of the names of the users who cast them
        return votes.entrySet().stream().filter(kv -> !kv.getValue())
                .map(kv -> kv.getKey()).collect(Collectors.toList());
    }
}
