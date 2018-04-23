package server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Class for generating api keys
 */
public abstract class Auth {

    /**
     * Generates an api key for the user in this session. Uses HMAC (Hashed Message Authentication Code)
     *
     * @param endPoint   the api being accessed.
     * @param user the user who sent the request
     * @param base64HashedPassword the password, hashed and then encoded as a base64 string for easier transmission.
     * @param systemTime the provided time when a request was launched
     * @return the encoded authentication information.
     */
    public static String getApiKey(String endPoint, String user, String base64HashedPassword, long systemTime) {
        // Compose raw info used to make api key
        String key = String.format("%d%s%s:%s", systemTime, endPoint, user, base64HashedPassword);

        // Hash and encode the overall key, and append the username before it.
        return String.format("%s:%s", user, hashAndEncodeBase64(key));
    }

    /**
     * Hashes the given key using SHA-256, and then encodes that in base64 for easier transmission
     * @param key the key to encode
     * @return the encoded key
     */
    public static String hashAndEncodeBase64(String key) {
        // Get hash algorithm
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // hash the raw key
            messageDigest.update(key.getBytes(Resources.CHARSET));
            byte[] hashedKey = messageDigest.digest();

            // Encode it
            return Base64.getEncoder().encodeToString(hashedKey);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
}
