package server.datastore.exceptions;

/**
 * Exception thrown when a user attempts to upload a file with extension other than png, jpg, or gif
 */
public class InvalidFileTypeException extends Exception {
    private String ext;

    public InvalidFileTypeException(String ext) {
        this.ext = ext;
    }

    @Override
    public String getMessage() {
        return String.format("Extension not allowed: %s", ext);
    }
}