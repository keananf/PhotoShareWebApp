package server.objects;

/**
 * Class representing a single user
 */
public class User {
    private final String name;
    private boolean admin;

    // trqnsient modifier ensures this won't be exposed during serialisation
    private transient final int password;

    public User(String name, int password) {
        this.name = name;
        this.password = password;
    }

    /**
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the user's password
     */
    public int getPassword() {
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
