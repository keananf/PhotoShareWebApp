package server.datastore;

import server.Auth;
import server.datastore.exceptions.*;
import server.objects.*;
import server.requests.AddCommentRequest;
import server.requests.EditCommentRequest;
import server.requests.UploadPhotoRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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

    // Allowed extensions
    private static Set<String> allowedExtensions = new HashSet<>();
    static {
        allowedExtensions.add("jpg");
        allowedExtensions.add("png");
    }

    /**
     * Verify the auth info sent by the client. Try to generate shared secret.
     * @param endPoint the api being accessed.
     * @param username the user who sent the request
     * @param apiKey the apiKey the user provided with the login request
     * @param date the timestamp of the sent request
     * @throws UnauthorisedException if bad provided info
     */
    public void verifyAuth(String endPoint, String username, String apiKey, String date) throws UnauthorisedException {
        // Check user exists on server
        try {
            User user = getUser(username);

            // Check timestamp isn't too old, provided not debug mode
            Date d = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(date);
            long time = System.currentTimeMillis(), dateTime = d.getTime();
            if(!DEBUG && time - dateTime > TIMEOUT) throw new UnauthorisedException();

            // Compare generated secret API key
            String key = Auth.getApiKey(endPoint, username, user.getPassword(), dateTime).split(":")[1];
            if(!key.equals(apiKey)) throw new UnauthorisedException();
        }
        catch (InvalidResourceRequestException | ParseException ignored) {throw new UnauthorisedException();}
    }

    /**
     * Verify the admin auth info sent by the client. Try to generate shared secret.
     * @param endPoint the api being accessed.
     * @param user the user who sent the request
     * @param apiKey the apiKey the user provided with the login request
     * @param date the timestamp of the sent request
     * @throws UnauthorisedException if bad provided info
     */
    public void verifyAdminAuth(String endPoint, String user, String apiKey, String date) throws UnauthorisedException,
            InvalidResourceRequestException {
        // Ensure valid user and client
        verifyAuth(endPoint, user, apiKey, date);

        // Ensure user is an admin
        if(!getUser(user).isAdmin()) throw new UnauthorisedException();
    }

    /**
     * Adds a new user to the data store
     *
     * @param username the user who sent the request
     * @param password the sent plaintext password
     * @throws ExistingException if the user already exists
     */
    public void addUser(String username, String password) throws ExistingException {
        // Ensure this user doesn't exist
        try {
            // If exception is NOT thrown, then user exists
            getUser(username);
            throw new ExistingException(username);
        }
        catch (InvalidResourceRequestException e) {}

        // If first user to be added, make user admin by default.
        boolean admin = (getUsers().size() == 0);

        // Persist user with hashed and encoded password
        dataStore.persistAddUser(username, Auth.hashAndEncodeBase64(password), admin);
    }

    /**
     * Logs in the provided auth
     * @param username the user who sent the request
     * @param password the sent plaintext password
     * @throws UnauthorisedException if invalid password presented
     * @throws InvalidResourceRequestException if invalid user presented
     */
    public LoginResult loginUser(String username, String password)
            throws UnauthorisedException, InvalidResourceRequestException {
        // Ensure user exists
        User user = getUser(username);

        // Check the stored, hashed password with the hash of the sent password.
        // If they don't match, then the request is unauthorised.
        if(!user.getPassword().equals(Auth.hashAndEncodeBase64(password))) throw new UnauthorisedException();

        // Return user information to client
        return new LoginResult(user.getUsername(), user.getPassword(), user.isAdmin());
    }

    /**
     * Uploads the given photo
     *
     * @param user the user who posted the photo
     * @param request the upload photo request
     */
    public Receipt uploadPhoto(String user, UploadPhotoRequest request)
            throws InvalidResourceRequestException, DoesNotOwnAlbumException, InvalidFileTypeException {
        // Ensure user is known
        getUser(user);

        // Ensure albumId is known, and that it belongs to the user
        Album album = getAlbum(request.getAlbumId());
        if(!album.getAuthorName().equals(user)) {
            throw new DoesNotOwnAlbumException(request.getAlbumId(), user);
        }

        // Ensure photo extension is permitted
        if(!allowedExtensions.contains(request.getExt().toLowerCase())) {
            throw new InvalidFileTypeException(request.getExt().toLowerCase());
        }

        // Create photo and persist it
        long id = CURRENT_ID++;
        dataStore.persistUploadPhoto(id, user, request);

        // Return receipt confirming photo was created
        return new Receipt(id);
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
     * Retrieves the photo contents for given photo
     * @param id the id of the photo
     * @param ext the provided file extension
     * @return the encoded photo contents
     * @throws InvalidResourceRequestException if the photo doesn't exist
     */
    public String getPhotoContents(long id, String ext) throws InvalidResourceRequestException {
        return dataStore.getPhotoContents(id, ext);
    }

    /**
     * Retrieves the given photo
     * @param id the id of the photo
     * @return the photo
     * @throws InvalidResourceRequestException if the photo doesn't exist
     */
    public Photo getPhotoMetaData(long id) throws InvalidResourceRequestException {
        return dataStore.getPhotoMetaData(id);
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
     * Updates an album's description.
     * @param user the user who submitted the request
     * @param albumId the album's id
     * @param description the new description
     */
    public void updateAlbumDescription(String user, long albumId, String description)
            throws InvalidResourceRequestException, DoesNotOwnAlbumException {
        // Ensure album exists, and that the user who made the request owns it.
        Album album = getAlbum(albumId);
        if(!album.getAuthorName().equals(user)) throw new DoesNotOwnAlbumException(albumId, user);

        // Persist description update
        dataStore.updateAlbumDescription(albumId, description);
    }

    /**
     * Updates a photo's description.
     * @param user the user who submitted the request
     * @param photoId the photo's id
     * @param description the new description
     * @throws InvalidResourceRequestException
     * @throws DoesNotOwnPhotoException
     */
    public void updatePhotoDescription(String user, long photoId, String description)
        throws InvalidResourceRequestException, DoesNotOwnPhotoException {

        // Ensure photo exists
        Photo photo = getPhotoMetaData(photoId);

        // Ensure photo is owned by requesting user
        if (!photo.getAuthorName().equals(user)) throw new DoesNotOwnPhotoException(photoId, user);

        // Persist description update
        dataStore.updatePhotoDescription(photoId, description);
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
        getPhotoMetaData(referenceId);
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
        if(request.getEventType().equals(EventType.REPLY)) {
            // Retrieve the parent comment and check it exists
            // (exception will be thrown, if not).
            getComment(request.getReferenceId());
        }
        else {
            // Retrieve the parent photo and check it exists
            // (exception will be thrown, if not).
            getPhotoMetaData(request.getReferenceId());
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
     * Edits the given comment
     * @param user the user requesting to change it
     * @param request the request for new comment content
     * @throws InvalidResourceRequestException if the comment doesn't exist
     */
    public Receipt editComment(String user, long commentId, EditCommentRequest request) throws InvalidResourceRequestException, DoesNotOwnCommentException {

        // Retrieve the parent comment and check it exists
        // (exception will be thrown, if not).
        Comment comment = getComment(commentId);
        if (!comment.getAuthor().equals(user)) throw new DoesNotOwnCommentException(commentId, user);

        // Persist comment to data store
        dataStore.persistEditComment(commentId, request.getCommentContents());

        // Return a receipt
        return new Receipt(commentId);
    }

    /**
     * Adds a notification on the photo / comment which the given comment
     * commented on
     * @param comment the new comment
     */
    private void addNotification(Comment comment) throws InvalidResourceRequestException {
        String parentName = "";

        // Get parent reference based on comment type
        if(comment.getEventType().equals(EventType.REPLY)) {
            parentName = getComment(comment.getReferenceId()).getAuthor();
        }
        else {
            parentName = getPhotoMetaData(comment.getReferenceId()).getAuthorName();
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
     * @throws InvalidResourceRequestException if the comment ID doesn't correspond to a valid comment
     */
    public void removeCommentAdmin(long commentId) throws InvalidResourceRequestException {

        // Checks that comment exists, throws an exception if not
        getComment(commentId);

        // Cascade deletes a comment
        dataStore.persistRemoveComment(commentId);
    }

    /**
     * Removes the given comment
     * @param commentId the given commentId
     * @throws InvalidResourceRequestException if the comment ID doesn't correspond to a valid comment
     * @throws DoesNotOwnCommentException if the comment does not belong to the requesting user
     */
    public void removeComment(String user, long commentId)
            throws InvalidResourceRequestException, DoesNotOwnCommentException {

        // Checks that comment exists and is owned by requesting user, throws an exception if not
        Comment c = getComment(commentId);
        if (!c.getAuthor().equals(user)) throw new DoesNotOwnCommentException(commentId, user);

        // Cascade deletes a comment
        dataStore.persistRemoveComment(commentId);
    }

    /**
     * Removes the given photo
     * @param photoId the given photoId
     * @throws InvalidResourceRequestException if the id doesn't correspond to a valid photo
     */
    public void removePhotoAdmin(long photoId) throws InvalidResourceRequestException {

        // Checks that the photo exists, throws an exception if not
        getPhotoMetaData(photoId);

        // Removes the photo from the database
        dataStore.persistRemovePhoto(photoId);
    }

    /**
     * Removes the given photo
     * @param photoId the given photoId
     * @throws InvalidResourceRequestException if the id doesn't correspond to a valid photo
     * @throws DoesNotOwnPhotoException if the photo does not belong to the requesting user
     */
    public void removePhoto(String user, long photoId)
            throws InvalidResourceRequestException, DoesNotOwnPhotoException {

        // Checks that the photo exists and is owned by requesting user, throws an exception if not
        Photo p = getPhotoMetaData(photoId);
        if (!p.getAuthorName().equals(user)) throw new DoesNotOwnPhotoException(photoId, user);

        // Removes the photo from the database
        dataStore.persistRemovePhoto(photoId);
    }

    /**
     * Registers the given vote on the given comment
     * @param commentId the id of the comment to vote on
     * @param user the user who cast this vote
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
        getPhotoMetaData(photoId);

        dataStore.persistPhotoRating(photoId, user, upvote);
    }

    /**
     * Attempts tp a user to follow the person a user has specified
     *
     * Will send notification to the followed user
     *
     * @param userFrom - the username of the user from whom the follow request comes
     * @param userTo - the username of the person the user is trying to follow
     * @throws InvalidResourceRequestException
     * @throws ExistingException
     */

    public void followUser(String userFrom, String userTo) throws InvalidResourceRequestException, ExistingException{

        // Check the user to follow exists
        getUser(userTo);


        // Check the user is not already following the userToFollow

        List<String> followers_usernames = getUsernamesOfFollowers(userTo);

        if (followers_usernames.contains(userFrom)){

            throw new ExistingException("You are already following " + userTo);

        }

        Follow follow = new Follow(userFrom, userTo, CURRENT_ID++);

        dataStore.persistFollowing(userFrom, userTo);
        dataStore.persistAddNotification(userTo, follow);

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
        getUser(userTo);


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
     * Utility to get the Persons (Users) a user followss
     *
     * @param username - username of the user trying to find out who their followers are
     * @return
     */

    public List<User> getFollowing(String username)  throws InvalidResourceRequestException{

        List<User> followers = dataStore.getFollowing(username);
        return followers;
    }

    /**
     * Utility to get the Persons (Users) a user is followed by
     *
     * @param username - username of the user trying to find out who their followers are
     * @return
     */
    public List<User> getFollowers(String username) throws InvalidResourceRequestException{

        List<User> followers = dataStore.getFollowers(username);
        return followers;
    }


    /**
     * Utility method to get the usernames of the persons by whom a user
     *
     * @param username - username of the user trying to find out who their followers are
     * @return
     */

    private List<String> getUsernamesOfFollowers(String username) throws InvalidResourceRequestException{

        List<User> followers = dataStore.getFollowers(username);
        List<String> followers_usernames = followers.stream()
                .map(object -> Objects.toString(object.getUsername(), null))
                .collect(Collectors.toList());

        return followers_usernames;
    }

    /**
     *  all the photos posted by the people a user is following
     *
     * @param username
     * @return
     * @throws InvalidResourceRequestException
     */

    public List<Photo> getNewsFeed(String username) throws InvalidResourceRequestException {

        List<User> following = getFollowing(username);
        List<Photo> newsFeed = new ArrayList<Photo>();

        for (User follower: following){

            List<Photo> photos = getPhotos(follower.getUsername());
            newsFeed.addAll(photos);
        }

        return newsFeed;
    }

    /**
     * Search for users whose name contains query
     *
     * @param name search query on the user's name
     * @return the given users, who have names like the query
     */

    public List<User> getUsersWithName(String name){
        return dataStore.getUserWithNameBegining(name);
    }

    public void clear() {
        // Empty records
        dataStore.clear();
    }
}
