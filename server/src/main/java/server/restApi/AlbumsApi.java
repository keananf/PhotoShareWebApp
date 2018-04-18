package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.Album;
import server.objects.Auth;
import server.objects.Receipt;
import server.requests.AddAlbumRequest;
import server.requests.AuthRequest;

import javax.ws.rs.*;
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
    public Response addAlbum(String message) {
        // Retrieve request wrapper
        try {
            AddAlbumRequest request = gson.fromJson(message, AddAlbumRequest.class);

            // Retrieve and verify provided auth info
            Auth auth = request.getAuth();
            RESOLVER.verifyAuth(Resources.ADD_ALBUM_PATH, auth);

            // Upload new album to the data store
            Receipt receipt = RESOLVER.addAlbum(auth.getUser(), request.getAlbumName(), request.getDescription());
            return Response.ok(gson.toJson(receipt)).build();
        }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
    }

    /**
     * Retrieves all albums a user has made
     * @param username the provided username in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a parsed list of all albums from the requested user in the system
     */
    @POST
    @Path(Resources.USERS_PATH + "/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllAlbums(@PathParam("username") String username, String jsonAuth) {
        // Retrieve provided auth info
        try {
            AuthRequest auth = gson.fromJson(jsonAuth, AuthRequest.class);
            String path = String.format("%s/%s", Resources.GET_USER_ALBUMS_PATH, username);
            RESOLVER.verifyAuth(path, auth.getAuth());

            // Retrieve list retrieved from data manipulation layer
            // and convert albums into JSON array
            List<Album> albums = RESOLVER.getAlbums(username);
            return Response.ok(gson.toJson(albums)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Retrieves a given photo from the server
     * @param message the serialised auth information
     * @return the serialised response
     */
    @POST
    @Path(Resources.ID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPhoto(@PathParam("id") long id, String message) {
        // Retrieve request wrapper
        try {
            AuthRequest request = gson.fromJson(message, AuthRequest.class);

            // Retrieve provided auth info
            Auth auth = request.getAuth();
            String path = String.format("%s/%s", Resources.GET_ALBUM_BY_ID_PATH, id);
            RESOLVER.verifyAuth(path, auth);

            // Upload encoded album to the data store
            Album album = RESOLVER.getAlbum(id);
            return Response.ok(gson.toJson(album)).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}