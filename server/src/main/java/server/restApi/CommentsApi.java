package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.requests.*;
import server.objects.*;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
    public Response addComment(String message) {
        // Retrieve request wrapper
        try {
            AddCommentRequest request = gson.fromJson(message, AddCommentRequest.class);

            // Retrieve provided auth info
            Auth auth = request.getAuth();
            RESOLVER.verifyAuth(Resources.ADD_COMMENT_PATH, auth);

            // Upload comment to the data store
            Receipt receipt = RESOLVER.addComment(auth.getUser(), request);
            return Response.ok(gson.toJson(receipt)).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Attempts to parse the message and edit a comment
     *
     * @param message the auth information and serialised comment
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.ADD_COMMENT)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editComment(String message) {
        // Retrieve request wrapper
        try {
            AddCommentRequest request = gson.fromJson(message, AddCommentRequest.class);

            // Retrieve provided auth info
            Auth auth = request.getAuth();
            RESOLVER.verifyAuth(Resources.EDIT_COMMENT_PATH, auth);

            // Upload comment to the data store
            Receipt receipt = RESOLVER.editComment(auth.getUser(), request);
            return Response.ok(gson.toJson(receipt)).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param username the provided username in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a parsed list of all comments from the requested user in the system
     */
    @POST
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllUserComments(@PathParam("username") String username, String jsonAuth) {
        // Retrieve provided auth info
        try {
            AuthRequest auth = gson.fromJson(jsonAuth, AuthRequest.class);
            String path = String.format("%s/%s", Resources.COMMENTS_PATH, username);
            RESOLVER.verifyAuth(path, auth.getAuth());

            // Retrieve list retrieved from data manipulation layer
            // and convert comments into JSON array
            List<Comment> comments = RESOLVER.getComments(username);
            return Response.ok(gson.toJson(comments)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param photoId the provided photoId in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a parsed list of all comments on the requested photo in the system
     */
    @POST
    @Path(Resources.PHOTOS_PATH + "/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTopLevelPhotoComments(@PathParam("photoId") long photoId, String jsonAuth) {
        // Retrieve provided auth info
        try {
            AuthRequest auth = gson.fromJson(jsonAuth, AuthRequest.class);
            String path = String.format("%s/%s", Resources.GET_ALL_PHOTO_COMMENTS_PATH, photoId);
            RESOLVER.verifyAuth(path, auth.getAuth());

            // Retrieve list retrieved from data manipulation layer
            // and convert comments into JSON array
            List<Comment> comments = RESOLVER.getPhotoComments(auth.getAuth().getUser(), photoId);
            return Response.ok(gson.toJson(comments)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param commentId the provided commentId in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a parsed list of all replies to the requested comment in the system
     */
    @POST
    @Path(Resources.REPLY_PATH + "/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getTopLevelReplies(@PathParam("commentId") long commentId, String jsonAuth) {
        // Retrieve provided auth info
        try {
            AuthRequest auth = gson.fromJson(jsonAuth, AuthRequest.class);
            String path = String.format("%s/%s", Resources.GET_ALL_REPLIES_PATH, commentId);
            RESOLVER.verifyAuth(path, auth.getAuth());

            // Retrieve list retrieved from data manipulation layer
            // and convert comments into JSON array
            List<Comment> comments = RESOLVER.getReplies(auth.getAuth().getUser(), commentId);
            return Response.ok(gson.toJson(comments)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Registers an upvote from the authorised user on the provided commentId, if it exists.
     * @param commentId the provided commentId in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a response indicating success / failure
     */
    @POST
    @Path(Resources.UPVOTE + "/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response upvote(@PathParam("commentId") long commentId, String jsonAuth) {
        // Retrieve provided auth info
        try {
            Auth auth = gson.fromJson(jsonAuth, AuthRequest.class).getAuth();
            String path = String.format("%s/%s", Resources.UPVOTE_PATH, commentId);
            RESOLVER.verifyAuth(path, auth);

            // Register upvote with server
            RESOLVER.vote(commentId, auth.getUser(), true);
            return Response.noContent().build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Registers a downvote from the authorised user on the provided commentId, if it exists.
     * @param commentId the provided commentId in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a response indicating success / failure
     */
    @POST
    @Path(Resources.DOWNVOTE + "/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response downvote(@PathParam("commentId") long commentId, String jsonAuth) {
        // Retrieve provided auth info
        try {
            Auth auth = gson.fromJson(jsonAuth, AuthRequest.class).getAuth();
            String path = String.format("%s/%s", Resources.DOWNVOTE_PATH, commentId);
            RESOLVER.verifyAuth(path, auth);

            // Register downvote with server
            RESOLVER.vote(commentId, auth.getUser(), false);
            return Response.noContent().build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}
