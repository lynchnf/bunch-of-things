package norman.bunch.of.things.gui;

import com.fasterxml.jackson.databind.JsonNode;
import norman.bunch.of.things.JsonUtils;
import norman.bunch.of.things.LoggingException;
import norman.bunch.of.things.bunch.Bunch;
import norman.bunch.of.things.gui.model.CharacterPlainDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class CharacterFrame extends JInternalFrame
        implements ItemListener, ActionListener, ChangeListener, DocumentListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterFrame.class);
    private static final boolean RESIZABLE = false;
    private static final boolean CLOSABLE = true;
    private static final boolean MAXIMIZABLE = false;
    private static final boolean ICONIFIABLE = true;
    private static final int OPEN_OFFSET_INCREMENT = 30;
    private static int openOffset = 0;
    private ResourceBundle bundle;
    private Bunch bunch = new Bunch();
    private Map<String, JComponent> componentMap = new HashMap<>();
    private Map<String, ButtonGroup> buttonGroups = new HashMap<>();
    private Map<String, String> guiToCharBinding = new HashMap<>();

    public CharacterFrame(String title, JsonNode gameBookJson) {
        super(title, RESIZABLE, CLOSABLE, MAXIMIZABLE, ICONIFIABLE);
        bundle = ResourceBundle.getBundle("norman.bunch.of.things.gui.CharacterFrame");
        setLocation(openOffset, openOffset);
        openOffset += OPEN_OFFSET_INCREMENT;
        Container contentPane = new JPanel(new GridBagLayout());
        setContentPane(contentPane);

        // Create GUI.
        JsonNode uiJson = gameBookJson.get("gui");
        Iterator<String> uiFieldNameIterator = uiJson.fieldNames();
        try {
            while (uiFieldNameIterator.hasNext()) {
                String fieldName = uiFieldNameIterator.next();
                JsonNode node = uiJson.get(fieldName);
                Map map = JsonUtils.jsonToMap(node);
                addUiComponent(contentPane, map, fieldName);
            }
            pack();
        } catch (LoggingException e) {
            JOptionPane.showMessageDialog(this, bundle.getString("error.message.invalid.gui.config"),
                    bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
        }

        // Load the rule book.
        JsonNode ruleBookJson = gameBookJson.get("ruleBook");
        try {
            bunch.loadRuleBook(ruleBookJson);
        } catch (LoggingException e) {
            JOptionPane.showMessageDialog(this, bundle.getString("error.message.invalid.rule.book.value"),
                    bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
        }

        // Initialize the bunch with a new character.
        JsonNode newCharacterJson = gameBookJson.get("newCharacter");
        try {
            bunch.initializeCharacter(newCharacterJson);
        } catch (LoggingException e) {
            JOptionPane.showMessageDialog(this, bundle.getString("error.message.invalid.character.value"),
                    bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
        }

        // Bind GUI and character.
        JsonNode bindingsJson = gameBookJson.get("bindings");
        try {
            this.guiToCharBinding.putAll(bunch.setBindings(this, bindingsJson));
        } catch (LoggingException e) {
            JOptionPane.showMessageDialog(this, bundle.getString("error.message.invalid.bindings.value"),
                    bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addUiComponent(Container container, Map map, String name) throws LoggingException {
        JComponent component = createComponent(map, name);
        componentMap.put(name, component);
        addComponentProperties(map, name, component);
        GridBagConstraints constraints = createContraints(map);
        container.add(component, constraints);

        // If the map is empty, that's all folks. If not, drill down.
        if (map.size() > 0) {
            for (Object key : map.keySet()) {
                Map value = (Map) map.get(key);
                addUiComponent(component, value, name + "." + key.toString());
            }
        }
    }

    private JComponent createComponent(Map map, String name) throws LoggingException {
        JComponent component;
        if (map.containsKey("_component")) {
            Object value = map.get("_component");
            if (value.equals(ComponentType.JCheckBox.name())) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.addItemListener(this);
                component = checkBox;
            } else if (value.equals(ComponentType.JComboBox.name())) {
                JComboBox<String> comboBox = new JComboBox<>();
                comboBox.addActionListener(this);
                component = comboBox;
            } else if (value.equals(ComponentType.JLabel.name())) {
                component = new JLabel();
            } else if (value.equals(ComponentType.JRadioButton.name())) {
                JRadioButton radioButton = new JRadioButton();
                radioButton.addItemListener(this);
                component = radioButton;
            } else if (value.equals(ComponentType.JSpinner.name())) {
                JSpinner spinner = new JSpinner();
                spinner.addChangeListener(this);
                component = spinner;
            } else if (value.equals(ComponentType.JTextArea.name())) {
                JTextArea textArea = new JTextArea();
                Document document = new CharacterPlainDocument(textArea);
                textArea.setDocument(document);
                document.addDocumentListener(this);
                component = textArea;
            } else if (value.equals(ComponentType.JTextField.name())) {
                JTextField textField = new JTextField();
                Document document = new CharacterPlainDocument(textField);
                textField.setDocument(document);
                document.addDocumentListener(this);
                component = textField;
            } else {
                throw new LoggingException(LOGGER, "Invalid component type=" + value);
            }
            component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            map.remove("_component");
        } else {
            component = new JPanel(new GridBagLayout());
        }
        component.setName(name);
        return component;
    }

    private void addComponentProperties(Map map, String name, JComponent component) {
        if (map.containsKey("_buttonGroup")) {
            String value = map.get("_buttonGroup").toString();
            if (component instanceof JRadioButton) {
                ButtonGroup buttonGroup = null;
                if (buttonGroups.containsKey(value)) {
                    buttonGroup = buttonGroups.get(value);
                } else {
                    buttonGroup = new ButtonGroup();
                    buttonGroups.put(value, buttonGroup);
                }
                buttonGroup.add((JRadioButton) component);
            } else {
                LOGGER.warn(
                        "Property _buttonGroup ignored for component " + name + " because it it not a JRadioButton.");
            }
            map.remove("_buttonGroup");
        }

        if (map.containsKey("_columns")) {
            int value = Integer.parseInt(map.get("_columns").toString());
            if (component instanceof JTextArea) {
                ((JTextArea) component).setColumns(value);
            } else if (component instanceof JTextField) {
                ((JTextField) component).setColumns(value);
            } else {
                LOGGER.warn("Property _columns ignored for component " + name +
                        " because it it not a JTextArea or JTextField.");
            }
            map.remove("_columns");
        }

        if (map.containsKey("_objects")) {
            String[] values = map.get("_objects").toString().split(",");
            if (component instanceof JComboBox) {
                JComboBox<String> comboBox = (JComboBox<String>) component;
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
                for (String value : values) {
                    model.addElement(value);
                }
            } else {
                LOGGER.warn("Property _objects ignored for component " + name + " because it it not a JComboBox.");
            }
            map.remove("_objects");
        }

        if (map.containsKey("_rows")) {
            int value = Integer.parseInt(map.get("_rows").toString());
            if (component instanceof JTextArea) {
                ((JTextArea) component).setRows(value);
            } else {
                LOGGER.warn("Property _rows ignored for component " + name + " because it it not a JTextArea.");
            }
            map.remove("_rows");
        }

        if (map.containsKey("_selected")) {
            boolean value = Boolean.parseBoolean(map.get("_selected").toString());
            if (component instanceof JCheckBox) {
                ((JCheckBox) component).setSelected(value);
            } else if (component instanceof JRadioButton) {
                ((JRadioButton) component).setSelected(value);
            } else {
                LOGGER.warn("Property _selected ignored for component " + name +
                        " because it it not a JCheckBox or JRadioButton.");
            }
            map.remove("_selected");
        }

        if (map.containsKey("_selectedObject")) {
            String value = map.get("_selectedObject").toString();
            if (component instanceof JComboBox) {
                ((JComboBox) component).setSelectedItem(value);
            } else {
                LOGGER.warn(
                        "Property _selectedObject ignored for component " + name + " because it it not a JComboBox.");
            }
            map.remove("_selectedObject");
        }

        if (map.containsKey("_text")) {
            String value = map.get("_text").toString();
            if (component instanceof JCheckBox) {
                ((JCheckBox) component).setText(value);
            } else if (component instanceof JLabel) {
                ((JLabel) component).setText(value);
            } else if (component instanceof JRadioButton) {
                ((JRadioButton) component).setText(value);
            } else if (component instanceof JTextArea) {
                ((JTextArea) component).setText(value);
            } else if (component instanceof JTextField) {
                ((JTextField) component).setText(value);
            } else {
                LOGGER.warn("Property _text ignored for component " + name +
                        " because it it not a JCheckBox, JLabel, JRadioButton, JTextArea, or JTextField.");
            }
            map.remove("_text");
        }

        if (map.containsKey("_value")) {
            int value = Integer.parseInt(map.get("_value").toString());
            if (component instanceof JSpinner) {
                ((JSpinner) component).setValue(value);
            } else {
                LOGGER.warn("Property _value ignored for component " + name + " because it it not a JSpinner.");
            }
            map.remove("_value");
        }
    }

    private GridBagConstraints createContraints(Map map) throws LoggingException {
        // Extract constraint properties from map.
        Integer gridx = null;
        if (map.containsKey("_gridx")) {
            gridx = Integer.valueOf(map.get("_gridx").toString());
            map.remove("_gridx");
        }

        Integer gridy = null;
        if (map.containsKey("_gridy")) {
            gridy = Integer.valueOf(map.get("_gridy").toString());
            map.remove("_gridy");
        }

        Integer gridwidth = null;
        if (map.containsKey("_gridwidth")) {
            gridwidth = Integer.valueOf(map.get("_gridwidth").toString());
            map.remove("_gridwidth");
        }

        Integer gridheight = null;
        if (map.containsKey("_gridheight")) {
            gridheight = Integer.parseInt(map.get("_gridheight").toString());
            map.remove("_gridheight");
        }

        Double weightx = null;
        if (map.containsKey("_weightx")) {
            weightx = Double.valueOf(map.get("_weightx").toString());
            map.remove("_weightx");
        }

        Double weighty = null;
        if (map.containsKey("_weighty")) {
            weighty = Double.valueOf(map.get("_weighty").toString());
            map.remove("_weighty");
        }

        Integer anchor = null;
        if (map.containsKey("_anchor")) {
            Object value = map.get("_anchor");
            if (value.equals(GridBagConstraintsAnchor.FIRST_LINE_START.name())) {
                anchor = GridBagConstraints.FIRST_LINE_START;
            } else if (value.equals(GridBagConstraintsAnchor.PAGE_START.name())) {
                anchor = GridBagConstraints.PAGE_START;
            } else if (value.equals(GridBagConstraintsAnchor.FIRST_LINE_END.name())) {
                anchor = GridBagConstraints.FIRST_LINE_END;
            } else if (value.equals(GridBagConstraintsAnchor.LINE_START.name())) {
                anchor = GridBagConstraints.LINE_START;
            } else if (value.equals(GridBagConstraintsAnchor.CENTER.name())) {
                anchor = GridBagConstraints.CENTER;
            } else if (value.equals(GridBagConstraintsAnchor.LINE_END.name())) {
                anchor = GridBagConstraints.LINE_END;
            } else if (value.equals(GridBagConstraintsAnchor.LAST_LINE_START.name())) {
                anchor = GridBagConstraints.LAST_LINE_START;
            } else if (value.equals(GridBagConstraintsAnchor.PAGE_END.name())) {
                anchor = GridBagConstraints.PAGE_END;
            } else if (value.equals(GridBagConstraintsAnchor.LAST_LINE_END.name())) {
                anchor = GridBagConstraints.LAST_LINE_END;
            } else {
                throw new LoggingException(LOGGER, "Invalid _anchor=" + value);
            }
            map.remove("_anchor");
        }

        Integer fill = null;
        if (map.containsKey("_fill")) {
            Object value = map.get("_fill");
            if (value.equals(GridBagConstraintsFill.NONE.name())) {
                anchor = GridBagConstraints.NONE;
            } else if (value.equals(GridBagConstraintsFill.HORIZONTAL.name())) {
                anchor = GridBagConstraints.HORIZONTAL;
            } else if (value.equals(GridBagConstraintsFill.VERTICAL.name())) {
                anchor = GridBagConstraints.VERTICAL;
            } else if (value.equals(GridBagConstraintsFill.BOTH.name())) {
                anchor = GridBagConstraints.BOTH;
            } else {
                throw new LoggingException(LOGGER, "Invalid _fill=" + value);
            }
            map.remove("_fill");
        }

        Integer insetsTop = null;
        if (map.containsKey("_insetsTop")) {
            insetsTop = Integer.valueOf(map.get("_insetsTop").toString());
            map.remove("_insetsTop");
        }

        Integer insetsLeft = null;
        if (map.containsKey("_insetsLeft")) {
            insetsLeft = Integer.valueOf(map.get("_insetsLeft").toString());
            map.remove("_insetsLeft");
        }

        Integer insetsBottom = null;
        if (map.containsKey("_insetsBottom")) {
            insetsBottom = Integer.valueOf(map.get("_insetsBottom").toString());
            map.remove("_insetsBottom");
        }

        Integer insetsRight = null;
        if (map.containsKey("_insetsRight")) {
            insetsRight = Integer.valueOf(map.get("_insetsRight").toString());
            map.remove("_insetsRight");
        }

        Integer ipadx = null;
        if (map.containsKey("_ipadx")) {
            ipadx = Integer.valueOf(map.get("_ipadx").toString());
            map.remove("_ipadx");
        }

        Integer ipady = null;
        if (map.containsKey("_ipady")) {
            ipady = Integer.parseInt(map.get("_ipady").toString());
            map.remove("_ipady");
        }

        // Create constraints with default properties.
        GridBagConstraints constraints = new GridBagConstraints();
        if (map.size() > 0) {
            constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        } else {
            constraints.fill = GridBagConstraints.BOTH;
            constraints.ipadx = 2;
            constraints.ipady = 2;
        }

        // Apply extracted properties to constraints.
        if (gridx != null) {
            constraints.gridx = gridx;
        }
        if (gridy != null) {
            constraints.gridy = gridy;
        }
        if (gridwidth != null) {
            constraints.gridwidth = gridwidth;
        }
        if (gridheight != null) {
            constraints.gridheight = gridheight;
        }
        if (weightx != null) {
            constraints.weightx = weightx;
        }
        if (weighty != null) {
            constraints.weighty = weighty;
        }
        if (anchor != null) {
            constraints.anchor = anchor;
        }
        if (fill != null) {
            constraints.fill = fill;
        }
        if (insetsTop != null) {
            constraints.insets.top = insetsTop;
        }
        if (insetsLeft != null) {
            constraints.insets.left = insetsLeft;
        }
        if (insetsBottom != null) {
            constraints.insets.bottom = insetsBottom;
        }
        if (insetsRight != null) {
            constraints.insets.right = insetsRight;
        }
        if (ipadx != null) {
            constraints.ipadx = ipadx;
        }
        if (ipady != null) {
            constraints.ipady = ipady;
        }
        return constraints;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        changeThing(((JComponent) itemEvent.getSource()).getName(),
                Boolean.valueOf((itemEvent.getStateChange() == ItemEvent.SELECTED)));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source instanceof JComboBox) {
            JComboBox<String> comboBox = (JComboBox<String>) source;
            changeThing(comboBox.getName(), comboBox.getSelectedItem());
        } else {
            changeThing(((JComponent) source).getName(), actionEvent);
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

    private void changeThing(String uiComponentName, Object value) {
        if (guiToCharBinding.containsKey(uiComponentName)) {
            String id = guiToCharBinding.get(uiComponentName);
            try {
                bunch.changeThing(id, value);
            } catch (LoggingException e) {
                JOptionPane.showMessageDialog(this, bundle.getString("error.message.unexpected"),
                        bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void changeComponent(String uiComponentName, Object value) {
        JComponent component = componentMap.get(uiComponentName);
        if (component instanceof JCheckBox) {
            ((JCheckBox) component).setSelected((boolean) value);
        } else if (component instanceof JRadioButton) {
            ((JRadioButton) component).setSelected((boolean) value);
        } else if (component instanceof JComboBox) {
            ((JComboBox) component).setSelectedItem(value);
        } else if (component instanceof JLabel) {
            ((JLabel) component).setText(value.toString());
        } else if (component instanceof JSpinner) {
            ((JSpinner) component).setValue(value);
        } else if (component instanceof JTextArea) {
            ((JTextArea) component).setText(value.toString());
        } else if (component instanceof JTextField) {
            ((JTextField) component).setText(value.toString());
        } else {
            LOGGER.error("Cannot set value. Invalid component type=" + component.getClass().getName() + ", name=" +
                    uiComponentName + ".");
            JOptionPane.showMessageDialog(this, bundle.getString("error.message.unexpected"),
                    bundle.getString("error.dialog.title"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
