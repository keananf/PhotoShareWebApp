package client;

import com.google.gson.Gson;
import server.objects.Auth;
import server.objects.CommentType;
import server.requests.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static server.Resources.*;

/**
 * Class representing a client to the restful api.
 */
public final class ApiClient {
    // Connector object facilitating actual objects.common
    private final Connector connector;
    // Gson object serialiser
    private final Gson gson;
    // Registered user
    private String user;
    private int password;
    // Set up client and base web target
    private Client client = ClientBuilder.newClient();
    private WebTarget baseTarget = client.target(BASE_URL);

    public ApiClient() {
        // Instantiate key fields
        this.connector = new Connector();
        this.gson = new Gson();
    }

    /**
     * Adds a new user to the system
     *
     * @param user     the new user to add
     * @param password the new user's pw
     * @return the response of the request.
     */
    public Response addUser(String user, String password) {
        // Convert the user into JSON
        String userJson = gson.toJson(new AddUserRequest(user, password.hashCode()));

        // POST jsonUser to the resource for adding users.
        return connector.postToUrl(baseTarget, ADD_USER_PATH, userJson);
    }

    /**
     * Logs the provided user in and registers it to this client.
     * Mostly for the client, since server is stateless (no notion of session).
     * The server simply checks that the provided auth info is valid
     *
     * @param user     the new user to add
     * @param password the new user's pw
     * @return the response of the request.
     */
    public Response loginUser(String user, String password) {
        // Encode auth information
        String authJson = getSerialisedAuthRequest(LOGIN_USER_PATH, user, password.hashCode());

        // POST the auth information to the log-in API.
        Response response = connector.postToUrl(baseTarget, LOGIN_USER_PATH, authJson);

        // Register user to this client if log-in was successful
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            this.user = user;
            this.password = password.hashCode();
        }

