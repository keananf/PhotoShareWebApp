import org.junit.Test;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.Notification;
import server.objects.Receipt;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static server.objects.EventType.PHOTO_COMMENT;
import static server.objects.EventType.REPLY;

public class NotificationAPITests extends TestUtility{
    @Test
    public void getPhotoCommentNotificationTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
        assertEquals(commentId, notifications[0].getContentId());
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(username, notifications[0].getAuthor());
    }

    @Test
    public void get0NotificationsFromUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

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
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
        assertEquals(username, notifications[0].getAuthor());
        assertEquals(username, notifications[1].getAuthor());

        // Ensure the second comment is registered as a reply to the first
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(commentId, notifications[0].getContentId());

        assertEquals(REPLY, notifications[1].getCommentType());
        assertEquals(replyId, notifications[1].getContentId());
    }

    @Test
    public void removeUserCommentNotificationTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
        assertEquals(commentId, notifications[0].getContentId());
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(username, notifications[0].getAuthor());

        // Get all user comments, such that the notification generated by this user's comment on its OWN photo
        // is removed.
        commentsResponse = apiClient.getAllComments(username);
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
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
        assertEquals(commentId, notifications[0].getContentId());
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(username, notifications[0].getAuthor());

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
        loginAndSetupNewUser(username);

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
        assertEquals(username, notifications[0].getAuthor());
        assertEquals(username, notifications[1].getAuthor());

        // Ensure the second comment is registered as a reply to the first
        assertEquals(PHOTO_COMMENT, notifications[0].getCommentType());
        assertEquals(commentId, notifications[0].getContentId());

        assertEquals(REPLY, notifications[1].getCommentType());
        assertEquals(replyId, notifications[1].getContentId());

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
    public void getFollowNotificationTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Add user to be a follower of sample
        String userFollowing = "userFollowing";
        loginAndSetupNewUser(userFollowing);

        // Send a follow request to sample user
        Response response = apiClient.followUser(username);

        // Check notifications for user
        apiClient.loginUser(username, pw);
        Response notificationsResponse = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), notificationsResponse.getStatus());

        // Parse notifications, and assert one was generated for the new comment on the photo
        Notification[] notifications = gson.fromJson(notificationsResponse.readEntity(String.class),
                Notification[].class);
        assertEquals(1, notifications.length);

    }

    @Test
    public void notificationAddedForFollowTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Set up users who are following our sample user
        String userFollowingOne = "user_following_one";
        loginAndSetupNewUser(userFollowingOne);
        apiClient.followUser(username);

        // Log back into the sample user
        apiClient.loginUser(username, pw);


        // Get followers, and ensure it was successful
        Response response = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String notifications = response.readEntity(String.class);

        assertEquals(gson.fromJson(notifications, Notification[].class).length, 1);
    }

    @Test
    public void notificationAddedForTwoFollowTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Set up users who are following our sample user
        String userFollowingOne = "user_following_one";
        loginAndSetupNewUser(userFollowingOne);
        apiClient.followUser(username);

        String userFollowingTwo = "user_following_two";
        loginAndSetupNewUser(userFollowingTwo);
        apiClient.followUser(username);

        // Log back into the sample user
        apiClient.loginUser(username, pw);


        // Get followers, and ensure it was successful
        Response response = apiClient.getNotifications();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String notifications = response.readEntity(String.class);
        assertEquals(gson.fromJson(notifications, Notification[].class).length, 2);
    }


}
