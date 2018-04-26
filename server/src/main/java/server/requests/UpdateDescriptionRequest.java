package server.requests;

/**
 * Request wrapper for updating an album's or photo's description
 */
public class UpdateDescriptionRequest {

    private final String description;
    private final long id;

    public UpdateDescriptionRequest(long id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * @return the id of the photo or ablum
     */
    public long getId() {
        return id;
    }

    /**
     * @return the description of the requested photo or album
     */
    public String getDescription() {
        return description;
    }
}
