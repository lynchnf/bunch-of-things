package norman.bunch.of.things.bunch;

import com.fasterxml.jackson.databind.JsonNode;
import norman.bunch.of.things.JsonUtils;
import norman.bunch.of.things.LoggingException;
import norman.bunch.of.things.gui.CharacterFrame;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;

public class Bunch {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bunch.class);
    private List<Rule> ruleBook = new ArrayList<>();
    private Map<String, Thing> thingMap = new HashMap<>();
    private Map<String, String> charToGuiBinding = new HashMap<>();
    private CharacterFrame gui;

    public void loadRuleBook(JsonNode ruleBookJson) throws LoggingException {
        if (!ruleBookJson.isArray()) {
            throw new LoggingException(LOGGER, "Element ruleBook is not an array.");
        } else {
            Iterator<JsonNode> ruleBookIterator = ruleBookJson.elements();
            while (ruleBookIterator.hasNext()) {
                JsonNode jsonObject = ruleBookIterator.next();
                String name = JsonUtils.jsonValueToString(jsonObject.get("name"));
                String regex = JsonUtils.jsonValueToString(jsonObject.get("regex"));
                Boolean add = JsonUtils.jsonValueToBoolean(jsonObject.get("add"));
                Boolean change = JsonUtils.jsonValueToBoolean(jsonObject.get("change"));
                Boolean remove = JsonUtils.jsonValueToBoolean(jsonObject.get("remove"));

                List<String> script = new ArrayList<>();
                JsonNode scriptJson = jsonObject.get("script");
                Iterator<JsonNode> scriptIterator = scriptJson.elements();
                while (scriptIterator.hasNext()) {
                    String line = JsonUtils.jsonValueToString(scriptIterator.next());
                    script.add(line);
                }

                Rule rule = new Rule(name, regex, add, change, remove, script);
                ruleBook.add(rule);
            }
        }
    }

    public void initializeCharacter(JsonNode characterJson) throws LoggingException {
        Iterator<String> newCharacterFieldNamesIterator = characterJson.fieldNames();
        while (newCharacterFieldNamesIterator.hasNext()) {
            String id = newCharacterFieldNamesIterator.next();
            JsonNode valueJson = characterJson.get(id);
            if (valueJson.isBoolean()) {
                addThing(id, JsonUtils.jsonValueToBoolean(valueJson));
            } else if (valueJson.isDouble()) {
                addThing(id, JsonUtils.jsonValueToDouble(valueJson));
            } else if (valueJson.isInt()) {
                addThing(id, JsonUtils.jsonValueToInteger(valueJson));
            } else if (valueJson.isTextual()) {
                addThing(id, JsonUtils.jsonValueToString(valueJson));
            } else {
                throw new LoggingException(LOGGER,
                        "Invalid value for character property, id=" + id + ", valueJson=" + valueJson);
            }
        }
    }

    public Map<String, String> setBindings(CharacterFrame characterFrame, JsonNode bindingsJson)
            throws LoggingException {
        this.gui = characterFrame;
        Map<String, String> guiToCharBinding = new HashMap<>();
        Iterator<String> bindingsFieldNamesIterator = bindingsJson.fieldNames();
        while (bindingsFieldNamesIterator.hasNext()) {
            String id = bindingsFieldNamesIterator.next();
            JsonNode valueJson = bindingsJson.get(id);
            List<String> names = JsonUtils.jsonValueToListOfStrings(valueJson);
            String uiComponentName = StringUtils.join(names, ".");
            guiToCharBinding.put(uiComponentName, id);
            charToGuiBinding.put(id, uiComponentName);
        }
        return guiToCharBinding;
    }

    public void addThing(String id, Object value) throws LoggingException {
        Thing thing = new Thing(this, id, value);
        thingMap.put(id, thing);
        LOGGER.trace(String.format("Adding thing=%s.", thing));
        // FIXME Add component.
        fireRules();
    }

    public void changeThing(String id, Object value) throws LoggingException {
        Thing thing = thingMap.get(id);
        thing.setValue(value);
        if (thing.changed()) {
            LOGGER.trace(String.format("Changing thing=%s.", thing));
            gui.changeComponent(charToGuiBinding.get(id), value);
            fireRules();
        }
    }

    public void removeThing(String id) throws LoggingException {
        Thing thing = thingMap.get(id);
        thing.flagForRemoval();
        LOGGER.trace(String.format("Removing thing=%s.", thing));
        // FIXME Remove component.
        fireRules();
    }

    protected LinkedHashMap<String, String> getAddRules(String id) {
        return getRulesImpl(id, rule -> rule.isAdd());
    }

    protected LinkedHashMap<String, String> getChangeRules(String id) {
        return getRulesImpl(id, rule -> rule.isChange());
    }

    protected LinkedHashMap<String, String> getRemoveRules(String id) {
        return getRulesImpl(id, rule -> rule.isRemove());
    }

    private LinkedHashMap<String, String> getRulesImpl(String id, Predicate<Rule> predicate) {
        // Use linked hash thingMap to preserve order.
        LinkedHashMap<String, String> ruleNamesAndBodies = new LinkedHashMap<>();
        for (Rule rule : ruleBook) {
            if (rule.getPattern().matcher(id).matches() && predicate.test(rule)) {
                String name = rule.getName();
                StringBuilder sb = new StringBuilder();
                for (String line : rule.getScript()) {
                    // Add a line separator to make it look pretty.
                    sb.append(String.format("%s%n", line));
                }
                ruleNamesAndBodies.put(name, sb.toString());
            }
        }
        return ruleNamesAndBodies;
    }

    private void fireRules() throws LoggingException {
        String jsonString = toJsonString();
        boolean notDoneYet = true;
        while (notDoneYet) {
            notDoneYet = false;
            for (String id : thingMap.keySet()) {
                Thing thing = thingMap.get(id);
                if (thing.added()) {
                    notDoneYet = true;
                    thing.unflagAdded();
                    jsonString = thing.fireAddRules(jsonString);
                } else if (thing.changed()) {
                    notDoneYet = true;
                    thing.unflagChanged();
                    jsonString = thing.fireChangeRules(jsonString);
                } else if (thing.flaggedForRemoval()) {
                    notDoneYet = true;
                    jsonString = thing.fireRemoveRules(jsonString);
                    thingMap.remove(id);
                }
            }
            updateFromJson(jsonString);
        }
    }

    private JsonNode toJsonObject() {
        Map<String, Object> valueMap = new HashMap<>();
        for (String id : thingMap.keySet()) {
            valueMap.put(id, thingMap.get(id).getValue());
        }
        return JsonUtils.mapToJson(valueMap);
    }

    private String toJsonString() {
        return JsonUtils.jsonTreeToString(toJsonObject());
    }

    private void updateFromJson(String jsonString) throws LoggingException {
        JsonNode jsonNode = JsonUtils.stringToJsonTree(jsonString);
        Map jsonMap = JsonUtils.jsonToMap(jsonNode);
        Set<String> removeIds = new HashSet<>(thingMap.keySet());
        for (Object key : jsonMap.keySet()) {
            String id = (String) key;
            if (thingMap.containsKey(id)) {
                removeIds.remove(id);
                Thing thing = thingMap.get(id);
                Object value = jsonMap.get(id);
                thing.setValue(value);
                if (thing.changed()) {
                    LOGGER.trace(String.format("Changing thing=%s.", thing));
                    gui.changeComponent(charToGuiBinding.get(id), value);
                }
            } else {
                Thing thing = new Thing(this, id, jsonMap.get(id));
                thingMap.put(id, thing);
                LOGGER.trace(String.format("Adding thing=%s.", thing));
                // FIXME Add component.
            }
        }
        for (String id : removeIds) {
            Thing thing = thingMap.get(id);
            thing.flagForRemoval();
            LOGGER.trace(String.format("Removing thing=%s.", thing));
            // FIXME Remove component.
        }
    }
}
