package server.datastore.exceptions;

/**
 * Exception thrown when invalid auth info provided
 */
public class UnauthorisedException extends Exception {

    @Override
    public String getMessage() {
        return "Invalid auth info provided";
    }
}
