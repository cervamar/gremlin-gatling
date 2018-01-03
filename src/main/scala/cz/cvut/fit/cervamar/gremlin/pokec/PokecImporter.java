package cz.cvut.fit.cervamar.gremlin.pokec;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import cz.cvut.fit.cervamar.gatling.protocol.GremlinServerClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created on 5/4/2017.
 *
 * @author Marek.Cervak
 */
public class PokecImporter {
    public static final String ID = "pokecId";
    public static final List<String> COLUMN_NAMES = Arrays.asList(ID, "public", "completion_percentage", "gender", "region", "last_login", "registration", "age", "body", "I_am_working_in_field", "spoken_languages", "hobbies",
            "I_most_enjoy_good_food", "pets", "body_type", "my_eyesight", "eye_color", "hair_color", "hair_type", "completed_level_of_education", "favourite_color",
            "relation_to_smoking", "relation_to_alcohol", "sign_in_zodiac", "on_pokec_i_am_looking_for", "love_is_for_me", "relation_to_casual_sex", "my_partner_should_be",
            "marital_status	children", "relation_to_children", "I_like_movies", "I_like_watching_movie", "I_like_music", "I_mostly_like_listening_to_music", "the_idea_of_good_evening",
            "I_like_specialties_from_kitchen", "fun", "I_am_going_to_concerts", "my_active_sports", "my_passive_sports", "profession", "I_like_books", "life_style", "music", "cars",
            "politics", "relationships", "art_culture", "hobbies_interests", "science_technologies", "computers_internet", "education", "sport", "movies", "travelling", "health",
            "companies_brands", "more");

    static final List<Integer> toStore = Arrays.asList(0, 3, 4, 7);
    static final Map<Integer, String> INDEXED_COLUMNS = toStore.stream().collect(Collectors.toMap(Function.identity(), COLUMN_NAMES::get));
    private final int LIMIT;
    static PokecQueryBuilder pokecQueryBuilder = new PokecQueryBuilder();

    private static final Log LOG = LogFactory.getLog(PokecImporter.class);

    private GremlinServerClient serverClient;

    public PokecImporter(GremlinServerClient serverClient) {
        this(serverClient, 100000);
    }

    public PokecImporter(GremlinServerClient serverClient, int limit) {
        this.serverClient = serverClient;
        this.LIMIT = limit;
    }


    public void loadVerticesToServer(String inputFile) throws InterruptedException, IOException {
        loadVerticesToServer(new File(inputFile));
    }

    public void loadVerticesToServer(File inputFile) throws IOException, InterruptedException {
        loadToServer(inputFile, line -> {
            Map<String, String> vertex = createVertexRecord(line);
            if(isInRange(vertex.getOrDefault(ID, "1"))) {
                return Optional.of(pokecQueryBuilder.createInsertQuery(vertex));
            }
            else return Optional.empty();
        });
    }

    public static Map<String, String> createVertexRecord(String recordLine) {
        Map<String, String> ret = new HashMap<>();
        String[] records = recordLine.split("\\t");
        for (int i = 0; i < records.length; i++) {
            String columnName = INDEXED_COLUMNS.get(i);
            if (columnName != null) {
                ret.put(columnName, records[i]);
            }
        }
        return ret;
    }

    public static EdgeWrapper createEdgeRecord(String recordLine) {
        String[] records = recordLine.split("\\t");
        assert (records.length == 2);
        return new EdgeWrapper(records[0], records[1]);
    }


    public void loadEdgesToServer(String relationFile) throws InterruptedException, IOException {
           loadEdgesToServer(new File(relationFile));
    }

    public void loadEdgesToServer(File relationFile) throws IOException, InterruptedException {
        loadToServer(relationFile, (String line) -> {
            EdgeWrapper edge = createEdgeRecord(line);
            if(isInRange(edge.from) && isInRange(edge.to)) {
                return Optional.of(pokecQueryBuilder.createInsertEdgeQuery(edge.from, edge.to, "likes"));
            }
            else return Optional.empty();
        });
    }

    private boolean isInRange(String value) {
        return Integer.parseInt(value) <= LIMIT;
    }

    private void loadToServer(File records, Function<String, Optional<QueryWrapper>> prepareRecord) throws InterruptedException, IOException {
        LineIterator it = FileUtils.lineIterator(records, "UTF-8");
        int cnt = 0;
        try {
            while (it.hasNext()) {
                if((cnt++)%1000 == 0) {
                    LOG.info("Processed " + cnt + " records");
                }
                String line = it.nextLine();
                try {
                    Optional<QueryWrapper> query = prepareRecord.apply(line);
                    if(query.isPresent()) {
                        serverClient.submit(query.get().query, query.get().variables);
                    }
                }
                catch (ExecutionException e) {
                    LOG.error(e);
                }
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QueryWrapper {
        private String query;
        private Map<String, Object> variables;
    }
}
