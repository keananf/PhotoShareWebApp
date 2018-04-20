package server.datastore;

import server.datastore.exceptions.DoesNotOwnAlbumException;
import server.datastore.exceptions.ExistingException;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.*;
import server.requests.AddCommentRequest;
import server.requests.UploadPhotoRequest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Layer which resolves requests received from a client,
 * and persists changes to the underlying data store
 */
public final class RequestResolver {
    public static boolean DEBUG = false;
    private static final long TIMEOUT = (long) (10 * (Math.pow(10, 9)));
    private int CURRENT_ID = 0;
    private DataStore dataStore = new DatabaseBackedDataStore();

    /**
     * Verify the auth info sent by the client. Try to generate shared secret.
     * @param endPoint the api being accessed.
     * @param auth the provided auth info with a request
     * @throws UnauthorisedException if bad provided info
     */
    public void verifyAuth(String endPoint, Auth auth) throws UnauthorisedException {
        // Retrieve user from server
        Auth serverAuth;
        try {
            User user = getUser(auth.getUser());
            serverAuth = new Auth(endPoint, user.getUsername(), user.getPassword());
        }
        catch (InvalidResourceRequestException ignored) {throw new UnauthorisedException();}

        // Check timestamp isn't too old
        long time = System.nanoTime();
        if(!DEBUG && time - auth.getTime() > TIMEOUT) throw new UnauthorisedException();

        // Compare generated secret API key
        String key = serverAuth.getApiKey(endPoint, auth.getTime());
        if(!key.equals(auth.getApiKey())) throw new UnauthorisedException();
    }

    /**
     * Verify the admin auth info sent by the client. Try to generate shared secret.
     * @param endPoint the api being accessed.
     * @param auth the provided auth info with a request
     * @throws UnauthorisedException if bad provided info
     */
    public void verifyAdminAuth(String endPoint, Auth auth) throws UnauthorisedException,
            InvalidResourceRequestException {
        // Ensure valid user and client
        verifyAuth(endPoint, auth);

        // Ensure user is an admin
        if(!getUser(auth.getUser()).isAdmin()) throw new UnauthorisedException();
    }

    /**
     * Logs in the provided auth
     * @param endPoint the api being accessed.
     * @param auth the auth info corresponding to the user to login
     * @throws UnauthorisedException if invalid password presented
     * @throws InvalidResourceRequestException if invalid user presented
     */
    public void loginUser(String endPoint, Auth auth) throws UnauthorisedException, InvalidResourceRequestException {
        // Verify auth information
        verifyAuth(endPoint, auth);
    }

    /**
     * Uploads the given photo
     *
     * @param user the user who posted the photo
     * @param request the upload photo request
     */
    public Receipt uploadPhoto(String user, UploadPhotoRequest request)
            throws InvalidResourceRequestException, DoesNotOwnAlbumException {
        // Ensure user is known
        getUser(user);

        // Ensure albumId is known, and that it belongs to the user
        Album album = getAlbum(request.getAlbumId());
        if(!album.getAuthorName().equals(user)) throw new DoesNotOwnAlbumException(request.getAlbumId(), user);

        // Create photo and persist it
        Photo newPhoto = new Photo(CURRENT_ID++, user, request);
        dataStore.persistUploadPhoto(newPhoto);

        // Return receipt confirming photo was created
        return new Receipt(newPhoto.getId());
    }

    /**
     * Retrieves photos a user has posted.
     * @param user the user's name
     * @return the list of photos this user has posted
     * @throws InvalidResourceRequestException if user has never posted a photo
     */
    public List<Photo> getPhotos(String user) throws InvalidResourceRequestException {
        // Ensure user exists
        getUser(user);

        return dataStore.getPhotos(user);
    }

    /**
     * Retrieves photos from a given album.
     * @param albumId the album's ID
     * @return the list of photos in this album
     * @throws InvalidResourceRequestException if the given album ID is unknown
     */
    public List<Photo> getPhotos(long albumId) throws InvalidResourceRequestException {
        // Ensure album exists
        getAlbum(albumId);

        return dataStore.getPhotos(albumId);
    }

    /**
     * Retrieves the given photo
     * @param id the id of the photo
     * @return the photo
     * @throws InvalidResourceRequestException if the photo doesn't exist
     */
    public Photo getPhoto(long id) throws InvalidResourceRequestException {
        return dataStore.getPhoto(id);
    }

    /**
     * Creates and persists the newly created album
     * @param author the author of the new album
     * @param albumName the name of the new album
     * @param description the description of the new album
     */
    public Receipt addAlbum(String author, String albumName, String description)
            throws InvalidResourceRequestException {
        // Ensure user is known
        getUser(author);

        // Create photo and persist it
        Album newAlbum = new Album(CURRENT_ID++, albumName, author, description, System.nanoTime());
        dataStore.persistAddAlbum(newAlbum);

        // Return receipt confirming photo was created
        return new Receipt(newAlbum.getAlbumId());
    }