        // Return response
        return response;
    }

    /**
     * Retrieves all users registered on the server
     *
     * @return the response of the request.
     */
    public Response getNotifications() {
        // Encode auth information
        String authJson = getSerialisedAuthRequest(NOTIFICATIONS_PATH, user, password);

        // POST request with auth information
        return connector.postToUrl(baseTarget, NOTIFICATIONS_PATH, authJson);
    }

    /**
     * Retrieves all users registered on the server
     *
     * @return the response of the request.
     */
    public Response getUsers() {
        // Encode auth information
        String authJson = getSerialisedAuthRequest(USERS_PATH, user, password);

        // POST request with auth information
        return connector.postToUrl(baseTarget, USERS_PATH, authJson);
    }

    /**
     * Adds a new album to the system
     *
     * @param albumName the album's name
     * @param description the album's description
     * @param user     the author of the album
     * @return the response of the request.
     */
    public Response addAlbum(String albumName, String description, String user) {
        // Convert the request into JSON
        String albumJSON = gson.toJson(new AddAlbumRequest(getAuth(ADD_ALBUM_PATH, user, password).getAuth(),
                albumName, description));

        // POST jsonUser to the resource for adding users.
        return connector.postToUrl(baseTarget, ADD_ALBUM_PATH, albumJSON);
    }

    /**
     * Encoded and uploads the given file
     *
     * @param photoName     the name of the photo
     * @param albumId the id of the album this photo is to be uploaded to
     * @param photoContents the byte[] representing the photo's contents
     * @return the response of the request.
     */
    public Response uploadPhoto(String photoName, long albumId, byte[] photoContents) {
        // Construct request
        UploadPhotoRequest request = new UploadPhotoRequest(getAuth(UPLOAD_PHOTO_PATH, user, password)
                .getAuth(), photoName, photoContents, albumId);

        // Encode request and POST
        return connector.postToUrl(baseTarget, UPLOAD_PHOTO_PATH, gson.toJson(request));
    }

    /**
     * Encoded and uploads the given file
     *
     * @param id the id of the photo
     * @return the response of the request.
     */
    public Response getPhoto(long id) {
        // Encode request and POST
        String path = String.format("%s/%s", GET_PHOTO_BY_ID_PATH, id);
        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    /**
     * Sends a persistVote request to the server.
     *
     * @param id the id of the comment
     * @return the response of the request.
     * @parm upvote whether or not this is an upvote
     */
    public Response vote(long id, boolean upvote) {
        // Encode request and POST
        String path = String.format("%s/%s", (upvote ? UPVOTE_PATH : DOWNVOTE_PATH), id);
        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    /**
     * Sends a remove request to the server.
     *
     * @param id the id of the comment
     * @return the response of the request.
     */
    public Response removeComment(long id) {
        // Encode request and POST
        String path = String.format("%s/%s", ADMIN_REMOVE_COMMENT_PATH, id);
        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    /**
     * Download all photos from a given user
     *
     * @param name the name of the user to retrieve photos from
     * @return the response of the request
     */
    public Response getAllPhotos(String name) {
        // Encode request and POST
        String path = String.format("%s/%s", GET_USER_PHOTOS_PATH, name);
        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    /**
     * Download all albums from a given user
     *
     * @param name the name of the user to retrieve albums from
     * @return the response of the request
     */
    public Response getAllAlbums(String name) {
        // Encode request and POST
        String path = String.format("%s/%s", GET_USER_ALBUMS_PATH, name);
        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    /**
     * Retrieve the given album
     *
     * @param id the album's id
     * @return the response of the request
     */
    public Response getAlbum(long id) {
        // Encode request and POST
        String path = String.format("%s/%s", GET_ALBUM_BY_ID_PATH, id);
        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    /**
     * Download all comments from a given user
     *
     * @param name the name of the user to retrieve comments from
     * @return the response of the request
     */
    public Response getAllComments(String name) {
        // Encode request and POST
        String path = String.format("%s/%s", COMMENTS_PATH, name);
        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    /**
     * Download all comments from a given user
     *
     * @param id the parent comment's id
     * @return the response of the request
     */
    public Response getAllReplies(long id) {
        // Encode request and POST
        String path = String.format("%s/%s", GET_ALL_REPLIES_PATH, id);
        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    /**
     * Download all comments for a given photo
     *
     * @param id the photo's id
     * @return the response of the request
     */
    public Response getAllPhotoComments(long id) {
        // Encode request and POST
        String path = String.format("%s/%s", GET_ALL_PHOTO_COMMENTS_PATH, id);
        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    /**
     * Requests to add a comment to the photo / comment represented by the given information
     *
     * @param id             the id the photo or comment which this is commenting on
     * @param type           the comment type (reply or photo comment)
     * @param commentContent the comment contents
     * @return the response of the request
     */
    public Response addComment(long id, CommentType type, String commentContent) {
        // Construct request
        AddCommentRequest request = new AddCommentRequest(getAuth(ADD_COMMENT_PATH, user, password).getAuth(),
                commentContent, id, type);

        // Encode request and POST
        return connector.postToUrl(baseTarget, ADD_COMMENT_PATH, gson.toJson(request));
    }

    /**
     * @param endPoint the endPoint being accessed
     * @param user     the user to get auth info for
     * @return the auth information in json
     */
    private AuthRequest getAuth(String endPoint, String user, int password) {
        // Return nothing if this client does not have a registered user
        if (user == null) return new AuthRequest(new Auth("", "", 0));

        // Wrap auth information in a request
        Auth auth = new Auth(endPoint, user, password);
        AuthRequest request = new AuthRequest(auth);

        return request;
    }

    /**
     * @param endPoint the endPoint being accessed
     * @param user     the user to get auth info for
     * @return the auth information in json
     */
    private String getSerialisedAuthRequest(String endPoint, String user, int password) {
        AuthRequest request = getAuth(endPoint, user, password);

        // Encode auth information
        String authJson = gson.toJson(request);
        return authJson;
    }

    /**
     * Follow a specified user
     *
     * @param name the name of the user to follow
     * @return the response of the request
     */
    public Response followUser(String name) {
        // Encode request and POST
        String path = USERS_PATH + FOLLOW;

        FollowUserRequest request = new FollowUserRequest(getAuth(path, user, password)
                .getAuth(), user, name);

        // Encode request and POST
        return connector.postToUrl(baseTarget, path, gson.toJson(request));

    }

    /**
     * Unfollow a specified user
     *
     * @param name the name of the user to follow
     * @return the response of the request
     */
    public Response unfollowUser(String name) {
        // Encode request and POST
        String path = USERS_PATH + UNFOLLOW;

        FollowUserRequest request = new FollowUserRequest(getAuth(path, user, password)
                .getAuth(), user, name);

        // Encode request and POST
        return connector.postToUrl(baseTarget, path, gson.toJson(request));

    }

    public Response getNewsFeed() {
        // Encode request  and POST

        String path = String.format("%s/%s", NEWS_FEED_PATH, user);

        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }

    public Response getFollowing() {
        // Encode request  and POST

        String path = String.format("%s/%s", USERS_PATH , user) + FOLLOWING;

        String authJson = getSerialisedAuthRequest(path, user, password);
        return connector.postToUrl(baseTarget, path, authJson);
    }


    /**
     * Resets the logged-in user
     */
    public void clear() {
        user = null;
    }
}
