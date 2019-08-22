package norman.bunch.of.things.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class BunchFrame extends JInternalFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(BunchFrame.class);
    private static final boolean RESIZABLE = true;
    private static final boolean CLOSABLE = true;
    private static final boolean MAXIMIZABLE = true;
    private static final boolean ICONIFIABLE = true;
    private static int count = 1;

    public BunchFrame(String title) {
        super(title + " " + count, RESIZABLE, CLOSABLE, MAXIMIZABLE, ICONIFIABLE);
        setSize(150, 300);
        setLocation(30 * (count - 1), 30 * (count - 1));
        count++;
    }
}