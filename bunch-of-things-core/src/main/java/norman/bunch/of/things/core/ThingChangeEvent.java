package norman.bunch.of.things.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ThingChangeEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThingChangeEvent.class);
    private String[] keyPath;
    private Object oldValue;
    private Object newValue;

    public ThingChangeEvent(String key, Object oldValue, Object newValue) {
        keyPath = new String[]{key};
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void prependToKeyPath(String key) {
        String[] newKeyPath = new String[keyPath.length + 1];
        newKeyPath[0] = key;
        System.arraycopy(keyPath, 0, newKeyPath, 1, keyPath.length);
        keyPath = newKeyPath;
    }

    @Override
    public String toString() {
        return "ThingChangeEvent{" + "keyPath=" + Arrays.toString(keyPath) + ", oldValue=" + oldValue + ", newValue=" +
                newValue + '}';
    }
}
