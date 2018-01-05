package cz.cvut.fit.cervamar.gremlin.server;

import java.io.InputStream;
import java.util.Scanner;

import cz.cvut.fit.cervamar.gatling.protocol.GremlinServerClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;

/**
 * Created on 1/5/2018.
 *
 * @author Marek.Cervak
 */
@Slf4j
public class EmbeddedServer {
    private static GremlinServer server;

    public static void main(String[] args) throws Exception {
        log.info("Starting Gremlin server");
        startServer();
        String cont;
        Scanner input = new Scanner(System.in);
        do {
            cont = input.next();
        }
        while(!"exit".equalsIgnoreCase(cont));
        log.info("Stopping Gremlin server");
        stopServer();
    }

    private static void startServer() throws Exception {
        final InputStream stream = GremlinServerClient.class.getClassLoader().getResourceAsStream("conf/gremlin-server-modern.yaml");
        Settings setting = Settings.read(stream);
        server = new GremlinServer(setting);
        server.start().join();
        log.info("Gremlin server started at port: {}", setting.port);
    }

    private static void stopServer() {
        server.stop().join();
        log.info("Gremlin server stopped.");
    }
}
