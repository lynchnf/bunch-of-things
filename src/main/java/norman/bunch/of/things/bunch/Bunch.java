package norman.bunch.of.things.bunch;

import com.fasterxml.jackson.databind.JsonNode;
import norman.bunch.of.things.JsonUtils;
import norman.bunch.of.things.LoggingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;

public class Bunch {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bunch.class);
    private List<Rule> ruleBook = new ArrayList<>();
    private Map<String, Thing> map = new HashMap<>();

    public void loadRuleBook(JsonNode ruleBookJson) throws LoggingException {
        if (!ruleBookJson.isArray()) {
            throw new LoggingException(LOGGER, "Element ruleBook is not an array.");
        } else {
            Iterator<JsonNode> ruleBookIterator = ruleBookJson.elements();
            while (ruleBookIterator.hasNext()) {
                JsonNode jsonObject = ruleBookIterator.next();
                String name = JsonUtils.jsonToString(jsonObject.get("name"));
                String regex = JsonUtils.jsonToString(jsonObject.get("regex"));
                Boolean add = JsonUtils.jsonToBoolean(jsonObject.get("add"));
                Boolean change = JsonUtils.jsonToBoolean(jsonObject.get("change"));
                Boolean remove = JsonUtils.jsonToBoolean(jsonObject.get("remove"));

                List<String> script = new ArrayList<>();
                JsonNode scriptJson = jsonObject.get("script");
                Iterator<JsonNode> scriptIterator = scriptJson.elements();
                while (scriptIterator.hasNext()) {
                    String line = JsonUtils.jsonToString(scriptIterator.next());
                    script.add(line);
                }

                Rule rule = new Rule(name, regex, add, change, remove, script);
                ruleBook.add(rule);
            }
        }
    }

    public LinkedHashMap<String, String> getAddRules(String id) {
        return getRulesImpl(id, rule -> rule.isAdd());
    }

    public LinkedHashMap<String, String> getChangeRules(String id) {
        return getRulesImpl(id, rule -> rule.isChange());
    }

    public LinkedHashMap<String, String> getRemoveRules(String id) {
        return getRulesImpl(id, rule -> rule.isRemove());
    }

    private LinkedHashMap<String, String> getRulesImpl(String id, Predicate<Rule> predicate) {
        // Use linked hash map to preserve order.
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

    public void initializeCharacter(JsonNode characterJson) throws LoggingException {
        Iterator<String> newCharacterFieldNamesIterator = characterJson.fieldNames();
        while (newCharacterFieldNamesIterator.hasNext()) {
            String id = newCharacterFieldNamesIterator.next();
            JsonNode valueJson = characterJson.get(id);
            if (valueJson.isBoolean()) {
                addThing(id, JsonUtils.jsonToBoolean(valueJson));
            } else if (valueJson.isDouble()) {
                addThing(id, JsonUtils.jsonToDouble(valueJson));
            } else if (valueJson.isInt()) {
                addThing(id, JsonUtils.jsonToInteger(valueJson));
            } else if (valueJson.isTextual()) {
                addThing(id, JsonUtils.jsonToString(valueJson));
            } else {
                throw new LoggingException(LOGGER,
                        "Invalid value for character property, id=" + id + ", valueJson=" + valueJson);
            }
        }
    }

    public void addThing(String id, Object value) throws LoggingException {
        Thing thing = new Thing(this, id, value);
        map.put(id, thing);
        LOGGER.trace(String.format("Adding thing=%s.", thing));
        fileRules();
    }

    public void changeThing(String id, Object value) throws LoggingException {
        Thing thing = map.get(id);
        thing.setValue(value);
        if (thing.changed()) {
            LOGGER.trace(String.format("Changing thing=%s.", thing));
            fileRules();
        }
    }

    public void removeThing(String id) throws LoggingException {
        Thing thing = map.get(id);
        thing.flagForRemoval();
        LOGGER.trace(String.format("Removing thing=%s.", thing));
        fileRules();
    }

    private void fileRules() throws LoggingException {
        String jsonString = toJsonString();
        boolean notDoneYet = true;
        while (notDoneYet) {
            notDoneYet = false;
            for (String id : map.keySet()) {
                Thing thing = map.get(id);
                if (thing.added()) {
                    notDoneYet = true;
                    thing.unflagAdded();
                    jsonString = thing.fileAddRules(jsonString);
                } else if (thing.changed()) {
                    notDoneYet = true;
                    thing.unflagChanged();
                    jsonString = thing.fileChangeRules(jsonString);
                } else if (thing.flaggedForRemoval()) {
                    notDoneYet = true;
                    jsonString = thing.fileRemoveRules(jsonString);
                    map.remove(id);
                }
            }
            updateFromJson(jsonString);
        }
    }

    private JsonNode toJsonObject() {
        Map<String, Object> valueMap = new HashMap<>();
        for (String id : map.keySet()) {
            valueMap.put(id, map.get(id).getValue());
        }
        return JsonUtils.mapToJson(valueMap);
    }

    private String toJsonString() {
        return toJsonObject().toString();
    }

    private void updateFromJson(String jsonString) throws LoggingException {
        JsonNode jsonNode = JsonUtils.stringToJson(jsonString);
        Map jsonMap = JsonUtils.jsonToMap(jsonNode);
        Set<String> removeIds = new HashSet<>(map.keySet());
        for (Object key : jsonMap.keySet()) {
            String id = (String) key;
            if (map.containsKey(id)) {
                removeIds.remove(id);
                Thing thing = map.get(id);
                thing.setValue(jsonMap.get(id));
                if (thing.changed()) {
                    LOGGER.trace(String.format("Changing thing=%s.", thing));
                }
            } else {
                Thing thing = new Thing(this, id, jsonMap.get(id));
                map.put(id, thing);
                LOGGER.trace(String.format("Adding thing=%s.", thing));
            }
        }
        for (String id : removeIds) {
            Thing thing = map.get(id);
            thing.flagForRemoval();
            LOGGER.trace(String.format("Removing thing=%s.", thing));
        }
    }
}
