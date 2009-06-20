package slash.gui.model;

import javax.swing.event.ChangeListener;

/**
 * A BooleanModel is a model with the states true and false. If the state
 * changes all listerner are notified.
 */


public interface BooleanModel {
    public abstract boolean getState();

    public abstract void setState(boolean newState);

    public abstract void addChangeListener(ChangeListener l);

    public abstract void removeChangeListener(ChangeListener l);
}

