package norman.bunch.of.things.bunch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import norman.bunch.of.things.gui.CharacterFrame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BunchTest {
    private ObjectMapper mapper;
    private Bunch bunch;
    private ArrayNode ruleBookJson;
    private ObjectNode characterJson;
    private ObjectNode bindingsJson;
    private CharacterFrame characterFrame;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        bunch = new Bunch();

        ruleBookJson = mapper.createArrayNode();
        ObjectNode addRule = mapper.createObjectNode();
        addRule.put("name", "Add Rule");
        addRule.put("regex", "^id$");
        addRule.put("add", Boolean.TRUE);
        addRule.put("change", Boolean.FALSE);
        addRule.put("remove", Boolean.FALSE);
        ArrayNode addScript = mapper.createArrayNode();
        addScript.add("bunch['otherId'] = 'added ' + bunch[thingId];");
        addRule.set("script", addScript);
        ruleBookJson.add(addRule);
        ObjectNode changeRule = mapper.createObjectNode();
        changeRule.put("name", "Change Rule");
        changeRule.put("regex", "^id$");
        changeRule.put("add", Boolean.FALSE);
        changeRule.put("change", Boolean.TRUE);
        changeRule.put("remove", Boolean.FALSE);
        ArrayNode changeScript = mapper.createArrayNode();
        changeScript.add("bunch['otherId'] = 'changed ' + bunch[thingId];");
        changeRule.set("script", changeScript);
        ruleBookJson.add(changeRule);
        ObjectNode removeRule = mapper.createObjectNode();
        removeRule.put("name", "Remove Rule");
        removeRule.put("regex", "^id$");
        removeRule.put("add", Boolean.FALSE);
        removeRule.put("change", Boolean.FALSE);
        removeRule.put("remove", Boolean.TRUE);
        ArrayNode removeScript = mapper.createArrayNode();
        removeScript.add("bunch['otherId'] = 'removed ' + bunch[thingId];");
        removeRule.set("script", removeScript);
        ruleBookJson.add(removeRule);

        characterJson = mapper.createObjectNode();
        characterJson.put("otherId", "other");

        characterFrame = Mockito.mock(CharacterFrame.class);

        bindingsJson = mapper.createObjectNode();
        ArrayNode characterNameArray = mapper.createArrayNode();
        characterNameArray.add("header");
        characterNameArray.add("characterName");
        bindingsJson.set("characterName", characterNameArray);
        ArrayNode strArray = mapper.createArrayNode();
        strArray.add("attribute");
        strArray.add("str");
        bindingsJson.set("str", strArray);
        ArrayNode intArray = mapper.createArrayNode();
        intArray.add("attribute");
        intArray.add("int");
        bindingsJson.set("int", intArray);
    }

    @After
    public void tearDown() throws Exception {
        mapper = null;
        bunch = null;
        ruleBookJson = null;
        characterJson = null;
        characterFrame = null;
        bindingsJson = null;
    }

    @Test
    public void loadRuleBook() throws Exception {
        bunch.loadRuleBook(ruleBookJson);

        LinkedHashMap<String, String> addRuleMap = bunch.getAddRules("id");
        assertEquals(1, addRuleMap.size());
        assertEquals("bunch['otherId'] = 'added ' + bunch[thingId];\n", addRuleMap.get("Add Rule"));

        LinkedHashMap<String, String> changeRuleMap = bunch.getChangeRules("id");
        assertEquals(1, changeRuleMap.size());
        assertEquals("bunch['otherId'] = 'changed ' + bunch[thingId];\n", changeRuleMap.get("Change Rule"));

        LinkedHashMap<String, String> removeRuleMap = bunch.getRemoveRules("id");
        assertEquals(1, removeRuleMap.size());
        assertEquals("bunch['otherId'] = 'removed ' + bunch[thingId];\n", removeRuleMap.get("Remove Rule"));
    }

    @Test
    public void initializeCharacter() throws Exception {
        bunch.initializeCharacter(characterJson);
    }

    @Test
    public void setBindings() throws Exception {
        Map<String, String> outBindingsMap = bunch.setBindings(characterFrame, bindingsJson);
        assertEquals(3, outBindingsMap.size());
        assertEquals("characterName", outBindingsMap.get("header.characterName"));
        assertEquals("str", outBindingsMap.get("attribute.str"));
        assertEquals("int", outBindingsMap.get("attribute.int"));
    }

    @Test
    public void addThing() throws Exception {
        bunch.initializeCharacter(characterJson);
        bunch.setBindings(characterFrame, bindingsJson);
        bunch.loadRuleBook(ruleBookJson);

        bunch.addThing("id", "value");
    }

    @Test
    public void changeThing() throws Exception {
        characterJson.put("id", "value");
        bunch.initializeCharacter(characterJson);
        bunch.setBindings(characterFrame, bindingsJson);
        bunch.loadRuleBook(ruleBookJson);

        bunch.changeThing("id", "newValue");
    }

    @Test
    public void removeThing() throws Exception {
        characterJson.put("id", "value");
        bunch.initializeCharacter(characterJson);
        bunch.setBindings(characterFrame, bindingsJson);
        bunch.loadRuleBook(ruleBookJson);

        bunch.removeThing("id");
    }
}