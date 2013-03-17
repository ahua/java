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
 * Implementation of java.lang.Integer based on Sun specification.
 */
public class Integer extends Number implements Comparable, Serializable {
    
    /** The smallest value of type int */
    public static final int MIN_VALUE = 0x80000000;
    
    /** The largest value of type int */
    public static final int MAX_VALUE = 0x7fffffff;
    
    /** The integer primitive class */
    public static final Class TYPE = Core.getIntClass();
    
    /** The value */
    private int value;
    
    /**
     * Create an Integer wrapper for the given value
     * @param value the value
     */
    public Integer(int value) {
        this.value = value;
    }
    
    /**
     * Create an Integer from the given string
     * @param s the string representation
     */
    public Integer(String s) {
        value = parseInt(s, 10);
    }
    
    /**
     * Convert value to signed representation of specified
     * radix and return as a string.
     * @param i the int value
     * @param radix the radix
     * @return the string representation
     */
    public static String toString(int i, int radix) {
        // use radix 10 if not within correct range
        if (radix < Character.MIN_RADIX
                || radix > Character.MAX_RADIX)
        {
            radix = 10;
        }

        // handle zero
        if (i == 0) { return "0"; }

        // need to handle min value separately because
        // abs function doesn't make it positive
        StringBuffer buf = new StringBuffer();
        int k = Math.abs(i);
        if (k == MIN_VALUE) {
            int digit = -(k % radix);
            buf.append(Character.forDigit(digit, radix));
            k = -(k / radix);
        }
        
        // process digits in reverse order
        while (k > 0) {
            int digit = k % radix;
            buf.append(Character.forDigit(digit, radix));
            k = k / radix;
        }
        
        // add sign if negative
        if (i < 0) { buf.append('-'); }
        return buf.reverse().toString();
    }
    
    /**
     * Convert to string representation of unsigned value, using
     * the specified number of bits per character. (bits should
     * be 1, 3, 4 for binary, octal, hexadecimal)
     */
    private static String toUnsignedString(int i, int bits) {
        if (i == 0) { return "0"; }
        StringBuffer buf = new StringBuffer();
        int k = i;
        int radix = 1 << bits;
        int digitmask = radix - 1;
        while (k != 0) {
            int digit = k & digitmask;
            buf.append(Character.forDigit(digit, radix));
            k = k >>> bits;
        }
        return buf.reverse().toString();
    }
    
    /**
     * @param i the int value
     * @return the value converted to unsigned hex string representation
     */
    public static String toHexString(int i) {
        return toUnsignedString(i, 4);
    }
    
    /**
     * @param i the int value
     * @return the value converted to unsigned octal string representation
     */
    public static String toOctalString(int i) {
        return toUnsignedString(i, 3);
    }
    
    /**
     * @param i the int value
     * @return the value converted to unsigned binary string representation
     */
    public static String toBinaryString(int i) {
        return toUnsignedString(i, 1);
    }
    
    /**
     * Convert value to signed decimal representation and return as a string
     * @param i the int value
     * @return the string representation
     */
    public static String toString(int i) {
        return toString(i, 10);
    }
    
    /**
     * Convert string to signed integer of specified radix
     * @param s the string
     * @param radix the radix
     * @return the int value
     */
    public static int parseInt(String s, int radix) {
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
        int k = 0;
        int magnitude = 1;
        for (int i = length - 1; i >= 0; i--) {
            char c = digits.charAt(i);
            int digit = Character.digit(c, radix);
            // invalid digit character
            if (digit == -1) {
                throw new NumberFormatException();
            }
            int contribution = digit * magnitude;
            int remaining = MIN_VALUE - k;
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
     * Convert string to signed decimal integer
     * @param s the string
     * @return int value
     */
    public static int parseInt(String s) {
        return parseInt(s, 10);
    }
    
    /**
     * Convert string to signed integer of specified radix
     * @param s the string
     * @param radix the radix
     * @return the integer value
     */
    public static Integer valueOf(String s, int radix) {
        return new Integer(parseInt(s, radix));
    }
    
    /**
     * Convert string to signed decimal integer
     * @param s the string
     * @return the integer value
     */
    public static Integer valueOf(String s) {
        return new Integer(parseInt(s));
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
     * @return a string representation
     */
    public String toString() {
        return toString(value);
    }
    
    /**
     * @return the hash code
     */
    public int hashCode() {
        return value;
    }
    
    /**
     * Check for equality
     * @param o the object to be tested
     * @return true if equal
     */
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Integer)) { return false; }
        Integer other = (Integer) o;
        return value == other.value;
    }
    
    /**
     * @param nm the name of the system property
     * @return system property as integer, null if not found
     */
    public static Integer getInteger(String nm) {
        return getInteger(nm, null);
    }
    
    /**
     * @param nm the name of the system property
     * @param val the default value
     * @return system property as int
     */
    public static Integer getInteger(String nm, int val) {
        Integer result = getInteger(nm, null);
        return (result == null) ? new Integer(val) : result;
    }
    
    /**
     * @param nm the name of the system property
     * @param val the default value
     * @return system property as integer
     */
    public static Integer getInteger(String nm, Integer val) {
        String s = System.getProperty(nm);
        return (s != null) ? decode(s) : val;
    }
    
    /**
     * Decode a string
     * @param nm the string to decode
     * @return the integer object
     */
    public static Integer decode(String nm) {
        // get the sign
        String s = nm;
        int sign = -1;
        if (s.startsWith("-")) {
            sign = 1;
            s = s.substring(1);
        }
        
        // get the radix
        int radix = 10;
        if (s.startsWith("0x") || s.startsWith("0X")) {
            radix = 16;
            s = s.substring(2);
        } else if (s.startsWith("#")) {
            radix = 16;
            s = s.substring(1);
        } else if (s.startsWith("0")) {
            radix = 8;
            s = s.substring(1);
        }
        
        // check for overflow
        int magnitude = parseInt("-" + s, radix);
        if (magnitude == MIN_VALUE && sign == -1) {
            throw new NumberFormatException();
        }
        return new Integer(sign * magnitude);
    }
    
    /**
     * Compare to another integer
     * @param other the integer to be compared with
     * @return int value indicating ordering
     */
    public int compareTo(Integer other) {
        int i = other.value;
        if (value < i) { return -1; }
        if (value > i) { return 1; }
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
