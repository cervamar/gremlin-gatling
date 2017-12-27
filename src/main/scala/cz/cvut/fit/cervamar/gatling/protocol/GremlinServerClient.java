package cz.cvut.fit.cervamar.gatling.protocol;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;

/**
 * Created on 6/15/2017.
 *
 * @author Marek.Cervak
 */
public class GremlinServerClient {

    public static final int QUERY_TIMEOUT = 20;
    private Client client;
    private final boolean supportNumericIds;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static GremlinServerClient createGremlinServerClient() throws ExecutionException, InterruptedException {
        return new GremlinServerClient(Cluster.open());
    }

    public static GremlinServerClient createGremlinServerClient(String serverConfig) throws FileNotFoundException, ExecutionException, InterruptedException {
        File serverConfigFile = new File(serverConfig);
        assert(serverConfigFile.exists()) : "Gremlin remote server configuration file " + serverConfigFile.getAbsolutePath() + " doesn't exist." ;
        return new GremlinServerClient(Cluster.build(serverConfigFile).create());
    }

    public GremlinServerClient(Cluster cluster) throws ExecutionException, InterruptedException {
        this.client = cluster.connect();
        supportNumericIds = submit("graph.features().vertex().supportsNumericIds()").get(0).getBoolean();
    }

    public void submitAsync(String gremlinQuery, Map<String, Object> variables, Consumer<List<Result>> consumer) {
        CompletableFuture<ResultSet> response = client.submitAsync(gremlinQuery, variables);
        response.thenAccept(results -> {
            results.all().acceptEither(timeoutAfter(QUERY_TIMEOUT, TimeUnit.SECONDS), consumer);
        });
    }

    public List<Result> submit(String gremlinQuery) throws ExecutionException, InterruptedException {
        return submit(gremlinQuery, Collections.emptyMap());
    }

    public List<Result> submit(String gremlinQuery, Map<String, Object> variables) throws ExecutionException, InterruptedException {
        return client.submit(gremlinQuery, variables).all().get();
    }

    private <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<T> result = new CompletableFuture<>();
        executor.schedule(() -> result.complete(null), timeout, unit);
        return result;
    }


    public boolean isSupportNumericIds() {
        return supportNumericIds;
    }
}
