package server.objects;


import java.util.List;

/**
 * Class representing an uploaded photo
 */
public final class Photo {
    // Photo information
    private final String authorName;
    private final String ext;
    private final String photoName;
    private final String photoTime;
    private final long id, albumId;
    private final String description;

    private List<String> votes;

    public Photo(String authorName, String photoName, String ext, String description, long id, long albumId,
                 List<String> photoRatings, String photoTime) {
        this.authorName = authorName;
        this.photoName = photoName;
        this.photoTime = photoTime;
        this.ext = ext;
        this.description = description;

        this.albumId = albumId;
        this.id = id;
        votes = photoRatings;
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
    public String getPhotoTime() {
        return photoTime;
    }

    /**
     * @return the photo's extension
     */
    public String getExt() {
        return ext;
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


    /**
     * @return the users who upvoted  this comment
     */
    public List<String> getLikes() {
        return votes;
    }

    /**
     * @return the description of this photo
     */
    public String getDescription() { return description; }
}
