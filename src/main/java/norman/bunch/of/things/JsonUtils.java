package norman.bunch.of.things;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtils() {
    }

    public static JsonNode fileToJson(File file) throws LoggingException {
        try {
            return objectMapper.readTree(file);
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    "Unable to read file as JSON, file=" + (file == null ? null : file.getAbsolutePath()), e);
        }
    }

    public static JsonNode fileToJsonIgnoreErrors(File file) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(file);
        } catch (IOException ignored) {
        }
        return jsonNode;
    }

    public static Boolean jsonToBoolean(JsonNode jsonNode) {
        return jsonNode.booleanValue();
    }

    public static Double jsonToDouble(JsonNode jsonNode) {
        return jsonNode.doubleValue();
    }

    public static Integer jsonToInteger(JsonNode jsonNode) {
        return jsonNode.intValue();
    }

    public static Map jsonToMap(JsonNode jsonNode) {
        return objectMapper.convertValue(jsonNode, Map.class);
    }

    public static String jsonToString(JsonNode jsonNode) {
        return jsonNode.textValue();
    }

    public static JsonNode mapToJson(Map map) {
        return objectMapper.valueToTree(map);
    }

    public static JsonNode stringToJson(String jsonString) throws LoggingException {
        try {
            return objectMapper.readTree(jsonString);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Unable to read string as JSON, string=" + jsonString, e);
        }
    }
}
