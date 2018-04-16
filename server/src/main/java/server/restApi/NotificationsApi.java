package server.restApi;

import com.google.gson.Gson;

import common.Resources;
import common.requests.*;
import common.*;
import server.objects.*;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static server.ServerMain.RESOLVER;


/**
 * Class describing the behaviour of the api at NOTIFICATIONS_PATH
 */
@Path(Resources.NOTIFICATIONS_PATH)
public class NotificationsApi {
    // Json serialiser and deserialiser
    private final Gson gson = new Gson();

    /**
     * Grabs notifications for the given user
     * @param jsonAuth the serialised auth information
     * @return a parsed list of all users in the system
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getNotifications(String jsonAuth) {
        // Retrieve provided auth info
        try {
            AuthRequest auth = gson.fromJson(jsonAuth, AuthRequest.class);
            RESOLVER.verifyAuth(Resources.NOTIFICATIONS_PATH, auth.getAuth());

            // Retrieve list retrieved from data manipulation layer
            // and convert notifications into JSON array
            List<Notification> notifications = RESOLVER.getNotifications(auth.getAuth().getUser());
            return Response.ok(gson.toJson(notifications)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}