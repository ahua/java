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

import org.pjos.common.device.Storage;

/**
 * Represents the data from one sector cached in memory.
 *
 * Not safe for use by multiple threads.
 */
class Sector extends Data {
    
    /** The underlying storage */
    private Storage storage;
    
    /** The offset of this sector's data */
    private int offset;

    /** The size of this sector in bytes */
    private int size;

    /** The present flag */
    private boolean present = false;
    
    /** The changed flag */
    private boolean changed = false;
    
    /** The data */
    private byte[] data = null;
    
    /**
     * Create a sector representing the data at the specified offset.
     * @param storage the storage
     * @param offset the offset
     * @param size the size
     */
    Sector(Storage storage, int offset, int size) {
        this.storage = storage;
        this.offset = offset;
        this.size = size;
    }
    
    /**
     * Return true if the sector is present, false otherwise
     */
    boolean present() {
        return present;
    }
    
    /**
     * @return true if the data has been changed since the
     *         last load or store, false otherwise
     */
    boolean changed() {
        return changed;
    }
    
    /**
     * @return the size (number of bytes) of this data.
     */
    int getSize() {
        return size;
    }

    /**
     * Set the unsigned 8-bit value at the specified offset
     * @param value the unsigned 8-bit value
     * @param offset the offset
     * @throws IOException if an error occurs
     */
    void set8(int value, int offset) throws IOException {
        if (offset < 0 || offset >= size) {
            throw new IllegalArgumentException("Invalid offset");
        }
        if (!present || data == null) {
            throw new IllegalStateException("Sector not present");
        }
        data[offset] = (byte) value;
        changed = true;
    }
    
    /**
     * @param offset the offset
     * @return the unsigned 8-bit value at the specified offset
     */
    int get8(int offset) throws IOException {
        if (offset < 0 || offset >= size) {
            throw new IllegalArgumentException("Invalid offset");
        }
        if (!present || data == null) {
            throw new IllegalStateException("Sector not present");
        }
        return data[offset] & 0xff;
    }
    
    /**
     * Load sector data from the storage.
     */
    void load() throws IOException {
        try {
            // create data array if necessary
            if (data == null) { data = new byte[size]; }
            
            // fill data array
            int count = storage.read(offset, data, 0, size);
            if (count != size) {
                throw new IOException("Unable to read sector at " + offset);
            }
            
            // set flags for successful load
            present = true;
            changed = false;
        } catch (IOException e) {
            // set flags for error before passing on exception
            present = false;
            changed = false;
            throw e;
        }
    }
    
    /**
     * Store sector data to the storage.
     * @throws IOException if an error occurs
     */
    void store() throws IOException {
        // this method should not be called
        // unless there is valid data to be saved
        if (!present || !changed || data == null) {
            throw new IllegalStateException();
        }
        
        // store the data
        storage.write(offset, data, 0, size);
        
        // clear changed flag after successful store
        changed = false;
    }
    
    /**
     * Clear the data in this sector
     */
    void clear() {
        // make sure data is array of zeroed bytes
        if (data == null) {
            data = new byte[size];
        } else {
            for (int i = 0; i < size; i++) { data[i] = 0; }
        }
        
        // set flags
        present = true;
        changed = true;
    }
    
    /**
     * Discard the data in this sector
     */
    void discard() {
        present = false;
        changed = false;
    }
    
}










