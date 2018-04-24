package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.Photo;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static server.ServerMain.RESOLVER;

/**
 * Class describing the behaviour of the api at NEWS_FEED_PATH
 *
 */
@Path(Resources.NEWS_FEED_PATH)
public final class NewsFeedAPI {
    // Json serialiser and deserialiser
    private final Gson gson = new Gson();

    /**
     * @return all the photos posted by the people a user is following
     */
    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsers(@PathParam("username") String username, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String[] authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION).split(":");
            String sender = authHeader[0], apiKey = authHeader[1];
            String date = headers.getHeaderString(HttpHeaders.DATE);
            String path = String.format("%s/%s", Resources.NEWS_FEED_PATH , username);
            RESOLVER.verifyAuth(path, sender, apiKey, date);

            // Processing Request
            List<Photo> photos = RESOLVER.getNewsFeed(username);
            return Response.ok(gson.toJson(photos)).build();

        }
        catch(UnauthorisedException e) {

            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (InvalidResourceRequestException e) {

            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }


}