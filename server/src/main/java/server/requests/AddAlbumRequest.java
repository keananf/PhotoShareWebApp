package server.requests;

import server.objects.Auth;

/**
 * Request wrapper for adding an album
 */
public class AddAlbumRequest extends AuthRequest {

    private final String albumName, description;

    public AddAlbumRequest(Auth auth, String albumName, String description) {
        super(auth);
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
