package server.objects;

/**
 * Class representing a single user
 */
public class User {
    private final String username;
    private boolean admin;

    // 'Transient' modifier ensures this won't be exposed during serialisation
    private transient final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

    /**
     * set this user's admin status
     * @param admin the new admin status
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
