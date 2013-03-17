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
import java.io.UnsupportedEncodingException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of java.lang.String based on Sun specification.
 */
public final class String implements Serializable, Comparable, CharSequence {
    
    /** A comparator that orders strings in a case insensitive manner */
    public static final Comparator CASE_INSENSITIVE_ORDER
            = new CaseInsensitiveComparator();
    
    /** The empty character array */
    private static final char[] EMPTY = new char[0];
    
    /** The map of interned strings */
    private static final Map interned = new HashMap();
    
    /** The characters in this string */
    private char[] chars;
    
    /** The index of the first character (inclusive) */
    private int first;
    
    /** The index of the last character (exclusive) */
    private int last;
    
    /** The number of characters in this string */
    private int length;
    
    /** The hash code */
    private int hashCode;
    
    /**
     * Create an empty string
     */
    public String() {
        init(EMPTY, 0, 0);
    }
    
    /**
     * Create a copy of the given string
     * @param original the string to copy
     */
    public String(String original) {
        init(original.chars, original.first, original.last);
    }
    
    /**
     * Create a string to represent the characters in the given array
     * @param value the characters
     */
    public String(char[] value) {
        this(value, 0, value.length);
    }
    
    /**
     * Allocate a new string from the specified characters in the given array
     * @param value the characters
     * @param offset the index of the first character
     * @param count the number of characters
     */
    public String(char[] value, int offset, int count) {
        char[] copy = copy(value, offset, count);
        init(copy, 0, count);
    }
    
    /**
     * Create a string (deprecated)
     * @param ascii the bytes
     * @param hibyte the high byte to use for decoding
     * @param offset index of first byte
     * @param count the number of bytes
     */
    public String(byte[] ascii, int hibyte, int offset, int count) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Create a string (deprecated)
     * @param ascii the bytes
     * @param hibyte the high byte to use for decoding
     */
    public String(byte[] ascii, int hibyte) {
        this(ascii, hibyte, 0, ascii.length);
    }
    
    /**
     * Create a string by decoding the given data
     * @param bytes the byte array
     * @param offset the index of the first character
     * @param length the number of characters
     * @param charsetName name of charset to use
     * @throws UnsupportedEncodingException if not supported
     */
    public String(byte[] bytes, int offset, int length, String charsetName)
            throws UnsupportedEncodingException
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * Create a string by decoding the given data
     * @param bytes the byte array to decode
     * @param charsetName name of charset to use
     * @throws UnsupportedEncodingException if not supported
     */
    public String(byte[] bytes, String charsetName)
            throws UnsupportedEncodingException
    {
        this(bytes, 0, bytes.length, charsetName);
    }
    
    /**
     * Create a string by decoding the data using the platform default charset
     * @param bytes the byte array
     * @param offset index of first character
     * @param length number of characters
     */
    public String(byte[] bytes, int offset, int length) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Create a string by decoding the data using the platform default charset
     * @param bytes the byte array to decode
     */
    public String(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }
    
    /**
     * Allocate a new string using the contents of the given string buffer
     * @param buf the buffer
     */
    public String(StringBuffer buf) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the length
     */
    public int length() {
        return length;
    }
    
