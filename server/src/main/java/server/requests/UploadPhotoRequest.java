package server.requests;

import server.objects.Auth;

import java.util.Base64;

/**
 * Request wrapper for uploading a photo
 */
public final class UploadPhotoRequest extends AuthRequest {

    private final String encodedPhotoContents;
    private final String photoName;
    private final long albumId;

    public UploadPhotoRequest(Auth auth, String photoName, byte[] photoContents, long albumId) {
        super(auth);
        this.photoName = photoName;
        this.albumId = albumId;

        // Encode photo contents into base 64, so it can be serialised into json.
        encodedPhotoContents = encodeContents(photoContents);
    }

    /**
     * Static method encoding byte[] into base64 string
     *
     * @param photoContents the raw photo contents
     * @return the encoded photo
     */
    static String encodeContents(byte[] photoContents) {
        return Base64.getEncoder().encodeToString(photoContents);
    }

    /**
     * Decodes the base64 encoded photo
     *
     * @param photoContents the base64 encoded contents
     * @return the decoded file
     */
    public static byte[] decodeContents(String photoContents) {
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
     * @return the id of the requested album to upload this photo to
     */
    public long getAlbumId() {
        return albumId;
    }
}