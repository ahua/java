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

/**
 * Implementation of java.lang.StringBuffer based on Sun specification.
 */
public final class StringBuffer implements Serializable, CharSequence {
    
    /** The character buffer */
    private char[] buffer;

    /** The current size */
    private int size = 0;
    
    /** The last string produced */
    private String converted;

    /**
     * Create a StringBuffer
     */
    public StringBuffer() {
        buffer = new char[16];
    }

    /**
     * Create an empty buffer with the given capacity.
     * @param length the desired capacity
     * @throws NegativeArraySizeException if negative length specified
     */
    public StringBuffer(int length) throws NegativeArraySizeException {
        if (length < 0) {
            throw new NegativeArraySizeException();
        }
        buffer = new char[length];
    }

    /**
     * Create a buffer using the given string
     * @param str the string
     */
    public StringBuffer(String str) {
        size = str.length();
        buffer = new char[size + 16];
        for (int i = 0; i < size; i++) {
            buffer[i] = str.charAt(i);
        }
    }

    /**
     * @return the length of this buffer
     */
    public synchronized int length() {
        return size;
    }

    /**
     * @return the capacity of this buffer
     */
    public synchronized int capacity() {
        return (buffer != null) ? buffer.length : converted.length();
    }

    /**
     * Ensure that this buffer can handle the given capacity
     * @param minimumCapacity the desired minimum capacity
     */
    public synchronized void ensureCapacity(int minimumCapacity) {
        if (buffer == null) {
            // create new buffer if string conversion has been done
            int max = (converted.length() * 2) + 2;
            if (max < minimumCapacity) { max = minimumCapacity; }
            buffer = new char[max];
            for (int i = 0; i < size; i++) {
                buffer[i] = converted.charAt(i);
            }
        } else if (buffer.length < minimumCapacity) {
            // increase buffer size
            int max = (buffer.length * 2) + 2;
            if (max < minimumCapacity) { max = minimumCapacity; }
            char[] newBuffer = new char[max];
            for (int i = 0; i < size; i++) {
                newBuffer[i] = buffer[i];
            }
            buffer = newBuffer;
        }
    }

    /**
     * Set the length to the given value
     * @param newLength the new length
     */
    public synchronized void setLength(int newLength) {
        if (newLength < 0) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(newLength);
        if (size > newLength) { size = newLength; }
        while (size < newLength) {
            buffer[size++] = '\u0000';
        }
    }

