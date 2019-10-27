package norman.bunch.of.things.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Bunch {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bunch.class);
    private Map<String, Object> map = new HashMap<>();

    public Bunch() {
    }

    public void initThing(String key, Object value) {
        map.put(key, value);
    }

    public void initThing(String[] keyPath, Object value) {
        if (keyPath.length == 1) {
            initThing(keyPath[0], value);
        } else {
            Bag bag = (Bag) map.get(keyPath[0]);
            String[] bagKeyPath = new String[keyPath.length - 1];
            System.arraycopy(keyPath, 1, bagKeyPath, 0, bagKeyPath.length);
            bag.initThing(bagKeyPath, value);
        }
    }

    public void initBag(String key, String... keysInBunch) {
        Bag bag = new Bag(keysInBunch);
        map.put(key, bag);
    }

    public BunchAddEvent addBunchToBag(String bagKey, String bunchKey) {
        Bag bag = (Bag) map.get(bagKey);
        BunchAddEvent event = bag.addBunch(bunchKey);
        event.setBagKey(bagKey);
        return event;
    }

    public BunchRemoveEvent removeBunchFromBag(String bagKey, String bunchKey) {
        Bag bag = (Bag) map.get(bagKey);
        BunchRemoveEvent event = bag.removeBunch(bunchKey);
        event.setBagKey(bagKey);
        return event;
    }

    public ThingChangeEvent changeThing(String key, Object value) {
        Object oldValue = map.get(key);
        map.put(key, value);
        ThingChangeEvent event = null;
        if (oldValue == null && value != null || !oldValue.equals(value)) {
            event = new ThingChangeEvent(key, oldValue, value);
        }
        return event;
    }

    public ThingChangeEvent changeThing(String[] keyPath, Object value) {
        if (keyPath.length == 1) {
            return changeThing(keyPath[0], value);
        } else {
            Bag bag = (Bag) map.get(keyPath[0]);
            String[] bagKeyPath = new String[keyPath.length - 1];
            System.arraycopy(keyPath, 1, bagKeyPath, 0, bagKeyPath.length);
            ThingChangeEvent event = bag.changeThing(bagKeyPath, value);
            event.prependToKeyPath(keyPath[0]);
            return event;
        }
    }

    @Override
    public String toString() {
        return toStringWithIndent(0);
    }

    public String toStringWithIndent(int indentWidth) {
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        if (entries.isEmpty()) {
            return "{}";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("{" + System.lineSeparator());
            for (Map.Entry<String, Object> entry : entries) {
                Object value = entry.getValue();
                String valueString = String.valueOf(value);
                if (value != null && value instanceof Bag) {
                    valueString = ((Bag) value).toStringWithIndent(indentWidth + 4);
                }
                sb.append(StringUtils.repeat(' ', indentWidth + 4) + entry.getKey() + "=" + valueString +
                        System.lineSeparator());
            }
            sb.append(StringUtils.repeat(' ', indentWidth) + "}");
            return sb.toString();
        }
    }
}
