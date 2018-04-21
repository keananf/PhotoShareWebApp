package server.objects;

/**
 * Class representing an uploaded photo
 */
public final class Photo {
    // Photo information
    private final String photoContents;
    private final String authorName;
    private final String photoName;
    private final long photoTime;
    private final long id, albumId;

    public Photo(String photoContents, String authorName, String photoName, long id, long albumId, long photoTime) {
        this.photoContents = photoContents;
        this.authorName = authorName;
        this.photoName = photoName;
        this.photoTime = photoTime;

        this.albumId = albumId;
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
    public String getAuthorName() {
        return authorName;
    }

    /**
     * @return the time this photo was created
     */
    public long getPhotoTime() {
        return photoTime;
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

    /**
     * @return the id of the album that this photo belongs to.
     */
    public long getAlbumId() {
        return albumId;
    }
}
