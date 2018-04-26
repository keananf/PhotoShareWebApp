package server.objects;

/**
 * Class representing the result of logging-in.
 * The username, pw, and admin are sent back to the corresponding client
 */
public class LoginResult {
    private final String username;
    private final boolean admin;
    private final String password;

    public LoginResult(String username, String password, boolean admin) {
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    /**
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the user's password, hashed and then encoded as a base64 string
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return if this user is an admin
     */
    public boolean isAdmin() {
        return admin;
    }
}
