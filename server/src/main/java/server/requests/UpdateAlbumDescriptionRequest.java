package server.requests;

import server.objects.Auth;

/**
 * Request wrapper for updating an album's description
 */
public class UpdateAlbumDescriptionRequest extends AuthRequest {

    private final String description;
    private final long albumId;

    public UpdateAlbumDescriptionRequest(Auth auth, long albumId, String description) {
        super(auth);
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
