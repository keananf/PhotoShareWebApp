package server.objects;

/**
 * Class representing an uploaded photo
 */
public final class Photo {
    // Photo information
    private final String photoContents;
    private final String postedBy;
    private final String photoName;
    private final long timestamp;
    private final long id;

    public Photo(String photoContents, String postedBy, String photoName, long id, long timestamp) {
        this.photoContents = photoContents;
        this.postedBy = postedBy;
        this.photoName = photoName;
        this.timestamp = timestamp;

        this.id = id;
    }

    /**
     * @return the base64 encoded contents
     */
    public String getPhotoContents() {
        return photoContents;
    }

    /**
     * @return the name of the user who posted this
     */
    public String getPostedBy() {
        return postedBy;
    }

    /**
     * @return the time this photo was created
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the name of the photo
     */
    public String getPhotoName() {
        return photoName;
    }

    /**
     * @return the id of this photo.
     */
    public long getId() {
        return id;
    }
}
