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
 * Implementation of java.lang.Character based on Sun specification.
 */
public final class Character implements Serializable, Comparable {

    /** COMBINING_SPACING_MARK */
    public static final byte COMBINING_SPACING_MARK = 8;

    /** CONNECTOR_PUNCTUATION */
    public static final byte CONNECTOR_PUNCTUATION = 23;

    /** CONTROL */
    public static final byte CONTROL = 15;

    /** CURRENCY_SYMBOL */
    public static final byte CURRENCY_SYMBOL = 26;

    /** DASH_PUNCTUATION */
    public static final byte DASH_PUNCTUATION = 20;

    /** DECIMAL_DIGIT_NUMBER */
    public static final byte DECIMAL_DIGIT_NUMBER = 9;

    /** DIRECTIONALITY_ARABIC_NUMBER */
    public static final byte DIRECTIONALITY_ARABIC_NUMBER = 6;

    /** DIRECTIONALITY_BOUNDARY_NEUTRAL */
    public static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 9;

    /** DIRECTIONALITY_COMMON_NUMBER_SEPARATOR */
    public static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 7;

    /** DIRECTIONALITY_EUROPEAN_NUMBER */
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER = 3;

    /** DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR */
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 4;

    /** DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR */
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 5;

    /** DIRECTIONALITY_LEFT_TO_RIGHT */
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT = 0;

    /** DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING */
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 14;

    /** DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE */
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 15;

    /** DIRECTIONALITY_NONSPACING_MARK */
    public static final byte DIRECTIONALITY_NONSPACING_MARK = 8;

    /** DIRECTIONALITY_OTHER_NEUTRALS */
    public static final byte DIRECTIONALITY_OTHER_NEUTRALS = 13;

    /** DIRECTIONALITY_PARAGRAPH_SEPARATOR */
    public static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 10;

    /** DIRECTIONALITY_POP_DIRECTIONAL_FORMAT */
    public static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 18;

    /** DIRECTIONALITY_RIGHT_TO_LEFT */
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT = 1;

    /** DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC */
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 2;

    /** DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING */
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 16;

    /** DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE */
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 17;

    /** DIRECTIONALITY_SEGMENT_SEPARATOR */
    public static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = 11;

    /** DIRECTIONALITY_UNDEFINED */
    public static final byte DIRECTIONALITY_UNDEFINED = -1;

    /** DIRECTIONALITY_WHITESPACE */
    public static final byte DIRECTIONALITY_WHITESPACE = 12;

    /** ENCLOSING_MARK */
    public static final byte ENCLOSING_MARK = 7;

    /** END_PUNCTUATION */
    public static final byte END_PUNCTUATION = 22;

    /** FINAL_QUOTE_PUNCTUATION */
    public static final byte FINAL_QUOTE_PUNCTUATION = 30;

    /** FORMAT */
    public static final byte FORMAT = 16;

    /** INITIAL_QUOTE_PUNCTUATION */
    public static final byte INITIAL_QUOTE_PUNCTUATION = 29;

    /** LETTER_NUMBER */
    public static final byte LETTER_NUMBER = 10;

    /** LINE_SEPARATOR */
    public static final byte LINE_SEPARATOR = 13;

    /** LOWERCASE_LETTER */
    public static final byte LOWERCASE_LETTER = 2;

    /** MATH_SYMBOL */
    public static final byte MATH_SYMBOL = 25;

    /** MAX_RADIX */
    public static final int MAX_RADIX = 36;

    /** MAX_VALUE */
    public static final char MAX_VALUE = 65535;

    /** MIN_RADIX */
    public static final int MIN_RADIX = 2;

    /** MIN_VALUE */
    public static final char MIN_VALUE = 0;

    /** MODIFIER_LETTER */
    public static final byte MODIFIER_LETTER = 4;

    /** MODIFIER_SYMBOL */
    public static final byte MODIFIER_SYMBOL = 27;

    /** NON_SPACING_MARK */
    public static final byte NON_SPACING_MARK = 6;

