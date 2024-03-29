package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.ExistingException;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.LoginResult;
import server.objects.Photo;
import server.objects.PhotoResult;
import server.objects.User;
import server.requests.AddOrLoginUserRequest;

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
 * Class describing the behaviour of the api at USERS_PATH
 */
@Path(Resources.USERS_PATH)
public final class UsersAPI {
    // Json serialiser and deserialiser
    private final Gson gson = new Gson();

    /**
     * @return a parsed list of all users in the system
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@Context HttpHeaders headers) {
        try {
            // Retrieve auth headers
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
            RESOLVER.verifyAuth(sender, apiKey, date);
        }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build(); }

        // Retrieve list retrieved from data manipulation layer
        // and convert users into JSON array
        List<User> users = RESOLVER.getUsers();
        return Response.ok(gson.toJson(users)).build();
    }

    /**
     * Attempts to parse the message as a user,
     * and adds the given user to the data store.
     *
     * @param message the message
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.ADD_USER)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(String message) {
        // Parse message as a AddOrLoginUserRequest
        AddOrLoginUserRequest request = gson.fromJson(message, AddOrLoginUserRequest.class);

        // Attempt to persist request in data store
        try {
            RESOLVER.addUser(request.getUsername(), request.getPassword());
        }
        catch (ExistingException e) {
            // User already exists. Return bad response code
            return Response.status(Response.Status.CONFLICT).build();
        }

        // Successful response
        return Response.noContent().build();
    }

    /**
     * Attempts to parse the message and log in the user
     *
     * @param message the serialised request info
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.LOGIN_USER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(String message) {
        // Parse message as a AddOrLoginUserRequest object
        AddOrLoginUserRequest request = gson.fromJson(message, AddOrLoginUserRequest.class);

        try {
            // Process request
            LoginResult user = RESOLVER.loginUser(request.getUsername(), request.getPassword());

            // Serialise the user info.
            return Response.ok(gson.toJson(user)).build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * @param username the provided username in the URL
     * @return a parsed list of all photos from the requested user in the system
     */
    @GET
    @Path("/{username}" + PHOTOS_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPhotosFromUser(@PathParam("username") String username, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);

            RESOLVER.verifyAuth(sender, apiKey, date);

            // Retrieve list retrieved from data manipulation layer
            List<Photo> photos = RESOLVER.getPhotos(username);

            // Find all top-level comments for each photo, and compose them into PhotoResult objects
            // This is converted into JSON and returned
            List<PhotoResult> result = photos.stream().map(p -> RESOLVER.getPhotoResult(sender, p)).collect(Collectors.toList());
            return Response.ok(gson.toJson(result)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     * Attempts to follow a user
     *
     * @return a parsed list of all photos from the requested user in the system
     */
    @PUT
    @Path(Resources.FOLLOW +"/{username}")
    public Response follow(@PathParam("username") String userTo, @Context HttpHeaders headers) {
        // Retrieve auth headers
        String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
        String sender = authHeader[0], apiKey = authHeader[1];
        String date = headers.getHeaderString(Resources.DATE_HEADER);

        try {
            // Process Request
            RESOLVER.verifyAuth(sender, apiKey, date);
            RESOLVER.followUser(sender, userTo);

        } catch (InvalidResourceRequestException ie) {

            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch (UnauthorisedException e) {

            return Response.status(Response.Status.UNAUTHORIZED).build();

        } catch (ExistingException e){
            return Response.status(Response.Status.CONFLICT).build();
        }

        return Response.noContent().build();
    }

    /**
     * Attempts to unfollow user
     *
     * @return a parsed list of all photos from the requested user in the system
     */

    @DELETE
    @Path(Resources.UNFOLLOW + "/{username}")
    public Response unfollow(@PathParam("username") String userTo, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
        String sender = authHeader[0], apiKey = authHeader[1];
        String date = headers.getHeaderString(Resources.DATE_HEADER);

        try {
            // Process Request
            RESOLVER.verifyAuth(sender, apiKey, date);
            RESOLVER.unfollowUser(sender, userTo);

        } catch (UnauthorisedException e) {

            return Response.status(Response.Status.UNAUTHORIZED).build();

        } catch (InvalidResourceRequestException e) {

            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.noContent().build();
    }

    /**
     * Gets a list of persons (Users) the user is currently following
     * @param username
     * @return
     */
    @GET
    @Path(Resources.FOLLOWING + "/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFollowing(@PathParam("username") String username, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
        String sender = authHeader[0], apiKey = authHeader[1];
        String date = headers.getHeaderString(Resources.DATE_HEADER);

        try {
            // Process request
            RESOLVER.verifyAuth(sender, apiKey, date);
            RESOLVER.getFollowers(username);

            List<User> following = RESOLVER.getFollowing(username);
            return Response.ok(gson.toJson(following)).build();

        } catch (InvalidResourceRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (UnauthorisedException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Gets a list of persons (Users) who follow the user
     * @param username
     * @return
     */
    @GET
    @Path(Resources.FOLLOWERS + "/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFollowers(@PathParam("username") String username, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
        String sender = authHeader[0], apiKey = authHeader[1];
        String date = headers.getHeaderString(Resources.DATE_HEADER);

        try {
            // Process request
            RESOLVER.verifyAuth(sender, apiKey, date);
            RESOLVER.getFollowers(username);

            List<User> following = RESOLVER.getFollowers(username);
            return Response.ok(gson.toJson(following)).build();

        } catch (InvalidResourceRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (UnauthorisedException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }

    @GET
    @Path(SEARCH)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bar(@QueryParam(NAME_PARAM) String value, @Context HttpHeaders headers) {

        // Retrieve provided auth info
        String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
        String sender = authHeader[0], apiKey = authHeader[1];
        String date = headers.getHeaderString(Resources.DATE_HEADER);


        try {
            // Process request
            RESOLVER.verifyAuth(sender, apiKey, date);

            List<User> users = RESOLVER.getUsersWithName(value);

            return Response.ok(gson.toJson(users)).build();

        } catch (UnauthorisedException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
