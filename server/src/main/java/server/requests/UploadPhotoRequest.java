package server.requests;

import java.util.Base64;

/**
 * Request wrapper for uploading a photo
 */
public final class UploadPhotoRequest {

    private final String encodedPhotoContents;
    private final String photoName;
    private final String extension;
    private final long albumId;
    public final String description;

    public UploadPhotoRequest(String photoName, String extension, String description, byte[] photoContents, long albumId) {
        this.photoName = photoName;
        this.extension = extension;
        this.albumId = albumId;
        this.description = description;

        // Encode photo contents into base 64, so it can be serialised into json.
        encodedPhotoContents = encodeContents(photoContents);
    }

    /**
     * Static method encoding byte[] into base64 string
     *
     * @param photoContents the raw photo contents
     * @return the encoded photo
     */
    private static String encodeContents(byte[] photoContents) {
        return Base64.getEncoder().encodeToString(photoContents);
    }

    /**
     * Decodes the base64 encoded photo
     *
     * @param photoContents the base64 encoded contents
     * @return the decoded file
     */
    public static byte[] decodeContents(String photoContents) {
        // Attempt to remove unnecessary info
        String[] components = photoContents.split("base64,");
        if(components.length == 2) photoContents = components[1];

        return Base64.getDecoder().decode(photoContents);
    }

    /**
     * @return the base64 encoded photo contents
     */
    public String getEncodedPhotoContents() {
        return encodedPhotoContents;
    }

    /**
     * @return the requested name of the photo
     */
    public String getPhotoName() {
        return photoName;
    }

    /**
     * @return the photo's extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @return the id of the requested album to upload this photo to
     */
    public long getAlbumId() {
        return albumId;
    }

    /**
     * @return the text description of the photo as provided by the user
     */
    public String getDescription() { return description; }
}
