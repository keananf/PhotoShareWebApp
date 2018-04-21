package server.objects;

/**
 * Class representing an album.
 */
public class Album {
    private final String albumName, description, authorName;
    private final long albumId, albumTime;

    public Album(long albumId, String albumName, String authorName, String description, long albumTime) {
        this.albumName = albumName;
        this.description = description;
        this.authorName = authorName;
        this.albumId = albumId;
        this.albumTime = albumTime;
    }

    /**
     * @return the album's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the album's name
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * @return the timestamp the album was created at
     */
    public long getAlbumTime() {
        return albumTime;
    }

    /**
     * @return the album's id
     */
    public long getAlbumId() {
        return albumId;
    }

    /**
     * @return the author of the album's name
     */
    public String getAuthorName() {
        return authorName;
    }
}
