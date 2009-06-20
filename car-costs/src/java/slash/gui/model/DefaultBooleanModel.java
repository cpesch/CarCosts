package slash.gui.model;

/**
 * An implementation of a BooleanModel. If the state changes all listerner
 * are notified by calling fireStateChanged.
 */

public class DefaultBooleanModel extends AbstractModel
        implements BooleanModel {

    /**
     * The state of this model.
     */
    private boolean state = false;

    /*
    * Return the state of the model.
    */
    public boolean getState() {
        return state;
    }

    /*
    * Set the state of the model.
    *
    * @parm newState the new state of the model. A state changed event is only fired,
    *                if the state changes
    */
    public void setState(boolean newState) {
        if (newState != state) {
            state = newState;
            fireStateChanged();
        }
    }
}

