package client;

import server.Auth;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static javax.ws.rs.core.HttpHeaders.*;

/**
 * Class which connects clients to URLs and
 * facilitates all requests to the RESTful PhotoShare server
 */
class Connector {

    private String user, password;

    /**
     * Performs a GET request on the provided path
     *
     * @param baseTarget the web target for the base url
     * @param path       the path to GET from
     */
    Response get(WebTarget baseTarget, String path) {
        // Get API resource
        WebTarget pathTarget = baseTarget.path(path);

        // Get result and return
        return headers(pathTarget.request(), path).get();
    }

    private Invocation.Builder headers(Invocation.Builder request, String path) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            // Get current time
            long time = System.currentTimeMillis();
            Date d = format.parse(format.format(new Date(time)));
            time = d.getTime();

            return request.header(USER_AGENT, user).header(DATE, format.format(d))
                    .header(AUTHORIZATION, Auth.getApiKey(path, user, password, time));
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        return request;
    }

    /**
     * Performs a DELETE request on the provided path
     *
     * @param baseTarget the web target for the base url
     * @param path       the path to GET from
     */
    Response delete(WebTarget baseTarget, String path) {
        // Get API resource
        WebTarget pathTarget = baseTarget.path(path);

        // Get result and return
        return headers(pathTarget.request(), path).delete();
    }

    /**
     * Performs a PUT request on the provided path
     *
     * @param baseTarget the web target for the base url
     * @param path       the path to GET from
     */
    Response put(WebTarget baseTarget, String path) {
        // Get API resource
        WebTarget pathTarget = baseTarget.path(path);

        // Post the message to the research and check the status.
        Response result = headers(pathTarget.request(), path).put(Entity.entity("{}", MediaType.APPLICATION_JSON));
        return result;
    }

    /**
     * Posts the given message body to the given path
     *
     * @param baseTarget the web target for the base url
     * @param path       the path to post the message to
     * @param message    the message to post
     */
    Response post(WebTarget baseTarget, String path, String message) {
        // Get API resource
        WebTarget pathTarget = baseTarget.path(path);

        // Post the message to the research and check the status.
        Response result = headers(pathTarget.request(), path).post(Entity.entity(message, MediaType.APPLICATION_JSON));
        return result;
    }

    void setUserAndPw(String user, String password) {
        this.user = user;
        this.password = password;
    }
}
