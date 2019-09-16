package norman.bunch.of.things.bunch;

import norman.bunch.of.things.LoggingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.LinkedHashMap;

public class Thing {
    private static final Logger LOGGER = LoggerFactory.getLogger(Thing.class);
    private Bunch bunch;
    private String id;
    private Object value;
    private Object oldValue;
    private boolean addFlag;
    private boolean removeFlag;
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    public Thing(Bunch bunch, String id, Object value) {
        this.bunch = bunch;
        this.id = id;
        this.value = value;
        oldValue = value;
        addFlag = true;
        removeFlag = false;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean added() {
        return addFlag;
    }

    public void unflagAdded() {
        addFlag = false;
    }

    public boolean changed() {
        return value == null && oldValue != null || value != null && !value.equals(oldValue);
    }

    public void unflagChanged() {
        oldValue = value;
    }

    public boolean flaggedForRemoval() {
        return removeFlag;
    }

    public void flagForRemoval() {
        removeFlag = true;
    }

    public String fireAddRules(String jsonString) throws LoggingException {
        LinkedHashMap<String, String> rules = bunch.getAddRules(id);
        return fireRulesImpl(jsonString, rules);
    }

    public String fireChangeRules(String jsonString) throws LoggingException {
        LinkedHashMap<String, String> rules = bunch.getChangeRules(id);
        return fireRulesImpl(jsonString, rules);
    }

    public String fireRemoveRules(String jsonString) throws LoggingException {
        LinkedHashMap<String, String> rules = bunch.getRemoveRules(id);
        return fireRulesImpl(jsonString, rules);
    }

    private String fireRulesImpl(String jsonString, LinkedHashMap<String, String> rules) throws LoggingException {
        for (String name : rules.keySet()) {
            String ruleScript = rules.get(name);
            engine.put("bunchString", jsonString);
            engine.put("thingId", id);
            StringBuilder fullScript = new StringBuilder();
            fullScript.append(String.format("%s%n", "var bunch = JSON.parse(bunchString);"));
            fullScript.append(ruleScript);
            fullScript.append(String.format("%s%n", "JSON.stringify(bunch);"));
            LOGGER.trace(String.format("For \"%s\" filing rule \"%s\".", id, name));
            try {
                jsonString = (String) engine.eval(fullScript.toString());
            } catch (ScriptException e) {
                throw new LoggingException(LOGGER, "Could not execute Javascript for rule=" + name, e);
            }
        }
        return jsonString;
    }

    @Override
    public String toString() {
        // @formatter:off
        return "Thing{" +
                "id='" + id + '\'' +
                ", value=" + value +
                (addFlag ? ", add" : "") +
                (changed() ? ", change" : "") +
                (removeFlag ? ", remove" : "") +
                '}';
        // @formatter:on
    }
}
