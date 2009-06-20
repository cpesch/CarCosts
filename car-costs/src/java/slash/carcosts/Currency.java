/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2001 Christian Pesch. All Rights Reserved.  
*/

package slash.carcosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * This is a currency for a filling or maintenance.
 *
 * @author Christian Pesch
 */

public class Currency {
   private static Map<String, Currency> currencies;

    private static void initialize() {
        if (currencies == null) {
            currencies = new HashMap<String, Currency>();

            Preferences preferences = Preferences.userNodeForPackage(Currency.class);
            addCurrency(new Currency("DM", "Pf.", 1.95583));
            addCurrency(new Currency("EUR", "Ct.", 1.0));
            addCurrency(new Currency("SEK", "Öre", preferences.getDouble("1-SEK-to-1-EUR", 0.0986476)));
        }
    }

    public Currency(String name, String fracture, double factor) {
        this.name = name;
        this.fracture = fracture;
        this.factor = factor;

        addCurrency(this);
    }

    public static void addCurrency(Currency currency) {
        initialize();
        currencies.put(currency.getName(), currency);
    }

    public static Currency getCurrency(String currencyName) {
        initialize();
        return currencies.get(currencyName);
    }

    public static List<Currency> getCurrencies() {
        initialize();
        return new ArrayList<Currency>(currencies.values());
    }

    public String getName() {
        return name;
    }

    public String getFracture() {
        return fracture;
    }

    /**
     * @return the factor of the currency relative to EUR
     */
    public double getFactor() {
        return factor;
    }

    public double toEuro(double value) {
        return value / factor;
    }

    public double fromEuro(double value) {
        return value * factor;
    }

    public String toString() {
        return "Currency " + getName() + " factor: " + getFactor();
    }

    private String name;
    private String fracture;
    private double factor;
}


