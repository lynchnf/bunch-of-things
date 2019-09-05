package norman.bunch.of.things;

import javax.swing.*;
import javax.swing.text.Document;

public class Foo {
    public static void main(String[] args) {
        JTextField component = new JTextField();
        System.out.println("component.class=\"" + component.getClass().getName() + "\"");
        Document model = component.getDocument();
        System.out.println("model.class=\"" + model.getClass().getName() + "\"");
    }
}
