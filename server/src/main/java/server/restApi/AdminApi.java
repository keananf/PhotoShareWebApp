package server.restApi;

import com.google.gson.Gson;
import server.objects.Auth;
import server.Resources;
import server.requests.AuthRequest;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.SQLException;

import static server.ServerMain.RESOLVER;

/**
 * Class representing the Admin API.
 */
@Path(Resources.ADMIN_PATH)
public class AdminApi {
    private final Gson gson = new Gson();

    /**
     * Attempts to parse the message and remove a comment
     *
     * @param commentId the provided comment id
     * @param message the auth information and serialised comment
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.REMOVE_COMMENT + "/{commentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeComment(@PathParam("commentId") long commentId, String message) {
        // Retrieve request wrapper
        try {
            // Retrieve provided auth info and verify it
            Auth auth = gson.fromJson(message, AuthRequest.class).getAuth();
            String path = String.format("%s/%s", Resources.ADMIN_REMOVE_COMMENT_PATH, commentId);
            RESOLVER.verifyAdminAuth(path, auth);

            // Delete comment from the data store
            RESOLVER.removeCommentAdmin(commentId);
            return Response.noContent().build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Attempts to parse the photo and remove it
     *
     * @param photoId the provided comment id
     * @param message the auth information
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.REMOVE_PHOTO + "/{photoId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removePhoto(@PathParam("photoId") long photoId, String message) {
        // Retrieve request wrapper
        try {
            // Retrieve provided auth info and verify it
            Auth auth = gson.fromJson(message, AuthRequest.class).getAuth();
            String path = String.format("%s/%s", Resources.ADMIN_REMOVE_PHOTO_PATH, photoId);
            RESOLVER.verifyAdminAuth(path, auth);

            // Delete photo from data store
            RESOLVER.removePhotoAdmin(photoId);
            return Response.noContent().build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}
