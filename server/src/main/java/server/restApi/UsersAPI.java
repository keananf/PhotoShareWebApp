package server.restApi;

import com.google.gson.Gson;
import common.Resources;
import common.requests.AddUserRequest;
import common.requests.AuthRequest;
import server.datastore.exceptions.ExistingException;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}
