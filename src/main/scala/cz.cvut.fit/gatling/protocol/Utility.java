package cz.cvut.fit.gatling.protocol;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created on 6/21/2017.
 *
 * @author Marek.Cervak
 */
public class Utility {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(20);

    public <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<T> result = new CompletableFuture<T>();
        executor.schedule(() -> result.complete(null), timeout, unit);
        return result;
    }
}
