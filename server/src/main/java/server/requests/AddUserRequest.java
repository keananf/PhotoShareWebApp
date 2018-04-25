package server.requests;

/**
 * A simple Add user request
 */
public class AddUserRequest {
    private final String user;
    private final String password;

    public AddUserRequest(String user, String password) {
        this.user = user;
        this.password = password;
    }

    /**
     * @return the pw, hashed and encoded as a base64 string
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the user name
     */
    public String getUser() {
        return user;
    }
}