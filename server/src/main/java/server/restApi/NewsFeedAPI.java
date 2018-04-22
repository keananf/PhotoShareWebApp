package server.restApi;

import com.google.gson.Gson;
import server.Resources;
import server.objects.Album;
import server.objects.Auth;
import server.objects.Photo;
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
 * Class describing the behaviour of the api at NEWS_FEED_PATH
 *
 */
@Path(Resources.NEWS_FEED_PATH)
public final class NewsFeedAPI {
    // Json serialiser and deserialiser
    private final Gson gson = new Gson();

    /**
     *
     * @return all the photos posted by the people a user is following
     */
    @POST
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsers(@PathParam("username") String username, String jsonAuth) {
        // Retrieve provided auth info

        try {

            // Checking user is authenticated
            AuthRequest auth = gson.fromJson(jsonAuth, AuthRequest.class);
            String path = String.format("%s/%s", Resources.NEWS_FEED_PATH , username);
            RESOLVER.verifyAuth(path, auth.getAuth());

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