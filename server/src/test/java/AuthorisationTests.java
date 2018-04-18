import server.objects.*;
import org.junit.Test;
import server.datastore.exceptions.InvalidResourceRequestException;

import javax.ws.rs.core.Response;

import static server.objects.CommentType.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests checking unauthorised use of APIs
 */
public class AuthorisationTests extends TestUtility {

    @Test
    public void loginTest() throws InvalidResourceRequestException {
        // Add sample user
        addUser(name);

        // Attempt to log the user in. Analyse the response and parse for the session info
        Response response = apiClient.loginUser(name, pw);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedLoginException() {
        // Add user
        addUser(name);

        // Create user with same name but different pw, and don't add it to the client or server.
        // Attempt to log the user in with the wrong password.
        // Analyse the response and parse for the session info. Assert unauthorised
        Response response = apiClient.loginUser(name, "");
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedGetPhotoTest() {
        // Assert unauthorised
        Response response = apiClient.getPhoto(100);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedGetUserTest() {
        // Call the getUser API from the client without having registered a user
        Response response = apiClient.getUsers();
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedUploadPhotoTest() {
        // Create sample data
        String photoName = "name";
        byte[] contents = new byte[] {1, 2, 3, 4, 5};

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, contents);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedGetAllPhotosTest() {
        // Assert unauthorised
        Response response = apiClient.getAllPhotos(name);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedVoteTest() {
        // Assert unauthorised when try to upvote
        long randomId = 100;
        Response response = apiClient.vote(randomId, true);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        // Assert unauthorised when try to downvote
        response = apiClient.vote(randomId, false);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedAddCommentToPhotoTest() {
        // Assert unauthorised
        Response response = apiClient.addComment(100, PHOTO_COMMENT, name);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedRemoveCommentTest() {
        // Assert unauthorised because no user logged in
        Response response = apiClient.removeComment(100);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedRemoveCommentTest2() {
        // Add two users and login as second. Only the first user will be an admin.
        addUser(name); // admin
        addUserAndLogin(name + "2"); // not admin

        // Assert unauthorised because it is NOT an admin calling this
        Response response = apiClient.removeComment(100);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedAddReplyTest() {
        // Assert unauthorised
        Response response = apiClient.addComment(100, REPLY, name);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedGetNotificationsTest() {
        // Assert unauthorised use of API
        Response response = apiClient.getNotifications();
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedGetAllUserCommentsTest() {
        // Create user but don't add it to the client or server.
        User user = new User(name, 0);

        // Assert unauthorised
        Response response = apiClient.getAllComments(user.getName());
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedGetAllPhotoCommentsTest() {
        // Assert unauthorised
        long randomId = -100;
        Response response = apiClient.getAllPhotoComments(randomId);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void unauthorisedGetAllRepliesTest() {
        // Assert unauthorised
        long randomId = -100;
        Response response = apiClient.getAllReplies(randomId);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }
}
