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
package java.lang;

import java.io.Serializable;

import org.pjos.common.runtime.Core;

/**
 * Implementation of java.lang.Long based on Sun specification.
 */
public class Long extends Number implements Comparable, Serializable {

    /** The smallest value of type long */
    public static final long MIN_VALUE = 0x8000000000000000L;

    /** The largest value of type long */
    public static final long MAX_VALUE = 0x7fffffffffffffffL;

    /** The long primitive class */
    public static final Class TYPE = Core.getLongClass();

    /** The value */
    private long value;

    /**
     * Create a Long wrapper for the given value
     * @param value the long value
     */
    public Long(long value) {
        this.value = value;
    }

    /**
     * Create a Long value for the given string
     * @param s the value
     */
    public Long(String s) {
        value = parseLong(s, 10);
    }

    /**
     * Convert value to signed representation of specified
     * radix and return as a string.
     * @param l the value
     * @param radix the radix
     * @return the string representation
     */
    public static String toString(long l, int radix) {
        // use radix 10 if not within correct range
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            radix = 10;
        }

        // handle zero
        if (l == 0) { return "0"; }

        // need to handle min value separately because
        // abs function doesn't make it positive
        StringBuffer buf = new StringBuffer();
        long k = Math.abs(l);
        if (k == MIN_VALUE) {
            long digit = -(k % radix);
            buf.append(Character.forDigit((int) digit, radix));
            k /= radix;
            k = -k;
        }

        // process digits in reverse order
        while (k > 0) {
            long digit = k % radix;
            buf.append(Character.forDigit((int) digit, radix));
            k = k / radix;
        }

