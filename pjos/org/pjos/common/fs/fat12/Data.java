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

import java.io.IOException;

/**
 * Represents a logical block of data in the filesystem.
 *
 * Not safe for use by multiple threads.
 */
abstract class Data {
    
    /**
     * Return the size (number of bytes) of this data.
     */
    abstract int getSize() throws IOException;
    
    /**
     * Set the unsigned 8-bit value at the specified offset. Implementations
     * are responsible for setting the value in the appropriate sector. An
     * IllegalArgumentException should be thrown if the specified offset value
     * is invalid.
     */
    abstract void set8(int value, int offset) throws IOException;
    
    /**
     * Return the unsigned 8-bit value at the specified offset. Implementations
     * are responsible for retrieving the value from the appropriate sector. An
     * IllegalArgumentException should be thrown if the specified offset value
     * is invalid.
     */
    abstract int get8(int offset) throws IOException;
    
    /**
     * Return the unsigned 16-bit value which is stored in little-endian format
     * at the specified offset.
     */
    int get16(int offset) throws IOException {
        return get8(offset)
            | (get8(offset + 1) << 8);
    }
    
    /**
     * Return the unsigned 24-bit value which is stored in little-endian format
     * at the specified offset.
     */
    int get24(int offset) throws IOException {
        return get8(offset)
            | (get8(offset + 1) << 8)
            | (get8(offset + 2) << 16);
    }

    /**
     * Return the signed 32-bit value which is stored in little-endian format
     * at the specified offset.
     */
    int get32(int offset) throws IOException {
        return get8(offset)
            | (get8(offset + 1) << 8)
            | (get8(offset + 2) << 16)
            | (get8(offset + 3) << 24);
    }

    /**
     * Set the unsigned 16-bit value at the specified
     * offset in little-endian byte order.
     */
    void set16(int value, int offset) throws IOException {
        set8(value & 0xff, offset);
        set8((value >> 8) & 0xff, offset + 1);
    }
    
    /**
     * Set the unsigned 24-bit value at the specified
     * offset in little-endian byte order.
     */
    void set24(int value, int offset) throws IOException {
        set8(value & 0xff, offset);
        set8((value >> 8) & 0xff, offset + 1);
        set8((value >> 16) & 0xff, offset + 2);
    }
    
    /**
     * Set the signed 32-bit value at the specified offset
     * in little-endian byte order.
     */
    void set32(int value, int offset) throws IOException {
        set8(value & 0xff, offset);
        set8((value >> 8) & 0xff, offset + 1);
        set8((value >> 16) & 0xff, offset + 2);
        set8((value >> 24) & 0xff, offset + 3);
    }
    
    /**
     * Write the given string to the specified offset. The low 8 bits of each
     * UNICODE character in the string are written to each byte.
     */
    void set(String value, int offset) throws IOException {
        for (int i = 0, n = value.length(); i < n; i++) {
            int c = (int) value.charAt(i);
            set8(c & 0xff, offset + i);
        }
    }
    
}










