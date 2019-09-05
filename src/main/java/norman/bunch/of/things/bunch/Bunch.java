package norman.bunch.of.things.bunch;

import norman.bunch.of.things.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Bunch {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bunch.class);
    private Map<String, Thing> map = new HashMap<>();

    public void addThing(String id, Object value) {
        Thing thing = new Thing(id, value);
        map.put(id, thing);
        LOGGER.trace(String.format("Adding thing=%s.", thing));
        fileRules();
    }

    public void changeThing(String id, Object value) {
        Thing thing = map.get(id);
        thing.setValue(value);
        if (thing.changed()) {
            LOGGER.trace(String.format("Changing thing=%s.", thing));
            fileRules();
        }
    }

    public void removeThing(String id) {
        Thing thing = map.get(id);
        thing.flagForRemoval();
        LOGGER.trace(String.format("Removing thing=%s.", thing));
        fileRules();
    }

    private void fileRules() {
    }
}
