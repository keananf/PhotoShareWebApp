package server.requests;

/**
 * Request wrapper for adding an album
 */
public class AddAlbumRequest {

    private final String albumName, description;

    public AddAlbumRequest(String albumName, String description) {
        this.albumName = albumName;
        this.description = description;
    }

    /**
     * @return the requested name of the album
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * @return the description of the requested album
     */
    public String getDescription() {
        return description;
    }
}