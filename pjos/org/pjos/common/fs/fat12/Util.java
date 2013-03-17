/*
Copyright 2002 Simon Daniel
email: simon@pjos.org

This file is part of PJOS.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.pjos.common.fs.fat12;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Some utility methods.
 *
 * Safe for use by multiple threads.
 */
class Util {
    
    /** The calendar used for time and date conversions */
    private static Calendar calendar;
    
    /**
     * Return an unsigned 16-bit value representing a FAT
     * date stamp. From the spec the format is:
     *     bits 0-4 = day of month [1, 31]
     *     bits 5-8 = month of year [1, 12] (ie. [jan, dec])
     *     bits 9-15 = count of years since 1980 [0, 127] (ie. [1980, 2107])
     * @param millis a timestamp of the format returned
     *        by java.lang.System.currentTimeMillis().
     * @return the datestamp
     */
    static synchronized int dateStamp(long millis) {
        if (calendar == null) { calendar = new GregorianCalendar(); }
        calendar.setTimeInMillis(millis);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return day | (month << 5) | (year << 9);
    }
    
    /**
     * Return an unsigned 16-bit value representing a FAT
     * time stamp. From the spec the fomat is:
     *     bits 0-4 = 2-second count [0, 29]
     *         (ie. [0, 58] in 2-second increments)
     *     bits 5-10 = minutes [0, 59]
     *     bits 11-15 = hours [0, 23]
     * @param millis a timestamp of the format returned
     *        by java.lang.System.currentTimeMillis().
     * @return the timestamp
     */
    static synchronized int timeStamp(long millis) {
        if (calendar == null) { calendar = new GregorianCalendar(); }
        calendar.setTimeInMillis(millis);
        int seconds = calendar.get(Calendar.SECOND) / 2;
        if (seconds > 29) { seconds = 29; }
        int minutes = calendar.get(Calendar.MINUTE);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        return seconds | (minutes << 5) | (hours << 11);
    }
    
    /**
     * Convert the FAT format time and date values to a java
     * long value of the format returned by
     * java.lang.System.currentTimeMillis().
     * @param timeStamp the timestamp
     * @param dateStamp the datestamp
     */
    static synchronized long convertToMillis(int timeStamp, int dateStamp) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        calendar.clear();
        calendar.set(Calendar.SECOND, (timeStamp & 0x1f) * 2);
        calendar.set(Calendar.MINUTE, (timeStamp >> 5) & 0x3f);
        calendar.set(Calendar.HOUR_OF_DAY, (timeStamp >> 11) & 0x1f);
        calendar.set(Calendar.DAY_OF_MONTH, dateStamp & 0x1f);
        calendar.set(Calendar.MONTH, (dateStamp >> 5) & 0xf);
        calendar.set(Calendar.YEAR, (dateStamp >> 9) & 0x7f);
        return calendar.getTimeInMillis();
    }

}










