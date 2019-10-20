package norman.bunch.of.things.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String args[]) {
        LOGGER.debug("Starting App");
        App me = new App();
        me.doIt();
    }

    private void doIt() {
        // Initialize character.
        Bunch bunch = new Bunch();
        bunch.initThing("name", "Joe");
        bunch.initThing("str", 10);
        bunch.initThing("int", 10);
        bunch.initThing("dex", 10);
        bunch.initThing("con", 10);
        bunch.initThing("speed", 5.00);
        bunch.initBag("messages", "msg");
        bunch.initBag("skills", "cat", "diff", "level", "points");
        // Add a sword skill.
        bunch.addBunchToBag("skills", "sword");
        bunch.initThing(new String[]{"skills", "sword", "cat"}, "P");
        bunch.initThing(new String[]{"skills", "sword", "diff"}, "A");
        bunch.initThing(new String[]{"skills", "sword", "level"}, 0);
        bunch.initThing(new String[]{"skills", "sword", "points"}, 0);
        // Change dex to 11.
        ThingChangeEvent event1 = bunch.changeThing("dex", 11);
        System.out.println("event=" + event1);
        // Change sword skill to 12.
        ThingChangeEvent event2 = bunch.changeThing(new String[]{"skills", "sword", "level"}, 12);
        System.out.println("event=" + event2);
        System.out.println("bunch=" + bunch);
    }
}
