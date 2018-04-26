package server.objects;


import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing an uploaded photo
 */
public final class Photo {
    // Photo information
    private final String authorName;
    private final String ext;
    private final String photoName;
    private final long photoTime;
    private final long id, albumId;
    private final String description;

    private HashMap<String, Boolean> votes;

    public Photo(String authorName, String photoName, String ext, String description, long id, long albumId,
                 HashMap<String, Boolean> photoRatings, long photoTime) {
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
    public long getPhotoTime() {
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
    public List<String> getUpvotes() {
        // Filter out all upvotes, and then create a list of the names of the users who cast them
        return votes.entrySet().stream().filter(kv -> kv.getValue())
                .map(kv -> kv.getKey()).collect(Collectors.toList());
    }

    /**
     * @return the users who downvoted  this comment
     */
    public List<String> getDownvotes() {
        // Filter out all downvotes, and then create a list of the names of the users who cast them
        return votes.entrySet().stream().filter(kv -> !kv.getValue())
                .map(kv -> kv.getKey()).collect(Collectors.toList());
    }

    /**
     * @return the description of this photo
     */
    public String getDescription() { return description; }
}
