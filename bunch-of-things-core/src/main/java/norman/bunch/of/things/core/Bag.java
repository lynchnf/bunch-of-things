package norman.bunch.of.things.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Bag {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bag.class);
    private Map<String, Bunch> map = new HashMap<>();
    private String[] keysInBunch;

    public Bag(String[] keysInBunch) {
        this.keysInBunch = keysInBunch;
    }

    public BunchAddEvent addBunch(String key) {
        Bunch bunch = new Bunch();
        for (String keyInBunch : keysInBunch) {
            bunch.initThing(keyInBunch, null);
        }
        map.put(key, bunch);
        BunchAddEvent event = new BunchAddEvent(key, bunch);
        return event;
    }

    public BunchRemoveEvent removeBunch(String key) {
        Bunch bunch = map.remove(key);
        BunchRemoveEvent event = new BunchRemoveEvent(key, bunch);
        return event;
    }

    public void initThing(String[] keyPath, Object value) {
        Bunch bunch = map.get(keyPath[0]);
        String[] bunchKeyPath = new String[keyPath.length - 1];
        System.arraycopy(keyPath, 1, bunchKeyPath, 0, bunchKeyPath.length);
        bunch.initThing(bunchKeyPath, value);
    }

    public ThingChangeEvent changeThing(String[] keyPath, Object value) {
        Bunch bunch = map.get(keyPath[0]);
        String[] bunchKeyPath = new String[keyPath.length - 1];
        System.arraycopy(keyPath, 1, bunchKeyPath, 0, bunchKeyPath.length);
        ThingChangeEvent event = bunch.changeThing(bunchKeyPath, value);
        event.prependToKeyPath(keyPath[0]);
        return event;
    }

    @Override
    public String toString() {
        return toStringWithIndent(0);
    }

    public String toStringWithIndent(int indentWidth) {
        Set<Map.Entry<String, Bunch>> entries = map.entrySet();
        if (entries.isEmpty()) {
            return "{}";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("{" + System.lineSeparator());
            for (Map.Entry<String, Bunch> entry : entries) {
                Bunch value = entry.getValue();
                String valueString = value.toStringWithIndent(indentWidth + 4);
                sb.append(StringUtils.repeat(' ', indentWidth + 4) + entry.getKey() + "=" + valueString +
                        System.lineSeparator());
            }
            sb.append(StringUtils.repeat(' ', indentWidth) + "}");
            return sb.toString();
        }
    }
}
