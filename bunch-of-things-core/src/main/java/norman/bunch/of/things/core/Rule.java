package norman.bunch.of.things.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rule {
    private static final Logger LOGGER = LoggerFactory.getLogger(Rule.class);
    private String regex;
    private String[] scriptLines;

    public Rule(String regex, String[] scriptLines) {
        this.regex = regex;
        this.scriptLines = scriptLines;
    }
}
