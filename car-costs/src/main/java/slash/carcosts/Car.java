/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2001 Christian Pesch. All Rights Reserved.  
*/

package slash.carcosts;

import slash.gui.model.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.io.*;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * This is a car of the car cost program.
 *
 * @author Christian Pesch
 */

public class Car {

    /**
     * Create a new car.
     */
    public Car() {
        fuel = new FuelCosts();
        fuel.addListDataListener(modelUpdater);

        maintenance = new MaintenanceCosts();
        maintenance.addListDataListener(modelUpdater);

        currency.addChangeListener(modelUpdater);
        name.addChangeListener(modelUpdater);
        sign.addChangeListener(modelUpdater);

        updateModels();

        save.setState(false);
    }

    /**
     * Create a new car.
     */
    public Car(File file) throws IOException {
        this();

        read(file);
    }


    /**
     * Get data.
     */
    public FuelCosts getFuelCosts() {
        return fuel;
    }

    /**
     * Get data.
     */
    public MaintenanceCosts getMaintenanceCosts() {
        return maintenance;
    }


    /**
     * Get data.
     */
    public String getName() {
        return name.getValue();
    }

    /**
     * Get data.
     */
    public String getSign() {
        return sign.getValue();
    }

    /**
     * Calculate data.
     */
    public int getMileage() {
        int minimum = Math.min(fuel.getMinimumMileage(), maintenance.getMinimumMileage());
        int maximum = Math.max(fuel.getMaximumMileage(), maintenance.getMaximumMileage());

        return maximum - minimum;
    }

    /**
     * Calculate data.
     */
    public double getFuelQuantity() {
        return fuel.getTotalQuantity();
    }

    /**
     * Calculate data.
     */
    public double getTotalFuelCosts() {
        return fuel.getTotalCosts();
    }

    /**
     * Calculate data.
     */
    public double getTotalMaintenanceCosts() {
        return maintenance.getTotalCosts();
    }

    /**
     * Calculate data.
     */
    public double getTotalCosts() {
        return getTotalFuelCosts() + getTotalMaintenanceCosts();
    }

    /**
     * Calculate data.
     */
    public Calendar getEarliestDate() {
        Calendar fuelCalendar = fuel.getEarliestDate();
        Calendar maintenanceCalendar = maintenance.getEarliestDate();
        return (fuelCalendar.before(maintenanceCalendar) ? fuelCalendar : maintenanceCalendar);
    }

    /**
     * Calculate data.
     */
    public Calendar getLatestDate() {
        Calendar fuelCalendar = fuel.getLatestDate();
        Calendar maintenanceCalendar = maintenance.getLatestDate();
        return (fuelCalendar.after(maintenanceCalendar) ? fuelCalendar : maintenanceCalendar);
    }

