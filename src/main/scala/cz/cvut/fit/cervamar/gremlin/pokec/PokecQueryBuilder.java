package cz.cvut.fit.cervamar.gremlin.pokec;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 12/26/2017.
 *
 * @author Marek.Cervak
 */
public class PokecQueryBuilder {
    public String createInsertEdgeQuery(String fromId, String toId, String relation) {
        return String.join("\n",
                findVertexAndAssign(Collections.singletonMap("id", fromId), "v1"),
                findVertexAndAssign(Collections.singletonMap("id", toId), "v2"),
                "v1.addEdge('" + relation + "',v2)");
    }

    String createInsertQuery(Map<String, String> vertexRecord) {
        return "graph.addVertex(" +
                vertexRecord.entrySet()
                        .stream()
                        .map(entry -> "'" + entry.getKey() + "','" + entry.getValue() + "'")
                        .collect(Collectors.joining(",")) +
                ")";
    }

    String findVertexAndAssign(Map<String, Object> properties, String assignTo) {
        return  assignTo + "=g.V()" +
                properties.entrySet()
                        .stream()
                        .map(e -> ".has('" + e.getKey() + "','" + e.getValue() + "')")
                        .collect(Collectors.joining())
                + ".next()";
    }
}
