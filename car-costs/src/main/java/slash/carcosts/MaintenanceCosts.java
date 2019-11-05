/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2001 Christian Pesch. All Rights Reserved.  
*/

package slash.carcosts;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * This is a aggregation of maintenances.
 *
 * @author Christian Pesch
 */

public class MaintenanceCosts extends AbstractListModel implements ListModel {

    // --- ListModel methods ----------------------------------------

    public int getSize() {
        return maintenances.size();
    }

    public Object getElementAt(int index) {
        return maintenances.get(index);
    }

    // --- additional methods ----------------------------------------

    public MaintenanceCosts() {
        maintenances = new ArrayList<Maintenance>();
    }

    public boolean existsMaintenance(Maintenance maintenance) {
        return maintenances.contains(maintenance);
    }

    public void addMaintenance(Maintenance maintenance) {
        maintenances.add(maintenance);
        int index = maintenances.indexOf(maintenance);
        fireIntervalAdded(maintenance, index, index);
    }

    public void changeMaintenance(Maintenance maintenance) {
        int index = maintenances.indexOf(maintenance);
        fireContentsChanged(maintenance, index, index);
    }

    public void removeMaintenance(Maintenance maintenance) {
        int index = maintenances.indexOf(maintenance);
        maintenances.remove(maintenance);
        fireIntervalRemoved(maintenance, index, index);
    }

    public void removeAllMaintenances() {
        int size = getSize();
        maintenances.clear();
        fireIntervalRemoved(this, 0, size);
    }

    public List<Maintenance> getMaintenances() {
        return new ArrayList<Maintenance>(maintenances);
    }


    /**
     * Gets this least minimum mileage of a maintenance.
     */
    public int getMinimumMileage() {
        int minimum = Integer.MAX_VALUE;
        for (Maintenance maintenance : maintenances) {
            if (maintenance.getMileage() < minimum) {
                minimum = maintenance.getMileage();
            }
        }
        return (minimum == Integer.MAX_VALUE) ? 0 : minimum;
    }

    /**
     * Gets this least maximum mileage of a maintenance.
     */
    public int getMaximumMileage() {
        int maximum = Integer.MIN_VALUE;
        for (Maintenance maintenance : maintenances) {
            if (maintenance.getMileage() > maximum)
                maximum = maintenance.getMileage();
        }
        return (maximum == Integer.MIN_VALUE) ? 0 : maximum;
    }

    /**
     * Gets the total maintenance costs.
     */
    public double getTotalCosts() {
        double costs = 0.0;
        for (Maintenance maintenance : maintenances) {
            Currency currency = maintenance.getCurrency();
            costs += currency.toEuro(maintenance.getCosts());
        }
        return costs;
    }


    /**
     * Gets the earliest maintenance date.
     */
    public Calendar getEarliestDate() {
        Calendar minimum = new GregorianCalendar();
        for (Maintenance maintenance : maintenances) {
            if (maintenance.getDate().before(minimum)) {
                minimum = maintenance.getDate();
            }
        }
        return minimum;
    }

    /**
     * Gets the latest maintenance date.
     */
    public Calendar getLatestDate() {
        Calendar maximum = getEarliestDate();
        for (Maintenance maintenance : maintenances) {
            if (maintenance.getDate().after(maximum)) {
                maximum = maintenance.getDate();
            }
        }
        return maximum;
    }

    //-- storage support ------------------------------------------------

    private static final String[] months = {"Jan", "Feb", "M�r", "Apr", "Mai", "Jun",
            "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"};

    private Calendar parseDate(String dateString) throws NoSuchElementException {
        StringTokenizer tokenizer = new StringTokenizer(dateString, "-");
        int day = Integer.parseInt(tokenizer.nextToken());
        String monthString = tokenizer.nextToken();
        int month = 1;
        for (int i = 0, c = months.length; i < c; i++) {
            if (months[i].equals(monthString)) {
                month = i;
                break;
            }
        }
        int year = Integer.parseInt(tokenizer.nextToken());

        // solve y2k problem by windowing
        if (year < 150)
            year += 1900;
        if (year < 1950)
            year += 100;

        return new GregorianCalendar(year, month, day);
    }

    private String formatNumber(int number) {
        String str = Integer.toString(number);
        return (number < 10) ? "0" + str : str;
    }

    private String formatDate(Calendar calendar) {
        int days = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        // do not subtract 1900 to solve y2k problem
        int year = calendar.get(Calendar.YEAR); // - 1900

        return formatNumber(days) + "-" + months[month] + "-" + Integer.toString(year);
    }

    private String formatCurrency(Currency currency) {
        return currency.getName();
    }


    private Maintenance readMaintenance(BufferedReader reader) throws Exception {
        String line = reader.readLine();
        // System.out.println("MaintenanceCosts read:"+line);
        if (line == null || line.startsWith("#"))
            return null;

        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        Calendar calendar = parseDate(tokenizer.nextToken());
        tokenizer.nextToken();
        double costs = new Double(tokenizer.nextToken());
        int mileage = 0;
        if (tokenizer.hasMoreTokens())
            mileage = Integer.parseInt(tokenizer.nextToken());

        // solve EUR transition
        Currency currency = Currency.getCurrency("DM");
        if (tokenizer.hasMoreTokens()) {
            String currencyString = tokenizer.nextToken();
            currency = Currency.getCurrency(currencyString);
        }

        String title = reader.readLine();
        String note = reader.readLine();

        return new Maintenance(calendar, mileage, title, note, costs, currency);
    }

    /**
     * Read in Amiga CarCosts format.
     */
    public void read(BufferedReader reader) throws IOException {
        removeAllMaintenances();

        for (; ;) {
            try {
                Maintenance maintenance = readMaintenance(reader);
                if (maintenance == null)
                    break;
                else
                    addMaintenance(maintenance);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Ung�ltige Wartung.");
            }
        }

        fireIntervalAdded(this, 0, getSize());
    }

    private void writeMaintenance(BufferedWriter writer, Maintenance maintenance) throws IOException {
        String line = formatDate(maintenance.getDate()) + " 0 " +
                maintenance.getCosts() + " " +
                maintenance.getMileage() + " " +
                formatCurrency(maintenance.getCurrency());
        writer.write(line, 0, line.length());
        writer.newLine();

        line = maintenance.getTitle();
        writer.write(line, 0, line.length());
        writer.newLine();

        line = maintenance.getNote();
        writer.write(line, 0, line.length());
        writer.newLine();
    }

    /**
     * Write in Amiga CarCosts format.
     */
    public void write(BufferedWriter writer) throws IOException {
        for (Maintenance maintenance : maintenances) {
            writeMaintenance(writer, maintenance);
        }
    }

    // -- member variables ----------------------------------------------

    private List<Maintenance> maintenances;
}