    /**
     * @param index the index
     * @return the character at the specified index
     */
    public char charAt(int index) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException();
        }
        return chars[first + index];
    }
    
    /**
     * Copy characters from this string into the given array
     * @param srcBegin start of substring (inclusive)
     * @param srcEnd end of substring (exclusive)
     * @param dst buffer to copy to
     * @param dstBegin location to copy to
     */
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        if (srcBegin < 0
                || srcBegin > srcEnd
                || srcEnd > length
                || dstBegin < 0
                || dstBegin + (srcEnd - dstBegin) > dst.length)
        {
            throw new IndexOutOfBoundsException();
        }
        int from = first + srcBegin;
        int to = dstBegin;
        while (from < srcEnd) {
            dst[to++] = chars[from++];
        }
    }
    
    /**
     * Copy characters into the given byte array (deprecated)
     * @param srcBegin start of substring (inclusive)
     * @param srcEnd end of substring (exclusive)
     * @param dst buffer to copy to
     * @param dstBegin location to copy to
     */
    public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Encode this string into a sequence of bytes
     * @param charsetName the charset to use
     * @return the array of bytes
     * @throws UnsupportedEncodingException if not supported
     */
    public byte[] getBytes(String charsetName)
            throws UnsupportedEncodingException
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * Encode this string into bytes using the platform default charset
     * @return the array of bytes
     */
    public byte[] getBytes() {
throw new UnsupportedOperationException();
    }
    
    /**
     * Test for equality
     * @param o the object to test
     * @return true if equal
     */
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof String)) { return false; }
        String other = (String) o;
        if (length != other.length) { return false; }
        int i = first;
        int j = other.first;
        for (int count = 0; count < length; count++) {
            if (chars[i++] != other.chars[j++]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param sb the buffer to be comparered with
     * @return true if this string represents the same characters
     *         as the given buffer
     */
    public boolean contentEquals(StringBuffer sb) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Test for case-insensitive equality
     * @param other the string to test
     * @return true if equal
     */
    public boolean equalsIgnoreCase(String other) {
        if (length != other.length) { return false; }
        int i = first;
        int j = other.first;
        for (int count = 0; count < length; count++) {
            if (!equalsIgnoreCase(chars[i++], other.chars[j++])) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Compare to another string
     * @param other the string to be compared with
     * @return int value indicating ordering
     */
    public int compareTo(String other) {
        int max = Math.min(length, other.length);
        int i = first;
        int j = other.first;
        for (int count = 0; count < max; count++) {
            int difference = chars[i++] - other.chars[j++];
            if (difference != 0) { return difference; }
        }
        return length - other.length;
    }
    
    /**
     * Compare to another object
     * @param o the object to be compared with
     * @return int value indicating ordering
     */
    public int compareTo(Object o) {
        return compareTo((String) o);
    }
    
    /**
     * Case-insensitive comparison
     * @param other the string to compare with
     * @return int value indicating ordering
     */
    public int compareToIgnoreCase(String other) {
        int max = Math.min(length, other.length);
        int i = first;
        int j = other.first;
        char[] otherchars = other.chars;
        for (int count = 0; count < max; count++) {
            char c = chars[i++];
            char d = otherchars[i++];
            c = Character.toLowerCase(Character.toUpperCase(c));
            d = Character.toLowerCase(Character.toUpperCase(d));
            int difference = c - d;
            if (difference != 0) { return difference; }
        }
        return length - other.length;
    }
    
    /**
     * Test if two string regions are equal
     * @param toffset location of region in this string
     * @param other the other string
     * @param ooffset location of region in other string
     * @param len number of characters in the regions
     * @return true if the regions are equal
     */
    public boolean regionMatches(
            int toffset,
            String other,
            int ooffset,
            int len)
    {
        return regionMatches(false, toffset, other, ooffset, len);
    }
    
    /**
     * Test if two string regions are equal
     * @param ignoreCase set to true to ignore case
     * @param toffset location of region in this string
     * @param other the other string
     * @param ooffset location of region in other string
     * @param len number of characters in the regions
     * @return true if the regions are equal
     */
    public boolean regionMatches(
            boolean ignoreCase,
            int toffset,
            String other,
            int ooffset,
            int len)
    {
        if (toffset < 0 || toffset + len > length) { return false; }
        if (ooffset < 0 || ooffset + len > other.length) { return false; }
        int i = toffset;
        int j = ooffset;
        for (int count = 0; count < len; count++) {
            char thisChar = chars[i++];
            char otherChar = other.chars[j++];
            if (ignoreCase) {
                if (!equalsIgnoreCase(thisChar, otherChar)) {
                    return false;
                }
            } else if (thisChar != otherChar) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param prefix the prefix
     * @param toffset the index of the substring
     * @return true if the specified substring starts with the given prefix
     */
    public boolean startsWith(String prefix, int toffset) {
        return (toffset < 0 || toffset > length)
                ? false
                :  substring(toffset).startsWith(prefix);
    }
    
    /**
     * @param prefix the prefix
     * @return true if this string starts with the given prefix
     */
    public boolean startsWith(String prefix) {
        return (prefix.length > length)
                ? false
                : substring(0, prefix.length).equals(prefix);
    }
    
    /**
     * @param suffix the suffix
     * @return true if this string ends with the given suffix
     */
    public boolean endsWith(String suffix) {
        return (suffix.length > length)
                ? false
                : substring(length - suffix.length).equals(suffix);
    }
    
    /**
     * @return a hash code for this string
     */
    public int hashCode() {
        if (hashCode == 0 && length > 0) {
            int result = (int) chars[first];
            for (int i = first + 1; i < last; i++) {
                result = (result * 31) + ((int) chars[i]);
            }
            hashCode = result;
        }
        return hashCode;
    }
    
    /**
     * @param c the character to find
     * @return the first index of the given character
     */
    public int indexOf(int c) {
        return indexOf(c, 0);
    }
    
    /**
     * @param c the character to find
     * @param fromIndex search from here
     * @return the first index of the given
     *         character starting from the specified index
     */
    public int indexOf(int c, int fromIndex) {
        if (fromIndex < 0) { fromIndex = 0; }
        for (int i = fromIndex; i < length; i++) {
            if (chars[first + i] == c) { return i; }
        }
        return -1;
    }
    
    /**
     * @param c the character to find
     * @return the last index of the given character
     */
    public int lastIndexOf(int c) {
        return lastIndexOf(c, length - 1);
    }
    
    /**
     * @param c the character to find
     * @param fromIndex search from here
     * @return the last index of the given character searching backwards
     *         from the specified index
     */
    public int lastIndexOf(int c, int fromIndex) {
        if (fromIndex >= length) { fromIndex = length - 1; }
        for (int i = fromIndex; i >= 0; i--) {
            if (chars[first + i] == c) { return i; }
        }
        return -1;
    }
    
    /**
     * @param s the string to find
     * @return the first index of the given string
     */
    public int indexOf(String s) {
        return indexOf(s, 0);
    }
    
    /**
     * @param s the string to find
     * @param fromIndex search from here
     * @return the first index of the given string searching forwards
     *         from the specified index
     */
    public int indexOf(String s, int fromIndex) {
        int start = (fromIndex >= 0) ? fromIndex : 0;
        for (int end = start + s.length; end <= length; end++, start++) {
            if (substring(start).startsWith(s)) { return start; }
        }
        return -1;
    }
    
    /**
     * @param s the string to find
     * @return the last index of the given string searching backwards
     *         from the specified index
     */
    public int lastIndexOf(String s) {
        return lastIndexOf(s, length - 1);
    }
    
    /**
     * @param s the string to find
     * @param fromIndex search from here
     * @return the last index of the given string searching backwards
     *         from the specified index
     */
    public int lastIndexOf(String s, int fromIndex) {
        int max = length - 1 - s.length;
        if (fromIndex > max) { fromIndex = max; }
        for (int i = fromIndex; i >= 0; i--) {
            if (substring(i).startsWith(s)) { return i; }
        }
        return -1;
    }
    
    /**
     * @param beginIndex the index of the first character (inclusive)
     * @return a substring of this string
     */
    public String substring(int beginIndex) {
        return substring(beginIndex, length);
    }
    
    /**
     * @param beginIndex the index of the first character (inclusive)
     * @param endIndex the index of the last character (exclusive)
     * @return a substring of this string
     */
    public String substring(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > length || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        return new String(this, first + beginIndex, first + endIndex);
    }
    
    /**
     * @param beginIndex the index of the first character (inclusive)
     * @param endIndex the index of the last character (exclusive)
     * @return a subsequence of this string
     */
    public CharSequence subSequence(int beginIndex, int endIndex) {
        return substring(beginIndex, endIndex);
    }
    
    /**
     * @param s the string to append
     * @return a concatenation of this string with the given string
     */
    public String concat(String s) {
        StringBuffer sb = new StringBuffer(this);
        sb.append(s);
        return sb.toString();
    }
    
    /**
     * @param oldChar the char to be replaced
     * @param newChar the replacement char
     * @return this string with the characters replaced
     */
    public String replace(char oldChar, char newChar) {
        StringBuffer sb = new StringBuffer(this);
        for (int i = 0; i < length; i++) {
            if (chars[first + i] == oldChar) {
                sb.setCharAt(i, newChar);
            }
        }
        return sb.toString();
    }
    
    /**
     * @param regex the regular expression
     * @return true if this string matches the regular expression
     */
    public boolean matches(String regex) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Replace the first matching substrings
     * @param regex the regular expression
     * @param replacement the replacement string
     * @return the resulting string
     */
    public String replaceFirst(String regex, String replacement) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Replace all matching substrings
     * @param regex the regular expression
     * @param replacement the replacement string
     * @return the resulting string
     */
    public String replaceAll(String regex, String replacement) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Split using regular expression
     * @param regex the regular expression
     * @param limit the limit
     * @return the tokens
     */
    public String[] split(String regex, int limit) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Split using regular expression
     * @param regex the regular expression
     * @return the tokens
     */
    public String[] split(String regex) {
        return split(regex, 0);
    }
    
    /**
     * @param locale the locale to use
     * @return this string converted to lower case according to locale
     */
    public String toLowerCase(Locale locale) {
throw new UnsupportedOperationException();
    }
    
    /**
     * NOT IMPLEMENTED PROPERLY!!!
     * @return this string converted to lower case
     */
    public String toLowerCase() {
        // temporary solution, need to implement UNICODE spec or use
        // GNU classpath implementation
        StringBuffer buf = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            buf.append(Character.toLowerCase(charAt(i)));
        }
        return buf.toString();
    }
    
    /**
     * @param locale the locale to use
     * @return this string converted to upper case according to locale
     */
    public String toUpperCase(Locale locale) {
throw new UnsupportedOperationException();
    }
    
    /**
     * NOT IMPLEMENTED PROPERLY!!!
     * @return this string converted to upper case
     */
    public String toUpperCase() {
        // temporary solution, need to implement UNICODE spec or use
        // GNU classpath implementation
        StringBuffer buf = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            buf.append(Character.toUpperCase(charAt(i)));
        }
        return buf.toString();
    }
    
    /**
     * @return this string leading and trailing whitespace removed.
     */
    public String trim() {
        // find index of first valid character
        char border = '\u0020';
        int from = first;
        while ((from < last) && (chars[from] <= border)) { from++; }

        // find index after last valid character
        int to = last;
        while ((to > from) && (chars[to - 1] <= border)) { to--; }

        // return appropriate result
        if (from == to) {
            return new String(); // empty
        } else if (from == first && to == last) {
            return this; // nothing was trimmed
        } else {
            return substring(from, to); // valid substring
        }
    }
    
    /**
     * @return this string
     */
    public String toString() {
        return this;
    }
    
    /**
     * @return a new character array containing the characters in this string
     */
    public char[] toCharArray() {
        char[] result = new char[length];
        for (int to = 0, from = first; to < length; ) {
            result[to++] = chars[from++];
        }
        return result;
    }
    
    /**
     * @param o the object
     * @return a string representation of the object, or the
     *         string "null" if it is null
     */
    public static String valueOf(Object o) {
        return (o == null) ? "null" : o.toString();
    }
    
    /**
     * @param data the characters
     * @return a string representation of the given char array
     */
    public static String valueOf(char[] data) {
        return new String(data);
    }
    
    /**
     * @param data the characters
     * @param offset index of first character
     * @param count the number of characters
     * @return a string representation of a portion of the given char array
     */
    public static String valueOf(char[] data, int offset, int count) {
        return new String(data, offset, count);
    }
    
    /**
     * @param data the characters
     * @return a string representation of the given char array
     */
    public static String copyValueOf(char[] data) {
        return valueOf(data);
    }
    
    /**
     * @param data the characters
     * @param offset index of first character
     * @param count the number of characters
     * @return a string representation of a portion of the given char array
     */
    public static String copyValueOf(char[] data, int offset, int count) {
        return valueOf(data, offset, count);
    }
    
    /**
     * @param b the boolean value
     * @return a string representation of the boolean value
     */
    public static String valueOf(boolean b) {
        return (b) ? "true" : "false";
    }
    
    /**
     * @param c the character
     * @return a string representation of the given character
     */
    public static String valueOf(char c) {
        String result = new String();
        char[] buf = new char[] {c};
        result.init(buf, 0, 1);
        return result;
    }
    
    /**
     * @param i the int value
     * @return a string representation of the int value
     */
    public static String valueOf(int i) {
        return Integer.toString(i);
    }
    
    /**
     * @param l the long value
     * @return a string representation of the long value
     */
    public static String valueOf(long l) {
        return Long.toString(l);
    }
    
    /**
     * @param f the float value
     * @return a string representation of the float value
     */
    public static String valueOf(float f) {
        return Float.toString(f);
    }
    
    /**
     * @param d the double value
     * @return a string representation of the double value
     */
    public static String valueOf(double d) {
        return Double.toString(d);
    }
    
    /**
     * @return the interned instance equal to this string
     */
    public String intern() {
        synchronized (interned) {
            String result = (String) interned.get(this);
            if (result == null) {
                interned.put(this, this);
                result = this;
            }
            return result;
        }
    }
    
    /**
     * Create a string consisting of a substring of the given string
     */
    private String(String s, int first, int last) {
        init(s.chars, first, last);
    }
    
    /**
     * Test two characters for case-insensitive equality
     */
    private static boolean equalsIgnoreCase(char a, char b) {
        return (a == b)
                || (Character.toUpperCase(a) == Character.toUpperCase(b))
                || (Character.toLowerCase(a) == Character.toLowerCase(b));
    }
    
    /**
     * Return a copy of a section of the given character array
     */
    private static char[] copy(char[] value, int offset, int count) {
        if (offset < 0 || count < 0 || offset + count > value.length) {
            throw new IndexOutOfBoundsException();
        }
        char[] result = new char[count];
        for (int to = 0, from = offset; to < count; ) {
            result[to++] = value[from++];
        }
        return result;
    }
    
    /**
     * Initialise the internal fields of this string
     * @param chars the character buffer
     * @param first index of first character
     * @param last index of last character
     */
    void init(char[] chars, int first, int last) {
        this.chars = chars;
        this.first = first;
        this.last = last;
        this.length = last - first;
    }
    
    /**
     * Used to compare strings in a case insensitive manner
     */
    static class CaseInsensitiveComparator implements Comparator {
        /**
         * Compare the two strings
         * @param o1 the first string
         * @param o2 the second string
         * @return an int value indicating ordering
         */
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.compareToIgnoreCase(s1);
        }
    }
    
}
