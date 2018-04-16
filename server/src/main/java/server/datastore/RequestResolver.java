package server.datastore;

import common.Auth;
import common.CommentType;
import common.requests.AddCommentRequest;
import server.datastore.exceptions.ExistingException;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.*;

import java.util.List;

/**
 * Layer which resolves requests received from a client,
 * and persists changes to the underlying data store
 */
public final class RequestResolver {
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
            serverAuth = new Auth(endPoint, user.getName(), user.getPassword());
        }
        catch (InvalidResourceRequestException ignored) {throw new UnauthorisedException();}

        // Check timestamp isn't too old
        long time = System.nanoTime();
        if(time - auth.getTime() > TIMEOUT) throw new UnauthorisedException();

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
     * @return the new session object to be serialised
     * @throws UnauthorisedException if invalid password presented
     * @throws InvalidResourceRequestException if invalid user presented
     */
    public void loginUser(String endPoint, Auth auth) throws UnauthorisedException, InvalidResourceRequestException {
        // Verify auth information
        verifyAuth(endPoint, auth);
    }

    /**
     * Uploads the given photo
     * @param encodedPhotoContents the base 64 encoded photo contents
     * @param photoName the name of the photo
     * @param user the user who posted the photo
     */
    public Receipt uploadPhoto(String encodedPhotoContents, String photoName, String user) {
        // Create photo object
        Photo newPhoto = new Photo(encodedPhotoContents, user, photoName, CURRENT_ID++, System.nanoTime());

        // Add photo to main photo collection
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
     * Retrieves the given photo
     * @param id the id of the photo
     * @return the photo
     * @throws InvalidResourceRequestException if the photo doesn't exist
     */
    public Photo getPhoto(long id) throws InvalidResourceRequestException {
        return dataStore.getPhoto(id);
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
            getUser(user.getName());
            throw new ExistingException(user.getName());
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
        if(request.getType().equals(CommentType.REPLY)) {
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
        Comment comment = new Comment(user, request);
        comment.setId(CURRENT_ID++);

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
            parentName = getComment(comment.getReferenceId()).getPostedBy();
        }
        else {
            parentName = getPhoto(comment.getReferenceId()).getPostedBy();
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
     * Registers the given persistVote on the given commemt
     * @param commentId the id of the comment to persistVote on
     * @param user the user who cast this persistVote
     * @param upvote whether or not this is an upvote or a downvote
     */
    public void vote(long commentId, String user, boolean upvote) throws InvalidResourceRequestException {
        getUser(user);
        getComment(commentId);

        dataStore.persistVote(commentId, user, upvote);
    }

    public void clear() {
        // Empty records
        dataStore.clear();
    }
}
