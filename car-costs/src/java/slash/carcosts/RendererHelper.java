/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2006 Christian Pesch. All Rights Reserved.
*/

package slash.carcosts;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Common helpers used during rendering.
 *
 * @author Christian Pesch
 */

public abstract class RendererHelper {
    private static final DecimalFormat numberFormat;
    private static final DecimalFormat pricePerLiterFormat;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    static {
        numberFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);
        numberFormat.applyPattern("###,##0.00");

        pricePerLiterFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);
        pricePerLiterFormat.applyPattern("0.000");
    }

    public static String formatCurrency(Currency currency) {
        return currency.getName();
    }

    public static String formatDate(Calendar calendar) {
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }

    public static String formatNumber(double number) {
        return numberFormat.format(number);
    }

    public static String formatPricePerLiter(double pricePerLiter) {
        return pricePerLiterFormat.format(pricePerLiter);
    }
}
