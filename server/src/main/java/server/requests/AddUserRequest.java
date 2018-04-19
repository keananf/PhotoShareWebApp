package server.requests;

/**
 * A simple Add user request
 */
public class AddUserRequest {
    private final String user;
    private final int password;

    public AddUserRequest(String user, int password) {
        this.user = user;
        this.password = password;
    }

    /**
     * @return the pw
     */
    public int getPassword() {
        return password;
    }

    /**
     * @return the user name
     */
    public String getUser() {
        return user;
    }
}