    /**
     * Retrieves the album associated with the given id.
     * @param albumId the id of the album to retrieve
     * @return the album with the given id
     * @throws InvalidResourceRequestException if the id does not correspond to an album
     */
    public Album getAlbum(long albumId) throws InvalidResourceRequestException {
        return dataStore.getAlbum(albumId);
    }

    /**
     * Retrieves all albums a user has made.
     * @param user the user's name
     * @return the list of albums this user has made
     */
    public List<Album> getAlbums(String user) throws InvalidResourceRequestException {
        // Ensure user exists
        getUser(user);

        return dataStore.getAlbums(user);
    }

    /**
     * Retrieves the given comment
     * @param id the id of the comment
     * @return the comment
     * @throws InvalidResourceRequestException if the comment doesn't exist
     */
    public Comment getComment(long id) throws InvalidResourceRequestException {
        return dataStore.getComment(id);
    }

    /**
     * @return a list of all users in the data store
     */
    public List<User> getUsers() {
        return dataStore.getUsers();
    }

    /**
     * @param username the user's name
     * @return the given user, if present
     * @throws InvalidResourceRequestException if invalid user presented
     */
    private User getUser(String username) throws InvalidResourceRequestException {
        return dataStore.getUser(username);
    }

    /**
     * Adds a new user to the data store
     *
     * @param user the new user to add
     * @throws ExistingException if the user already exists
     */
    public void addUser(User user) throws ExistingException {
        // Ensure this user doesn't exist
        try {
            // If exception is NOT thrown, then user exists
            getUser(user.getUsername());
            throw new ExistingException(user.getUsername());
        }
        catch (InvalidResourceRequestException e) {}

        // If first user to be added, make user admin by default. Admins can appoint other
        // admins from there.
        if(getUsers().size() == 0) user.setAdmin(true);

        // Persist user
        dataStore.persistAddUser(user);
    }

    /**
     * Retrieves all comments a user has made.
     * Relevant notifications will be implicitly removed, as well.
     * @param username the user to retrieve comments for
     * @return the list of comments by this user
     * @throws InvalidResourceRequestException if the user has no comments
     */
    public List<Comment> getComments(String username) throws InvalidResourceRequestException {
        // Ensure the user exists
        getUser(username);

        // Get user comments
        List<Comment> comments = dataStore.getComments(username);

        // Remove any notifications for these comments that may exist for this user
        for (Comment reply : comments) {
            removeNotification(username, reply.getId());
        }

        // Return comments;
        return comments;
    }
    /**
     * Retrieves all top-level comments made on the comment / photo with the given reference id
     * Relevant notifications will be implicitly removed, as well.
     * @param user the user who made the request
     * @param referenceId the id of the resource
     * @return the list of comments on this resource
     * @throws InvalidResourceRequestException if the reference doesn't exist
     */
    public List<Comment> getPhotoComments(String user, long referenceId) throws InvalidResourceRequestException {
        // Get photo the reference is referring to (exception thrown if doesn't exist)
        getPhoto(referenceId);
        getUser(user);

        // Find all comments on this photo
        List<Comment> photoComments = dataStore.getPhotoComments(user, referenceId);

        // Remove any notifications for these comments that may exist for this user
        for (Comment comment : photoComments) {
            removeNotification(user, comment.getId());
        }

        // Return all photoComments
        return photoComments;
    }

    /**
     * Retrieves all top-level comments made on the comment / photo with the given reference id
     * Relevant notifications will be implicitly removed, as well.
     * @param user the user who made the request
     * @param referenceId the id of the resource
     * @return the list of comments on this resource
     * @throws InvalidResourceRequestException if the reference doesn't exist
     */
    public List<Comment> getReplies(String user, long referenceId) throws InvalidResourceRequestException {
        // Get comment the reference is referring to (exception thrown if doesn't exist)
        getComment(referenceId);
        getUser(user);

        // Find all comments on this comment
        List<Comment> replies = dataStore.getReplies(user, referenceId);

        // Remove any notifications for these comments that may exist for this user
        for (Comment reply : replies) {
            removeNotification(user, reply.getId());
        }

        // Return all replies
        return replies;
    }

    /**
     * Retrieves all notifications for the given user
     * @param user the user to retrieve comments for
     * @return the list of notifications for this user
     * @throws InvalidResourceRequestException if the user doesn't exist
     */
    public List<Notification> getNotifications(String user) throws InvalidResourceRequestException {
        // Ensure user exists
        getUser(user);

        return dataStore.getNotifications(user);
    }

    /**
     * Adds the given comment
     * @param user the user who posted it
     * @param request the request for a new comment
     * @throws InvalidResourceRequestException if the parent of the comment doesn't exist
     */
    public Receipt addComment(String user, AddCommentRequest request) throws InvalidResourceRequestException {
        // Check comment type
        if(request.getCommentType().equals(CommentType.REPLY)) {
            // Retrieve the parent comment and check it exists
            // (exception will be thrown, if not).
            getComment(request.getReferenceId());
        }
        else {
            // Retrieve the parent photo and check it exists
            // (exception will be thrown, if not).
            getPhoto(request.getReferenceId());
        }

        // Add unique id to be able to future identify this comment
        Comment comment = new Comment(CURRENT_ID++, user, request);

        // Persist comment to data store
        dataStore.persistAddComment(comment);

        // Create notification for this comment to the appropriate user
        addNotification(comment);

        // Return a receipt
        return new Receipt(comment.getId());
    }

