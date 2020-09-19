/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2004 Christian Pesch. All Rights Reserved.  
*/

package slash.carcosts;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * This is an aggregation of fillings.
 *
 * @author Christian Pesch
 */

public class FuelCosts extends AbstractListModel implements ListModel {

    // --- ListModel methods ----------------------------------------

    public int getSize() {
        return fillings.size();
    }

    public Object getElementAt(int index) {
        return fillings.get(Math.min(index, fillings.size() - 1));
    }

    // --- additional methods ----------------------------------------

    public FuelCosts() {
        fillings = new ArrayList<Filling>();
    }

    public boolean existsFilling(Filling filling) {
        return fillings.contains(filling);
    }

    public void addFilling(Filling filling) {
        fillings.add(filling);
        int index = fillings.indexOf(filling);
        fireIntervalAdded(filling, index - 1, index + 1);
    }

    public void changeFilling(Filling filling) {
        int index = fillings.indexOf(filling);
        fireContentsChanged(filling, index, index);
    }

    public void removeFilling(Filling filling) {
        int index = fillings.indexOf(filling);
        fillings.remove(filling);
        fireIntervalRemoved(filling, index, index);
    }

    public void removeAllFillings() {
        int size = getSize();
        fillings.clear();
        fireIntervalRemoved(this, 0, size);
    }

    public List<Filling> getFillings() {
        return new ArrayList<Filling>(fillings);
    }


    /**
     * @return the least minimum mileage of a filling.
     */
    public int getMinimumMileage() {
        int minimum = Integer.MAX_VALUE;
        for (Filling filling : fillings) {
            if (filling.getMileage() < minimum) {
                minimum = filling.getMileage();
            }
        }
        return (minimum == Integer.MAX_VALUE) ? 0 : minimum;
    }

    /**
     * @return the least maximum mileage of a filling.
     */
    public int getMaximumMileage() {
        int maximum = Integer.MIN_VALUE;
        for (Filling filling : fillings) {
            if (filling.getMileage() > maximum)
                maximum = filling.getMileage();
        }
        return (maximum == Integer.MIN_VALUE) ? 0 : maximum;
    }


    /**
     * @return the total fuel costs.
     */
    public double getTotalCosts() {
        double costs = 0.0;
        for (Filling filling : fillings) {
            Currency currency = filling.getCurrency();
            costs += currency.toEuro(filling.getCosts());
        }
        return costs;
    }

    /**
     * @return the total fuel quantity.
     */
    public double getTotalQuantity() {
        double quantity = 0.0;
        for (Filling filling : fillings) {
            quantity += filling.getQuantity();
        }
        return quantity;
    }


    /**
     * @return the earliest filling date.
     */
    public Calendar getEarliestDate() {
        Calendar minimum = new GregorianCalendar();
        for (Filling filling : fillings) {
            if (filling.getDate().before(minimum)) {
                minimum = filling.getDate();
            }
        }
        return minimum;
    }

    /**
     * @return the latest filling date.
     */
    public Calendar getLatestDate() {
        Calendar maximum = getEarliestDate();
        for (Filling filling : fillings) {
            if (filling.getDate().after(maximum)) {
                maximum = filling.getDate();
            }
        }
        return maximum;
    }

    public void updateCurrencies() {
        fireContentsChanged(this, 0, Integer.MAX_VALUE);
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


    private Filling readFilling(String line) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        Calendar calendar = parseDate(tokenizer.nextToken());
        tokenizer.nextToken();
        int mileage = Integer.parseInt(tokenizer.nextToken());
        double quantity = new Double(tokenizer.nextToken());
        double costs = new Double(tokenizer.nextToken());

        // solve EUR transition
        Currency currency = Currency.getCurrency("DM");
        if (tokenizer.hasMoreTokens()) {
            String currencyString = tokenizer.nextToken();
            currency = Currency.getCurrency(currencyString);
        }

        return new Filling(calendar, mileage, quantity, costs, currency);
    }

    /**
     * Read in Amiga CarCosts format.
     * @param reader where to read from
     */
    public void read(BufferedReader reader) throws IOException {
        removeAllFillings();

        for (; ;) {
            String line = reader.readLine();
            if (line.startsWith("#")) {
                break;
            }

            // System.out.println("FuelCosts read:"+line);
            try {
                Filling filling = readFilling(line);
                addFilling(filling);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Ung�ltige Tankung.");
            }
        }

        fireIntervalAdded(this, 0, getSize());
    }

    private void writeFilling(BufferedWriter writer, Filling filling) throws IOException {
        String line = formatDate(filling.getDate()) + " 0 " +
                filling.getMileage() + " " +
                filling.getQuantity() + " " +
                filling.getCosts() + " " +
                formatCurrency(filling.getCurrency());
        writer.write(line, 0, line.length());
        writer.newLine();
    }

    /**
     * Write in Amiga CarCosts format.
     * @param writer where to write to
     */
    public void write(BufferedWriter writer) throws IOException {
        for (Filling filling : fillings) {
            writeFilling(writer, filling);
        }
    }

    // -- member variables ----------------------------------------------

    private List<Filling> fillings;
}


