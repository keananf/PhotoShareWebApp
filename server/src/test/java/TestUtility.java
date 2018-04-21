import client.ApiClient;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import server.ServerMain;
import server.datastore.RequestResolver;
import server.objects.Receipt;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Helper methods for tests.
 */
public abstract class TestUtility {
    // Default user information
    String username = "John";
    String pw = "1";

    // Default album information
    long albumId = 0;
    String albumName = "albumName", description = "Descriptions";

    // Server
    static final ServerMain server;
    static final RequestResolver resolver;

    // Client
    public static ApiClient apiClient;
    public static final Gson gson;

    // Initialise in a static block so all children classes can access
    // the same server, without having to change config.
    static {
        gson = new Gson();
        apiClient = new ApiClient();

        server = new ServerMain();
        server.startServer();

        resolver = ServerMain.RESOLVER;
        resolver.DEBUG = true;
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
     * Creates a user with the given username and default password
     * and sets it up as the registered user for this client.
     * Also, a default album is set up.
     * @param name the username of the user
     */
    public void loginAndSetupNewUser(String name) {
        // Add user to server
        addUser(name);

        // login as user
        Response response = apiClient.loginUser(name, pw);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Add new album, and retrieve the returned id
        response = apiClient.addAlbum(albumName, description, name);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        albumId = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();
    }

    /**
     * Attempts to add a user with the given username
     * @param name the username
     */
    public void addUser(String name) {
        // Call the persistAddUser API from the client
        Response response = apiClient.addUser(name, pw);

        // Ensure successful return code
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Ensure changes reflected in the server's data store
        // This works because User.equals() is overriden.
        assertTrue(resolver.getUsers().stream().anyMatch(u -> u.getUsername().equals(name)));
    }
}
