package server.requests;

import server.objects.Auth;

/**
 * Request wrapper for following a user
 *
 */

public class FollowUserRequest extends AuthRequest{

    // User who is trying to become a follower
    private final String userFrom;

    // Username of the person whom the user is trying to follow
    private final String userTo;

    public FollowUserRequest(Auth auth, String userFrom, String userTo) {

        super(auth);

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

