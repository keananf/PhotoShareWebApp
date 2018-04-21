package server.datastore;

import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.*;

import java.util.List;

/**
 * Interface defining a data storage mechanism.
 * Options are either a database-backed implementation or an
 * in-memory implementation.
 */
interface DataStore {

    /**
     * Uploads the given photo
     * @param newPhoto the photo
     */
    void persistUploadPhoto(Photo newPhoto);

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
    Photo getPhoto(long id) throws InvalidResourceRequestException;


    /**
     * Adds the given album
     * @param album the new album to add
     */
    void persistAddAlbum(Album album) throws InvalidResourceRequestException;

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
     * @param user the new user to add
     */
    void persistAddUser(User user);

    /**
     * Retrieves all comments a user has made.
     * Relevant notifications will be implicitly removed, as well.
     * @param username the user to retrieve comments for
     * @return the list of comments by this user
     */
    List<Comment> getComments(String username);

    /**
     * Retrieves all top-level comments made on the comment / photo with the given reference id
     * Relevant notifications will be implicitly removed, as well.
     * @param user the user who made the request
     * @param referenceId the id of the resource
     * @return the list of comments on this resource
     */
    List<Comment> getPhotoComments(String user, long referenceId);

    /**
     * Retrieves all top-level comments made on the comment / photo with the given reference id
     * Relevant notifications will be implicitly removed, as well.
     * @param user the user who made the request
     * @param referenceId the id of the resource
     * @return the list of comments on this resource
     */
    List<Comment> getReplies(String user, long referenceId);

    /**
     * Retrieves all notifications for the given user
     * @param user the user to retrieve comments for
     * @return the list of notifications for this user
     */
    List<Notification> getNotifications(String user);

    /**
     * Adds the given comment
     * @param comment the comment
     *
     */
    void persistAddComment(Comment comment);

    /**
     * Internally used. Adds a notification on the photo / comment which the given comment
     * commented on
     * @param parentName the name of the user who posted the original photo / comment
     * @param comment the new comment
     */
    void persistAddNotification(String parentName, Comment comment);

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
    void persistRemoveComment(long commentId) throws InvalidResourceRequestException;

    /**
     * Registers the given persistVote on the given commemt
     * @param commentId the id of the comment to persistVote on
     * @param user the user who cast this persistVote
     * @param upvote whether or not this is an upvote or a downvote
     */
    void persistVote(long commentId, String user, boolean upvote) throws InvalidResourceRequestException;

    /**
     * Attempts to follow the person a user has specified
     *
     * @param userFrom - the username of the user from whom the follow request comes
     * @param userTo - the username of the person the user is trying to follow
     *
     */

    void persistFollowing(String userFrom, String userTo);

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
     * Retrieve a list of the Persons (Users) a user followss
     *
     * @param username - username of the user trying to find out who their followers are
     * @return
     */
    List<User> getFollowing(String username)  throws InvalidResourceRequestException;

    /**
     * Empties the data store
     *
     */
    void clear();
}
