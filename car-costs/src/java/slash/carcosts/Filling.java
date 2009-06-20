/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2001 Christian Pesch. All Rights Reserved.  
*/

package slash.carcosts;

import slash.gui.model.CalendarModel;
import slash.gui.model.DoubleModel;
import slash.gui.model.IntegerModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This is a filling at the filling station.
 *
 * @author Christian Pesch
 */

public class Filling {

    /**
     * Create new filling.
     */
    public Filling(Calendar date, int mileage, double quantity, double costs, Currency currency) {
        this.date = new CalendarModel(date);
        this.mileage = new IntegerModel(mileage);
        this.quantity = new DoubleModel(quantity);
        this.costs = new DoubleModel(costs);
        this.currency = new CurrencyModel(currency);
    }

    /**
     * Create new filling.
     */
    public Filling(Currency currency) {
        this(new GregorianCalendar(), 0, 0.0, 0.0, currency);
    }

    /**
     * Create new filling.
     */
    public Filling(Filling filling) {
        this(filling.getCurrency());
        setFilling(filling);
    }


    /**
     * Get content of model.
     */
    public Calendar getDate() {
        return date.getValue();
    }

    /**
     * Get content of model.
     */
    public int getMileage() {
        return mileage.getValue();
    }

    /**
     * Get content of model.
     */
    public double getQuantity() {
        return quantity.getValue();
    }

    /**
     * Get content of model.
     */
    public double getCosts() {
        return costs.getValue();
    }

    /**
     * Get content of model.
     */
    public Currency getCurrency() {
        return currency.getValue();
    }

    /**
     * Get content of model.
     */
    public double getAverageCosts() {
        return getCurrency().toEuro(costs.getValue()) / getQuantity();
    }


    /**
     * Get model.
     */
    public CalendarModel getDateModel() {
        return date;
    }

    /**
     * Get model.
     */
    public IntegerModel getMileageModel() {
        return mileage;
    }

    /**
     * Get model.
     */
    public DoubleModel getQuantityModel() {
        return quantity;
    }

    /**
     * Get model.
     */
    public DoubleModel getCostsModel() {
        return costs;
    }

    /**
     * Get model.
     */
    public CurrencyModel getCurrencyModel() {
        return currency;
    }


    /**
     * Set all data.
     */
    public void setFilling(Filling filling) {
        date.setValue(filling.date.getValue());
        mileage.setValue(filling.mileage.getValue());
        quantity.setValue(filling.quantity.getValue());
        costs.setValue(filling.costs.getValue());
        currency = filling.currency;
    }


    /**
     * Convert to String.
     */
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = date.getValue();
        format.setCalendar(calendar);
        String dateString = format.format(calendar.getTime());

        return dateString + " " + mileage.getValue() + "km " +
                quantity.getValue() + "l " + costs.getValue() + currency.getValue().getName();
    }

    // --- member variables ------------------------------------

    private CalendarModel date;
    private IntegerModel mileage;
    private DoubleModel quantity;
    private DoubleModel costs;
    private CurrencyModel currency;
}


