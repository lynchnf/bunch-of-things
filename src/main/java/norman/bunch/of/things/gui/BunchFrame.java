package norman.bunch.of.things.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.Map;

public class BunchFrame extends JInternalFrame implements ItemListener, ActionListener, ChangeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BunchFrame.class);
    private static final boolean RESIZABLE = false;
    private static final boolean CLOSABLE = true;
    private static final boolean MAXIMIZABLE = false;
    private static final boolean ICONIFIABLE = true;
    private static int openOffset = 0;

    public BunchFrame(String title, JsonNode uiJson) {
        super(title, RESIZABLE, CLOSABLE, MAXIMIZABLE, ICONIFIABLE);
        setLocation(openOffset, openOffset);
        openOffset += 30;
        Container contentPane = new JPanel(new GridBagLayout());
        setContentPane(contentPane);

        ObjectMapper objectMapper = new ObjectMapper();
        Iterator<String> fieldNameIterator = uiJson.fieldNames();
        while (fieldNameIterator.hasNext()) {
            String fieldName = fieldNameIterator.next();
            JsonNode node = uiJson.get(fieldName);
            Map map = objectMapper.convertValue(node, Map.class);
            addUiComponent(contentPane, map, fieldName);
        }
        pack();
    }

    private void addUiComponent(Container container, Map map, String name) {
        JComponent component = null;
        if (map.containsKey("_component")) {
            Object value = map.get("_component");
            if (value.equals(ComponentType.JCheckBox.name())) {
                component = new JCheckBox();
                JCheckBox checkBox = (JCheckBox) component;
                checkBox.addItemListener(this);
            } else if (value.equals(ComponentType.JRadioButton.name())) {
                component = new JRadioButton();
            } else if (value.equals(ComponentType.JComboBox.name())) {
                component = new JComboBox();
                JComboBox comboBox = (JComboBox) component;
                comboBox.addActionListener(this);
            } else if (value.equals(ComponentType.JLabel.name())) {
                component = new JLabel();
            } else if (value.equals(ComponentType.JSpinner.name())) {
                component = new JSpinner();
                JSpinner spinner = (JSpinner) component;
                spinner.addChangeListener(this);
            } else if (value.equals(ComponentType.JTextArea.name())) {
                component = new JTextArea();
            } else if (value.equals(ComponentType.JTextField.name())) {
                component = new JTextField();
            } else {
                // FIXME Throw exception.
                LOGGER.error("Invalid component type=" + value);
            }
            component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            map.remove("_component");
        } else {
            component = new JPanel(new GridBagLayout());
        }
        component.setName(name);

        if (map.containsKey("_text")) {
            String value = map.get("_text").toString();
            if (component instanceof JLabel) {
                ((JLabel) component).setText(value);
            } else if (component instanceof JTextField) {
                ((JTextField) component).setText(value);
            }
            map.remove("_text");
        }

        if (map.containsKey("_columns")) {
            int value = Integer.parseInt(map.get("_columns").toString());
            if (component instanceof JTextField) {
                ((JTextField) component).setColumns(value);
            }
            map.remove("_columns");
        }

        if (map.containsKey("_objects")) {
            String[] values = map.get("_objects").toString().split(",");
            if (component instanceof JComboBox) {
                ((JComboBox) component).setModel(new DefaultComboBoxModel(values));
            }
            map.remove("_objects");
        }

        if (map.containsKey("_selectedObject")) {
            String value = map.get("_selectedObject").toString();
            if (component instanceof JComboBox) {
                ((JComboBox) component).setSelectedItem(value);
            }
            map.remove("_selectedObject");
        }

        // Grid Bag Constraint parameters.
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

        GridBagConstraints constraints = new GridBagConstraints();
        if (map.size() > 0) {
            constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        } else {
            constraints.fill = GridBagConstraints.BOTH;
            constraints.ipadx = 2;
            constraints.ipady = 2;
        }

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

        container.add(component, constraints);

        if (map.size() > 0) {
            for (Object key : map.keySet()) {
                Map value = (Map) map.get(key);
                addUiComponent(component, value, name + "." + key.toString());
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        LOGGER.debug("itemEvent=\"" + itemEvent + "\"");
        LOGGER.debug("itemEvent.source.name=\"" + ((JComponent) itemEvent.getSource()).getName() + "\"");
        LOGGER.debug("itemEvent.stateChange=\"" + itemEvent.getStateChange() + "\"");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        LOGGER.debug("actionEvent=\"" + actionEvent + "\"");
        LOGGER.debug("actionEvent.source.name=\"" + ((JComponent) actionEvent.getSource()).getName() + "\"");
        LOGGER.debug(
                "actionEvent.source.selectedItem=\"" + ((JComboBox) actionEvent.getSource()).getSelectedItem() + "\"");
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        LOGGER.debug("changeEvent=\"" + changeEvent + "\"");
        LOGGER.debug("changeEvent.source.name=\"" + ((JComponent) changeEvent.getSource()).getName() + "\"");
        LOGGER.debug("changeEvent.source.value=\"" + ((JSpinner) changeEvent.getSource()).getValue() + "\"");
    }
}
