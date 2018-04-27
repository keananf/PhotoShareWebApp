package server.objects;

import java.util.List;

/**
 * Class representing a photo result.
 */
public class PhotoResult {
    private final Photo photo;
    private final List<Comment> childComments;

    public PhotoResult(Photo photo, List<Comment> photoComments) {
        this.photo = photo;
        childComments = photoComments;
    }

    /**
     * @return the parent photo
     */
    public Photo getPhoto() {
        return photo;
    }

    /**
     * @return top-level replies to the parent comment
     */
    public List<Comment> getChildComments() {
        return childComments;
    }
}
