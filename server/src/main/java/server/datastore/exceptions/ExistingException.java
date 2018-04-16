package server.datastore.exceptions;

/**
 * Exception thrown when a resource already exists
 */
public class ExistingException extends Exception {
    private String msg;

    public ExistingException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
