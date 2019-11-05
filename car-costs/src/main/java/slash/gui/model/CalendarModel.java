package slash.gui.model;

/**
 * A CalendarModel holds a date, provides get and set
 * methods for it and notifies listeners if the value changes. 
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarModel extends AbstractModel {

    public static DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    /**
     * Construct a new CalendarModel.
     */
    public CalendarModel() {
        this(null);
    }

    /**
     * Construct a new CalendarModel.
     */
    public CalendarModel(Calendar value) {
        setValue(value);
    }

    /**
     * Get the date value that is holded by the model.
     */
    public Calendar getValue() {
        return value;
    }

    /**
     * Set the value to hold.
     */
    public void setValue(Calendar newValue) {
        this.value = newValue;

        fireStateChanged();
    }

    public String toString() {
        if (value == null)
            return "0000.00.00 00:00:00";
        else {
            synchronized (format) {
                format.setCalendar(value);
                return format.format(value.getTime());
            }
        }
    }

    private Calendar value;
}
