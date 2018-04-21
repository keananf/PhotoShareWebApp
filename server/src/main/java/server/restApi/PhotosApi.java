package server.restApi;

import com.google.gson.Gson;
import server.datastore.exceptions.DoesNotOwnAlbumException;
import server.objects.Auth;
import server.Resources;
import server.requests.*;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.Photo;
import server.objects.Receipt;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static server.ServerMain.RESOLVER;

/**
 * Class describing the behaviour of the api at PHOTOS_PATH
 */
@Path(Resources.PHOTOS_PATH)
public class PhotosApi {
    private final Gson gson = new Gson();

    /**
     * Attempts to parse the message and upload a photo
     *
     * @param message the auth information and encoded photo contents
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.UPLOAD_PHOTO)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response uploadPhoto(String message) {
        // Retrieve request wrapper
        try {
            UploadPhotoRequest request = gson.fromJson(message, UploadPhotoRequest.class);

            // Retrieve provided auth info
            Auth auth = request.getAuth();
            RESOLVER.verifyAuth(Resources.UPLOAD_PHOTO_PATH, auth);

            // Upload encoded photo to the data store
            Receipt receipt = RESOLVER.uploadPhoto(request.getEncodedPhotoContents(),
                    request.getPhotoName(), auth.getUser(), request.getAlbumId());
            return Response.ok(gson.toJson(receipt)).build();
        }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
        catch(InvalidResourceRequestException | DoesNotOwnAlbumException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * @param username the provided username in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a parsed list of all photos from the requested user in the system
     */
    @POST
    @Path(Resources.USERS_PATH + "/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllPhotosFromUser(@PathParam("username") String username, String jsonAuth) {
        // Retrieve provided auth info
        try {
            AuthRequest auth = gson.fromJson(jsonAuth, AuthRequest.class);
            String path = String.format("%s/%s", Resources.GET_USER_PHOTOS_PATH, username);
            RESOLVER.verifyAuth(path, auth.getAuth());

            // Retrieve list retrieved from data manipulation layer
            // and convert photos into JSON array
            List<Photo> photos = RESOLVER.getPhotos(username);
            return Response.ok(gson.toJson(photos)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param albumId the provided album ID in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a parsed list of all photos from the requested album in the system
     */
    @POST
    @Path(Resources.ALBUMS_PATH + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllPhotosFromAlbum(@PathParam("id") long albumId, String jsonAuth) {
        // Retrieve provided auth info
        try {
            AuthRequest auth = gson.fromJson(jsonAuth, AuthRequest.class);
            String path = String.format("%s/%s", Resources.GET_PHOTOS_BY_ALBUM_PATH, albumId);
            RESOLVER.verifyAuth(path, auth.getAuth());

            // Retrieve list retrieved from data manipulation layer
            // and convert photos into JSON array
            List<Photo> photos = RESOLVER.getPhotos(albumId);
            return Response.ok(gson.toJson(photos)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param message the serialised GetPhotoRequest
     * @return a parsed list of all photos from the requested user in the system
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
            String path = String.format("%s/%s", Resources.GET_PHOTO_BY_ID_PATH, id);
            RESOLVER.verifyAuth(path, auth);

            // Upload encoded photo to the data store
            Photo photo = RESOLVER.getPhoto(id);
            return Response.ok(gson.toJson(photo)).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}
