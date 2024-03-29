package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.DoesNotOwnCommentException;
import server.requests.*;
import server.objects.*;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static server.ServerMain.RESOLVER;

/**
 * Class describing the behaviour of the api at COMMENTS_PATH
 */
@Path(Resources.COMMENTS_PATH)
public class CommentsApi {
    private final Gson gson = new Gson();

    /**
     * Attempts to parse the message and add a comment
     *
     * @param message the auth information and serialised comment
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.ADD_COMMENT)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addComment(String message, @Context HttpHeaders headers) {
        // Retrieve request wrapper
        try {
            AddCommentRequest request = gson.fromJson(message, AddCommentRequest.class);

            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Upload comment to the data store
            Receipt receipt = RESOLVER.addComment(sender, request, date);
            return Response.ok(gson.toJson(receipt)).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Allows users to delete their own comments
     *
     * @param commentId the ID of the comment to be removed, provided in the URL
     * @return a response object containing the result of the request
     */
    @DELETE
    @Path(Resources.DELETE_COMMENT + "/{commentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeComment(@PathParam("commentId") long commentId, @Context HttpHeaders headers) {

        // Retrieve request wrapper
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Upload comment to the data store
            RESOLVER.removeComment(sender, commentId);
            return Response.noContent().build();

        } catch (InvalidResourceRequestException | DoesNotOwnCommentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /** Attempts to parse the message and edit a comment
     *
     * @param message the auth information and serialised comment
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.EDIT_COMMENT + "/{commentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editComment(@PathParam("commentId") long commentId, String message, @Context HttpHeaders headers) {
        // Retrieve request wrapper
        try {
            EditCommentRequest request = gson.fromJson(message, EditCommentRequest.class);

            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Update comment in the data store
            Receipt receipt = RESOLVER.editComment(sender, commentId, request);
            return Response.ok(gson.toJson(receipt)).build();

        } catch(InvalidResourceRequestException | DoesNotOwnCommentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param user the provided username in the URL
     * @return a parsed list of all comments from the requested user in the system
     */
    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUserComments(@PathParam("username") String user, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Retrieve list retrieved from data manipulation layer
            // and convert comments into JSON array
            List<Comment> comments = RESOLVER.getComments(user);

            // Find all top-level replies for each comment, and compose them into CommentResult objects
            // This is converted into JSON and returned
            List<CommentResult> result = comments.stream().map(c -> RESOLVER.getCommentResult(sender, c)).collect(Collectors.toList());
            return Response.ok(gson.toJson(result)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param photoId the provided photoId in the URL
     * @return a parsed list of all comments on the requested photo in the system
     */
    @GET
    @Path(Resources.PHOTOS_PATH + "/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopLevelPhotoComments(@PathParam("photoId") long photoId, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        try {
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Retrieve list retrieved from data manipulation layer
            // and convert comments into JSON array
            List<Comment> comments = RESOLVER.getPhotoComments(sender, photoId);

            // Find all top-level replies for each comment, and compose them into CommentResult objects
            // This is converted into JSON and returned
            List<CommentResult> result = comments.stream().map(c -> RESOLVER.getCommentResult(sender, c)).collect(Collectors.toList());
            return Response.ok(gson.toJson(result)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param commentId the provided commentId in the URL
     * @return a parsed list of all replies to the requested comment in the system
     */
    @GET
    @Path(Resources.REPLY_PATH + "/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopLevelReplies(@PathParam("commentId") long commentId, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        try {
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Retrieve list retrieved from data manipulation layer
            // and convert comments into JSON array
            List<Comment> comments = RESOLVER.getReplies(sender, commentId);

            // Find all top-level replies for each comment, and compose them into CommentResult objects
            // This is converted into JSON and returned
            List<CommentResult> result = comments.stream().map(c -> RESOLVER.getCommentResult(sender, c)).collect(Collectors.toList());
            return Response.ok(gson.toJson(result)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Registers an upvote from the authorised user on the provided commentId, if it exists.
     * @param commentId the provided commentId in the URL
     * @return a response indicating success / failure
     */
    @PUT
    @Path(Resources.LIKE + "/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response like(@PathParam("commentId") long commentId, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        try {
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Register like with server
            RESOLVER.voteOnComment(commentId, sender, true);
            return Response.noContent().build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Registers a downvote from the authorised user on the provided commentId, if it exists.
     * @param commentId the provided commentId in the URL
     * @return a response indicating success / failure
     */
    @PUT
    @Path(Resources.UNLIKE + "/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unlike(@PathParam("commentId") long commentId, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Register unlike with server
            RESOLVER.voteOnComment(commentId, sender, false);
            return Response.noContent().build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}