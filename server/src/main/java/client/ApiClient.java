package client;

import com.google.gson.Gson;
import server.Auth;
import server.Resources;
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
    private String password;

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
        String userJson = gson.toJson(new AddUserRequest(user, Auth.hashAndEncodeBase64(password)));

        // POST jsonUser to the resource for adding users.
        return connector.post(baseTarget, ADD_USER_PATH, userJson);
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
        // Send the auth information to the log-in API.
        connector.setUserAndPw(user, Auth.hashAndEncodeBase64(password));
        Response response = connector.get(baseTarget, LOGIN_USER_PATH);

        // Register user to this client if log-in was successful
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            this.password = Auth.hashAndEncodeBase64(password);
            this.user = user;
        }
        else {
            // Unset the entered username and password if the request failed
            connector.setUserAndPw("", "");
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
        return connector.get(baseTarget, NOTIFICATIONS_PATH);
    }

    /**
     * Retrieves all users registered on the server
     *
     * @return the response of the request.
     */
    public Response getUsers() {
        return connector.get(baseTarget, USERS_PATH);
    }

    /**
     * Adds a new album to the system
     *
     * @param albumName the album's name
     * @param description the album's description
     * @return the response of the request.
     */
    public Response addAlbum(String albumName, String description) {
        // Convert the request into JSON
        String albumJSON = gson.toJson(new AddAlbumRequest(albumName, description));

        // POST to the resource for adding an album.
        return connector.post(baseTarget, ADD_ALBUM_PATH, albumJSON);
    }

    /**
     * Encoded and uploads the given file
     *
     * @param photoName     the name of the photo
     * @param ext the photo's extension
     * @param description the photo's description
     * @param albumId the id of the album this photo is to be uploaded to
     * @param photoContents the byte[] representing the photo's contents
     * @return the response of the request.
     */
    public Response uploadPhoto(String photoName, String ext, String description, long albumId, byte[] photoContents) {
        // Construct request
        UploadPhotoRequest request = new UploadPhotoRequest(photoName, ext, description, photoContents, albumId);

        // Encode request and POST
        return connector.post(baseTarget, UPLOAD_PHOTO_PATH, gson.toJson(request));
    }

    /**
     * Sends a remove request to the server.
     *
     * @param id the id of the photo
     * @return the response of the request.
     */
    public Response removePhoto(long id) {
        // Encode request and POST
        String path = String.format("%s/%s", DELETE_PHOTO_PATH, id);
        return connector.delete(baseTarget, path);
    }

    /**
     * Retrieves the photo contents from the given photo
     *
     * @param id the id of the photo
     * @return the response of the request.
     */
    public Response getPhotoContents(long id) {
        // Encode path and GET the requested photo
        String path = String.format(PHOTO_CONTENTS_PATH, id);
        return connector.get(baseTarget, path);
    }

    /**
     * Retrieves the photo meta-data for the given photo
     *
     * @param id the id of the photo
     * @return the response of the request.
     */
    public Response getPhotoMetaData(long id) {
        // Encode path and GET the requested photo
        String path = String.format("%s/%s", PHOTOS_PATH, id);
        return connector.get(baseTarget, path);
    }

    /**
     * Sends a comment vote request to the server.
     *
     * @param id the id of the comment
     * @return the response of the request.
     * @parm upvote whether or not this is an upvote
     */
    public Response voteOnComment(long id, boolean upvote) {
        // Encode request and PUT the given vote
        String path = String.format("%s/%s", (upvote ? COMMENT_UPVOTE_PATH : COMMENT_DOWNVOTE_PATH), id);
        return connector.put(baseTarget, path);
    }

    /**
     * Sends a photo rating request to the server.
     *
     * @param id the id of the photo
     * @return the response of the request.
     * @parm upvote whether or not this is an upvote
     */
    public Response ratePhoto(long id, boolean upvote) {
        // Encode request and PUT the given vote
        String path = String.format("%s/%s", (upvote ? PHOTO_UPVOTE_PATH : PHOTO_DOWNVOTE_PATH), id);
        return connector.put(baseTarget, path);
    }

    /**
     * Sends a remove request to the server.
     *
     * @param id the id of the comment
     * @return the response of the request.
     */
    public Response adminRemoveComment(long id) {
        // Encode request and DELETE
        String path = String.format("%s/%s", ADMIN_REMOVE_COMMENT_PATH, id);
        return connector.delete(baseTarget, path);
    }


    /**
     * Sends an update album description request to the server.
     *
     * @param id the id of the album
     * @param description the new description
     * @return the response of the request.
     */
    public Response updateAlbumDescription(long id, String description) {
        // Construct request
        UpdateAlbumDescriptionRequest request = new UpdateAlbumDescriptionRequest(id, description);

        // Encode request and POST
        return connector.post(baseTarget, UPDATE_ALBUM_DESCRIPTION_PATH, gson.toJson(request));
    }

    /**
     * Sends a remove request to the server.
     *
     * @param id the id of the photo
     * @return the response of the request.
     */
    public Response adminRemovePhoto(long id) {
        String path = String.format("%s/%s", ADMIN_REMOVE_PHOTO_PATH, id);
        return connector.delete(baseTarget, path);
    }

    /**
     * Download all photos from a given user
     *
     * @param name the name of the user to retrieve photos from
     * @return the response of the request
     */
    public Response getAllPhotos(String name) {
        String path = String.format(Resources.GET_USER_PHOTOS_PATH, name);
        return connector.get(baseTarget, path);
    }

    /**
     * Download all photos from a given album
     *
     * @param albumId the id of the album to retrieve photos from
     * @return the response of the request
     */
    public Response getAllPhotos(long albumId) {
        String path = String.format("%s/%s", GET_PHOTOS_BY_ALBUM_PATH, albumId);
        return connector.get(baseTarget, path);
    }

    /**
     * Download all albums from a given user
     *
     * @param name the name of the user to retrieve albums from
     * @return the response of the request
     */
    public Response getAllAlbums(String name) {
        String path = String.format("%s/%s", GET_USER_ALBUMS_PATH, name);
        return connector.get(baseTarget, path);
    }

    /**
     * Retrieve the given album
     *
     * @param id the album's id
     * @return the response of the request
     */
    public Response getAlbum(long id) {
        String path = String.format("%s/%s", GET_ALBUM_BY_ID_PATH, id);
        return connector.get(baseTarget, path);
    }

    /**
     * Download all comments from a given user
     *
     * @param name the name of the user to retrieve comments from
     * @return the response of the request
     */
    public Response getAllComments(String name) {
        String path = String.format("%s/%s", COMMENTS_PATH, name);
        return connector.get(baseTarget, path);
    }

    /**
     * Download all comments from a given user
     *
     * @param id the parent comment's id
     * @return the response of the request
     */
    public Response getAllReplies(long id) {
        String path = String.format("%s/%s", GET_ALL_REPLIES_PATH, id);
        return connector.get(baseTarget, path);
    }

    /**
     * Download all comments for a given photo
     *
     * @param id the photo's id
     * @return the response of the request
     */
    public Response getAllPhotoComments(long id) {
        String path = String.format("%s/%s", GET_ALL_PHOTO_COMMENTS_PATH, id);
        return connector.get(baseTarget, path);
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
        AddCommentRequest request = new AddCommentRequest(commentContent, id, type);

        // Encode request and POST
        return connector.post(baseTarget, ADD_COMMENT_PATH, gson.toJson(request));
    }

    public Response removeComment(long id) {
        String path = String.format("%s/%s", DELETE_COMMENT_PATH, id);
        return connector.delete(baseTarget, path);
    }

    /**
     * Requests to edit a comment represented by the given information
     *
     * @param id                the id of the comment being edited
     * @param commentContent    the new comment contents
     * @return the response of the request
     */
    public Response editComment(long id, String commentContent) {
        // Construct request
        String path = String.format("%s/%s", EDIT_COMMENT_PATH, id);
        EditCommentRequest request = new EditCommentRequest(commentContent);

        // Encode request and POST
        return connector.post(baseTarget, path, gson.toJson(request));
    }

    /**
     * Follow a specified user
     *
     * @param name the name of the user to follow
     * @return the response of the request
     */
    public Response followUser(String name) {
        String path = String.format("%s/%s", FOLLOW_USERS_PATH, name);
        return connector.put(baseTarget, path);

    }

    /**
     * Unfollow a specified user
     *
     * @param name the name of the user to follow
     * @return the response of the request
     */
    public Response unfollowUser(String name) {
        String path = String.format("%s/%s", UNFOLLOW_USERS_PATH, name);
        return connector.delete(baseTarget, path);
    }

    public Response getNewsFeed() {
        String path = String.format("%s/%s", NEWS_FEED_PATH, user);
        return connector.get(baseTarget, path);
    }

    public Response getFollowing() {
        String path = String.format("%s/%s", USERS_FOLLOWING_PATH , user);
        return connector.get(baseTarget, path);
    }

    public Response getFollowers() {
        String path = String.format("%s/%s", USERS_FOLLOWERS_PATH , user);
        return connector.get(baseTarget, path);
    }


    /**
     * Resets the logged-in user
     */
    public void clear() {
        user = null;
    }
}
