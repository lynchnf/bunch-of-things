package norman.bunch.of.things.bunch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Rule {
    private String name;
    private Pattern pattern;
    private boolean add;
    private boolean change;
    private boolean remove;
    private List<String> script = new ArrayList<>();

    public Rule(String name, String regex, boolean add, boolean change, boolean remove, List<String> script) {
        this.name = name;
        pattern = Pattern.compile(regex);
        this.add = add;
        this.change = change;
        this.remove = remove;
        this.script.addAll(script);
    }

    public String getName() {
        return name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean isAdd() {
        return add;
    }

    public boolean isChange() {
        return change;
    }

    public boolean isRemove() {
        return remove;
    }

    public List<String> getScript() {
        return script;
    }

    @Override
    public String toString() {
        // @formatter:off
        return "Rule{" +
                "name='" + name + '\'' +
                ", pattern=" + pattern +
                (add ? ", add" : "") +
                (change ? ", change" : "") +
                (remove ? ", remove" : "") +
                '}';
        // @formatter:on
    }
}