        // add sign if negative
        if (l < 0) { buf.append('-'); }
        return buf.reverse().toString();
    }

    /**
     * Convert to string representation of unsigned value, using
     * the specified number of bits per character. (bits should
     * be 1, 3, 4 for binary, octal, hexadecimal)
     */
    private static String toUnsignedString(long l, int bits) {
        if (l == 0) { return "0"; }
        StringBuffer buf = new StringBuffer();
        long k = l;
        int radix = 1 << bits;
        int digitmask = radix - 1;
        while (k != 0) {
            long digit = k & digitmask;
            buf.append(Character.forDigit((int) digit, radix));
            k = k >>> bits;
        }
        return buf.reverse().toString();
    }

    /**
     * @param l the value
     * @return the value converted to unsigned hex string representation
     */
    public static String toHexString(long l) {
        return toUnsignedString(l, 4);
    }
    
    /**
     * @param l the value
     * @return the value converted to unsigned octal string representation
     */
    public static String toOctalString(long l) {
        return toUnsignedString(l, 3);
    }
    
    /**
     * @param l the value
     * @return the value converted to unsigned binary string representation
     */
    public static String toBinaryString(long l) {
        return toUnsignedString(l, 1);
    }

    /**
     * Convert value to signed decimal representation and return as a string
     * @param l the value
     * @return the string representation
     */
    public static String toString(long l) {
        return toString(l, 10);
    }

    /**
     * Convert string to signed long of specified radix
     * @param s the string
     * @param radix the radix
     * @return the long representation
     */
    public static long parseLong(String s, int radix) {
        // check args
        if (s == null) { throw new NumberFormatException(); }
        int length = s.length();
        if (length == 0
                || radix < Character.MIN_RADIX
                || radix > Character.MAX_RADIX)
        {
            throw new NumberFormatException();
        }

        // extract sign
        String digits = s;
        int sign = -1;
        if (s.charAt(0) == '-') {
            digits = s.substring(1);
            sign = 1;
            length--;
            if (length == 0) { throw new NumberFormatException(); }
        }

        // loop through digits backwards adding contribution
        long k = 0;
        long magnitude = 1;
        for (int i = length - 1; i >= 0; i--) {
            char c = digits.charAt(i);
            long digit = Character.digit(c, radix);
            if (digit == -1) { throw new NumberFormatException(); }
            long contribution = digit * magnitude;
            long remaining = MIN_VALUE - k;
            if (remaining == 0 && contribution > 0) {
                throw new NumberFormatException();
            }
            if ((remaining + contribution) > 0) {
                throw new NumberFormatException();
            }
            k -= contribution;
            magnitude *= radix;
        }

        // because max == |min| - 1
        if (k == MIN_VALUE && sign == -1) {
            throw new NumberFormatException();
        }
        return sign * k;
    }

    /**
     * Convert string to signed decimal long
     * @param s the string
     * @return the long value
     */
    public static long parseLong(String s) {
        return parseLong(s, 10);
    }

    /**
     * Convert string to signed long of specified radix
     * @param s the string
     * @param radix the radix
     * @return the Long representation
     */
    public static Long valueOf(String s, int radix) {
        return new Long(parseLong(s, radix));
    }

    /**
     * Convert string to signed decimal long
     * @param s the string
     * @return the Long representation
     */
    public static Long valueOf(String s) {
        return new Long(parseLong(s));
    }

    /**
     * Decode a string
     * @param nm the string
     * @return the Long representation
     */
    public static Long decode(String nm) {
        // get the sign
        String k = nm;
        int sign = -1;
        if (k.startsWith("-")) {
            sign = 1;
            k = k.substring(1);
        }

        // get the radix
        int radix = 10;
        if (k.startsWith("0x") || k.startsWith("0X")) {
            radix = 16;
            k = k.substring(2);
        } else if (k.startsWith("#")) {
            radix = 16;
            k = k.substring(1);
        } else if (k.startsWith("0")) {
            radix = 8;
            k = k.substring(1);
        }

        // check for overflow
        long magnitude = parseLong("-" + k, radix);
        if (magnitude == MIN_VALUE && sign == -1) {
            throw new NumberFormatException();
        }
        return new Long(sign * magnitude);
    }

    /**
     * @return as byte
     */
    public byte byteValue() {
        return (byte) value;
    }
    
    /**
     * @return as short
     */
    public short shortValue() {
        return (short) value;
    }
    
    /**
     * @return as int
     */
    public int intValue() {
        return (int) value;
    }
    
    /**
     * @return as long
     */
    public long longValue() {
        return (long) value;
    }
    
    /**
     * @return as float
     */
    public float floatValue() {
        return (float) value;
    }
    
    /**
     * @return as double
     */
    public double doubleValue() {
        return (double) value;
    }

    /**
     * Convert to string
     * @return the string representation
     */
    public String toString() {
        return toString(value);
    }

    /**
     * @return hash code
     */
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    /**
     * Check for equality
     * @param o the object to test
     * @return true if equal
     */
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Long)) { return false; }
        Long other = (Long) o;
        return value == other.value;
    }

    /**
     * Return system property as long
     * @param nm the system property name
     * @return system property as long
     */
    public static Long getLong(String nm) {
        return getLong(nm, null);
    }

    /**
     * Return system property as long
     * @param nm the system property name
     * @param val the default value
     * @return system property as long
     */
    public static Long getLong(String nm, long val) {
        Long result = getLong(nm, null);
        return (result == null) ? new Long(val) : result;
    }

    /**
     * Return system property as long
     * @param nm the system property name
     * @param val the default value
     * @return system property as long
     */
    public static Long getLong(String nm, Long val) {
        String s = System.getProperty(nm);
        return (s != null) ? decode(s) : val;
    }

    /**
     * Compare to another long
     * @param other the long to be compared with
     * @return int value indicating ordering
     */
    public int compareTo(Long other) {
        long l = other.value;
        if (value < l) { return -1; }
        if (value > l) { return 1; }
        return 0;
    }

    /**
     * Compare to another object
     * @param o the object to be compared with
     * @return int value indicating ordering
     */
    public int compareTo(Object o) {
        return compareTo((Integer) o);
    }
}
