package cz.cvut.fit.cervamar.gremlin.pokec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import cz.cvut.fit.cervamar.gremlin.pokec.PokecImporter.QueryWrapper;

import static cz.cvut.fit.cervamar.gremlin.pokec.PokecImporter.ID;

/**
 * Created on 12/26/2017.
 *
 * @author Marek.Cervak
 */
public class PokecQueryBuilder {

    public static final String PARAM = "__param__";

    public QueryWrapper createInsertEdgeQuery(String fromId, String toId, String relation) {
        Map<String, Object> params = new HashMap<>();
        return new QueryWrapper(String.join(".",
                "g",
                findVertexAndAssign(Collections.singletonMap(ID, fromId), "v1", params),
                findVertexAndAssign(Collections.singletonMap(ID, toId), "v2", params),
                "addE('" + relation + "').from('v1').to('v2')"), params);
    }

    QueryWrapper createInsertQuery(Map<String, String> vertexRecord) {
         return new QueryWrapper("graph.addVertex(" +
                vertexRecord.entrySet()
                        .stream()
                        .map(entry -> "'" + entry.getKey() + "'," + createParam(entry.getKey()))
                        .collect(Collectors.joining(",")) +
                ")", toParamMap(vertexRecord));
    }

    private Map<String, Object> toParamMap(Map<String, String> vertexRecord) {
        Map<String, Object> ret = new HashMap<>();
        vertexRecord.forEach((key, value) -> ret.put(createParam(key), value));
        return ret;
    }

    private Map<String, Object> toParamMap(String prefix, Map<String, String> vertexRecord) {
        Map<String, Object> ret = new HashMap<>();
        vertexRecord.forEach((key, value) -> ret.put(createParam(prefix, key), value));
        return ret;
    }

    private String createParam(String key) {
        return PARAM + key;
    }

    private String createParam(String prefix, String key) {
        return prefix + PARAM + key;
    }

    String findVertexAndAssign(Map<String, String> properties, String assignTo, Map<String, Object> params) {
        params.putAll(toParamMap(assignTo, properties));
        return  "V()" +
                properties.entrySet()
                        .stream()
                        .map(e -> ".has('" + e.getKey() + "'," + createParam(assignTo, e.getKey()) + ")")
                        .collect(Collectors.joining())
                + ".as('" + assignTo + "')";
    }
}
