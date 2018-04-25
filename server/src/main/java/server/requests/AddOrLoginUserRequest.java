package server.requests;

/**
 * A simple Add / login user request
 */
public class AddOrLoginUserRequest {
    private final String username;
    private final String password;

    public AddOrLoginUserRequest(String user, String password) {
        this.username = user;
        this.password = password;
    }

    /**
     * @return the plaintext password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the user name
     */
    public String getUsername() {
        return username;
    }
}