    /** OTHER_LETTER */
    public static final byte OTHER_LETTER = 5;

    /** OTHER_NUMBER */
    public static final byte OTHER_NUMBER = 11;

    /** OTHER_PUNCTUATION */
    public static final byte OTHER_PUNCTUATION = 24;

    /** OTHER_SYMBOL */
    public static final byte OTHER_SYMBOL = 28;

    /** PARAGRAPH_SEPARATOR */
    public static final byte PARAGRAPH_SEPARATOR = 14;

    /** PRIVATE_USE */
    public static final byte PRIVATE_USE = 18;

    /** SPACE_SEPARATOR */
    public static final byte SPACE_SEPARATOR = 12;

    /** START_PUNCTUATION */
    public static final byte START_PUNCTUATION = 21;

    /** SURROGATE */
    public static final byte SURROGATE = 19;

    /** TITLECASE_LETTER */
    public static final byte TITLECASE_LETTER = 3;

    /** UNASSIGNED */
    public static final byte UNASSIGNED = 0;

    /** UPPERCASE_LETTER */
    public static final byte UPPERCASE_LETTER = 1;
    
    /** The character primitive class */
    public static final Class TYPE = Core.getCharClass();
    
    /** The string used by the forDigit method */
    private static final String DIGITS = "0123456789abcdefghijklmnopqrstuvwxyz";

    /** The value */
    private char value;

    /**
     * Create a character wrapper instance
     * @param value the character to wrap
     */
    public Character(char value) {
        this.value = value;
    }
    
    /**
     * @return the value as a char
     */
    public char charValue() {
        return value;
    }
    
    /**
     * @return a hash code value
     */
    public int hashCode() {
        return (int) value;
    }
    
    /**
     * Check for equality
     * @param o the object to test
     * @return true if equal, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Character)) { return false; }
        Character c = (Character) o;
        return value == c.value;
    }
    
    /**
     * @return a string representing this character
     */
    public String toString() {
        return String.valueOf(value);
    }
    
    /**
     * @return a string representing the given character
     * @param c the character
     */
    public static String toString(char c) {
        return String.valueOf(c);
    }
    
    /**
     * Determine if the given character is lower case.
     * NOT PROPERLY IMPLEMENTED!!
     * @param c the character
     * @return true if the character is in lower case, false otherwise
     */
    public static boolean isLowerCase(char c) {
return c >= 'a' && c <= 'z';
    }
    
    /**
     * Determine if the given character is upper case.
     * NOT PROPERLY IMPLEMENTED!!
     * @param c the character
     * @return true if the character is in upper case, false otherwise
     */
    public static boolean isUpperCase(char c) {
return c >= 'A' && c <= 'Z';
    }
    
