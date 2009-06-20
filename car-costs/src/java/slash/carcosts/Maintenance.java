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
import slash.gui.model.StringModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This is a maintenance of the car.
 *
 * @author Christian Pesch
 */

public class Maintenance {

    /**
     * Create new maintenance
     */
    public Maintenance(Calendar date, int mileage, String title, String note, double costs, Currency currency) {
        this.date = new CalendarModel(date);
        this.mileage = new IntegerModel(mileage);
        this.title = new StringModel(title);
        this.note = new StringModel(note);
        this.costs = new DoubleModel(costs);
        this.currency = new CurrencyModel(currency);
    }

    /**
     * Create new maintenance.
     */
    public Maintenance(Currency currency) {
        this(new GregorianCalendar(), 0, "", "", 0.0, currency);
    }

    /**
     * Create new maintenance.
     */
    public Maintenance(Maintenance maintenance) {
        this(maintenance.getCurrency());
        setMaintenance(maintenance);
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
    public String getTitle() {
        return title.getValue();
    }

    /**
     * Get content of model.
     */
    public String getNote() {
        return note.getValue();
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
    public StringModel getTitleModel() {
        return title;
    }

    /**
     * Get model.
     */
    public StringModel getNoteModel() {
        return note;
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
    public void setMaintenance(Maintenance maintenance) {
        date.setValue(maintenance.date.getValue());
        mileage.setValue(maintenance.mileage.getValue());
        title.setValue(maintenance.title.getValue());
        note.setValue(maintenance.note.getValue());
        costs.setValue(maintenance.costs.getValue());
        currency = maintenance.currency;
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
                title.getValue() + "/" + note.getValue() + " " + costs.getValue() + currency.getValue().getName();
    }

    // --- member variables ------------------------------------

    private CalendarModel date;
    private IntegerModel mileage;
    private StringModel title;
    private StringModel note;
    private DoubleModel costs;
    private CurrencyModel currency;
}


