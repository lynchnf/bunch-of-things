package norman.bunch.of.things.bunch;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RuleTest {
    @Test
    public void getEverything() {
        List<String> script = new ArrayList<>();
        script.add("line1");
        script.add("line2");

        Rule rule = new Rule("name", "^regex$", true, false, true, script);

        assertEquals("name", rule.getName());
        assertEquals("^regex$", rule.getPattern().pattern());
        assertTrue(rule.isAdd());
        assertFalse(rule.isChange());
        assertTrue(rule.isRemove());
        assertEquals(2, rule.getScript().size());
        assertEquals("line1", rule.getScript().get(0));
        assertEquals("line2", rule.getScript().get(1));
    }
}