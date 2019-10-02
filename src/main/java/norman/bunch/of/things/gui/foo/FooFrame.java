package norman.bunch.of.things.gui.foo;

import norman.bunch.of.things.gui.model.CharacterPlainDocument;
import norman.bunch.of.things.gui.table.FooTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Document;
import java.awt.*;

public class FooFrame extends JInternalFrame implements DocumentListener, ChangeListener, TableModelListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FooFrame.class);
    private static final boolean RESIZABLE = true;
    private static final boolean CLOSABLE = true;
    private static final boolean MAXIMIZABLE = true;
    private static final boolean ICONIFIABLE = true;
    private static final int OPEN_OFFSET_INCREMENT = 30;
    private static int openOffset = 0;
    private static String[] columnNames = {"First Column", "Second Column", "Third Column"};
    // @formatter:off
    private static Object[][] rowData = {{"One", Integer.valueOf(111), Boolean.TRUE},
                {"Two", Integer.valueOf(222), Boolean.FALSE},
                {"Three", Integer.valueOf(333), Boolean.TRUE},
                {"Four", Integer.valueOf(444), Boolean.FALSE},
                {"Five", Integer.valueOf(555), Boolean.TRUE},
                {"Six", Integer.valueOf(666), Boolean.FALSE},
                {"Seven", Integer.valueOf(777), Boolean.TRUE},
                {"Eight", Integer.valueOf(888), Boolean.FALSE},
                {"Nine", Integer.valueOf(999), Boolean.TRUE}};
        // @formatter:on

    public FooFrame(String title) {
        super(title, RESIZABLE, CLOSABLE, MAXIMIZABLE, ICONIFIABLE);
        setLocation(openOffset, openOffset);
        openOffset += OPEN_OFFSET_INCREMENT;
        Container contentPane = new JPanel(new GridBagLayout());
        setContentPane(contentPane);

        addHeader(contentPane);
        addAttributes(contentPane);
        addSkills(contentPane);

        pack();
    }

    private void addHeader(Container container) {
        JComponent component = new JPanel(new GridBagLayout());
        component.setName("header");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        container.add(component, constraints);
        addNameLabel(component);
        addNameText(component);
        logStuff(component);
    }

    private void addNameLabel(Container container) {
        JComponent component = new JLabel();
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("header.namelabel");
        ((JLabel) component).setText("Name");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 0;
        constraints.gridy = 0;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addNameText(Container container) {
        JTextField textField = new JTextField();
        Document document = new CharacterPlainDocument(textField);
        textField.setDocument(document);
        document.addDocumentListener(this);
        JComponent component = textField;
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("header.nametext");
        ((JTextField) component).setColumns(20);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 1;
        constraints.gridy = 0;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addAttributes(Container container) {
        JComponent component = new JPanel(new GridBagLayout());
        component.setName("attributes");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 1;
        container.add(component, constraints);
        addStrLabel(component);
        addStrValue(component);
        addStrPoints(component);
        addIntLabel(component);
        addIntValue(component);
        addIntPoints(component);
        addConLabel(component);
        addConValue(component);
        addConPoints(component);
        logStuff(component);
    }

    private void addStrLabel(Container container) {
        JComponent component = new JLabel();
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("attributes.strlabel");
        ((JLabel) component).setText("STR");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 0;
        constraints.gridy = 0;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addStrValue(Container container) {
        JSpinner spinner = new JSpinner();
        spinner.addChangeListener(this);
        JComponent component = spinner;
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("attributes.strvalue");
        ((JSpinner) component).setValue(10);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 1;
        constraints.gridy = 0;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addStrPoints(Container container) {
        JComponent component = new JLabel();
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("attributes.strpoints");
        ((JLabel) component).setText("0");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 2;
        constraints.gridy = 0;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addIntLabel(Container container) {
        JComponent component = new JLabel();
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("attributes.intlabel");
        ((JLabel) component).setText("INT");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 0;
        constraints.gridy = 1;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addIntValue(Container container) {
        JSpinner spinner = new JSpinner();
        spinner.addChangeListener(this);
        JComponent component = spinner;
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("attributes.intvalue");
        ((JSpinner) component).setValue(10);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 1;
        constraints.gridy = 1;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addIntPoints(Container container) {
        JComponent component = new JLabel();
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("attributes.intpoints");
        ((JLabel) component).setText("0");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 2;
        constraints.gridy = 1;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addConLabel(Container container) {
        JComponent component = new JLabel();
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("attributes.conlabel");
        ((JLabel) component).setText("CON");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 0;
        constraints.gridy = 2;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addConValue(Container container) {
        JSpinner spinner = new JSpinner();
        spinner.addChangeListener(this);
        JComponent component = spinner;
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("attributes.convalue");
        ((JSpinner) component).setValue(10);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 1;
        constraints.gridy = 2;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addConPoints(Container container) {
        JComponent component = new JLabel();
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("attributes.conpoints");
        ((JLabel) component).setText("0");
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 2;
        constraints.gridy = 2;
        container.add(component, constraints);
        logStuff(component);
    }

    private void addSkills(Container container) {
        FooTable table = new FooTable();
        table.addTableModelListener(this);
        JComponent component = table;
        component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        component.setName("skills");

        //_columnNames
        Object bigStringOfColumnNames = "First Column,Second Column,Third Column";
        String[] values = bigStringOfColumnNames.toString().split(",");
        table.setColumnNames(values);

        //_preferredHeight
        table.setPreferredHeight(150);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.gridx = 1;
        constraints.gridy = 1;
        container.add(component, constraints);
        logStuff(component);
    }

    private void logStuff(JComponent component) {
        Dimension pre = component.getPreferredSize();
        Dimension min = component.getMinimumSize();
        Dimension max = component.getMaximumSize();
        // @formatter:off
        LOGGER.debug("Component=" + component.getName() +
                (pre == null ? "" : ", Preferred Size=" + pre.getWidth() + "/" + pre.getHeight()) +
                (min == null ? "" : ", Minimum Size=" + min.getWidth() + "/" + min.getHeight()) +
                (max == null ? "" : ", Maximum Size=" + pre.getWidth() + "/" + pre.getHeight()));
        // @formatter:on
    }

    private void changeThing(String name, Object value) {
        LOGGER.debug("name=\"" + name + "\",value=\"" + value + "\"");
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        documentUpdate(documentEvent);
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        documentUpdate(documentEvent);
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        documentUpdate(documentEvent);
    }

    private void documentUpdate(DocumentEvent documentEvent) {
        CharacterPlainDocument document = (CharacterPlainDocument) documentEvent.getDocument();
        Object source = document.getSource();
        if (source instanceof JTextArea) {
            JTextArea textArea = (JTextArea) source;
            changeThing(textArea.getName(), textArea.getText());
        } else if (source instanceof JTextField) {
            JTextField textField = (JTextField) source;
            changeThing(textField.getName(), textField.getText());
        } else {
            changeThing(((JComponent) source).getName(), source.toString());
        }
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        Object source = changeEvent.getSource();
        if (source instanceof JSpinner) {
            JSpinner spinner = (JSpinner) source;
            changeThing(spinner.getName(), spinner.getValue());
        } else {
            changeThing(((JComponent) source).getName(), changeEvent);
        }
    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {
        LOGGER.debug("tableModelEvent=\"" + tableModelEvent + "\"");
        Object source = tableModelEvent.getSource();
        LOGGER.debug("source.class=\"" + source.getClass().getName() + "\"");
    }
}
