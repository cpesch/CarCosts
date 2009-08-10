/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 1998-2006 Christian Pesch. All Rights Reserved.
*/

package slash.carcosts;

import java.util.Calendar;
import java.util.Comparator;

/**
 * A comparator for <code>Maintenances</code>.
 *
 * @author Christian Pesch
 */
public class MaintenanceComparator implements Comparator {

    private int compareCalendars(Calendar calendar1, Calendar calendar2) {
        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);
        if (year1 != year2)
            return year2 - year1;

        int month1 = calendar1.get(Calendar.MONTH);
        int month2 = calendar2.get(Calendar.MONTH);
        if (month1 != month2)
            return month2 - month1;

        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
        if (day1 != day2)
            return day2 - day1;

        return 0;
    }

    /**
     * Compare two <code>Maintenances</code> by their date.
     */
    public int compare(Object o1, Object o2) {
        Maintenance left = (Maintenance) o1;
        Maintenance right = (Maintenance) o2;

        int result = compareCalendars(left.getDate(), right.getDate());
        return result != 0 ? result : right.getMileage() - left.getMileage();
    }
}
