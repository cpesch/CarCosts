/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2006 Christian Pesch. All Rights Reserved.
*/

package slash.carcosts;

import slash.gui.model.AbstractModel;

/**
 * An CurrencyModel holds a Currency and provides get and set
 * methods for it.
 *
 * @author Christian Pesch
 */

public class CurrencyModel extends AbstractModel {

    /**
     * Construct a new CurrencyModel.
     */
    public CurrencyModel() {
        this(Currency.getCurrency("DM"));
    }

    /**
     * Construct a new CurrencyModel.
     */
    public CurrencyModel(Currency value) {
        setValue(value);
    }

    /**
     * Get the Currency value that is holded by the model.
     */
    public Currency getValue() {
        return value;
    }

    /**
     * Set the Currency value to hold.
     */
    public void setValue(Currency newValue) {
        if (value != newValue) {
            this.value = newValue;

            fireStateChanged();
        }
    }

    public String toString() {
        return "CurrencyModel(" + value + ")";
    }

    private Currency value;
}
