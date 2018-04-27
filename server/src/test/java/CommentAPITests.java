import org.junit.Test;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.Comment;
import server.objects.Receipt;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static server.objects.EventType.PHOTO_COMMENT;
import static server.objects.EventType.REPLY;

public class CommentAPITests extends TestUtility{

    @Test
    public void commentVoteTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send new comment request to server
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send upvote request to server, and check it was successful on the server.
        Response voteResponse = apiClient.voteOnComment(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getComment(id).getLikes().size());
    }

    @Test
    public void idempotentCommentVoteTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send new comment request to server
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send upvote request to server, and check it was successful on the server.
        Response voteResponse = apiClient.voteOnComment(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getComment(id).getLikes().size());

        // Send same upvote request again. Ensure it worked, but that nothing changed on the server
        voteResponse = apiClient.voteOnComment(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getComment(id).getLikes().size());
    }

    @Test
    public void undoCommentVoteTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send new comment request to server
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send upvote request to server, and check it was successful on the server.
        Response voteResponse = apiClient.voteOnComment(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getComment(id).getLikes().size());

        // Send downvote request to server, and check it was successful on the server.
        // Also check it overwrote previous persistCommentVote
        voteResponse = apiClient.voteOnComment(id, false);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(0, resolver.getComment(id).getLikes().size());
    }

    @Test
    public void addCommentToPhotoTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
    }

    @Test
    public void adminRemoveCommentTest() throws InvalidResourceRequestException {
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

        // Check data-store has comment recorded
        List<Comment> comments = resolver.getComments(username);
        Comment recordedComment = comments.get(0);
        assertEquals(1, comments.size());
        assertEquals(comment, recordedComment.getCommentContents());

        // Remove comment because 'user' is admin
        Response removeResponse = apiClient.adminRemoveComment(id);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), removeResponse.getStatus());

        // Check comment was removed
        assertEquals(0, resolver.getComments(username).size());
    }

    @Test
    public void userRemoveCommentTest() throws InvalidResourceRequestException {
        // Add and register 2 sample users, only the first is the admin
        addUser(username);
        String user = username + "2";
        loginAndSetupNewUser(user);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo
        Response commentsResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        id = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Check data-store has comment recorded
        List<Comment> comments = resolver.getComments(user);
        Comment recordedComment = comments.get(0);
        assertEquals(1, comments.size());
        assertEquals(comment, recordedComment.getCommentContents());

        // Remove comment because 'user' is author
        Response removeResponse = apiClient.removeComment(id);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), removeResponse.getStatus());

        // Check comment was removed
        assertEquals(0, resolver.getComments(user).size());
    }

    @Test
    public void editCommentTest() throws InvalidResourceRequestException {
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

        // Check data-store has comment recorded
        List<Comment> comments = resolver.getComments(username);
        Comment recordedComment = comments.get(0);
        assertEquals(1, comments.size());
        assertEquals(comment, recordedComment.getCommentContents());

        // Edit comment
        String newComment = "new comment";
        Response editResponse = apiClient.editComment(id, newComment);
        assertEquals(Response.Status.OK.getStatusCode(), editResponse.getStatus());

        // Check comment was changed
        recordedComment = resolver.getComments(username).get(0);
        assertEquals(newComment, recordedComment.getCommentContents());
    }

    @Test
    public void addReplyTest() throws InvalidResourceRequestException {
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

        // Check data-store has comment recorded
        List<Comment> comments = resolver.getComments(username);
        assertEquals(2, comments.size());

        // Ensure correct contents
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getCommentContents());
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
        commentsResponse = apiClient.addComment(commentId, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Check data-store has comments recorded
        List<Comment> comments = resolver.getComments(username);
        assertEquals(2, comments.size());

        // Ensure correct contents
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getCommentContents());
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
        commentsResponse = apiClient.addComment(commentId, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());
        long commentId2 = gson.fromJson(commentsResponse.readEntity(String.class), Receipt.class).getReferenceId();

        // Send reply request to previous reply. It will contain same contents as first comment.
        commentsResponse = apiClient.addComment(commentId2, REPLY, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Check data-store has comments recorded
        List<Comment> comments = resolver.getComments(username);
        assertEquals(3, comments.size());

        // Ensure correct contents
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getCommentContents());
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
    public void userRemoveReplyTest() throws InvalidResourceRequestException {
        // Add two users and login as second. Only the first user will be an admin.
        addUser(username);
        String user = username + "2";
        loginAndSetupNewUser(user);

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
        List<Comment> comments = resolver.getComments(user);
        assertEquals(2, comments.size());

        // Ensure correct contents
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getCommentContents());
        }

        // Remove reply because 'user' is author
        Response removeResponse = apiClient.removeComment(id);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), removeResponse.getStatus());

        // Check comment was removed
        comments = resolver.getComments(user);
        assertEquals(1, comments.size());
    }

    @Test
    public void get0CommentsFromUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Get all comments from user on server
        Response commentsResponse = apiClient.getAllComments(username);

        // Parse JSON
        String comments = commentsResponse.readEntity(String.class);
        Comment[] comm = gson.fromJson(comments, Comment[].class);
        assertEquals(0, comm.length);
    }

    @Test
    public void getAllUserCommentsTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send request to add comment to recently uploaded photo TWICE
        Response commentResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentResponse.getStatus());
        commentResponse = apiClient.addComment(id, PHOTO_COMMENT, comment);
        assertEquals(Response.Status.OK.getStatusCode(), commentResponse.getStatus());

        // Ask server for all comments made by this user
        Response commentsResponse = apiClient.getAllComments(username);
        assertEquals(Response.Status.OK.getStatusCode(), commentsResponse.getStatus());

        // Parse comments array and ensure it has two elements in it
        Comment[] comments = gson.fromJson(commentsResponse.readEntity(String.class), Comment[].class);
        assertEquals(2, comments.length);

        // Check each comment. They're identical.
        for(Comment recordedComment : comments) {
            assertEquals(comment, recordedComment.getCommentContents());
        }
    }

    @Test
    public void getAllPhotoCommentsTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
            assertEquals(comment, recordedComment.getCommentContents());
        }
    }

    @Test
    public void getAllRepliesTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
        assertEquals(comment, comments[0].getCommentContents());
    }

    @Test
    public void get0PhotoCommentsTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
        loginAndSetupNewUser(username);

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
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
