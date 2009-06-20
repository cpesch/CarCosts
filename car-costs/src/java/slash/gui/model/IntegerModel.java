package slash.gui.model;

/**
 * An IntegerModel holds an int and provides get and set
 * methods for it.
 */

public class IntegerModel extends AbstractModel {

    /**
     * Construct a new IntegerModel.
     */
    public IntegerModel() {
        this(0);
    }

    /**
     * Construct a new IntegerModel.
     */
    public IntegerModel(int value) {
        setValue(value);
    }

    /**
     * Get the int value that is holded by the model.
     */
    public int getValue() {
        return value;
    }

    /**
     * Set the int value to hold.
     */
    public void setValue(int newValue) {
        if (value != newValue) {
            this.value = newValue;

            fireStateChanged();
        }
    }

    public String toString() {
        return "IntegerModel(" + value + ")";
    }

    private int value;
}
