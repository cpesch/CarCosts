package slash.gui.model;

/**
 * An StringModel holds a String and provides get and set
 * methods for it.
 */

public class StringModel extends AbstractModel {

    /**
     * Construct a new StringModel.
     */
    public StringModel() {
        this("");
    }

    /**
     * Construct a new StringModel.
     */
    public StringModel(String value) {
        setValue(value);
    }

    /**
     * Get the String value that is holded by the model.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the String value to hold.
     */
    public void setValue(String newValue) {
        if (value == null || !value.equals(newValue)) {
            this.value = newValue;

            fireStateChanged();
        }
    }

    public String toString() {
        return "StringModel(" + value + ")";
    }

    private String value;
}
