package slash.gui.model;

/**
 * An DoubleModel holds a double and provides get and set
 * methods for it.
 */

public class DoubleModel extends AbstractModel {

    /**
     * Construct a new DoubleModel.
     */
    public DoubleModel() {
        this(0.0);
    }

    /**
     * Construct a new DoubleModel.
     */
    public DoubleModel(double value) {
        setValue(value);
    }

    /**
     * Get the double value that is holded by the model.
     */
    public double getValue() {
        return value;
    }

    /**
     * Set the double value to hold.
     */
    public void setValue(double newValue) {
        if (value != newValue) {
            this.value = newValue;

            fireStateChanged();
        }
    }

    public String toString() {
        return "DoubleModel(" + value + ")";
    }

    private double value;
}
