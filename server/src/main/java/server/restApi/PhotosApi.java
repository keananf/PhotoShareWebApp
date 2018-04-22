package server.restApi;

import com.google.gson.Gson;
import server.datastore.exceptions.DoesNotOwnAlbumException;
import server.datastore.exceptions.InvalidPhotoFormatException;
import server.datastore.exceptions.DoesNotOwnPhotoException;
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
            Receipt receipt = RESOLVER.uploadPhoto(auth.getUser(), request);
            return Response.ok(gson.toJson(receipt)).build();
        }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
        catch(InvalidResourceRequestException | DoesNotOwnAlbumException | InvalidPhotoFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path(Resources.DELETE_PHOTO + "/{photoId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removePhoto(@PathParam("photoId") long photoId, String message) {
        // Retrieve request wrapper
        try {
            // Retrieve provided auth info and verify it
            Auth auth = gson.fromJson(message, AuthRequest.class).getAuth();
            String path = String.format("%s/%s", Resources.DELETE_PHOTO_PATH, photoId);
            RESOLVER.verifyAuth(path, auth);

            // Upload comment to the data store
            RESOLVER.removePhoto(auth.getUser(), photoId);
            return Response.noContent().build();

        } catch (InvalidResourceRequestException | DoesNotOwnPhotoException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
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


    /**
     * Registers an upvote from the authorised user on the provided photoId, if it exists.
     * @param photoId the provided photoId in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a response indicating success / failure
     */
    @POST
    @Path(Resources.UPVOTE + "/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response upvote(@PathParam("photoId") long photoId, String jsonAuth) {
        // Retrieve provided auth info
        try {
            Auth auth = gson.fromJson(jsonAuth, AuthRequest.class).getAuth();
            String path = String.format("%s/%s", Resources.PHOTO_UPVOTE_PATH, photoId);
            RESOLVER.verifyAuth(path, auth);

            // Register upvote with server
            RESOLVER.ratePhoto(photoId, auth.getUser(), true);
            return Response.noContent().build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Registers a downvote from the authorised user on the provided photoId, if it exists.
     * @param photoId the provided photoId in the URL
     * @param jsonAuth the serialised AuthRequest passed as the request body.
     * @return a response indicating success / failure
     */
    @POST
    @Path(Resources.DOWNVOTE + "/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response downvote(@PathParam("photoId") long photoId, String jsonAuth) {
        // Retrieve provided auth info
        try {
            Auth auth = gson.fromJson(jsonAuth, AuthRequest.class).getAuth();
            String path = String.format("%s/%s", Resources.PHOTO_DOWNVOTE_PATH, photoId);
            RESOLVER.verifyAuth(path, auth);

            // Register downvote with server
            RESOLVER.ratePhoto(photoId, auth.getUser(), false);
            return Response.noContent().build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}
