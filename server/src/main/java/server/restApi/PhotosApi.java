package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.DoesNotOwnAlbumException;
import server.datastore.exceptions.DoesNotOwnPhotoException;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.Photo;
import server.objects.Receipt;
import server.requests.UploadPhotoRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static server.Resources.PHOTOS_PATH;
import static server.ServerMain.RESOLVER;

/**
 * Class describing the behaviour of the api at PHOTOS_PATH
 */
@Path(PHOTOS_PATH)
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
    public Response uploadPhoto(String message, @Context HttpHeaders headers) {
        // Retrieve request wrapper
        try {
            UploadPhotoRequest request = gson.fromJson(message, UploadPhotoRequest.class);

            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(HttpHeaders.DATE);

            RESOLVER.verifyAuth(Resources.UPLOAD_PHOTO_PATH, sender, apiKey, date);

            // Upload encoded photo to the data store
            Receipt receipt = RESOLVER.uploadPhoto(sender, request);
            return Response.ok(gson.toJson(receipt)).build();
        }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
        catch(InvalidResourceRequestException | DoesNotOwnAlbumException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path(Resources.DELETE_PHOTO + "/{photoId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removePhoto(@PathParam("photoId") long photoId, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(HttpHeaders.DATE);

            String path = String.format("%s/%s", Resources.DELETE_PHOTO_PATH, photoId);
            RESOLVER.verifyAuth(path, sender, apiKey, date);

            // Upload comment to the data store
            RESOLVER.removePhoto(sender, photoId);
            return Response.noContent().build();

        } catch (InvalidResourceRequestException | DoesNotOwnPhotoException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param albumId the provided album ID in the URL
     * @return a parsed list of all photos from the requested album in the system
     */
    @GET
    @Path(Resources.ALBUMS_PATH + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllPhotosFromAlbum(@PathParam("id") long albumId, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(HttpHeaders.DATE);

            String path = String.format("%s/%s", Resources.GET_PHOTOS_BY_ALBUM_PATH, albumId);
            RESOLVER.verifyAuth(path, sender, apiKey, date);

            // Retrieve list retrieved from data manipulation layer
            // and convert photos into JSON array
            List<Photo> photos = RESOLVER.getPhotos(albumId);
            return Response.ok(gson.toJson(photos)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @return the requested photo, serialised in JSON
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPhoto(@PathParam("id") long id, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(HttpHeaders.DATE);

            String path = String.format("%s/%s", PHOTOS_PATH, id);
            RESOLVER.verifyAuth(path, sender, apiKey, date);

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
     * @param headers the headers of the http request.
     * @return a response indicating success / failure
     */
    @PUT
    @Path(Resources.UPVOTE + "/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response upvote(@PathParam("photoId") long photoId, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        try {
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(HttpHeaders.DATE);

            String path = String.format("%s/%s", Resources.PHOTO_UPVOTE_PATH, photoId);
            RESOLVER.verifyAuth(path, sender, apiKey, date);

            // Register upvote with server
            RESOLVER.ratePhoto(photoId, sender, true);
            return Response.noContent().build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Registers a downvote from the authorised user on the provided photoId, if it exists.
     * @param photoId the provided photoId in the URL
     * @return a response indicating success / failure
     */
    @PUT
    @Path(Resources.DOWNVOTE + "/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response downvote(@PathParam("photoId") long photoId, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        try {
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(HttpHeaders.DATE);

            String path = String.format("%s/%s", Resources.PHOTO_DOWNVOTE_PATH, photoId);
            RESOLVER.verifyAuth(path, sender, apiKey, date);

            // Register downvote with server
            RESOLVER.ratePhoto(photoId, sender, false);
            return Response.noContent().build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}