package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.ExistingException;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.Photo;
import server.objects.User;
import server.requests.AddUserRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsers(@Context HttpHeaders headers) {
        try {
            // Retrieve auth headers
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(HttpHeaders.DATE);

            RESOLVER.verifyAuth(Resources.USERS_PATH, sender, apiKey, date);
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
        // Parse message as a User object
        AddUserRequest request = gson.fromJson(message, AddUserRequest.class);

        // Attempt to persist request in data store
        try {
            RESOLVER.addUser(new User(request.getUser(), request.getPassword()));
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
     * @return a response object containing the result of the request
     */
    @GET
    @Path(Resources.LOGIN_USER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(@Context HttpHeaders headers) {
        // Retrieve auth headers
        String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
        String sender = authHeader[0], apiKey = authHeader[1];
        String date = headers.getHeaderString(HttpHeaders.DATE);

        // Attempt to record new session in the data store,
        // and void any previous session.
        try {
            // Process request
            User user = RESOLVER.loginUser(Resources.LOGIN_USER_PATH, sender, apiKey, date);

            // Serialise the session. Indicate status as accepted and pass the serialised Session
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
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllPhotosFromUser(@PathParam("username") String username, @Context HttpHeaders headers) {

        System.out.println("Yo");


        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];

            String date = headers.getHeaderString(HttpHeaders.DATE);

            String path = String.format("%s/%s", USERS_PATH, sender) + PHOTOS_PATH;
            RESOLVER.verifyAuth(path, sender, apiKey, date);


            System.out.println(path);

            // Retrieve list retrieved from data manipulation layer
            // and convert photos into JSON array
            List<Photo> photos = RESOLVER.getPhotos(username);
            return Response.ok(gson.toJson(photos)).build();

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
        String date = headers.getHeaderString(HttpHeaders.DATE);

        try {
            // Process Request
            RESOLVER.verifyAuth(Resources.USERS_FOLLOWING_PATH + "/" + userTo, sender, apiKey, date);
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
        String date = headers.getHeaderString(HttpHeaders.DATE);

        try {
            // Process Request
            RESOLVER.verifyAuth(Resources.USERS_FOLLOWING_PATH + "/" + userTo, sender, apiKey, date);
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
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFollowing(@PathParam("username") String username, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
        String sender = authHeader[0], apiKey = authHeader[1];
        String date = headers.getHeaderString(HttpHeaders.DATE);

        try {
            // Process request
            String path = String.format("%s/%s", USERS_FOLLOWING_PATH , username);
            RESOLVER.verifyAuth(path, sender, apiKey, date);
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
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getFollowers(@PathParam("username") String username, @Context HttpHeaders headers) {
        // Retrieve provided auth info
        String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
        String sender = authHeader[0], apiKey = authHeader[1];
        String date = headers.getHeaderString(HttpHeaders.DATE);

        try {
            // Process request
            String path = String.format("%s/%s", USERS_FOLLOWERS_PATH , username);
            RESOLVER.verifyAuth(path, sender, apiKey, date);
            RESOLVER.getFollowers(username);

            List<User> following = RESOLVER.getFollowers(username);
            return Response.ok(gson.toJson(following)).build();

        } catch (InvalidResourceRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (UnauthorisedException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}