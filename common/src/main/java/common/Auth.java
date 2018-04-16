package common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Class representing auth information.
 * Auth instances are serialised and sent as POST message bodies,
 * such that they aren't cached.
 */
public class Auth {
    private final String user;
    private final long time;
    private final String apiKey;
    private final int password;

    public Auth(String endPoint, String user, int password) {
        this.time = System.nanoTime();

        // Handle null users
        if (user != null) {
            this.user = user;
            this.password = password;
            this.apiKey = getApiKey(endPoint, time);
        }
        else {
            this.user = "";
            this.apiKey = "";
            this.password = 0;
        }
    }


    /**
     * Generates an api key for the user in this session. Uses HMAC (Hashed Message Authentication Code)
     *
     * @param endPoint   the api being accessed.
     * @param systemTime the provided time when a request was launched
     * @return the encoded authentication information.
     */
    public String getApiKey(String endPoint, long systemTime) {
        // Compose raw info used to make api key
        String key = String.format("%d%s%s:%s", systemTime, endPoint, user, password);

        try {
            // Get hash algorithm
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

        // Empty string as default
        return "";
    }

    public String getUser() {
        return user;
    }

    public long getTime() {
        return time;
    }

    public String getApiKey() {
        return apiKey;
    }
}
