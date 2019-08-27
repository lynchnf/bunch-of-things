package norman.bunch.of.things.gui;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ThingSpinnerNumberModel extends SpinnerNumberModel {
    private Object source;
    private Object oldValue;

    public ThingSpinnerNumberModel(JSpinner source) {
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
        PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(source, "value", oldValue, getValue());
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == PropertyChangeListener.class) {
                ((PropertyChangeListener) listenerList[i + 1]).propertyChange(propertyChangeEvent);
            }
        }
    }

    @Override
    public void setValue(Object value) {
        oldValue = getValue();
        super.setValue(value);
        Object newValue = getValue();
        if (oldValue == null && newValue != null || oldValue != null && !oldValue.equals(newValue)) {
            firePropertyChange();
        }
    }
}
