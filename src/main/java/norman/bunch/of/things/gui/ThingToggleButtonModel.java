package norman.bunch.of.things.gui;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ThingToggleButtonModel extends JToggleButton.ToggleButtonModel {
    private Object source;
    private Object oldValue;

    public ThingToggleButtonModel(JCheckBox source) {
        this.source = source;
    }

    public ThingToggleButtonModel(JRadioButton source) {
        this.source = source;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listenerList.add(PropertyChangeListener.class, listener);
    }

    public void removeActionListener(PropertyChangeListener listener) {
        listenerList.remove(PropertyChangeListener.class, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return listenerList.getListeners(PropertyChangeListener.class);
    }

    private void firePropertyChange() {
        Object[] listenerList = this.listenerList.getListenerList();
        PropertyChangeEvent propertyChangeEvent =
                new PropertyChangeEvent(source, "value", oldValue, Boolean.valueOf(isSelected()));
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == PropertyChangeListener.class) {
                ((PropertyChangeListener) listenerList[i + 1]).propertyChange(propertyChangeEvent);
            }
        }
    }

    @Override
    public void setSelected(boolean selected) {
        oldValue = Boolean.valueOf(isSelected());
        super.setSelected(selected);
        Object newValue = Boolean.valueOf(isSelected());
        if (oldValue == null && newValue != null || oldValue != null && !oldValue.equals(newValue)) {
            firePropertyChange();
        }
    }
}
