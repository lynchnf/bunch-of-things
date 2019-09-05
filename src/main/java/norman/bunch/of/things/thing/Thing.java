package norman.bunch.of.things.thing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Thing {
    private static final Logger logger = LoggerFactory.getLogger(Thing.class);
    private String id;
    private Object value;
    private Object oldValue;
    private boolean addFlag;
    private boolean removeFlag;

    public Thing(String id, Object value) {
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
