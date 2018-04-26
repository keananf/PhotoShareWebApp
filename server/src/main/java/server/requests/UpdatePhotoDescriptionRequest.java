package server.requests;

/**
 * Request wrapper for updating an photo's description
 */
public class UpdatePhotoDescriptionRequest {

    private final String description;
    private final long photoId;

    public UpdatePhotoDescriptionRequest(long photoId, String description) {
        this.photoId = photoId;
        this.description = description;
    }

    /**
     * @return the id of the photo
     */
    public long getPhotoId() {
        return photoId;
    }

    /**
     * @return the description of the requested photo
     */
    public String getDescription() {
        return description;
    }
}
