import client.ApiClient;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import server.ServerMain;
import server.datastore.RequestResolver;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Helper methods for tests.
 */
public abstract class TestUtility {
    String name = "John";
    String pw = "1";

    // Server
    static final ServerMain server;
    static final RequestResolver resolver;

    // Client
    static ApiClient apiClient;
    static final Gson gson;

    // Initialise in a static block so all children classes can access
    // the same server, without having to change config.
    static {
        gson = new Gson();
        apiClient = new ApiClient();

        server = new ServerMain();
        server.startServer();

        resolver = ServerMain.RESOLVER;
        resolver.clear();
    }

    @Before
    public void setUp() {
        apiClient.clear();
    }

    @After
    public void tearDown() {
        // Clear data store for next test
        resolver.clear();
    }

    /**
     * Creates a user with the given name and default password
     * and sets it up as the registered user for this client.
     * @param name the name of the user
     */
    protected void addUserAndLogin(String name) {
        // Add user to server
        addUser(name);

        // login as user
        Response response = apiClient.loginUser(name, pw);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    /**
     * Attempts to add a user with the given name
     * @param name the name
     */
    void addUser(String name) {
        // Call the persistAddUser API from the client
        Response response = apiClient.addUser(name, pw);

        // Ensure successful return code
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Ensure changes reflected in the server's data store
        // This works because User.equals() is overriden.
        assertTrue(resolver.getUsers().stream().anyMatch(u -> u.getName().equals(name)));
    }
}
