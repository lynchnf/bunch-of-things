package norman.bunch.of.things.gui.model;

import javax.swing.*;
import javax.swing.text.PlainDocument;

public class CharacterPlainDocument extends PlainDocument {
    private Object source;

    public CharacterPlainDocument(JTextArea source) {
        this.source = source;
    }

    public CharacterPlainDocument(JTextField source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