    /**
     * Calculate data.
     */
    public Currency getCurrency() {
        return currency.getValue();
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
    public DoubleModel getFuelQuantityModel() {
        return fuelQuantity;
    }

    /**
     * Get model.
     */
    public DoubleModel getTotalFuelCostsModel() {
        return totalFuelCosts;
    }

    /**
     * Get model.
     */
    public DoubleModel getTotalMaintenanceCostsModel() {
        return totalMaintenanceCosts;
    }

    /**
     * Get model.
     */
    public DoubleModel getTotalCostsModel() {
        return totalCosts;
    }

    /**
     * Get model.
     */
    public DoubleModel getAverageFuelCostsModel() {
        return avFuelCosts;
    }

    /**
     * Get model.
     */
    public DoubleModel getAverageMaintenanceCostsModel() {
        return avMaintenanceCosts;
    }

    /**
     * Get model.
     */
    public DoubleModel getAverageTotalCostsModel() {
        return avTotalCosts;
    }

    /**
     * Get model.
     */
    public DoubleModel getAverageQuantityModel() {
        return averageQuantity;
    }

    /**
     * Get model.
     */
    public DoubleModel getAverageMileageModel() {
        return averageMileage;
    }

    /**
     * Get model.
     */
    public DoubleModel getAverageFuelQuantityCostsModel() {
        return averageFuelQuantityCosts;
    }


    /**
     * Get model.
     */
    public StringModel getNameModel() {
        return name;
    }

    /**
     * Get model.
     */
    public StringModel getSignModel() {
        return sign;
    }

    /**
     * Get model.
     */
    public CalendarModel getFromDateModel() {
        return fromDate;
    }

    /**
     * Get model.
     */
    public CalendarModel getToDateModel() {
        return toDate;
    }

    /**
     * Get model, which indicates whether data was changed and has to be
     * saved.
     */
    public BooleanModel getSaveModel() {
        return save;
    }

    public CurrencyModel getCurrencyModel() {
        return currency;
    }

    private double fromEuro(double value) {
        return currency.getValue().fromEuro(value);
    }

    private void updateModels() {
        int difference = fuel.getMaximumMileage() - fuel.getMinimumMileage();

        mileage.setValue(difference);
        fuelQuantity.setValue(getFuelQuantity());

        totalFuelCosts.setValue(fromEuro(getTotalFuelCosts()));
        totalMaintenanceCosts.setValue(fromEuro(getTotalMaintenanceCosts()));
        totalCosts.setValue(fromEuro(getTotalCosts()));
        avFuelCosts.setValue(fromEuro(getTotalFuelCosts() /
                difference *
                100.0));
        avMaintenanceCosts.setValue(fromEuro(getTotalMaintenanceCosts() /
                difference *
                100.0));
        avTotalCosts.setValue(fromEuro(getTotalCosts() /
                difference *
                100.0));

        long latestDays =
                getLatestDate().get(Calendar.YEAR) * 365 +
                        (getLatestDate().get(Calendar.MONTH) + 1) * 30 +
                        getLatestDate().get(Calendar.DAY_OF_MONTH);

        long earliestDays =
                getEarliestDate().get(Calendar.YEAR) * 365 +
                        (getEarliestDate().get(Calendar.MONTH) + 1) * 30 +
                        getEarliestDate().get(Calendar.DAY_OF_MONTH);

        double years = (latestDays - earliestDays) / 365.0;

        averageMileage.setValue(difference / years);
        averageQuantity.setValue(getFuelQuantity() /
                difference *
                100.0);
        averageFuelQuantityCosts.setValue(fromEuro(getTotalFuelCosts()) / getFuelQuantity());

        fromDate.setValue(getEarliestDate());
        toDate.setValue(getLatestDate());

        save.setState(true);
    }

    //-- storage support -------------------------------------------------

    private void readFirstLine(String line) throws Exception {
        // simply ignore the first line of the amiga file
    }

    private void readSecondLine(String line) throws NoSuchElementException {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        sign.setValue(tokenizer.nextToken());
        name.setValue(tokenizer.nextToken());

        // solve EUR transition
        Currency c = Currency.getCurrency("DM");
        if (tokenizer.hasMoreTokens()) {
            String currencyString = tokenizer.nextToken();
            c = Currency.getCurrency(currencyString);
        }
        currency.setValue(c);
    }

    /**
     * Read in Amiga CarCosts format.
     */
    public void read(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String line = buffReader.readLine();
        if (!line.startsWith("#"))
            throw new IOException("Dateikennung '#' nicht gefunden.");

        try {
            readFirstLine(buffReader.readLine());
            readSecondLine(buffReader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Dateikopf nicht korrekt.");
        }

        line = buffReader.readLine();
        if (!line.startsWith("#"))
            throw new IOException("Dateikennung '#' nicht gefunden.");

        fuel.read(buffReader);
        maintenance.read(buffReader);

        buffReader.close();
        buffReader.close();

        updateModels();

        save.setState(false);
    }

    /**
     * Write Amiga CarCosts format.
     */
    public void write(File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter buffWriter = new BufferedWriter(fileWriter);

        buffWriter.write("#", 0, 1);
        buffWriter.newLine();

        String firstLine = "01-Jan-80 0 0 0"; // dummy for Amiga program
        buffWriter.write(firstLine, 0, firstLine.length());
        buffWriter.newLine();

        String secondLine = sign.getValue() + "," + name.getValue() + "," + currency.getValue().getName();
        buffWriter.write(secondLine, 0, secondLine.length());
        buffWriter.newLine();

        buffWriter.write("#", 0, 1);
        buffWriter.newLine();

        fuel.write(buffWriter);

        buffWriter.write("#", 0, 1);
        buffWriter.newLine();

        maintenance.write(buffWriter);

        buffWriter.close();
        fileWriter.close();

        save.setState(false);
    }


    /**
     * Convert to String.
     */
    public String toString() {
        return "Car " + name + ", Sign " + sign;
    }

    // --- inner classes ---------------------------------------

    public class ModelUpdater implements ListDataListener, ChangeListener {
        public void contentsChanged(ListDataEvent e) {
           // intentionally left empty to avoid "changed" state just by opening a view
        }

        public void intervalAdded(ListDataEvent e) {
            updateModels();
        }

        public void intervalRemoved(ListDataEvent e) {
            updateModels();
        }

        public void stateChanged(ChangeEvent e) {
            updateModels();
        }
    }

    // --- member variables ------------------------------------

    private FuelCosts fuel;
    private MaintenanceCosts maintenance;

    private ModelUpdater modelUpdater = new ModelUpdater();
    private BooleanModel save = new DefaultBooleanModel();

    private CurrencyModel currency = new CurrencyModel(Currency.getCurrency("DM"));
    private StringModel name = new StringModel();
    private StringModel sign = new StringModel();

    private CalendarModel fromDate = new CalendarModel();
    private CalendarModel toDate = new CalendarModel();

    private IntegerModel mileage = new IntegerModel();
    private DoubleModel fuelQuantity = new DoubleModel();
    private DoubleModel totalFuelCosts = new DoubleModel();
    private DoubleModel totalMaintenanceCosts = new DoubleModel();
    private DoubleModel totalCosts = new DoubleModel();
    private DoubleModel avFuelCosts = new DoubleModel();
    private DoubleModel avMaintenanceCosts = new DoubleModel();
    private DoubleModel avTotalCosts = new DoubleModel();
    private DoubleModel averageMileage = new DoubleModel();
    private DoubleModel averageQuantity = new DoubleModel();
    private DoubleModel averageFuelQuantityCosts = new DoubleModel();
}


