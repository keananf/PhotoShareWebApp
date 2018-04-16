package client;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Class which connects clients to URLs and
 * facilitates arbitrary POST and GET objects.common
 */
public class Connector {

    /**
     * Performs a GET request on the provided path
     *
     * @param baseTarget the web target for the base url
     * @param path       the path to GET from
     */
    protected Response getFromUrl(WebTarget baseTarget, String path) {
        // Get API resource
        WebTarget search = baseTarget.path(path);

        // Get result and return
        return search.request().get();
    }

    /**
     * Posts the given message body to the given path
     *
     * @param baseTarget the web target for the base url
     * @param path       the path to post the message to
     * @param message    the message to post
     */
    protected Response postToUrl(WebTarget baseTarget, String path, String message) {
        // Get API resource
        WebTarget pathTarget = baseTarget.path(path);

        // Post the message to the research and check the status.
        Response result = pathTarget.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
        return result;
    }
}
