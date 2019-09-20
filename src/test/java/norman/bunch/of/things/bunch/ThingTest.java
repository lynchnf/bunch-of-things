package norman.bunch.of.things.bunch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedHashMap;

import static org.junit.Assert.*;

public class ThingTest {
    private Thing thing;
    private ObjectMapper mapper;
    private String inJsonString;

    @Before
    public void setUp() throws Exception {
        // Construct mock bunch.
        Bunch mockBunch = Mockito.mock(Bunch.class);

        LinkedHashMap<String, String> addRules = new LinkedHashMap<>();
        addRules.put("Add Rule", "bunch['otherId'] = 'added ' + bunch[thingId];");
        Mockito.when(mockBunch.getAddRules(Mockito.anyString())).thenReturn(addRules);

        LinkedHashMap<String, String> changeRules = new LinkedHashMap<>();
        changeRules.put("Change Rule", "bunch['otherId'] = 'changed ' + bunch[thingId];");
        Mockito.when(mockBunch.getChangeRules(Mockito.anyString())).thenReturn(changeRules);

        LinkedHashMap<String, String> removeRules = new LinkedHashMap<>();
        removeRules.put("Remove Rule", "bunch['otherId'] = 'removed ' + bunch[thingId];");
        Mockito.when(mockBunch.getRemoveRules(Mockito.anyString())).thenReturn(removeRules);

        // Create thing with mock bunch.
        thing = new Thing(mockBunch, "id", "value");

        // Create JSON object mapper
        mapper = new ObjectMapper();

        // Create JSON for testing rules.
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", "value");
        objectNode.put("otherId", "other");
        inJsonString = objectNode.toString();
    }

    @After
    public void tearDown() throws Exception {
        thing = null;
        mapper = null;
        inJsonString = null;
    }

    @Test
    public void getValue() {
        assertEquals("value", thing.getValue());
        thing.setValue("newValue");
        assertEquals("newValue", thing.getValue());
    }

    @Test
    public void added() {
        assertTrue(thing.added());
        thing.unflagAdded();
        assertFalse(thing.added());
    }

    @Test
    public void changed() {
        assertFalse(thing.changed());
        thing.setValue("newValue");
        assertTrue(thing.changed());
        thing.unflagChanged();
        assertFalse(thing.changed());
    }

    @Test
    public void flaggedForRemoval() {
        assertFalse(thing.flaggedForRemoval());
        thing.flagForRemoval();
        assertTrue(thing.flaggedForRemoval());
    }

    @Test
    public void fireAddRules() throws Exception {
        String outJsonString = thing.fireAddRules(inJsonString);
        JsonNode jsonNode = mapper.readTree(outJsonString);
        assertEquals("added value", jsonNode.get("otherId").asText());
    }

    @Test
    public void fireChangeRules() throws Exception {
        String outJsonString = thing.fireChangeRules(inJsonString);
        JsonNode jsonNode = mapper.readTree(outJsonString);
        assertEquals("changed value", jsonNode.get("otherId").asText());
    }

    @Test
    public void fireRemoveRules() throws Exception {
        String outJsonString = thing.fireRemoveRules(inJsonString);
        JsonNode jsonNode = mapper.readTree(outJsonString);
        assertEquals("removed value", jsonNode.get("otherId").asText());
    }
}