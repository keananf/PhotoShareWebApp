package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
     * @return a response object containing the result of the request
     */
    @DELETE
    @Path(Resources.REMOVE_COMMENT + "/{commentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeComment(@PathParam("commentId") long commentId, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            String path = String.format("%s/%s", Resources.ADMIN_REMOVE_COMMENT_PATH, commentId);
            RESOLVER.verifyAdminAuth(path, sender, apiKey, date);

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
     * @return a response object containing the result of the request
     */
    @DELETE
    @Path(Resources.REMOVE_PHOTO + "/{photoId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removePhoto(@PathParam("photoId") long photoId, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            String path = String.format("%s/%s", Resources.ADMIN_REMOVE_PHOTO_PATH, photoId);
            RESOLVER.verifyAdminAuth(path, sender, apiKey, date);

            // Delete photo from data store
            RESOLVER.removePhotoAdmin(photoId);
            return Response.noContent().build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}