package server.datastore;

import common.CommentType;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.Comment;
import server.objects.Notification;
import server.objects.Photo;
import server.objects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory implementation of a server's data storage
 */
final class InMemoryDataStore implements DataStore {
    // Maintain collection for users
    private final HashMap<String, User> allUsers;
    private HashMap<String, ArrayList<Notification>> notifications;

    // Maintain collections photos
    private final HashMap<String, ArrayList<Photo>> photosByUser;
    private final HashMap<Long, Photo> allPhotos;

    // Maintain collections comments
    private final HashMap<String, ArrayList<Comment>> commentsByUser;
    private final HashMap<Long, Comment> allComments;

    public InMemoryDataStore() {
        // Instantiate all collections
        this.allUsers = new HashMap<>();
        this.photosByUser = new HashMap<>();
        this.commentsByUser = new HashMap<>();
        this.allComments = new HashMap<>();
        this.allPhotos = new HashMap<>();
        this.notifications = new HashMap<>();
    }

    @Override
    public void persistUploadPhoto(Photo newPhoto) {
        // Add photo to main photo collection
        allPhotos.put(newPhoto.getId(), newPhoto);

        // Check if user has posted before and add photo to secondary collection
        // mapping users to their photos. This greatly improves efficiency and scalability
        // for certain data access patterns.
        String user = newPhoto.getPostedBy();
        if(!photosByUser.containsKey(user)) photosByUser.put(user, new ArrayList<>());
        photosByUser.get(user).add(newPhoto);

    }

    @Override
    public List<Photo> getPhotos(String user) {
        // Return all the user's photos
        return photosByUser.getOrDefault(user, new ArrayList<>());
    }

    @Override
    public Photo getPhoto(long id) throws InvalidResourceRequestException {
        // Return the photo if it exists
        if (allPhotos.containsKey(id)) return allPhotos.get(id);

        // No photo exists with this id
        throw new InvalidResourceRequestException(id);
    }

    @Override
    public Comment getComment(long id) throws InvalidResourceRequestException {
        // No photo exists with this id
        if (!allComments.containsKey(id)) throw new InvalidResourceRequestException(id);

        // Get comment and return
        return allComments.get(id);
    }

    @Override
    public List<User> getUsers() {
        return allUsers.values().stream().collect(Collectors.toList());
    }

    @Override
    public User getUser(String username) throws InvalidResourceRequestException {
        // Throw exception if user doesn't exist
        if(!allUsers.containsKey(username)) throw new InvalidResourceRequestException(username);

        return allUsers.get(username);
    }

    @Override
    public void persistAddUser(User user) {
        // Add user to hashmap of all users
        allUsers.put(user.getName(), user);
    }

    @Override
    public List<Comment> getComments(String username) {
        // Get user comments
        return commentsByUser.getOrDefault(username, new ArrayList<>());
    }

    @Override
    public List<Comment> getPhotoComments(String user, long referenceId) {
        // Find all comments on this photo
        return allComments.values().stream().filter(c -> c.getCommentType()
                == CommentType.PHOTO_COMMENT && c.getReferenceId() == referenceId).collect(Collectors.toList());
    }

    @Override
    public List<Comment> getReplies(String user, long referenceId) {
        // Find all comments on this comment
        return allComments.values().stream().filter(c -> c.getCommentType()
                == CommentType.REPLY && c.getReferenceId() == referenceId).collect(Collectors.toList());
    }

    @Override
    public List<Notification> getNotifications(String user) {
        // Get notifications for user
        if(!notifications.containsKey(user)) notifications.put(user, new ArrayList<>());
        return notifications.get(user);
    }

    @Override
    public void persistAddComment(Comment comment)  {
        // If user has never commented before, add them to the map
        if(!commentsByUser.containsKey(comment.getPostedBy())) {
            commentsByUser.put(comment.getPostedBy(), new ArrayList<>());
        }

        // Add comment to collection mapping users -> comments AND
        // Add comment to overall comment collection
        commentsByUser.get(comment.getPostedBy()).add(comment);
        allComments.put(comment.getId(), comment);
    }

    @Override
    public void persistAddNotification(String parentName, Comment comment) {
        // Add notification using found parent's name
        if(!notifications.containsKey(parentName)) notifications.put(parentName, new ArrayList<>());
        notifications.get(parentName).add(new Notification(comment, parentName));
    }

    @Override
    public void persistRemoveNotification(String user, long id) {
        // Retrieve notification and remove it, if present.
        Optional<Notification> note = notifications.get(user).stream().filter(n -> n.getCommentId() == id).findFirst();
        note.ifPresent(n -> notifications.get(user).remove(n));
    }

    @Override
    public void persistRemoveComment(long commentId) throws InvalidResourceRequestException {
        // Simply overwrites comment with "Removed By Admin"
        getComment(commentId).remove();
    }

    @Override
    public void persistVote(long commentId, String user, boolean upvote) throws InvalidResourceRequestException {
        getComment(commentId).vote(user, upvote);
    }

    @Override
    public void clear() {
        // Empty records
        allUsers.clear();
        allComments.clear();
        commentsByUser.clear();
        notifications.clear();
        allPhotos.clear();
        photosByUser.clear();
    }
}
