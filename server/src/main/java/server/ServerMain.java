package server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import server.datastore.RequestResolver;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static server.Resources.BASE_URL;

/**
 * Jersey RESTful server wrapped in Grizzly HTTP server
 */
public class ServerMain {
    // The data store where all data lives
    public static final RequestResolver RESOLVER = new RequestResolver();

    /**
     * @return a new Http server running at the given URL.
     */
    public HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        final ResourceConfig rc = new ResourceConfig().packages("server.restApi");

        // Create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URL.
        HttpServer serv = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), rc);

        // Statically load in the html client code
        try {
            String path = new File(".").getCanonicalPath() + "/static/";
            System.out.println(String.format("Client exposed from: %s", path));
            serv.getServerConfiguration().addHttpHandler(new StaticHttpHandler(path), "/");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return serv;
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = new ServerMain().startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl%nHit enter to stop it...", BASE_URL));
        System.in.read();
        server.stop();
    }
}

