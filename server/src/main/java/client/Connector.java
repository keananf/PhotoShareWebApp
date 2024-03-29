package client;

import server.Auth;
import server.Resources;

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
        return headers(pathTarget.request()).get();
    }

    /**
     * Add the date and authorisation headers
     * @param request the request
     * @return the request with the headers added
     */
    private Invocation.Builder headers(Invocation.Builder request) {
        try {
            // The date format that the server expects
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            // Get current time. and convert it into the correct format while rounding to the nearest second.
            Date d = format.parse(format.format(new Date(System.currentTimeMillis())));
            String dateStr = format.format(d);

            // Add header for encoded date and the apiKey
            return request.header(Resources.DATE_HEADER, dateStr)
                    .header(AUTHORIZATION, Auth.getApiKey(user, password, dateStr));
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
        return headers(pathTarget.request()).delete();
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
        Response result = headers(pathTarget.request()).put(Entity.entity("{}", MediaType.APPLICATION_JSON));
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
        Response result = headers(pathTarget.request()).post(Entity.entity(message, MediaType.APPLICATION_JSON));
        return result;
    }

    /**
     * Performs a GET request on the provided path with query
     *
     * @param baseTarget the web target for the base url
     * @param path       the path to GET from
     */
    Response getWithQuery(WebTarget baseTarget, String path, String queryKey, String queryValue) {

        // Get API resource
        WebTarget pathTarget = baseTarget.path(path);
        // Get API resource
        WebTarget search = pathTarget.queryParam(queryKey, queryValue);

        // Get result and return
        return headers(search.request()).get();
    }

    void setUserAndPw(String user, String password) {
        this.user = user;
        this.password = password;
    }
}
