package server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class for generating api keys
 */
public abstract class Auth {

    /**
     * Generates an api key for the user in this session. Uses HMAC (Hashed Message Authentication Code)
     *
     * @param user the user who sent the request
     * @param base64HashedPassword the password, hashed and then encoded as a base64 string for easier transmission.
     * @param systemTime the provided time when a request was launched
     * @return the encoded authentication information.
     */
    public static String getApiKey(String user, String base64HashedPassword, String systemTime) {
        // Compose raw info used to make api key
        String key = String.format("%s:%s:%s", systemTime, user, base64HashedPassword);

        // Hash and encode the overall key, and append the username before it.
        return String.format("%s:%s", user, hashAndEncodeHex(key));
    }

    /**
     * Hashes the given key using SHA-256, and then encodes that in hex for easier transmission
     * @param key the key to encode
     * @return the encoded key
     */
    public static String hashAndEncodeHex(String key) {
        // Get hash algorithm
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // Hash the raw key
            messageDigest.update(key.getBytes(Resources.CHARSET));
            byte[] hash = messageDigest.digest();

            // Convert to hex string
            StringBuffer hexString = new StringBuffer();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
}