    /**
     * Adds a notification on the photo / comment which the given comment
     * commented on
     * @param comment the new comment
     */
    private void addNotification(Comment comment) throws InvalidResourceRequestException {
        String parentName = "";

        // Get parent reference based on comment type
        if(comment.getCommentType().equals(CommentType.REPLY)) {
            parentName = getComment(comment.getReferenceId()).getAuthor();
        }
        else {
            parentName = getPhoto(comment.getReferenceId()).getAuthorName();
        }

        // Add notification using found parent's name
        dataStore.persistAddNotification(parentName, comment);
    }

    /**
     * Removes the given notification, if it exists
     * @param user the user to remove the notification from
     * @param id the id of the notification to remove
     * @throws InvalidResourceRequestException if the id doesn't correspond to a valid notification
     */
    private void removeNotification(String user, long id) throws InvalidResourceRequestException {
        // Retrieve notification and remove it, if present.
        dataStore.persistRemoveNotification(user, id);
    }

    /**
     * Removes the given comment
     * @param commentId the given commentId
     * @throws InvalidResourceRequestException if the id doesn't correspond to a valid comment
     */
    public void removeComment(long commentId) throws InvalidResourceRequestException {
        // Simply overwrites comment with "Removed By Admin"
        dataStore.persistRemoveComment(commentId);
    }

    /**
     * Registers the given comment voteOnComment
     * @param commentId the id of the comment to voteOnComment on
     * @param user the user who cast this voteOnComment
     * @param upvote whether or not this is an upvote or a downvote
     */
    public void voteOnComment(long commentId, String user, boolean upvote) throws InvalidResourceRequestException {
        getUser(user);
        getComment(commentId);

        dataStore.persistCommentVote(commentId, user, upvote);
    }

    /**
     * Registers the given photo rating
     * @param photoId the id of the comment to voteOnComment on
     * @param user the user who cast this voteOnComment
     * @param upvote whether or not this is an upvote or a downvote
     */
    public void ratePhoto(long photoId, String user, boolean upvote) throws InvalidResourceRequestException {
        getUser(user);
        getPhoto(photoId);

        dataStore.persistPhotoRating(photoId, user, upvote);
    }

    /**
     * Attempts tp a user to follow the person a user has specified
     *
     * @param userFrom - the username of the user from whom the follow request comes
     * @param userTo - the username of the person the user is trying to follow
     * @throws InvalidResourceRequestException
     * @throws ExistingException
     */

    public void followUser(String userFrom, String userTo) throws InvalidResourceRequestException, ExistingException{

        // Check the user to follow exists

        try {
            getUser(userTo);
        }catch (InvalidResourceRequestException e){
            throw e;
        }

        // Check the user is not already following the userToFollow

        List<String> followers_usernames = getUsernamesOfFollowers(userTo);

        if (followers_usernames.contains(userFrom)){

            throw new ExistingException("You are already following " + userTo);

        }

        dataStore.persistFollowing(userFrom, userTo);

    }

    /**
     * Attempts to a user to follow the person a user has specified
     *
     * @param userFrom - the username of the user from whom the follow request comes
     * @param userTo - the username of the person the user is trying to follow
     * @throws InvalidResourceRequestException
     */

    public void unfollowUser(String userFrom, String userTo) throws InvalidResourceRequestException{

        // Check the followed user exists

        try {
            getUser(userTo);
        }catch (InvalidResourceRequestException e){
            throw e;
        }

        // usernames of followers
        List<String> followers_usernames = getUsernamesOfFollowers(userTo);


        // Check if the user is following the subject of deletion
        if (followers_usernames.contains(userFrom)){

            // Deletion is possible
            dataStore.persistDeleteFollowing(userFrom, userTo);

        }else{

            // Deletion of the following is not possible because it does not exist
            throw new InvalidResourceRequestException(userTo);
        }

    }


    /**
     * Utility to get the Persons (Users) a user is followed by
     *
     * @param username - username of the user trying to find out who their followers are
     * @return
     */
    private List<User> getFollowers(String username){

        List<User> followers = dataStore.getFollowers(username);
        return followers;
    }

    /**
     * Utility method to get the usernames of the persons by whom a user
     *
     * @param username - username of the user trying to find out who their followers are
     * @return
     */

    private List<String> getUsernamesOfFollowers(String username){

        List<User> followers = dataStore.getFollowers(username);
        List<String> followers_usernames = followers.stream()
                .map(object -> Objects.toString(object.getUsername(), null))
                .collect(Collectors.toList());

        return followers_usernames;
    }

    public void clear() {
        // Empty records
        dataStore.clear();
    }
}
