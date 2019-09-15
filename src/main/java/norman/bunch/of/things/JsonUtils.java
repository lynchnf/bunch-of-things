package norman.bunch.of.things;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static ObjectMapper mapper = new ObjectMapper();

    private JsonUtils() {
    }

    public static JsonNode fileToJson(File file) throws LoggingException {
        try {
            return mapper.readTree(file);
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    "Unable to read file as JSON, file=" + (file == null ? null : file.getAbsolutePath()), e);
        }
    }

    public static JsonNode fileToJsonIgnoreErrors(File file) {
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(file);
        } catch (IOException ignored) {
        }
        return jsonNode;
    }

    public static String jsonTreeToString(JsonNode jsonNode) {
        return jsonNode.toString();
    }

    public static List<String> jsonValueToListOfStrings(JsonNode jsonNode) throws LoggingException {
        if (!jsonNode.isArray()) {
            throw new LoggingException(LOGGER, "Json Node is not an array. jsonNode=" + jsonNode);
        }
        Iterator<JsonNode> elements = jsonNode.elements();
        List<String> strings = new ArrayList<>();
        while (elements.hasNext()) {
            strings.add(elements.next().asText());
        }
        return strings;
    }

    public static Boolean jsonValueToBoolean(JsonNode jsonNode) throws LoggingException {
        if (!jsonNode.isBoolean()) {
            throw new LoggingException(LOGGER, "Json Node is not a boolean. jsonNode=" + jsonNode);
        }
        return jsonNode.booleanValue();
    }

    public static Double jsonValueToDouble(JsonNode jsonNode) throws LoggingException {
        if (!jsonNode.isDouble()) {
            throw new LoggingException(LOGGER, "Json Node is not a double. jsonNode=" + jsonNode);
        }
        return jsonNode.doubleValue();
    }

    public static Integer jsonValueToInteger(JsonNode jsonNode) throws LoggingException {
        if (!jsonNode.isInt()) {
            throw new LoggingException(LOGGER, "Json Node is not an integer. jsonNode=" + jsonNode);
        }
        return jsonNode.intValue();
    }

    public static Map jsonToMap(JsonNode jsonNode) throws LoggingException {
        if (!jsonNode.isObject()) {
            throw new LoggingException(LOGGER, "Json Node is not an object. jsonNode=" + jsonNode);
        }
        return mapper.convertValue(jsonNode, Map.class);
    }

    public static String jsonValueToString(JsonNode jsonNode) throws LoggingException {
        if (!jsonNode.isTextual()) {
            throw new LoggingException(LOGGER, "Json Node is not text. jsonNode=" + jsonNode);
        }
        return jsonNode.textValue();
    }

    public static JsonNode mapToJson(Map map) {
        return mapper.valueToTree(map);
    }

    public static JsonNode stringToJsonTree(String jsonString) throws LoggingException {
        try {
            return mapper.readTree(jsonString);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Unable to read string as JSON, string=" + jsonString, e);
        }
    }
}
