package server.datastore.exceptions;

public class InvalidPhotoFormatException extends Exception {
    private final String message;

    public InvalidPhotoFormatException() {
        message = String.format("Photos must not be larger than 4MB.");
    }

    @Override
    public String getMessage() {
        return message;
    }
}
