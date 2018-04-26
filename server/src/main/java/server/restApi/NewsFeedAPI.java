package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.datastore.exceptions.UnauthorisedException;
import server.objects.Photo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    public Response getUsers(@PathParam("username") String username, @Context HttpHeaders headers) {
        try {
            // Retrieve provided auth info
            String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
            String[] authHeaderComponents = authHeader.split(":");
            String sender = authHeaderComponents[0], apiKey = authHeaderComponents[1];
            String date = headers.getHeaderString(Resources.DATE_HEADER);
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