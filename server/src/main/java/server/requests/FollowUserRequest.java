package server.requests;

/**
 * Request wrapper for following a user
 *
 */

public class FollowUserRequest {

    // User who is trying to become a follower
    private final String userFrom;

    // Username of the person whom the user is trying to follow
    private final String userTo;

    public FollowUserRequest(String userFrom, String userTo) {

        this.userFrom = userFrom;
        this.userTo = userTo;
    }

    /**
     * @return the userFrom
     */
    public String getUserFrom() {
        return userFrom;
    }

    /**
     * @return the user userTo
     */
    public String getUserTo() {
        return userTo;
    }
}

