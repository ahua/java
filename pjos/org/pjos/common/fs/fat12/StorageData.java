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
 * An implementation of Data which writes directly
 * to a storage object.
 *
 * Not safe for use by multiple threads.
 */
class StorageData extends Data {
    
    /** The storage */
    private Storage storage;

    /** The size */
    private int size;

    /**
     * Create a storage data object.
     * @param storage the underlying storage
     * @throws IOException if an error occurs
     */
    StorageData(Storage storage) throws IOException {
        this.storage = storage;
        long total = storage.getSize();
        if (total > Integer.MAX_VALUE) {
            throw new IOException("Storage too large");
        }
        size = (int) total;
    }
    
    /**
     * @return the size (number of bytes) of this data.
     */
    int getSize() {
        return size;
    }
    
    /**
     * Set the unsigned 8-bit value at the specified offset.
     * @param value the unsigned 8-bit value
     * @param offset the offset
     * @throws IOException if an error occurs
     */
    void set8(int value, int offset) throws IOException {
        storage.write(offset, value);
    }
    
    /**
     * @param offset the offset
     * @return the unsigned 8-bit value at the specified offset.
     * @throws IOException if an error occurs
     */
    int get8(int offset) throws IOException {
        return storage.read(offset);
    }
    
}










