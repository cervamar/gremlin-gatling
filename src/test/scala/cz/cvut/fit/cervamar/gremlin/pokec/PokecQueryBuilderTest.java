package cz.cvut.fit.cervamar.gremlin.pokec;

import java.util.HashMap;
import java.util.Map;

import cz.cvut.fit.cervamar.gremlin.pokec.PokecImporter.QueryWrapper;
import org.junit.Test;

import static cz.cvut.fit.cervamar.gremlin.pokec.PokecImporter.ID;
import static cz.cvut.fit.cervamar.gremlin.pokec.PokecQueryBuilder.PARAM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on 12/26/2017.
 *
 * @author Marek.Cervak
 */
public class PokecQueryBuilderTest {

    private PokecQueryBuilder pokecQueryBuilder = new PokecQueryBuilder();

    @Test
    public void createInsertQuery() {
        Map<String, String> vertex = new HashMap<>();
        vertex.put(ID, "1");
        QueryWrapper query = pokecQueryBuilder.createInsertQuery(vertex);
        assertEquals("graph.addVertex('" + ID + "'," + PARAM + ID + ")", query.getQuery());
        assertEquals("1", query.getVariables().get(PARAM + ID));
    }


    @Test
    public void createInsertQueryMoreProperties() {
        Map<String, String> vertex = new HashMap<>();
        vertex.put(ID, "1");
        vertex.put("gender", "1");
        String query = pokecQueryBuilder.createInsertQuery(vertex).getQuery();
        //order is not important
        assertTrue(("graph.addVertex('" + ID + "'," + PARAM + ID + ",'gender'," + PARAM + "gender)").equals(query) ||
                ("graph.addVertex('gender'," + PARAM + "gender,'" + ID + "'," + PARAM + ID + "')").equals(query));
    }

    @Test
    public void createFindVertexByQuery() {
        Map<String, String> properties = new HashMap<>();
        properties.put(ID, "1");
        Map<String, Object> params = new HashMap<>();
        String query = pokecQueryBuilder.findVertexAndAssign(properties, "v1", params);
        assertEquals("V().has('" + ID + "',v1" + PARAM + ID + ").as('v1')", query);
        assertEquals("1", params.get("v1" + PARAM + ID));
    }

    @Test
    public void createInsertEdgeQuery() {
        String from = "1";
        String to = "2";
        QueryWrapper query = pokecQueryBuilder.createInsertEdgeQuery(from, to, "likes");
        assertEquals("g.V().has('" + ID + "',v1" + PARAM + ID + ").as('v1')" +
                        ".V().has('" + ID + "',v2" + PARAM + ID + ").as('v2')" +
                        ".addE('likes').from('v1').to('v2')", query.getQuery());
        assertEquals("1", query.getVariables().get("v1" + PARAM + ID));
        assertEquals("2", query.getVariables().get("v2" + PARAM + ID));
    }
}