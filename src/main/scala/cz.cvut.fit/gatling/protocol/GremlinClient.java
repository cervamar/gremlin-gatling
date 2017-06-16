package cz.cvut.fit.gatling.protocol;


import java.io.File;
import java.io.FileNotFoundException;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;

/**
 * Created on 6/15/2017.
 *
 * @author Marek.Cervak
 */
public class GremlinClient {
    private final Cluster cluster = Cluster.build(new File("C:\\Users\\marek.cervak\\diplomka\\apache-tinkerpop-gremlin-console-3.2.4\\conf\\remote.yaml")).create();
    private Client client = cluster.connect();

    public GremlinClient() throws FileNotFoundException {
    }

    public Client getClient() {
        return client;
    }
}
