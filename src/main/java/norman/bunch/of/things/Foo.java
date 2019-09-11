package norman.bunch.of.things;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Foo {
    private static final Logger LOGGER = LoggerFactory.getLogger(Foo.class);

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/example_game_book.gamebook");
        String string = "{\"foo\":\"bar\",\"baz\":{\"qux\":\"thud\"}}";
        try {
            // File/String --> JsonNode
            printStuff(objectMapper.readTree(file));
            printStuff(objectMapper.readTree(string));
            // File/String --> POJO/Map
            printStuff(objectMapper.readValue(file, Map.class));
            printStuff(objectMapper.readValue(string, Map.class));

            // JsonNode --> File
            File newFile = new File(file.getParent(), "string.json");
            JsonNode jsonNode = objectMapper.readTree(string);
            objectMapper.writeValue(newFile, jsonNode);
            // JsonNode -- String
            String jsonAsString = jsonNode.toString();
            printStuff(jsonAsString);

            // POJO/Map --> JsonNode
            Map map = objectMapper.readValue(string, Map.class);
            JsonNode mapAsJson = objectMapper.valueToTree(map);
            printStuff(mapAsJson);

            // Create JsonNode from scratch;
            Boolean aBoolean = Boolean.valueOf(true);
            Integer aInteger = Integer.valueOf(123);
            Double aDouble = Double.valueOf(12.34);
            String aString = "ABCdef";
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode = objectNode.put("fieldBoolean", aBoolean);
            objectNode = objectNode.put("fieldInteger", aInteger);
            objectNode = objectNode.put("fieldFloat", aDouble);
            objectNode = objectNode.put("fieldString", aString);
            printStuff(objectNode);

            Object objBoolean = Boolean.valueOf(true);
            Object objInteger = Integer.valueOf(123);
            Object objDouble = Double.valueOf(12.34);
            Object objString = "ABCdef";
            ObjectNode objectNode2 = objectMapper.createObjectNode();
            objectNode2 = objectNode2.putPOJO("fieldBoolean", objectMapper.valueToTree(objBoolean));
            objectNode2 = objectNode2.putPOJO("fieldInteger", objectMapper.valueToTree(objInteger));
            objectNode2 = objectNode2.putPOJO("fieldFloat", objectMapper.valueToTree(objDouble));
            objectNode2 = objectNode2.putPOJO("fieldString", objectMapper.valueToTree(objString));
            printStuff(objectNode2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printStuff(Object stuff) {
        System.out.println("stuff=\"" + stuff + "\"");
        System.out.println("stuff.class=\"" + stuff.getClass().getName() + "\"");
    }
}
