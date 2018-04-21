import server.objects.*;
import org.junit.Test;
import server.datastore.exceptions.InvalidResourceRequestException;

import javax.ws.rs.core.Response;

import java.util.List;

import static server.objects.CommentType.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests demonstrating behaviour of APIs when presented with
 * data which doesn't reflect the server's internal state.
 */
public class InvalidInputTests extends TestUtility {

    @Test
    public void loginUnknownUsernameTest() throws InvalidResourceRequestException {
        // Attempt to log the user in. Analyse the response and parse for the session info
        Response response = apiClient.loginUser(username, pw);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        // Check the server has no user
        assertEquals(0, resolver.getUsers().size());
    }

    @Test
    public void addExistingUserTest() {
        // Add sample user
        addUser(username);

        // Try to add user again, and ensure an error was returned as the response
        Response response = apiClient.addUser(username, pw);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getAllPhotosBadUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Try to get photos from an unknown user from the server. Will fail
        Response photosResponse = apiClient.getAllPhotos("l");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), photosResponse.getStatus());

        // Without anything after the final '/' in the URL, a 404 will be raised as it won't even
        // get processed by the server.
        photosResponse = apiClient.getAllPhotos("");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), photosResponse.getStatus());
    }

    @Test
    public void getAllCommentsBadUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Try to get photos from an unknown user from the server. Will fail
        Response photosResponse = apiClient.getAllComments("l");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), photosResponse.getStatus());

        // Without anything after the final '/' in the URL, a 404 will be raised as it won't even
        // get processed by the server.
        photosResponse = apiClient.getAllComments("");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), photosResponse.getStatus());
    }

    @Test
    public void getPhotoBadIdTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Try to get an unknown photo from the server. Will fail
        long randomId = -1000000;
        Response photosResponse = apiClient.getPhoto(randomId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), photosResponse.getStatus());
    }

    @Test
    public void getAllAlbumsBadUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Try to get photos from an unknown user from the server. Will fail
        Response photosResponse = apiClient.getAllAlbums("l");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), photosResponse.getStatus());

        // Without anything after the final '/' in the URL, a 404 will be raised as it won't even
        // get processed by the server.
        photosResponse = apiClient.getAllAlbums("");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), photosResponse.getStatus());
    }

    @Test
    public void getAlbumBadIdTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Try to get an unknown album from the server. Will fail
        long randomId = -1000000;
        Response photosResponse = apiClient.getAlbum(randomId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), photosResponse.getStatus());
    }

    @Test
    public void getAllPhotosBadAlbumIdTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Assert bad request, since album was unknown
        long randomAlbumId = -100;
        Response response = apiClient.getAllPhotos(randomAlbumId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPhotoToUnknownAlbumTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Create sample data
        String photoName = "photo";

        // Send request to add photo to unknown album. Fail because unknown album id
        long randomId = -100;
        Response commentsResponse = apiClient.uploadPhoto(photoName, randomId, new byte[] {});
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());

        // Check data-store that no comment is recorded
        List<Comment> comments = resolver.getComments(username);
        assertEquals(0, comments.size());
    }

    @Test
    public void addPhotoToAlbumUnauthorisedTest() throws InvalidResourceRequestException {
        // Create sample data
        String photoName = "photo";
        String username2 = username + "2";

        // Add sample users and register them
        loginAndSetupNewUser(username);
        long albumId1 = albumId;
        loginAndSetupNewUser(username2);
        long albumId2 = albumId;

        // Since username2 is currently logged-in, attempt to upload a photo
        // to username's album. This will fail, as the indicated album is NOT owned by the logged-in user.
        Response response = apiClient.uploadPhoto(photoName, albumId1, new byte[] {});
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        // Check data-store that no photo is uploaded
        assertEquals(0, resolver.getPhotos(username).size());
    }

    @Test
    public void voteBadIdTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Assert unknown when try to upvote
        long randomId = 100;
        Response response = apiClient.voteOnComment(randomId, true);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        // Assert unknown when try to downvote
        response = apiClient.voteOnComment(randomId, false);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void rateBadIdTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Assert unknown when try to upvote
        long randomId = 100;
        Response response = apiClient.ratePhoto(randomId, true);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        // Assert unknown when try to downvote
        response = apiClient.ratePhoto(randomId, false);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void addCommentToUnknownPhotoTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Create sample data
        String comment = "comment";

        // Send request to add comment to unknown photo. Fail because unknown photo id
        long randomId = -100;
        Response commentsResponse = apiClient.addComment(randomId, PHOTO_COMMENT,  comment);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());

        // Check data-store that no comment is recorded
        List<Comment> comments = resolver.getComments(username);
        assertEquals(0, comments.size());
    }

    @Test
    public void replyToUnknownCommentTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Create sample data
        String comment = "comment";

        // Send request to add comment to unknown comment. Fail because unknown id
        long randomId = -100;
        Response commentsResponse = apiClient.addComment(randomId, REPLY,  comment);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());

        // Check data-store that no comment is recorded
        List<Comment> comments = resolver.getComments(username);
        assertEquals(0, comments.size());
    }

    @Test
    public void adminRemoveUnknownCommentTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Send request to remove unknown comment.
        long randomId = -100;
        Response commentsResponse = apiClient.adminRemoveComment(randomId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());
    }

    @Test
    public void editUnknownCommentTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        //Send request to edit unknown comment
        long randomId = -100;
        Response commentsResponse = apiClient.editComment(randomId, "some new content");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());
    }

    @Test
    public void editCommentUnauthorisedTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Create sample data
        String photoName = "username";
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
        List<Comment> comments = resolver.getComments(username);
        Comment recordedComment = comments.get(0);
        assertEquals(1, comments.size());
        assertEquals(comment, recordedComment.getCommentContents());

        // Add another sample users and register them
        String username2 = username + "2";
        loginAndSetupNewUser(username2);

        // Since username2 is currently logged-in, attempt to edit the comment
        // This will fail, as the indicated comment is NOT owned by the logged-in user.
        response = apiClient.editComment(recordedComment.getId(), "Mallory was here.");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        // Check data-store that comment was not changed
        assertEquals(comment, recordedComment.getCommentContents());
    }

    @Test
    public void addReplyToUnknownCommentTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Create sample data
        long randomId = -100;
        String comment = "comment";

        // Send reply request to an unknown comment
        Response commentsResponse = apiClient.addComment(randomId, REPLY, comment);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());

        // Check data-store that no comment is recorded
        List<Comment> comments = resolver.getComments(username);
        assertEquals(0, comments.size());
    }

    @Test
    public void getAllUserCommentsInvalidUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Ask server for all comments made by unknown user
        Response commentsResponse = apiClient.getAllComments("a");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());

        // Without anything after the final '/' in the URL, a 404 will be raised as it won't even
        // get processed by the server.
        commentsResponse = apiClient.getAllComments("");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), commentsResponse.getStatus());
    }

    @Test
    public void getAllRepliesInvalidCommentTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Ask server for all comments made on unknown photo
        long randomId = -100;
        Response commentsResponse = apiClient.getAllReplies(randomId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());
    }

    @Test
    public void getAllPhotoCommentsInvalidPhotoTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Ask server for all comments made on unknown photo
        long randomId = -100;
        Response commentsResponse = apiClient.getAllPhotoComments(randomId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());
    }

    @Test
    public void followNonExistingUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        String randomName = "I-don't-Exist";

        // Post a follow request
        Response followResponse = apiClient.followUser(randomName);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), followResponse.getStatus());
    }


    @Test
    public void unfollowNonExistingUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        String randomName = "I-don't-Exist";

        // Post an unfollow request
        Response followResponse = apiClient.followUser(randomName);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), followResponse.getStatus());
    }


    @Test
    public void doubleFollowUserTest() {

        // A user should not be able to follow the same person twice
        String randomName = "Eminem";

        // Create two users and attempt for one to follow the other twice

        // Add sample user and register it
        loginAndSetupNewUser(username);
        loginAndSetupNewUser(randomName);

        // The second following should return a conflict
        Response firstFollowResponse = apiClient.followUser(randomName);
        Response secondFollowResponse = apiClient.followUser(randomName);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), secondFollowResponse.getStatus());
    }

    @Test
    public void deleteUnownedCommentTest() throws InvalidResourceRequestException {
        // Add two users and login as second. Only the first user will be an admin.
        loginAndSetupNewUser(username); // admin

        // Create sample data
        String photoName = "photo";
        String comment = "a comment";
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
        List<Comment> comments = resolver.getComments(username);
        Comment recordedComment = comments.get(0);
        assertEquals(1, comments.size());
        assertEquals(comment, recordedComment.getCommentContents());

        // Attempt to remove comment as non-admin user who didn't write it
        loginAndSetupNewUser(username + "2"); // not admin
        Response removeResponse = apiClient.removeComment(id);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), removeResponse.getStatus());

        // Check comment is still there
        assertEquals(1, resolver.getComments(username).size());
    }

    @Test
    public void deleteUnknownCommentTest() {
        // Add two users and login as second. Only the first user will be an admin.
        loginAndSetupNewUser(username); // admin
        loginAndSetupNewUser(username + "2"); // not admin
        Response removeResponse = apiClient.removeComment(-100);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), removeResponse.getStatus());
    }
}
