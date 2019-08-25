package norman.bunch.of.things.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Map;

public class BunchFrame extends JInternalFrame {
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

        Iterator<JsonNode> uiIterator = uiJson.iterator();
        ObjectMapper objectMapper = new ObjectMapper();
        while (uiIterator.hasNext()) {
            JsonNode node = uiIterator.next();
            Map map = objectMapper.convertValue(node, Map.class);
            addUiComponent(contentPane, map);
        }
        pack();
    }

    private void addUiComponent(Container container, Map map) {
        JComponent component = null;
        if (map.containsKey("_component")) {
            Object value = map.get("_component");
            if (value.equals(ComponentType.JLabel.name())) {
                component = new JLabel();
            } else if (value.equals(ComponentType.JTextField.name())) {
                component = new JTextField();
            } else if (value.equals(ComponentType.JButton.name())) {
                component = new JButton();
            }
            component.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            map.remove("_component");
        } else {
            component = new JPanel(new GridBagLayout());
        }

        if (map.containsKey("_text")) {
            String value = map.get("_text").toString();
            if (component instanceof JLabel) {
                ((JLabel) component).setText(value);
            } else if (component instanceof JTextField) {
                ((JTextField) component).setText(value);
            } else if (component instanceof JButton) {
                ((JButton) component).setText(value);
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
                addUiComponent(component, value);
            }
        }
    }
}
