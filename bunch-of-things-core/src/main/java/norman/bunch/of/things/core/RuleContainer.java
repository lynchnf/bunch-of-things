package norman.bunch.of.things.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RuleContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleContainer.class);
    private Bunch bunch;
    private List<Rule> rules = new ArrayList<>();

    public RuleContainer(Bunch bunch) {
        this.bunch = bunch;
    }

    public void addRule(String regex, String... scriptLines) {
        Rule rule = new Rule(regex, scriptLines);
        rules.add(rule);
    }
}
