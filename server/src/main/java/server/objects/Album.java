package server.objects;

/**
 * Class representing an album.
 */
public class Album {
    private final String albumName, albumDescription, authorName;
    private final long albumId, albumTime;

    public Album(long albumId, String albumName, String authorName, String albumDescription, long albumTime) {
        this.albumName = albumName;
        this.albumDescription = albumDescription;
        this.authorName = authorName;
        this.albumId = albumId;
        this.albumTime = albumTime;
    }

    /**
     * @return the album's description
     */
    public String getAlbumDescription() {
        return albumDescription;
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
