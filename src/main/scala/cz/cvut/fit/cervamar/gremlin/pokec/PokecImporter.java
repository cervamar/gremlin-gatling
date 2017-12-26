package cz.cvut.fit.cervamar.gremlin.pokec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.shaded.kryo.Kryo;
import org.apache.tinkerpop.shaded.kryo.io.Input;
import org.apache.tinkerpop.shaded.kryo.io.Output;

/**
 * Created on 5/4/2017.
 *
 * @author Marek.Cervak
 */
public class PokecImporter {
    public static final List<String> COLUMN_NAMES = Arrays.asList("id", "public", "completion_percentage", "gender", "region", "last_login", "registration", "age", "body", "I_am_working_in_field", "spoken_languages", "hobbies",
            "I_most_enjoy_good_food", "pets", "body_type", "my_eyesight", "eye_color", "hair_color", "hair_type", "completed_level_of_education", "favourite_color",
            "relation_to_smoking", "relation_to_alcohol", "sign_in_zodiac", "on_pokec_i_am_looking_for", "love_is_for_me", "relation_to_casual_sex", "my_partner_should_be",
            "marital_status	children", "relation_to_children", "I_like_movies", "I_like_watching_movie", "I_like_music", "I_mostly_like_listening_to_music", "the_idea_of_good_evening",
            "I_like_specialties_from_kitchen", "fun", "I_am_going_to_concerts", "my_active_sports", "my_passive_sports", "profession", "I_like_books", "life_style", "music", "cars",
            "politics", "relationships", "art_culture", "hobbies_interests", "science_technologies", "computers_internet", "education", "sport", "movies", "travelling", "health",
            "companies_brands", "more");

    static List<Integer> toStore = Arrays.asList(0, 3, 4, 7);
    static Map<Integer, String> INDEXED_COLUMNS = toStore.stream().collect(Collectors.toMap(Function.identity(), c -> COLUMN_NAMES.get(c)));

    private static final Log LOG = LogFactory.getLog(PokecImporter.class);
    private static Vertex lastVertex;


    public static List<Map<String, String>> readRecords(File inputFile) throws IOException {
        List<Map<String, String>> ret = new ArrayList<>();
        LineIterator it = FileUtils.lineIterator(inputFile, "UTF-8");
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                ret.add(createVertexRecord(line));
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        return ret;
    }

    public static Map<String, Object> readAndStoreVerticesRecords(File inputFile, Graph graph) throws IOException {
        return readAndStoreRecords(inputFile, graph, true, null);
    }

    public static Map<String, Object> readAndStoreEdgeRecords(File inputFile, Graph graph, Map<String, Object> idMap) throws IOException {
        return readAndStoreRecords(inputFile, graph, false, idMap);
    }


    private static Map<String, Object> readAndStoreRecords(File inputFile, Graph graph, boolean vertex, Map<String, Object> idMap) throws IOException {
        Map<String, Object> ret = new HashMap<>();
        LineIterator it = FileUtils.lineIterator(inputFile, "UTF-8");
        int cnt = 0;
        try {
            while (it.hasNext()) {
                if((cnt++)%1000 == 0) {
                    LOG.info("Processed " + cnt + " records");
                }
                String line = it.nextLine();
                if(vertex) {
                    Optional<Record> record = loadVertexToGraph(graph, createVertexRecord(line));
                    if(record.isPresent()) {
                        ret.put(record.get().getId(), record.get().getDatabaseId());
                    }
                }
                else {
                    loadEdgeToGraph(graph, line.split("\\t"), idMap);
                }
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        return ret;
    }

    private static void loadEdgeToGraph(Graph graph, String[] split, Map<String, Object> idMap) {
        assert (split.length == 2);
        Object fromId = idMap.get(split[0]);
        Object toId = idMap.get(split[1]);
        if (fromId == null || toId == null) {
            //LOG.debug("Creating edge between not-existing vertices " + split[0] + " --> " + split[1]);
            return;
        }
        Iterator<Vertex> it = graph.vertices(fromId, toId);
        Vertex from = it.next();
        Vertex to = it.next();
        from.addEdge("likes", to);
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

    public static Optional<Record> loadVertexToGraph(Graph graph, Map<String, String> record) {
        if(Integer.parseInt(record.get("id")) > 100000) return Optional.empty();
        Object[] properties = ArrayUtils.addAll(record.entrySet().stream().flatMap(x -> Stream.of(x.getKey(), x.getValue())).toArray(), T.label, "person");
        Vertex vertex = graph.addVertex(properties);
        return Optional.of(new Record(record.get("id"), vertex.id()));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Record {
        private String id;
        private Object databaseId;
    }

    public static Map<String, Object> readIdMapFromFile (String kryoFilePath) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        try (Input idMap = new Input(new FileInputStream(kryoFilePath))){
            return (Map<String, Object>) kryo.readObject(idMap,  HashMap.class);
        }
    }

    public static void saveIdMaptoFile(Map <String, Object> ids, String pathToFile) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        File toStore = new File(pathToFile);
        FileUtils.deleteQuietly(toStore);
        try(Output output = new Output(new FileOutputStream(toStore))) {
            kryo.writeObject(output, ids);
            output.close();
        }
    }

}
