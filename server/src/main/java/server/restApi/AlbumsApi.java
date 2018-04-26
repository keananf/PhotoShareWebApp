package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.DoesNotOwnAlbumException;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.Album;
import server.objects.Receipt;
import server.requests.AddAlbumRequest;
import server.requests.UpdateAlbumDescriptionRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static server.ServerMain.RESOLVER;

/**
 * Class describing the behaviour of the api at ALBUMS_PATH
 */
@Path(Resources.ALBUMS_PATH)
public class AlbumsApi {
    private final Gson gson = new Gson();

    /**
     * Attempts to parse the message and add an album
     *
     * @param message the auth information and new album information
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.ADD_ALBUM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAlbum(String message, @Context HttpHeaders headers) {
        // Retrieve request wrapper
        try {
            AddAlbumRequest request = gson.fromJson(message, AddAlbumRequest.class);

            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Upload new album to the data store
            Receipt receipt = RESOLVER.addAlbum(sender, request.getAlbumName(), request.getDescription());
            return Response.ok(gson.toJson(receipt)).build();
        }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
    }


    /**
     * Attempts to update a given album's description
     *
     * @param message the auth information and new album information
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.UPDATE_ALBUM_DESCRIPTION)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAlbumDescription(String message, @Context HttpHeaders headers) {
        // Retrieve request wrapper
        try {
            UpdateAlbumDescriptionRequest request = gson.fromJson(message, UpdateAlbumDescriptionRequest.class);

            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Upload new description to data store
            RESOLVER.updateAlbumDescription(sender, request.getAlbumId(), request.getDescription());
            return Response.noContent().build();
        }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
        catch(InvalidResourceRequestException | DoesNotOwnAlbumException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves all albums a user has made
     * @param user the provided username in the URL
     * @return a parsed list of all albums from the requested user in the system
     */
    @GET
    @Path(Resources.USERS_PATH + "/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAlbums(@PathParam("username") String user, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        try {
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Retrieve list retrieved from data manipulation layer
            // and convert albums into JSON array
            List<Album> albums = RESOLVER.getAlbums(user);
            return Response.ok(gson.toJson(albums)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Retrieves a given photo from the server
     * @return the serialised response
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPhoto(@PathParam("id") long id, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Upload encoded album to the data store
            Album album = RESOLVER.getAlbum(id);
            return Response.ok(gson.toJson(album)).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}