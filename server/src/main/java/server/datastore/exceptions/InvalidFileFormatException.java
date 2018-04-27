package server.datastore.exceptions;

public class InvalidFileFormatException extends Exception {
    private String message;

    public InvalidFileFormatException(String ext) {
        message = String.format("Extension not allowed: %s", ext);
    }

    public InvalidFileFormatException() {
        message = String.format("Photos must not be larger than 4MB.");
    }

    @Override
    public String getMessage() {
        return message;
    }
}