    /**
     * @param index the index
     * @return the character at the given index
     */
    public synchronized char charAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return buffer[index];
    }

    /**
     * Copy characters from this buffer into the specified array
     * @param srcBegin the start (inclusive)
     * @param srcEnd the end (exclusive)
     * @param dst the buffer to copy into
     * @param dstBegin location to copy to
     */
    public synchronized void getChars(
            int srcBegin,
            int srcEnd,
            char[] dst,
            int dstBegin)
    {
        if (srcBegin < 0
                || dstBegin < 0
                || srcBegin > srcEnd
                || srcEnd > size
                || dstBegin + srcEnd - srcBegin > dst.length)
        {
            throw new IndexOutOfBoundsException();
        }
        int numChars = srcEnd - srcBegin;
        for (int i = 0; i < numChars; i++) {
            dst[dstBegin + i] = buffer[srcBegin + i];
        }
    }

    /**
     * Set the the char at the given index to the given value
     * @param index the location
     * @param ch the new value
     */
    public synchronized void setCharAt(int index, char ch) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        buffer[index] = ch;
    }

    /**
     * Append a string representation of the given object to this buffer.
     * @param obj the object
     * @return a reference to this object
     */
    public StringBuffer append(Object obj) {
        return append(String.valueOf(obj));
    }

    /**
     * Append the given string
     * @param str the string
     * @return a reference to this object
     */
    public StringBuffer append(String str) {
        return insert(size, str);
    }
    
    /**
     * Append the characters from the given string buffer
     * @param sb the buffer
     * @return a reference to this object
     */
    public StringBuffer append(StringBuffer sb) {
        return append(String.valueOf(sb));
    }

    /**
     * Append the characters of the given array to this buffer.
     * @param str the chars
     * @return a reference to this object
     */
    public StringBuffer append(char[] str) {
        return insert(size, str, 0, str.length);
    }

    /**
     * Append the specified characters of the given array to this buffer.
     * @param str the chars
     * @param offset the location of the first char
     * @param len the number of chars
     * @return a reference to this object
     */
    public StringBuffer append(char[] str, int offset, int len) {
        return insert(size, str, offset, len);
    }

    /**
     * Append a string representation of the given boolean to this buffer.
     * @param b the value
     * @return a reference to this object
     */
    public StringBuffer append(boolean b) {
        return append(String.valueOf(b));
    }

    /**
     * Append the given character
     * @param c the char
     * @return a reference to this object
     */
    public StringBuffer append(char c) {
        return append(String.valueOf(c));
    }

    /**
     * Append the given integer value
     * @param i the value
     * @return a reference to this object
     */
    public StringBuffer append(int i) {
        return append(String.valueOf(i));
    }

    /**
     * Append the given long value
     * @param l the value
     * @return a reference to this object
     */
    public StringBuffer append(long l) {
        return append(String.valueOf(l));
    }

    /**
     * Append the given float value
     * @param f the value
     * @return a reference to this object
     */
    public StringBuffer append(float f) {
        return append(String.valueOf(f));
    }

    /**
     * Append the given double value
     * @param d the value
     * @return a reference to this object
     */
    public StringBuffer append(double d) {
        return append(String.valueOf(d));
    }
    
    /**
     * Delete characters from the buffer
     * @param start first character (inclusive)
     * @param end last character (exclusive)
     * @return a reference to this object
     */
    public synchronized StringBuffer delete(int start, int end) {
        if (start < 0 || start > size || start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        if (start == end) { return this; }
        for (int from = end, to = start; from < size;) {
            buffer[to++] = buffer[from++];
        }
        size -= (end - start);
        return this;
    }
    
    /**
     * Delete a character from the buffer
     * @param index the location of the character to be deleted
     * @return a reference to this object
     */
    public synchronized StringBuffer deleteCharAt(int index) {
        if (index < 0 || index >= size) {
            throw new StringIndexOutOfBoundsException();
        }
        for (int to = index, from = index + 1; from < size;) {
            buffer[to++] = buffer[from++];
        }
        size--;
        return this;
    }
    
    /**
     * Replace a portion of this buffer with the characters from the
     * given string
     * @param start start of section to be replaced (inclusive)
     * @param end end of section to be replaced (exclusive)
     * @param str the replacement value
     * @return a reference to this object
     */
    public synchronized StringBuffer replace(int start, int end, String str) {
        // THIS IMPLEMENTATION COULD BE IMPROVED!!!
        delete(start, end);
        insert(start, str);
        return this;
    }
    
    /**
     * @param start index of first char (inclusive)
     * @return a substring of this buffer
     */
    public String substring(int start) {
        return substring(start, size);
    }
    
    /**
     * @param start index of first char (inclusive)
     * @param end index of last char (exclusive)
     * @return a subsequence of this buffer
     */
    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }
    
    /**
     * @param start index of first char (inclusive)
     * @param end index of last char (exclusive)
     * @return a substring of this buffer
     */
    public synchronized String substring(int start, int end) {
        if (start < 0 || end > size || start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        return new String(buffer, start, end - start);
    }
    
    /**
     * Insert some characters into this buffer
     * @param index the location
     * @param str the characters
     * @param offset index of first character
     * @param len number of characters
     * @return a reference to this object
     */
    public synchronized StringBuffer insert(
            int index,
            char[] str,
            int offset,
            int len)
    {
        if (index < 0 || offset < 0 || len < 0 || offset + len > str.length) {
            throw new StringIndexOutOfBoundsException();
        }
        ensureCapacity(size + len);
        size += len;
        
        // copy chars to end
        for (int i = size - 1; i >= index + len; i--) {
            buffer[i] = buffer[i - len];
        }
        
        // insert new chars
        for (int i = 0; i < len; i++) {
            buffer[index + i] = str[offset + i];
        }
        return this;
    }
    
    /**
     * Insert the string representation of the given object
     * @param offset the location
     * @param obj the value to insert
     * @return a reference to this object
     */
    public StringBuffer insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }
    
    /**
     * Insert the given string into the buffer at the given offset.
     * @param index the location
     * @param str the value to insert
     * @return a reference to this object
     */
    public synchronized StringBuffer insert(int index, String str) {
        if (str == null) { str = "null"; }
        if (index < 0 || index > size) {
            throw new StringIndexOutOfBoundsException();
        }
        int len = str.length();
        ensureCapacity(size + len);
        size += len;
        
        // copy chars to end
        for (int i = size - 1; i >= index + len; i--) {
            buffer[i] = buffer[i - len];
        }
        
        // insert new chars
        for (int i = 0; i < len; i++) {
            buffer[index + i] = str.charAt(i);
        }
        return this;
    }
    
    /**
     * Insert the given char array at the specified offset
     * @param offset the location
     * @param str the chars to insert
     * @return a reference to this object
     */
    public StringBuffer insert(int offset, char[] str) {
        return insert(offset, str, 0, str.length);
    }
    
    /**
     * Insert the string representation of the given boolean
     * @param offset the location
     * @param b the value to insert
     * @return a reference to this object
     */
    public StringBuffer insert(int offset, boolean b) {
        return insert(offset, String.valueOf(b));
    }
    
    /**
     * Insert the string representation of the given character
     * @param offset the location
     * @param c the char to insert
     * @return a reference to this object
     */
    public StringBuffer insert(int offset, char c) {
        return insert(offset, String.valueOf(c));
    }
    
    /**
     * Insert the string representation of the given integer
     * @param offset the location
     * @param i the value to insert
     * @return a reference to this object
     */
    public StringBuffer insert(int offset, int i) {
        return insert(offset, String.valueOf(i));
    }
    
    /**
     * Insert the string representation of the given long
     * @param offset the location
     * @param l the value to insert
     * @return a reference to this object
     */
    public StringBuffer insert(int offset, long l) {
        return insert(offset, String.valueOf(l));
    }
    
    /**
     * Insert the string representation of the given float
     * @param offset the location
     * @param f the value to insert
     * @return a reference to this object
     */
    public StringBuffer insert(int offset, float f) {
        return insert(offset, String.valueOf(f));
    }
    
    /**
     * Insert the string representation of the given double
     * @param offset the location
     * @param d the value to insert
     * @return a reference to this object
     */
    public StringBuffer insert(int offset, double d) {
        return insert(offset, String.valueOf(d));
    }
    
    /**
     * @param str the substring to search for
     * @return the index of the first occurrence of the specified substring
     */
    public int indexOf(String str) {
        return indexOf(str, 0);
    }
    
    /**
     * @param str the substring to search for
     * @param fromIndex search from here
     * @return the index of the first occurrence of the specified substring
     */
    public synchronized int indexOf(String str, int fromIndex) {
        if (fromIndex < 0) { fromIndex = 0; }
        int len = str.length();
        for (int i = fromIndex; i < size - len; i++) {
            if (matchesString(i, str)) { return i; }
        }
        return -1;
    }
    
    /**
     * @param str the substring to search for
     * @return the last index of the given substring
     */
    public int lastIndexOf(String str) {
        return lastIndexOf(str, size - 1);
    }
    
    /**
     * @param str the substring to search for
     * @param fromIndex search from here
     * @return the last index of the given substring searching backwards
     *         from the given index
     */
    public synchronized int lastIndexOf(String str, int fromIndex) {
        int len = str.length();
        int max = size - len - 1;
        if (fromIndex > max) { fromIndex = max; }
        for (int i = fromIndex; i >= 0; i--) {
            if (matchesString(i, str)) { return i; }
        }
        return -1;
    }
    
    /**
     * Reverse the order of the characters in this buffer.
     * @return a reference to this object.
     */
    public synchronized StringBuffer reverse() {
        for (int a = 0, b = size - 1; a < b; a++, b--) {
            char c = buffer[b];
            buffer[b] = buffer[a];
            buffer[a] = c;
        }
        return this;
    }

    /**
     * @return the contents of this buffer as a string
     */
    public synchronized String toString() {
        converted = new String();
        converted.init(buffer, 0, size);
        buffer = null;
        return converted;
    }

    /**
     * Return true if the specified region matches the given string
     */
    private boolean matchesString(int offset, String str) {
        for (int i = 0, n = str.length(); i < n; i++) {
            if (buffer[offset + i] != str.charAt(i)) {
                return false;
            }
        }
        return true;
    }

}
