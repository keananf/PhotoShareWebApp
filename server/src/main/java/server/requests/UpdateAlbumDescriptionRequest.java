package server.requests;

/**
 * Request wrapper for updating an album's description
 */
public class UpdateAlbumDescriptionRequest {

    private final String description;
    private final long albumId;

    public UpdateAlbumDescriptionRequest(long albumId, String description) {
        this.albumId = albumId;
        this.description = description;
    }

    /**
     * @return the id of the album
     */
    public long getAlbumId() {
        return albumId;
    }

    /**
     * @return the description of the requested album
     */
    public String getDescription() {
        return description;
    }
}
