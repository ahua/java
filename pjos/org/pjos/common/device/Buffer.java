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
package org.pjos.common.device;

import java.io.IOException;

/**
 * Buffers a subset of the data from a storage object
 * in a byte array and provides methods to access the
 * data in different formats.
 *
 * Not safe for access by multiple threads.
 *
 * The methods in this class will throw ArrayIndexOutOfBoundsExceptions
 * if invalid offsets are supplied.
 */
public final class Buffer {
    
    /** The data */
    private byte[] data;
    
    /** The location of the data in the storage */
    private long pos;
    
    /** The underlying storage */
    private Storage storage;
    
    /**
     * Create a buffer
     * @param storage the underlying storage
     * @param pos location in underlying storage
     * @param size number of bytes to buffer
     * @param IOException if an error occurs
     */
    public Buffer(Storage storage, long pos, int size) throws IOException {
        if (pos < 0 || size < 0 || pos + size > storage.getSize()) {
            throw new IllegalArgumentException();
        }
        data = new byte[size];
        this.pos = pos;
        this.storage = storage;
    }
    
    /**
     * Load contents from storage
     * @if an error occurs
     */
    public void load() throws IOException {
        storage.read(pos, data, 0, data.length);
    }
    
    /**
     * Save contents to storage
     * @if an error occurs
     */
    public void save() throws IOException {
        storage.write(pos, data, 0, data.length);
    }
    
    /**
     * @return the size of this buffer
     */
    public int getSize() {
        return data.length;
    }

    /**
     * @return the unsigned 8-bit value at the specified offset
     * @param offset the location within the buffer
     */
    public int getU8(int offset) {
        return data[offset] & 0xff;
    }
    
    /**
     * @return the signed 8-bit value at the specified offset
     * @param offset the location within the buffer
     */
    public byte getS8(int offset) {
        return data[offset];
    }
    
    /**
     * Set the unsigned 8-bit value at the specified offset
     * @param value the unsigned 8-bit value
     * @param offset the location within the buffer
     */
    public void setU8(int offset, int value) {
        data[offset] = (byte) value;
    }
    
    /**
     * Set the signed 8-bit value at the specified offset
     * @param value the signed 8-bit value
     * @param offset the location within the buffer
     */
    public void setS8(int offset, byte value) {
        data[offset] = value;
    }
    
    /**
     * @return the unsigned 16-bit little-endian value at the specified offset
     * @param offset the location within the buffer
     */
    public int getU16L(int offset) {
        return getU8(offset)
                | getU8(offset + 1) << 8;
    }
    
    /**
     * @return the unsigned 16-bit big-endian value at the specified offset
     * @param offset the location within the buffer
     */
    public int getU16B(int offset) {
        return getU8(offset + 1)
                | getU8(offset) << 8;
    }
    
    /**
     * @return the signed 16-bit little-endian value at the specified offset
     * @param offset the location within the buffer
     */
    public short getS16L(int offset) {
        return (short) getU16L(offset);
    }
    
    /**
     * @return the signed 16-bit big-endian value at the specified offset
     * @param offset the location within the buffer
     */
    public short getS16B(int offset) {
        return (short) getU16B(offset);
    }
    
    /**
     * Set the unsigned 16-bit little-endian value at the specified offset
     * @param offset the location within the buffer
     * @param value the unsigned 16-bit value
     */
    public void setU16L(int offset, int value) {
        data[offset] = (byte) value;
        data[offset + 1] = (byte) (value >> 8);
    }
    
    /**
     * Set the unsigned 16-bit big-endian value at the specified offset
     * @param offset the location within the buffer
     * @param value the unsigned 16-bit value
     */
    public void setU16B(int offset, int value) {
        data[offset + 1] = (byte) value;
        data[offset] = (byte) (value >> 8);
    }
    
    /**
     * Set the signed 16-bit little-endian value at the specified offset
     * @param offset the location within the buffer
     * @param value the signed 16-bit value
     */
    public void setS16L(int offset, short value) {
        setU16L(offset, value);
    }
    
    /**
     * Set the signed 16-bit big-endian value at the specified offset
     * @param offset the location within the buffer
     * @param value the signed 16-bit value
     */
    public void setS16B(int offset, short value) {
        setU16B(offset, value);
    }
    
    /**
     * @return the unsigned 32-bit little-endian value at the specified offset
     * @param offset the location within the buffer
     */
    public long getU32L(int offset) {
        return getS32L(offset) & 0x00000000ffffffffL;
    }
    
    /**
     * @return the unsigned 32-bit big-endian value at the specified offset
     * @param offset the location within the buffer
     */
    public long getU32B(int offset) {
        return getS32B(offset) & 0x00000000ffffffffL;
    }
    
    /**
     * @return the signed 32-bit little-endian value at the specified offset
     * @param offset the location within the buffer
     */
    public int getS32L(int offset) {
        return getU8(offset)
                | getU8(offset + 1) << 8
                | getU8(offset + 2) << 16
                | getU8(offset + 3) << 32;
    }
    
    /**
     * @return the signed 32-bit big-endian value at the specified offset
     * @param offset the location within the buffer
     */
    public int getS32B(int offset) {
        return getU8(offset + 3)
                | getU8(offset + 2) << 8
                | getU8(offset + 1) << 16
                | getU8(offset) << 32;
    }
    
    /**
     * Set the unsigned 32-bit little-endian value at the specified offset
     * @param offset the location within the buffer
     * @param value the unsigned 32-bit value
     */
    public void setU32L(int offset, long value) {
        setS32L(offset, (int) value);
    }
    
    /**
     * Set the unsigned 32-bit big-endian value at the specified offset
     * @param offset the location within the buffer
     * @param value the unsigned 32-bit value
     */
    public void setU32B(int offset, long value) {
        setS32B(offset, (int) value);
    }
    
    /**
     * Set the signed 32-bit little-endian value at the specified offset
     * @param offset the location within the buffer
     * @param value the signed 32-bit value
     */
    public void setS32L(int offset, int value) {
        data[offset] = (byte) value;
        data[offset + 1] = (byte) (value >> 8);
        data[offset + 2] = (byte) (value >> 16);
        data[offset + 3] = (byte) (value >> 24);
    }
    
    /**
     * Set the signed 32-bit big-endian value at the specified offset
     * @param offset the location within the buffer
     * @param value the signed 32-bit value
     */
    public void setS32B(int offset, int value) {
        data[offset + 3] = (byte) value;
        data[offset + 2] = (byte) (value >> 8);
        data[offset + 1] = (byte) (value >> 16);
        data[offset] = (byte) (value >> 24);
    }
    
}










