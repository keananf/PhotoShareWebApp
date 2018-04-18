package server.objects;

import server.objects.requests.AddCommentRequest;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a comment on either a photo / another comment
 */
public class Comment {
    private final String postedBy;
    private final long timestamp;
    private String contents;
    private long id;

    // Indicates if this is a reply or not
    // as well as notes the id of the 'parent' photo or comment
    private final long referenceId;
    private final CommentType type;

    private HashMap<String, Boolean> votes;

    public Comment(String postedBy, String contents, long referenceId, CommentType type, long time) {
        // Comment information
        this.postedBy = postedBy;
        this.contents = contents;
        this.timestamp = time;
        votes = new HashMap<>();

        // Reference information
        this.referenceId = referenceId;
        this.type = type;
    }

    public Comment(String postedBy, AddCommentRequest request) {
        this(postedBy, request.getCommentContents(), request.getReferenceId(),
                request.getType(), request.getTimestamp());
    }

    /**
     * @return the name of the user who posted this
     */
    public String getPostedBy() {
        return postedBy;
    }

    /**
     * @return the time this photo was created
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the actual parent
     */
    public String getContents() {
        return contents;
    }

    /**
     * @return the id of this comment.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id of this comment.
     * @param id the new id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the id of the photo / comment this is commenting on / replying to.
     */
    public long getReferenceId() {
        return referenceId;
    }

    /**
     * @return the type of comment (photo comment or reply)
     */
    public CommentType getCommentType() {
        return type;
    }

    /**
     * Replaces the message with "Removed By Admin."
     */
    public void remove() {
        contents = Resources.REMOVAL_STRING;
    }

    /**
     * Registers the persistVote on this comment from the given user
     * @param user the user who made this persistVote
     * @param upvote whether this is an upvote or not
     */
    public void vote(String user, boolean upvote) {
        // Using a map ensures that a user can never persistVote more than once
        votes.put(user, upvote);
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

    /**
     * @param votes the votes made on this comment
     */
    public void setVotes(HashMap<String, Boolean> votes) {
        this.votes = votes;
    }
}
