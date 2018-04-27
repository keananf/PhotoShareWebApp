package server.restApi;

import com.google.gson.Gson;
import server.datastore.exceptions.DoesNotOwnAlbumException;
import server.datastore.exceptions.DoesNotOwnPhotoException;
import server.Resources;
import server.datastore.exceptions.*;
import server.objects.Photo;
import server.objects.PhotoResult;
import server.objects.Receipt;
import server.requests.UpdateDescriptionRequest;
import server.requests.UploadPhotoRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static server.Resources.*;
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
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Upload encoded photo to the data store
            Receipt receipt = RESOLVER.uploadPhoto(sender, request, date);
            return Response.ok(gson.toJson(receipt)).build();
        }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
        catch(InvalidResourceRequestException | DoesNotOwnAlbumException | InvalidFileFormatException e) {
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
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

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
    public Response getAllPhotosFromAlbum(@PathParam("id") long albumId, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Retrieve list retrieved from data manipulation layer
            List<Photo> photos = RESOLVER.getPhotos(albumId);

            // Find all top-level comments for each photo, and compose them into PhotoResult objects
            // This is converted into JSON and returned
            List<PhotoResult> result = photos.stream().map(p -> RESOLVER.getPhotoResult(sender, p)).collect(Collectors.toList());
            return Response.ok(gson.toJson(result)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @return the requested photo contents, sent as raw data
     */
    @GET
    @Path(PHOTO_CONTENTS + PNG + "/{id}")
    @Produces("image/png")
    public Response getPhotoContentsPNG(@PathParam("id") String idAndExt, @Context HttpHeaders headers) {
        // Parse parameter in form id.ext into an id and file extension
        String[] components = idAndExt.split("\\.");
        long id = Long.parseLong(components[0]);
        String ext = components[1];
        if(!ext.toLowerCase(RESOLVER.LOCALE).equals("png")) return Response.status(Response.Status.BAD_REQUEST).build();

        try {
            // Send the given photo's contents back to the client, while ensuring it is
            // decoded from base64 to its raw representation
            byte[] contents = UploadPhotoRequest.decodeContents(RESOLVER.getPhotoContents(id, ext));
            return Response.ok(contents).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
    }


    /**
     * @return the requested photo contents, sent as raw data
     */
    @GET
    @Path(PHOTO_CONTENTS + JPG + "/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("image/jpg")
    public Response getPhotoContentsJPG(@PathParam("id") String idAndExt, @Context HttpHeaders headers) {
        // Parse parameter in form id.ext into an id and file extension
        String[] components = idAndExt.split("\\.");
        long id = Long.parseLong(components[0]);
        String ext = components[1];
        if(!ext.toLowerCase(RESOLVER.LOCALE).equals("jpg")) return Response.status(Response.Status.BAD_REQUEST).build();

        try {
            // Send the given photo's contents back to the client, while ensuring it is
            // decoded from base64 to its raw representation
            byte[] contents = UploadPhotoRequest.decodeContents(RESOLVER.getPhotoContents(id, ext));
            return Response.ok(contents).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
    }

    /**
     * @return the requested meta-data, sent as JSON.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPhotoMetaData(@PathParam("id") long id, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Send the given photo's meta data back to the client
            Photo photo = RESOLVER.getPhotoMetaData(id);

            // Find all top-level comments for the photo
            PhotoResult result = RESOLVER.getPhotoResult(sender, photo);
            return Response.ok(gson.toJson(result)).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    @POST
    @Path(Resources.UPDATE_PHOTO_DESCRIPTION)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editPhotoDescription(String message, @Context HttpHeaders headers) {
        try {
            UpdateDescriptionRequest request = gson.fromJson(message, UpdateDescriptionRequest.class);

            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Update description of relevant photo to the data store
            RESOLVER.updatePhotoDescription(sender, request.getId(), request.getDescription());
            return Response.noContent().build();
        }
        catch(InvalidResourceRequestException | DoesNotOwnPhotoException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Registers a like from the authorised user on the provided photoId, if it exists.
     * @param photoId the provided photoId in the URL
     * @param headers the headers of the http request.
     * @return a response indicating success / failure
     */
    @PUT
    @Path(Resources.LIKE + "/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response like(@PathParam("photoId") long photoId, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        try {
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Register like with server
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
    @Path(Resources.UNLIKE + "/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unlike(@PathParam("photoId") long photoId, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        try {
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);

            // Register unlike with server
            RESOLVER.ratePhoto(photoId, sender, false);
            return Response.noContent().build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}