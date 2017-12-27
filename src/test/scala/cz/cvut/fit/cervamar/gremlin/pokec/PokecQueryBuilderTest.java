package cz.cvut.fit.cervamar.gremlin.pokec;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on 12/26/2017.
 *
 * @author Marek.Cervak
 */
public class PokecQueryBuilderTest {

    public static final String ID = "id";
    private PokecQueryBuilder pokecQueryBuilder = new PokecQueryBuilder();

    @Test
    public void createInsertQuery() {
        Map<String, String> vertex = new HashMap<>();
        vertex.put(ID, "1");
        String query = pokecQueryBuilder.createInsertQuery(vertex);
        assertEquals("graph.addVertex('id','1')", query);
    }


    @Test
    public void createInsertQueryMoreProperties() {
        Map<String, String> vertex = new HashMap<>();
        vertex.put(ID, "1");
        vertex.put("gender", "1");
        String query = pokecQueryBuilder.createInsertQuery(vertex);
        //order is not important
        assertTrue("graph.addVertex('id','1','gender','1')".equals(query) ||
                "graph.addVertex('gender','1','id','1')".equals(query));
    }

    @Test
    public void createFindVertexByQuery() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ID, "1");
        String query = pokecQueryBuilder.findVertexAndAssign(properties, "v1");
        assertEquals("v1=g.V().has('id','1').next()", query);
    }

    @Test
    public void createInsertEdgeQuery() {
        String from = "1";
        String to = "2";
        String query = pokecQueryBuilder.createInsertEdgeQuery(from, to, "likes");
        assertEquals("v1=g.V().has('id','1').next()\n" +
                "v2=g.V().has('id','2').next()\n" +
                "v1.addEdge('likes',v2)", query);
    }
}