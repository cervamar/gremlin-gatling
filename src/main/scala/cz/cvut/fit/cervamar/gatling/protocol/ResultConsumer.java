package cz.cvut.fit.cervamar.gatling.protocol;

import java.util.List;
import java.util.function.Consumer;

import org.apache.tinkerpop.gremlin.driver.Result;

/**
 * Created on 1/3/2018.
 *
 * @author Marek.Cervak
 */
public interface ResultConsumer extends Consumer<List<Result>> {
    void acceptError(Throwable t);
}
