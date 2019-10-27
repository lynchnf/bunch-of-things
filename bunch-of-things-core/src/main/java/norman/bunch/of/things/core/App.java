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
        Bunch bunch = new Bunch();
        // New character.
        bunch.initBag("messages", "msg");
        bunch.initThing("characterName", "");
        bunch.initThing("playerName", "");
        bunch.initThing("totalAttributePoints", 20);
        bunch.initThing("unspentAttributePoints", 20);
        bunch.initThing("musclesValue", 10);
        bunch.initThing("musclesPoints", 0);
        bunch.initThing("brainsValue", 10);
        bunch.initThing("brainsPoints", 0);
        bunch.initThing("congenialityValue", 10);
        bunch.initThing("congenialityPoints", 0);
        bunch.initThing("cloutValue", 10);
        bunch.initThing("cloutPoints", 0);
        bunch.initThing("wealthValue", 10);
        bunch.initThing("wealthPoints", 0);
        bunch.initThing("looksValue", 10);
        bunch.initThing("looksPoints", 0);
        bunch.initThing("level", 3);
        bunch.initBag("classLevels", "class");
        // My Character
        System.out.println(bunch.changeThing("characterName", "Joe"));
        System.out.println(bunch.changeThing("playerName", "Norman"));
        System.out.println(bunch.changeThing("musclesValue", 18));
        System.out.println(bunch.changeThing("looksValue", 14));
        System.out.println(bunch.changeThing("brainsValue", 7));
        System.out.println(bunch.addBunchToBag("classLevels", "01"));
        bunch.initThing(new String[]{"classLevels", "01", "class"}, "JOCK");
        System.out.println(bunch.addBunchToBag("classLevels", "02"));
        bunch.initThing(new String[]{"classLevels", "02", "class"}, "MODEL");
        System.out.println(bunch.addBunchToBag("classLevels", "03"));
        bunch.initThing(new String[]{"classLevels", "03", "class"}, "JOCK");
        //
        System.out.println("bunch=" + bunch);
        //
        RuleContainer container = new RuleContainer(bunch);
        // @formatter:off
        container.addRule("^(muscles|brains|congeniality|clout|wealth|looks)Value$",
                "var value = bunch[thingId];",
                "var len = thingId.length();",
                "var ptsId = thingId.substring(0, len-5) + 'Points';",
                "if (value < 8) {",
                "  bunch[ptsId] = bunch[thingId] * 2 - 18;",
                "} else if (value >= 8 && value < 13) {",
                "  bunch[ptsId] = bunch[thingId] - 10;",
                "} else if (value >= 13 && value < 15) {",
                "  bunch[ptsId] = bunch[thingId] * 2 - 23;",
                "} else if (value >= 15 && value < 17) {",
                "  bunch[ptsId] = bunch[thingId] * 3 - 38;",
                "} else {",
                "  bunch[ptsId] = bunch[thingId] * 4 - 55;",
                "};");
        container.addRule("^(muscles|brains|congeniality|clout|wealth|looks)Points$",
                "var spentPoints = bunch['musclesPoints'] + bunch['brainsPoints'] + bunch['congenialityPoints'] + bunch['cloutPoints'] + bunch['wealthPoints'] + bunch['looksPoints'];",
                "bunch['unspentAttributePoints'] = bunch['totalAttributePoints'] - spentPoints;");
        // @formatter:on
    }

    private void doIt2() {
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
        // Change dex to 11.
        ThingChangeEvent event1 = bunch.changeThing("dex", 11);
        System.out.println("event=" + event1);
        // Add a axe skill.
        BunchAddEvent event2 = bunch.addBunchToBag("skills", "axe");
        System.out.println("event=" + event2);
        bunch.initThing(new String[]{"skills", "axe", "cat"}, "P");
        bunch.initThing(new String[]{"skills", "axe", "diff"}, "E");
        bunch.initThing(new String[]{"skills", "axe", "level"}, 0);
        bunch.initThing(new String[]{"skills", "axe", "points"}, 0);
        // Change axe skill to 13.
        ThingChangeEvent event3 = bunch.changeThing(new String[]{"skills", "axe", "level"}, 13);
        System.out.println("event=" + event3);
        // Add a sword skill.
        BunchAddEvent event4 = bunch.addBunchToBag("skills", "sword");
        System.out.println("event=" + event4);
        bunch.initThing(new String[]{"skills", "sword", "cat"}, "P");
        bunch.initThing(new String[]{"skills", "sword", "diff"}, "A");
        bunch.initThing(new String[]{"skills", "sword", "level"}, 0);
        bunch.initThing(new String[]{"skills", "sword", "points"}, 0);
        // Change sword skill to 12.
        ThingChangeEvent event5 = bunch.changeThing(new String[]{"skills", "sword", "level"}, 12);
        System.out.println("event=" + event5);
        // Remove axe skill.
        BunchRemoveEvent event6 = bunch.removeBunchFromBag("skills", "axe");
        System.out.println("event=" + event6);
        //
        System.out.println("bunch=" + bunch);
    }
}
