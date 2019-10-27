package norman.bunch.of.things.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BunchAddEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(BunchAddEvent.class);
    private String bagKey;
    private String bunchKey;
    private Bunch bunch;

    public BunchAddEvent(String bunchKey, Bunch bunch) {
        this.bunchKey = bunchKey;
        this.bunch = bunch;
    }

    public void setBagKey(String bagKey) {
        this.bagKey = bagKey;
    }

    @Override
    public String toString() {
        return "BunchAddEvent{" + "bagKey='" + bagKey + '\'' + ", bunchKey='" + bunchKey + '\'' + '}';
    }
}
