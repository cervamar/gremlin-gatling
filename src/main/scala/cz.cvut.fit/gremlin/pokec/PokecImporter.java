package cz.cvut.fit.gremlin.pokec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 5/4/2017.
 *
 * @author Marek.Cervak
 */
public class PokecImporter {
    public static final List<String> COLUMN_NAMES = Arrays.asList("id", "public", "completion_percentage",	"gender", "region", "last_login",	"registration",	"age", "body", "I_am_working_in_field", "spoken_languages",	"hobbies",
            "I_most_enjoy_good_food",	"pets",	"body_type", "my_eyesight",	"eye_color",	"hair_color",	"hair_type",	"completed_level_of_education",	"favourite_color",
            "relation_to_smoking",	"relation_to_alcohol",	"sign_in_zodiac",	"on_pokec_i_am_looking_for",	"love_is_for_me",	"relation_to_casual_sex",	"my_partner_should_be",
            "marital_status	children",	"relation_to_children",	"I_like_movies",	"I_like_watching_movie",	"I_like_music",	"I_mostly_like_listening_to_music",	"the_idea_of_good_evening",
            "I_like_specialties_from_kitchen",	"fun",	"I_am_going_to_concerts",	"my_active_sports",	"my_passive_sports",	"profession",	"I_like_books",	"life_style",	"music",	"cars",
            "politics",	"relationships",	"art_culture",	"hobbies_interests",	"science_technologies",	"computers_internet", "education",	"sport",	"movies",	"travelling",	"health",
            "companies_brands",	"more");

    static List<Integer> toStore = Arrays.asList(0, 3, 4, 7);
    static Map <Integer, String> INDEXED_COLUMNS = toStore.stream().collect(Collectors.toMap(Function.identity(), c -> COLUMN_NAMES.get(c)));


    public static List<Map<String, String>> processRecords(File inputFile) throws IOException {
        List<Map<String, String>> ret = new ArrayList<>();
        LineIterator it = FileUtils.lineIterator(inputFile, "UTF-8");
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                ret.add(createRecord(line));
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        return ret;
    }

    public static Map<String, String> createRecord(String recordLine) {
        Map<String,String> ret = new HashMap<>();
        String [] records = recordLine.split("\\t");
        for (int i = 0; i < records.length; i++) {
            String columnName = INDEXED_COLUMNS.get(i);
            if (columnName != null) {
                ret.put(columnName, records[i]);
            }
        }
        return ret;
    }

    public static Record loadVertexToGraph(Graph graph, Map<String, String> record) {
        Vertex vertex = graph.addVertex(record.entrySet().stream().flatMap(x -> Stream.of(x.getKey(), x.getValue())).toArray());
        return new Record(record.get("id"), vertex.id());
    }


    public static class Record {
        private final String id;
        private final Object databaseId;

        public Record(String id, Object databaseId) {
            this.id = id;
            this.databaseId = databaseId;
        }
    }

}
