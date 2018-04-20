package server.objects;

import server.Resources;
import server.requests.AddCommentRequest;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a comment on either a photo / another comment
 */
public class Comment {
    private final String author;
    private final long commentTime;
    private String commentContents;
    private long id;

    // Indicates if this is a reply or not
    // as well as notes the id of the 'parent' photo or comment
    private final long referenceId;
    private final CommentType commentType;

    private HashMap<String, Boolean> votes;

    public Comment(String author, String commentContents, long referenceId, CommentType commentType, long time) {
        // Comment information
        this.author = author;
        this.commentContents = commentContents;
        this.commentTime = time;
        votes = new HashMap<>();

        // Reference information
        this.referenceId = referenceId;
        this.commentType = commentType;
    }

    public Comment(String author, AddCommentRequest request) {
        this(author, request.getCommentContents(), request.getReferenceId(),
                request.getCommentType(), System.nanoTime());
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
     * @return the commentType of comment (photo comment or reply)
     */
    public CommentType getCommentType() {
        return commentType;
    }

    /**
     * Replaces the message with "Removed By Admin."
     */
    public void remove() {
        commentContents = Resources.REMOVAL_STRING;
    }

    /**
     * Registers the persistCommentVote on this comment from the given user
     * @param user the user who made this persistCommentVote
     * @param upvote whether this is an upvote or not
     */
    public void vote(String user, boolean upvote) {
        // Using a map ensures that a user can never persistCommentVote more than once
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
