package account.bank.client;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static final String BASE_URI = "http://localhost:8079/account_service/";

    public static void startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("account.bank.client");
        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
        l.setLevel(Level.ALL);
        l.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        l.addHandler(ch);

        startServer();

        System.out.printf("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...%n", BASE_URI);
        System.in.read();
    }
}
