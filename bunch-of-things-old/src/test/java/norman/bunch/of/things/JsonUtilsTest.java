package norman.bunch.of.things;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonUtilsTest {
    public static final String[] fieldsNames =
            new String[]{"arrayField", "booleanField", "doubleField", "integerField", "mapField", "nullField",
                    "stringField"};
    private JsonNode objectNode;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        // Array of strings.
        ArrayNode arrayNode = mapper.createArrayNode();
        arrayNode.add("one");
        arrayNode.add("two");
        objectNode.set("arrayField", arrayNode);

        // Boolean.
        objectNode.put("booleanField", true);

        // Double.
        objectNode.put("doubleField", 123.45);

        // Integer.
        objectNode.put("integerField", 12345);

        // Complex map with string, array, object.
        ObjectNode mapNode = mapper.createObjectNode();
        String string = "foo";
        mapNode.put("string", string);
        ArrayNode array = mapper.createArrayNode();
        array.add("bar");
        array.add("baz");
        mapNode.set("array", array);
        ObjectNode object = mapper.createObjectNode();
        object.put("qux", "thud");
        mapNode.set("object", object);
        objectNode.set("mapField", mapNode);

        // Null.
        objectNode.putNull("nullField");

        // String.
        objectNode.put("stringField", "string value");

        this.objectNode = objectNode;
    }

    @After
    public void tearDown() throws Exception {
        objectNode = null;
    }

    @Test
    public void fileToJson() throws Exception {
        // Good file contents should convert to a non-null json node without blowing up.
        URL url = Thread.currentThread().getContextClassLoader().getResource("data/good.json");
        File file = new File(url.toURI());
        JsonNode jsonNode = JsonUtils.fileToJson(file);
        assertNotNull(jsonNode);
    }

    @Test
    public void fileToJsonWithException() throws Exception {
        // Bad file contents should blow up.
        URL url = Thread.currentThread().getContextClassLoader().getResource("data/bad.json");
        File file = new File(url.toURI());
        boolean exceptionThrown = false;
        try {
            JsonUtils.fileToJson(file);
        } catch (LoggingException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void fileToJsonIgnoreErrors() throws Exception {
        // Bad file contents should convert to a null.
        URL url = Thread.currentThread().getContextClassLoader().getResource("data/bad.json");
        File file = new File(url.toURI());
        JsonNode jsonNode = JsonUtils.fileToJsonIgnoreErrors(file);
        assertNull(jsonNode);
    }

    @Test
    public void jsonTreeToFile() throws Exception {
        // Should create a file from a JSON object without blowing up.
        File tempFile = File.createTempFile("test", ".json");
        JsonUtils.jsonTreeToFile(objectNode, tempFile);

        // Now we should be able to read the JSON object from the file.
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(tempFile);
        assertNotNull(jsonNode);
    }

    @Test
    public void jsonTreeToString() throws Exception {
        // A full json object should convert to a string which can be converted back into json.
        String string = JsonUtils.jsonTreeToString(objectNode);
        assertNotNull(string);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(string);
        assertNotNull(jsonNode);

        // This json string should contain all the field names.
        for (String fieldName : fieldsNames) {
            assertTrue(string.contains(fieldName));
        }
    }

    @Test
    public void jsonValueToListOfStrings() throws Exception {
        // Only the array field should convert to a list of strings. All other should blow up.
        for (String fieldName : fieldsNames) {
            JsonNode jsonNode = objectNode.get(fieldName);
            if (fieldName.equals("arrayField")) {
                List<String> value = JsonUtils.jsonValueToListOfStrings(jsonNode);
                assertEquals(2, value.size());
                assertEquals("one", value.get(0));
                assertEquals("two", value.get(1));
            } else {
                boolean exceptionThrown = false;
                try {
                    JsonUtils.jsonValueToListOfStrings(jsonNode);
                } catch (LoggingException e) {
                    exceptionThrown = true;
                }
                assertTrue(exceptionThrown);
            }
        }
    }

    @Test
    public void jsonValueToBoolean() throws Exception {
        // Only the boolean field should convert to a boolean value. All other should blow up.
        for (String fieldName : fieldsNames) {
            JsonNode jsonNode = objectNode.get(fieldName);
            if (fieldName.equals("booleanField")) {
                Boolean value = JsonUtils.jsonValueToBoolean(jsonNode);
                assertTrue(value);
            } else {
                boolean exceptionThrown = false;
                try {
                    JsonUtils.jsonValueToBoolean(jsonNode);
                } catch (LoggingException e) {
                    exceptionThrown = true;
                }
                assertTrue(exceptionThrown);
            }
        }
    }

    @Test
    public void jsonValueToDouble() throws Exception {
        // Only the double field should convert to a double value. All other should blow up.
        for (String fieldName : fieldsNames) {
            JsonNode jsonNode = objectNode.get(fieldName);
            if (fieldName.equals("doubleField")) {
                Double value = JsonUtils.jsonValueToDouble(jsonNode);
                assertEquals(123.45, value, 0.005);
            } else {
                boolean exceptionThrown = false;
                try {
                    JsonUtils.jsonValueToDouble(jsonNode);
                } catch (LoggingException e) {
                    exceptionThrown = true;
                }
                assertTrue(exceptionThrown);
            }
        }
    }

    @Test
    public void jsonValueToInteger() throws Exception {
        // Only the integer field should convert to an integer value. All other should blow up.
        for (String fieldName : fieldsNames) {
            JsonNode jsonNode = objectNode.get(fieldName);
            if (fieldName.equals("integerField")) {
                Integer value = JsonUtils.jsonValueToInteger(jsonNode);
                assertEquals(12345, value.intValue());
            } else {
                boolean exceptionThrown = false;
                try {
                    JsonUtils.jsonValueToInteger(jsonNode);
                } catch (LoggingException e) {
                    exceptionThrown = true;
                }
                assertTrue(exceptionThrown);
            }
        }
    }

    @Test
    public void jsonToMap() throws Exception {
        // Only the map field should convert to a complex map. All other should blow up.
        for (String fieldName : fieldsNames) {
            JsonNode jsonNode = objectNode.get(fieldName);
            if (fieldName.equals("mapField")) {
                Map value = JsonUtils.jsonToMap(jsonNode);
                assertEquals(3, value.size());

                Object stringValue = value.get("string");
                assertTrue(stringValue instanceof String);
                assertEquals("foo", stringValue);

                Object arrayValue = value.get("array");
                assertTrue(arrayValue instanceof List);
                List castToList = (List) arrayValue;
                assertEquals(2, castToList.size());
                assertEquals("bar", castToList.get(0));
                assertEquals("baz", castToList.get(1));

                Object objectValue = value.get("object");
                assertTrue(objectValue instanceof Map);
                Map castToMap = (Map) objectValue;
                assertEquals(1, castToMap.size());
                assertEquals("thud", castToMap.get("qux"));
            } else {
                boolean exceptionThrown = false;
                try {
                    JsonUtils.jsonToMap(jsonNode);
                } catch (LoggingException e) {
                    exceptionThrown = true;
                }
                assertTrue(exceptionThrown);
            }
        }
    }

    @Test
    public void jsonValueToString() throws Exception {
        // Only the string field should convert to a string value. All other should blow up.
        for (String fieldName : fieldsNames) {
            JsonNode jsonNode = objectNode.get(fieldName);
            if (fieldName.equals("stringField")) {
                String value = JsonUtils.jsonValueToString(jsonNode);
                assertEquals("string value", value);
            } else {
                boolean exceptionThrown = false;
                try {
                    JsonUtils.jsonValueToString(jsonNode);
                } catch (LoggingException e) {
                    exceptionThrown = true;
                }
                assertTrue(exceptionThrown);
            }
        }
    }

    @Test
    public void mapToJson() {
        // A complex map object should convert to json without blowing up.
        Map map = new LinkedHashMap();
        map.put("one", 2);
        map.put("three", "four");
        map.put("five", new String[]{"six", "seven"});
        Map map2 = new HashMap();
        map2.put("eight", "nine");
        map.put("ten", map2);

        JsonNode jsonNode = JsonUtils.mapToJson(map);
        assertNotNull(jsonNode);
    }

    @Test
    public void stringToJsonTree() throws Exception {
        // A good json string should convert to json without blowing up.
        String goodJsonString =
                "{\"ant\":99,\"bat\":\"cat\",\"dog\":[\"elephant\",\"flea\"],\"goat\":{\"horse\":\"iguana\"}}";
        JsonNode jsonNode = JsonUtils.stringToJsonTree(goodJsonString);
        assertNotNull(jsonNode);
    }

    @Test
    public void stringToJsonTreeWithException() throws Exception {
        // A bad json string should blow up.
        String badJsonString = "This is a test. This is only a test.";
        boolean exceptionThrown = false;
        try {
            JsonUtils.stringToJsonTree(badJsonString);
        } catch (LoggingException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}