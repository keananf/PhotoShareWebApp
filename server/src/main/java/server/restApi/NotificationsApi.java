package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.Notification;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
     * @return a parsed list of all users in the system
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getNotifications(@Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(HttpHeaders.DATE);

            RESOLVER.verifyAuth(Resources.NOTIFICATIONS_PATH, sender, apiKey, date);

            // Retrieve list retrieved from data manipulation layer
            // and convert notifications into JSON array
            List<Notification> notifications = RESOLVER.getNotifications(sender);
            return Response.ok(gson.toJson(notifications)).build();

        }
        catch(InvalidResourceRequestException e) { return Response.status(Response.Status.BAD_REQUEST).build(); }
        catch(UnauthorisedException e) { return Response.status(Response.Status.UNAUTHORIZED).build();}
    }
}