    /**
     * Determine if the given character is title case.
     * @param c the character
     * @return true if the character is in title case, false otherwise
     */
    public static boolean isTitleCase(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Determine if the given character is a digit.
     * NOT PROPERLY IMPLEMENTED!!
     * @param c the character
     * @return true if the character is a digit, false otherwise
     */
    public static boolean isDigit(char c) {
return c >= '0' && c <= '9';
    }
    
    /**
     * Determine if the given character is defined
     * @param c the character
     * @return true if the character is defined, false otherwise
     */
    public static boolean isDefined(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Determine if the given character is a letter
     * NOT PROPERLY IMPLEMENTED!!
     * @param c the character
     * @return true if the character is a letter, false otherwise
     */
    public static boolean isLetter(char c) {
return isLowerCase(c) || isUpperCase(c);
    }
    
    /**
     * Determine if the given character is a letter or a digit
     * NOT PROPERLY IMPLEMENTED!!
     * @param c the character
     * @return true if the character is a digit or letter, false otherwise
     */
    public static boolean isLetterOrDigit(char c) {
return isLetter(c) || isDigit(c);
    }
    
    /**
     * Determine if the given character is a java letter (deprecated)
     * @param c the character
     * @return true if the character is a java letter, false otherwise
     */
    public static boolean isJavaLetter(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Determine if the given character is a java letter or digit (deprecated)
     * @param c the character
     * @return true if the character is a java digit or letter, false otherwise
     */
    public static boolean isJavaLetterOrDigit(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Determine if the given character may start a java identifier
     * @param c the character
     * @return true if the character can start a java identifier,
     *         false otherwise
     */
    public static boolean isJavaIdentifierStart(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Determine if the given character may be part of a java identifier
     * @param c the character
     * @return true if the character can be part of a java identifier,
     *         false otherwise
     */
    public static boolean isJavaIdentifierPart(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Determine if the given character may start a Unicode identifier
     * @param c the character
     * @return true if the character can start a unicode identifier,
     *         false otherwise
     */
    public static boolean isUnicodeIdentifierStart(char c) {
throw new UnsupportedOperationException();
    }
     
    /**
     * Determine if the given character may be part of a Unicode identifier
     * @param c the character
     * @return true if the character can be part of a unicode identifier,
     *         false otherwise
     */
    public static boolean isUnicodeIdentifierPart(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Determine if the given character is ignorable
     * @param c the character
     * @return true if the character is ignorable, false otherwise
     */
    public static boolean isIdentifierIgnorable(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * NOT IMPLEMENTED PROPERLY!!!
     * @param c the character
     * @return the character converted to lower case
     */
    public static char toLowerCase(char c) {
return (isUpperCase(c)) ? (char) (c - 'A' + 'a') : c;
    }
    
    /**
     * NOT IMPLEMENTED PROPERLY!!!
     * @param c the character
     * @return the character converted to upper case
     */
    public static char toUpperCase(char c) {
return (isLowerCase(c)) ? (char) (c - 'a' + 'A') : c;
    }
    
    /**
     * Convert to title case
     * @param c the character
     * @return the character converted to title case
     */
    public static char toTitleCase(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param c the character
     * @param radix the radix
     * @return the character's numeric value according to the radix
     */
    public static int digit(char c, int radix) {
        if (radix < MIN_RADIX || radix > MAX_RADIX || c >= radix) {
            return -1;
        }
        return DIGITS.indexOf(c);
    }
    
    /**
     * @param c the character
     * @return the int value that the specified unicode character represents
     */
    public static int getNumericValue(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param c the character
     * @return true if the character represents white space
     */
    public static boolean isSpace(char c) {
        return c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == ' ';
    }
    
    /**
     * @param c the character
     * @return true if the character is a unicode space character
     */
    public static boolean isSpaceChar(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param c the character
     * @return true if the character is a white space character
     */
    public static boolean isWhiteSpace(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param c the character
     * @return true if the character is an ISO control character
     */
    public static boolean isISOControl(char c) {
        return (c >= '\u0000' && c <= '\u001F')
                || (c >= '\u007F' && c <= '\u009F');
    }
    
    /**
     * @param c the character
     * @return a value representing the general category of the character
     */
    public static int getType(char c) {
throw new UnsupportedOperationException();
    }

    /**
     * @param digit the digit
     * @param radix the radix
     * @return the character that represents the digit according to the radix 
     */
    public static char forDigit(int digit, int radix) {
        return (radix < MIN_RADIX
                || radix > MAX_RADIX
                || digit < 0
                || digit >= radix)
            ? '\u0000'
            : DIGITS.charAt(digit);
    }
    
    /**
     * @param c the character
     * @return Unicode directionality property for the given character
     */
    public static byte getDirectionality(char c) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param c the character
     * @return true if the character is mirrored
     */
    public static boolean isMirrored(char c) {
throw new UnsupportedOperationException();
    }

    /**
     * Compare this character to the given character
     * @param c the character
     * @return an int value indicating the comparison result
     */
    public int compareTo(Character c) {
        char other = c.value;
        if (value < other) { return -1; }
        if (value > other) { return 1; }
        return 0;
    }
    
    /**
     * Compare this character to the given object
     * @param o the object to be compared with
     * @return an int value indicating the comparison result
     */
    public int compareTo(Object o) {
        return compareTo((Character) o);
    }

}
