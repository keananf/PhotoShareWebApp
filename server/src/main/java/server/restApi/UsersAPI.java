package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.objects.Auth;
import server.requests.AddUserRequest;
import server.requests.AuthRequest;
import server.datastore.exceptions.ExistingException;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.User;
import server.requests.FollowUserRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsers(String jsonAuth) {
        // Retrieve provided auth info
        try {
            AuthRequest auth = gson.fromJson(jsonAuth, AuthRequest.class);
            RESOLVER.verifyAuth(Resources.USERS_PATH, auth.getAuth());
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
        AddUserRequest user = gson.fromJson(message, AddUserRequest.class);

        // Attempt to persist user in data store
        try {
            RESOLVER.addUser(new User(user.getUser(), user.getPassword()));
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
     * @param message the auth information
     * @return a response object containing the result of the request
     */
    @POST
    @Path(Resources.LOGIN_USER)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(String message) {
        // Parse message as a Auth object
        AuthRequest auth = gson.fromJson(message, AuthRequest.class);

        // Attempt to record new session in the data store,
        // and void any previous session.
        try {
            // Process request
            RESOLVER.loginUser(Resources.LOGIN_USER_PATH, auth.getAuth());

            // Serialise the session. Indicate status as accepted and pass the serialised Session
            return Response.noContent().build();
        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }

    /**
     *
     * Attempts to follow a user
     *
     * @param json the serialised FollowUserRequest passed as the request body.
     * @return a parsed list of all photos from the requested user in the system
     */

    @POST
    @Path(Resources.FOLLOW)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postFollow(String json) {

        // Retrieve provided auth info
        FollowUserRequest request = gson.fromJson(json, FollowUserRequest.class);


        // Retrieve provided auth info
        Auth auth = request.getAuth();

        try {

            // Process Request
            RESOLVER.verifyAuth( Resources.USERS_PATH + Resources.FOLLOW, auth);

            String userFrom = request.getUserFrom();
            String userTo = request.getUserTo();

            RESOLVER.followUser(userFrom, userTo);

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
     *
     * Attempts to unfollow user
     *
     * @param json the serialised FollowUserRequest passed as the request body.
     * @return a parsed list of all photos from the requested user in the system
     */

    @POST
    @Path(Resources.UNFOLLOW)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUnfollow(String json) {
        // Retrieve provided auth info

        FollowUserRequest request = gson.fromJson(json, FollowUserRequest.class);

        // Retrieve provided auth info
        Auth auth = request.getAuth();

        String userFrom = request.getUserFrom();
        String userTo = request.getUserTo();

        try {

            // Process Request
            RESOLVER.verifyAuth(Resources.USERS_PATH + Resources.UNFOLLOW, auth);
            RESOLVER.unfollowUser(userFrom, userTo);

        } catch (UnauthorisedException e) {

            return Response.status(Response.Status.UNAUTHORIZED).build();

        } catch (InvalidResourceRequestException e) {

            return Response.status(Response.Status.BAD_REQUEST).build();
        }


        return Response.noContent().build();
    }
}
