import org.junit.Test;

import server.Resources;
import server.requests.*;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.*;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static server.objects.CommentType.*;

/**
 * Tests Server behaviour in response to RESTful API calls
 */
public final class ServerTests extends TestUtility {
    @Test
    public void addUserTest() {
        addUser(name);
    }

    @Test
    public void getUsersTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Get users, and ensure it was successful
        Response response = apiClient.getUsers();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String users = response.readEntity(String.class);
        assertEquals(gson.fromJson(users, User[].class)[0].getName(), name);
    }

    @Test
    public void addAlbumTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Add new album, and retrieve the returned id
        Response response = apiClient.addAlbum(albumName, description, name);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long albumId = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Check server has record of album
        Album album = resolver.getAlbum(albumId);
        assertEquals(albumName, album.getAlbumName());
        assertEquals(description, album.getAlbumDescription());
    }

    @Test
    public void uploadPhotoTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String name = "name";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(name, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Check server has record of photo by decoding its base64 representation and checking for
        // equivalence.
        List<Photo> photos = resolver.getPhotos(this.name);
        assertArrayEquals(contents, UploadPhotoRequest.decodeContents(photos.get(0).getPhotoContents()));
        assertEquals(name, photos.get(0).getPhotoName());
    }

    @Test
    public void uploadSamePhotoTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Upload photo again
        response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id2 = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Check server has record of both photos, that their contents and names are identical,
        // but that their ids are different.
        List<Photo> photos = resolver.getPhotos(name);
        assertEquals(2, photos.size());
        assertNotEquals(id, id2);
        for(Photo p : photos) {
            assertArrayEquals(contents, UploadPhotoRequest.decodeContents(p.getPhotoContents()));
            assertEquals(photoName, p.getPhotoName());
        }
    }

    @Test
    public void getAllPhotosFromUserTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Get all photos from user on server
        Response photosResponse = apiClient.getAllPhotos(name);

        // Parse JSON and check photo contents and who posted it
        String photos = photosResponse.readEntity(String.class);
        Photo photo = gson.fromJson(photos, Photo[].class)[0];
        assertEquals(photo.getPostedBy(), name);
        assertEquals(photo.getPhotoName(), photoName);
        assertArrayEquals(contents, UploadPhotoRequest.decodeContents(photo.getPhotoContents()));
    }

    @Test
    public void get0PhotosFromUserTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Get all photos from user on server
        Response photosResponse = apiClient.getAllPhotos(name);

        // Parse JSON
        String photos = photosResponse.readEntity(String.class);
        Photo[] photo = gson.fromJson(photos, Photo[].class);
        assertEquals(0, photo.length);
    }

    @Test
    public void getPhotoTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Get recently uploaded photo from server
        Response photosResponse = apiClient.getPhoto(id);
        assertEquals(Response.Status.OK.getStatusCode(), photosResponse.getStatus());

        // Parse JSON and check photo contents and who posted it
        String photoStr = photosResponse.readEntity(String.class);
        Photo photo = gson.fromJson(photoStr, Photo.class);
        assertEquals(photo.getPostedBy(), name);
        assertEquals(photo.getPhotoName(), photoName);
        assertArrayEquals(contents, UploadPhotoRequest.decodeContents(photo.getPhotoContents()));
    }

    @Test
    public void voteTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name", comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send new comment request to server
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send upvote request to server, and check it was successful on the server.
        Response voteResponse = apiClient.vote(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getComment(id).getUpvotes().size());
        assertEquals(0, resolver.getComment(id).getDownvotes().size());
    }

    @Test
    public void idempotentVoteTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name", comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send new comment request to server
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send upvote request to server, and check it was successful on the server.
        Response voteResponse = apiClient.vote(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getComment(id).getUpvotes().size());
        assertEquals(0, resolver.getComment(id).getDownvotes().size());

        // Send same upvote request again. Ensure it worked, but that nothing changed on the server
        voteResponse = apiClient.vote(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getComment(id).getUpvotes().size());
        assertEquals(0, resolver.getComment(id).getDownvotes().size());
    }

    @Test
    public void undoVoteTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name", comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send new comment request to server
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send upvote request to server, and check it was successful on the server.
        Response voteResponse = apiClient.vote(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getComment(id).getUpvotes().size());
        assertEquals(0, resolver.getComment(id).getDownvotes().size());

        // Send downvote request to server, and check it was successful on the server.
        // Also check it overwrote previous persistVote
        voteResponse = apiClient.vote(id, false);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getComment(id).getDownvotes().size());
        assertEquals(0, resolver.getComment(id).getUpvotes().size());
    }

    @Test
    public void addCommentToPhotoTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Check data-store has comment recorded
        List<Comment> comments = resolver.getComments(name);
        Comment recordedComment = comments.get(0);

        assertEquals(1, comments.size());
        assertEquals(comment, recordedComment.getContents());
    }

    @Test
    public void removeCommentTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Check data-store has comment recorded
        List<Comment> comments = resolver.getComments(name);
        Comment recordedComment = comments.get(0);
        assertEquals(1, comments.size());
        assertEquals(comment, recordedComment.getContents());

        // Remove comment because 'user' is admin
        Response removeResponse = apiClient.removeComment(id);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), removeResponse.getStatus());

        // Check comment was removed
        recordedComment = resolver.getComments(name).get(0);
        assertEquals(Resources.REMOVAL_STRING, recordedComment.getContents());
    }

    @Test
    public void addReplyTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send reply request. Reply will contain same contents as first comment.
        commentsResponse = apiClient.addComment(id, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Check data-store has comment recorded
        List<Comment> comments = resolver.getComments(name);
        assertEquals(2, comments.size());

        // Ensure correct contents
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getContents());
        }
    }

    /**
     * This test illustrates how replies to top-level comments on photos
     * are not retrieved. This design decision greatly simplified data storage and
     * modification, and actually resembles how a lot of social media handle comments anyway
     * (i.e. a 'load replies' button for each comment).
     * @throws InvalidResourceRequestException
     */
    @Test
    public void getTopLevelCommentsTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        long commentId = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send reply request. Reply will contain same contents as first comment.
        commentsResponse = apiClient.addComment(commentId, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Check data-store has comments recorded
        List<Comment> comments = resolver.getComments(name);
        assertEquals(2, comments.size());

        // Ensure correct contents
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getContents());
        }

        // Now, ask for photo comments for the photo.
        commentsResponse = apiClient.getAllPhotoComments(id);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Parse response, asserting that only the first comment was retrieved
        Comment[] photoComments = gson.fromJson(commentsResponse.readEntity(String.class), Comment[].class);
        assertEquals(1, photoComments.length);
        assertEquals(commentId, photoComments[0].getId());
        assertEquals(id, photoComments[0].getReferenceId());
    }

    /**
     * This test illustrates how only top-level replies to a comment are retrieved when asking for replies.
     * This design decision greatly simplified data storage and
     * modification, and actually resembles how a lot of social media handle comments anyway
     * (i.e. a 'load replies' button for each comment).
     * @throws InvalidResourceRequestException
     */
    @Test
    public void getTopLevelRepliesTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        long commentId = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send reply request. Reply will contain same contents as first comment.
        commentsResponse = apiClient.addComment(commentId, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        long commentId2 = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send reply request to previous reply. It will contain same contents as first comment.
        commentsResponse = apiClient.addComment(commentId2, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Check data-store has comments recorded
        List<Comment> comments = resolver.getComments(name);
        assertEquals(3, comments.size());

        // Ensure correct contents
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getContents());
        }

        // Now, ask for replies for the original comment.
        commentsResponse = apiClient.getAllReplies(commentId);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Parse response, asserting that only the top-level reply was retrieved
        Comment[] replies = gson.fromJson(commentsResponse.readEntity(String.class), Comment[].class);
        assertEquals(1, replies.length);
        assertEquals(commentId2, replies[0].getId());
        assertEquals(commentId, replies[0].getReferenceId());
    }

    @Test
    public void removeReplyTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send reply request. Reply will contain same contents as first comment.
        commentsResponse = apiClient.addComment(id, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Check data-store has comment recorded
        List<Comment> comments = resolver.getComments(name);
        assertEquals(2, comments.size());

        // Ensure correct contents
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getContents());
        }

        // Remove reply because 'user' is admin
        Response removeResponse = apiClient.removeComment(id);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), removeResponse.getStatus());

        // Check comment was removed
        comments = resolver.getComments(name);
        assertEquals(Resources.REMOVAL_STRING, comments.get(1).getContents());
    }

    @Test
    public void getPhotoCommentNotificationTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        long commentId = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Check notifications for user
        Response notificationsResponse = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), notificationsResponse.getStatus());

        // Parse notifications, and assert one was generated for the new comment on the photo
        Notification[] notifications = gson.fromJson(notificationsResponse.readEntity(String.class),
                Notification[].class);
        assertEquals(1, notifications.length);
        assertEquals(commentId, notifications[0].getCommentId());
        assertEquals(id, notifications[0].getReferenceId());
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(name, notifications[0].getCommentPostedBy());
    }

    @Test
    public void get0CommentsFromUserTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Get all comments from user on server
        Response commentsResponse = apiClient.getAllComments(name);

        // Parse JSON
        String comments = commentsResponse.readEntity(String.class);
        Comment[] comm = gson.fromJson(comments, Comment[].class);
        assertEquals(0, comm.length);
    }

    @Test
    public void get0NotificationsFromUserTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Get all notifications from user on server
        Response notificationsResponse = apiClient.getNotifications();

        // Parse JSON
        String notifications = notificationsResponse.readEntity(String.class);
        Notification[] n = gson.fromJson(notifications, Notification[].class);
        assertEquals(0, n.length);
    }

    @Test
    public void getReplyNotificationTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        long commentId = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send reply request. Reply will contain same contents as first comment.
        Response replyResponse = apiClient.addComment(commentId, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), replyResponse.getStatus());
        long replyId = gson.fromJson(replyResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Check notifications for user
        Response notificationsResponse = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), notificationsResponse.getStatus());

        // Parse notifications, and assert two were generated for the new comment and reply
        Notification[] notifications = gson.fromJson(notificationsResponse.readEntity(String.class),
                Notification[].class);
        assertEquals(2, notifications.length);

        // Ensure the user posted both
        assertEquals(name, notifications[0].getCommentPostedBy());
        assertEquals(name, notifications[1].getCommentPostedBy());

        // Ensure the second comment is registered as a reply to the first
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(id, notifications[0].getReferenceId());
        assertEquals(commentId, notifications[0].getCommentId());

        assertEquals(REPLY, notifications[1].getCommentType());
        assertEquals(replyId, notifications[1].getCommentId());
        assertEquals(commentId, notifications[1].getReferenceId());
    }

    @Test
    public void removeUserCommentNotificationTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        long commentId = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Check notifications for user
        Response notificationsResponse = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), notificationsResponse.getStatus());

        // Parse notifications, and assert one was generated for the new comment on the photo
        Notification[] notifications = gson.fromJson(notificationsResponse.readEntity(String.class),
                Notification[].class);
        assertEquals(1, notifications.length);
        assertEquals(commentId, notifications[0].getCommentId());
        assertEquals(id, notifications[0].getReferenceId());
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(name, notifications[0].getCommentPostedBy());

        // Get all user comments, such that the notification generated by this user's comment on its OWN photo
        // is removed.
        commentsResponse = apiClient.getAllComments(name);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Check notifications AGAIN for user, ensure there are none since comment was read
        notificationsResponse = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), notificationsResponse.getStatus());
        notifications = gson.fromJson(notificationsResponse.readEntity(String.class),
                Notification[].class);
        assertEquals(0, notifications.length);
    }

    @Test
    public void removePhotoCommentNotificationTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        long commentId = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Check notifications for user
        Response notificationsResponse = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), notificationsResponse.getStatus());

        // Parse notifications, and assert one was generated for the new comment on the photo
        Notification[] notifications = gson.fromJson(notificationsResponse.readEntity(String.class),
                Notification[].class);
        assertEquals(1, notifications.length);
        assertEquals(commentId, notifications[0].getCommentId());
        assertEquals(id, notifications[0].getReferenceId());
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(name, notifications[0].getCommentPostedBy());

        // Get all photo comments
        commentsResponse = apiClient.getAllPhotoComments(id);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Check notifications AGAIN for user, ensure there are none since comment was read
        notificationsResponse = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), notificationsResponse.getStatus());
        notifications = gson.fromJson(notificationsResponse.readEntity(String.class),
                Notification[].class);
        assertEquals(0, notifications.length);
    }

    @Test
    public void removeReplyNotificationTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        long commentId = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send reply request. Reply will contain same contents as first comment.
        Response replyResponse = apiClient.addComment(commentId, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), replyResponse.getStatus());
        long replyId = gson.fromJson(replyResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Check notifications for user
        Response notificationsResponse = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), notificationsResponse.getStatus());

        // Parse notifications, and assert two were generated for the new comment and reply
        Notification[] notifications = gson.fromJson(notificationsResponse.readEntity(String.class),
                Notification[].class);
        assertEquals(2, notifications.length);

        // Ensure the user posted both
        assertEquals(name, notifications[0].getCommentPostedBy());
        assertEquals(name, notifications[1].getCommentPostedBy());

        // Ensure the second comment is registered as a reply to the first
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(id, notifications[0].getReferenceId());
        assertEquals(commentId, notifications[0].getCommentId());

        assertEquals(REPLY, notifications[1].getCommentType());
        assertEquals(replyId, notifications[1].getCommentId());
        assertEquals(commentId, notifications[1].getReferenceId());

        // Get all replies
        commentsResponse = apiClient.getAllReplies(commentId);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Check notifications AGAIN for user, ensure the notification for the photo comment is still there
        // and that the notification for the reply is gone.
        notificationsResponse = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), notificationsResponse.getStatus());
        notifications = gson.fromJson(notificationsResponse.readEntity(String.class),
                Notification[].class);
        assertEquals(1, notifications.length);
    }

    @Test
    public void getAllUserCommentsTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo TWICE
        Response commentResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentResponse.getStatus());
        commentResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentResponse.getStatus());

        // Ask server for all comments made by this user
        Response commentsResponse = apiClient.getAllComments(name);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Parse comments array and ensure it has two elements in it
        Comment[] comments = gson.fromJson(commentsResponse.readEntity(String.class), Comment[].class);
        assertEquals(2, comments.length);

        // Check each comment. They're identical.
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getContents());
        }
    }

    @Test
    public void getAllPhotoCommentsTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo TWICE
        Response commentResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentResponse.getStatus());
        commentResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentResponse.getStatus());

        // Ask server for all comments made on this photo
        Response commentsResponse = apiClient.getAllPhotoComments(id);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Parse comments array and ensure it has two elements in it
        Comment[] comments = gson.fromJson(commentsResponse.readEntity(String.class), Comment[].class);
        assertEquals(2, comments.length);

        // Check each comment. They're identical.
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getContents());
        }
    }

    @Test
    public void getAllRepliesTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentResponse.getStatus());
        id = gson.fromJson(commentResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send reply request to the previous comment
        commentResponse = apiClient.addComment(id, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentResponse.getStatus());

        // Ask server for all replies made on this comment
        Response commentsResponse = apiClient.getAllReplies(id);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Parse comments array and ensure it has two elements in it
        Comment[] comments = gson.fromJson(commentsResponse.readEntity(String.class), Comment[].class);
        assertEquals(1, comments.length);
        assertEquals(comment, comments[0].getContents());
    }

    @Test
    public void get0PhotoCommentsTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Ask server for all comments made on this photo
        Response commentsResponse = apiClient.getAllPhotoComments(id);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Parse comments array and ensure it has two elements in it
        Comment[] comments = gson.fromJson(commentsResponse.readEntity(String.class), Comment[].class);
        assertEquals(0, comments.length);
    }

    @Test
    public void get0RepliesTest() {
        // Add sample user and register it
        addUserAndLogin(name);

        // Create sample data
        String photoName = "name";
        String comment = "comment";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentResponse.getStatus());
        id = gson.fromJson(commentResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Ask server for all comments made by this user
        Response commentsResponse = apiClient.getAllReplies(id);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Parse comments array and ensure it has two elements in it
        Comment[] comments = gson.fromJson(commentsResponse.readEntity(String.class), Comment[].class);
        assertEquals(0, comments.length);
    }
}
