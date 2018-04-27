package server.datastore;

import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.*;
import server.requests.AddCommentRequest;
import server.requests.UploadPhotoRequest;

import java.util.List;

/**
 * Interface defining a data storage mechanism.
 * Options are either a database-backed implementation or an
 * in-memory implementation.
 */
interface DataStore {

    /**
     * Uploads the given photo
     * @param author the user who posted the photo
     * @param request the photo request
     * @param date the formatted date string
     * @return the newly generated id
     */
    long persistUploadPhoto(String author, UploadPhotoRequest request, String date);

    /**
     * Retrieves photos a user has posted.
     * @param user the user's name
     * @return the list of photos this user has posted
     */
    List<Photo> getPhotos(String user);

    /**
     * Retrieves photos from a given album.
     * @param albumId the album's ID
     * @return the list of photos in this album
     * @throws InvalidResourceRequestException if the given album ID is unknown
     */
    List<Photo> getPhotos(long albumId);

    /**
     * Retrieves the given photo
     * @param id the id of the photo
     * @return the photo
     * @throws InvalidResourceRequestException if the photo doesn't exist
     */
    Photo getPhotoMetaData(long id) throws InvalidResourceRequestException;

    /**
     * Retrieves the photo contents of given photo
     * @param id the id of the photo
     * @param ext the provided file extension
     * @return the photo contents
     * @throws InvalidResourceRequestException if the photo doesn't exist
     */
    String getPhotoContents(long id, String ext) throws InvalidResourceRequestException;

    /**
     * Creates and persists the newly created album
     * @param albumName the name of the new album
     * @param author the author of the new album
     * @param description the description of the new album
     * @param date the formatted date in which the request was sent
     * @return the newly generated id
     */
    long persistAddAlbum(String albumName, String author, String description, String date);

    /**
     * Retreives the album associated with the given id.
     * @param albumId the id of the album to retrieve
     * @return the album with the given id
     * @throws InvalidResourceRequestException if the id does not correspond to an album
     */
    Album getAlbum(long albumId) throws InvalidResourceRequestException;

    /**
     * Retrieves all albums a user has made.
     * @param user the user's name
     * @return the list of albums this user has made
     */
    List<Album> getAlbums(String user);

    /**
     * Updates an album's description.
     * @param albumId the album's id
     * @param description the new description
     */
    void updateAlbumDescription(long albumId, String description) throws InvalidResourceRequestException;

    /**
     * Updates a photo's description.
     * @param photoId the photo's id
     * @param description the new description
     * @throws InvalidResourceRequestException
     */
    void updatePhotoDescription(long photoId, String description) throws InvalidResourceRequestException;

    /**
     * Retrieves the given comment
     * @param id the id of the comment
     * @return the comment
     * @throws InvalidResourceRequestException if the comment doesn't exist
     */
    Comment getComment(long id) throws InvalidResourceRequestException;

    /**
     * @return a list of all users in the data store
     */
    List<User> getUsers();

    /**
     * @param name the user's name
     * @return the given user, if present
     * @throws InvalidResourceRequestException if the user doesn't exist
     */
    User getUser(String name) throws InvalidResourceRequestException;

    /**
     * Adds a new user to the data store
     *
     * @param username the user who sent the request
     * @param password the hashed, base64-encoded password
     * @param admin whether or not this user is an admin.
     */
    void persistAddUser(String username, String password, boolean admin);

    /**
     * Retrieves all comments a user has made.
     * Relevant notifications will be implicitly removed, as well.
     * @param username the user to retrieve comments for
     * @return the list of comments by this user
     */
    List<Comment> getComments(String username);

    /**
     * Retrieves all top-level comments made on the comment / photo with the given reference id
     * @param referenceId the id of the resource
     * @return the list of comments on this resource
     */
    List<Comment> getPhotoComments(long referenceId);

    /**
     * Retrieves all top-level comments made on the comment / photo with the given reference id
     * @param referenceId the id of the resource
     * @return the list of comments on this resource
     */
    List<Comment> getReplies(long referenceId);

    /**
     * Retrieves all notifications for the given user
     * @param user the user to retrieve comments for
     * @return the list of notifications for this user
     */
    List<Notification> getNotifications(String user);

    /**
     * Adds the given comment
     * @param user the user who posted it
     * @param request the request for a new comment
     * @param date the formatted date in which the request was sent
     */
    long persistAddComment(String user, AddCommentRequest request, String date);

    /**
     * Edits the given comment
     * @param commentId the comment Id
     * @param content the new comment content
     *
     */
    void persistEditComment(long commentId, String content);

    /**
     * Internally used. Adds a notification on the photo / comment which the given comment
     * commented on
     * @param parentName the name of the user who posted the original photo / comment
     * @param event the event to be the subject of the notification
     */
    void persistAddNotification(String parentName, NotifiableEvent event);

    /**
     * Removes the given notification, if it exists
     * @param user the user to remove the notification from
     * @param id the id of the notification to remove
     */
    void persistRemoveNotification(String user, long id);

    /**
     * Removes the given comment
     * @param commentId the given commentId
     * @throws InvalidResourceRequestException if the id doesn't correspond to a valid comment
     */
    void persistRemoveComment(long commentId);

    /**
     * Removes the given photo
     * @param photoId the given photoId
     * @throws InvalidResourceRequestException if the id doesn't correspond to a valid photo
     */
    void persistRemovePhoto(long photoId);

    /**
     * Registers the given vote on the given comment
     * @param commentId the id of the comment to vote on
     * @param user the user who cast this vote
     * @param upvote whether or not this is an upvote or a downvote
     */
    void persistCommentVote(long commentId, String user, boolean upvote) throws InvalidResourceRequestException;

    /**
     * Registers the given persistCommentVote on the given commemt
     * @param commentId the id of the comment to persistCommentVote on
     * @param user the user who cast this persistCommentVote
     * @param upvote whether or not this is an upvote or a downvote
     */
    void persistPhotoRating(long commentId, String user, boolean upvote) throws InvalidResourceRequestException;


    /**
     * Attempts to follow the person a user has specified
     *  @param userFrom - the username of the user from whom the follow request comes
     * @param userTo - the username of the person the user is trying to follow
     *
     */

    long persistFollowing(String userFrom, String userTo);

    /**
     * Retrieve a list of the Persons (Users) a user is followed by
     *
     * @param username - username of the user trying to find out who their followers are
     * @return
     */
    List<User> getFollowers(String username)  throws InvalidResourceRequestException;

    /**
     * Attempts to unfollow the person a user has specified
     *
     * @param userFrom - the username of the user from whom the follow request comes
     * @param userTo - the username of the person the user is trying to follow
     *
     */
    void persistDeleteFollowing(String userFrom, String userTo);

    /**
     * Retrieve a list of the Persons (Users) a user follows
     *
     * @param username - username of the user trying to find out who their followers are
     * @return
     */
    List<User> getFollowing(String username)  throws InvalidResourceRequestException;

    /**
     * Search for users whose name contains query
     *
     * @param name search query on the user's name
     * @return the given users, who have names like the query
     */

    List<User> getUserWithNameBegining(String name);

    /**
     * Empties the data store
     *
     */

    void clear();
}
