import org.junit.Test;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.Comment;
import server.objects.Receipt;

import javax.ws.rs.core.Response;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static server.objects.EventType.PHOTO_COMMENT;
import static server.objects.EventType.REPLY;

public class AdminAPITests extends TestUtility{

    @Test(expected = InvalidResourceRequestException.class)
    public void adminRemovePhotoTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Remove photo because 'user' is admin
        Response removeResponse = apiClient.adminRemovePhoto(id);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), removeResponse.getStatus());

        // Check photo was removed, InvalidResourceRequestException should be thrown
        resolver.getPhotoContents(id, ext);
    }

    @Test (expected = InvalidResourceRequestException.class)
    public void userRemovePhotoTest() throws InvalidResourceRequestException {
        // Add non-admin sample user and register it
        addUser(username); // admin
        String user = username + "2"; // not admin
        loginAndSetupNewUser(user);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Remove photo because 'user' is author
        Response removeResponse = apiClient.removePhoto(id);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), removeResponse.getStatus());

        // Check photo was removed, InvalidResourceRequestException should be thrown
        resolver.getPhotoContents(id, ext);
    }


    @Test
    public void adminRemoveReplyTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
        List<Comment> comments = resolver.getComments(username);
        assertEquals(2, comments.size());

        // Ensure correct contents
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getCommentContents());
        }

        // Remove reply because 'user' is admin
        Response removeResponse = apiClient.adminRemoveComment(id);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), removeResponse.getStatus());

        // Check comment was removed
        comments = resolver.getComments(username);
        assertEquals(1, comments.size());
    }

}
