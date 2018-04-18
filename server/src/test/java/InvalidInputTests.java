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
        Response response = apiClient.vote(randomId, true);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        // Assert unknown when try to downvote
        response = apiClient.vote(randomId, false);
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
    public void removeUnknownCommentTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Send request to remove unknown comment.
        long randomId = -100;
        Response commentsResponse = apiClient.removeComment(randomId);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), commentsResponse.getStatus());
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
}
