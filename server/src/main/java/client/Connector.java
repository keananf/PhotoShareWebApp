package client;

import com.sun.xml.internal.xsom.impl.scd.SimpleCharStream;
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

    /**
     * Add the date and authorisation headers
     * @param request the request
     * @param path the endpoint
     * @return the request with the headers added
     */
    private Invocation.Builder headers(Invocation.Builder request, String path) {
        try {
            // The date format that the server expects
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            // Get current time. and convert it into the correct format while rounding to the nearest second.
            long time = System.currentTimeMillis();
            Date d = format.parse(format.format(new Date(time)));
            time = d.getTime();

            // Add header for encoded date and the apiKey
            return request.header(DATE, format.format(d))
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

    /**
     * Performs a GET request on the provided path with query
     *
     * @param baseTarget the web target for the base url
     * @param path       the path to GET from
     */
    protected Response getWithQuery(WebTarget baseTarget, String path, String queryKey, String queryValue) {

        // Get API resource
        WebTarget pathTarget = baseTarget.path(path);
        // Get API resource
        WebTarget search = pathTarget.queryParam(queryKey, queryValue);

        String query = String.format("%s=%s", queryKey, queryValue);
        String fullpath = String.format("%s?%s", path, query);

        // Get result and return
        return headers(search.request(), fullpath).get();
    }

    void setUserAndPw(String user, String password) {
        this.user = user;
        this.password = password;
    }
}
