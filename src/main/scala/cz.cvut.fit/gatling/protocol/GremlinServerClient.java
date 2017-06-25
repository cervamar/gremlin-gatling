package cz.cvut.fit.gatling.protocol;


import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created on 6/15/2017.
 *
 * @author Marek.Cervak
 */
public class GremlinServerClient {

    private final Cluster cluster;
    private Client client;

    public static GremlinServerClient createGremlinServerClient() throws FileNotFoundException {
        return new GremlinServerClient("src/main/resources/remote.yaml");
    }

    public static GremlinServerClient createGremlinServerClient(String path) throws FileNotFoundException {
        return new GremlinServerClient(path);
    }

    public GremlinServerClient(String serverConfig) throws FileNotFoundException {
        File serverConfigFile = new File(serverConfig);
        assert(serverConfigFile.exists()) : "Gremlin remote server configuration file " + serverConfigFile.getAbsolutePath() + " doesn't exist." ;
        this.cluster = Cluster.build(serverConfigFile).create();
        this.client = cluster.connect();
    }


    public Client getClient() {
        return client;
    }
}
