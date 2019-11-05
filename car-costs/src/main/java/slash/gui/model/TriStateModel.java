package slash.gui.model;

import javax.swing.event.ChangeListener;

/**
 * A TriStateModel is a model with the states YES, NO and DON_T_CARE. If
 * the state changes all listerner are notified.
 */

public interface TriStateModel {
    public static final int NO = 0;
    public static final int YES = 1;
    public static final int DON_T_CARE = 2;

    public abstract int getState();

    public abstract void setState(int newState);

    public abstract void addChangeListener(ChangeListener l);

    public abstract void removeChangeListener(ChangeListener l);
}
