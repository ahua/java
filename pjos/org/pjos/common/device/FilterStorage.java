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
 * An implementation of storage that forwards requests to another
 * storage object.
 *
 * Safe for use by multiple threads.
 */
public final class FilterStorage implements Storage {
    
    /** The underlying storage */
    private Storage storage;
    
    /**
     * Create a filter storage object to forward
     * requests to the given storage.
     * @param storage the storage
     */
    public FilterStorage(Storage storage) {
        if (storage == null) { throw new NullPointerException(); }
        this.storage = storage;
    }
    
    /**
     * Change the underlying storage
     * @param storage the new storage
     */
    public synchronized void setStorage(Storage storage) {
        if (storage == null) { throw new NullPointerException(); }
        this.storage = storage;
    }
    
    /**
     * @return the file size in bytes
     * @throws IOException if an error occurs
     */
    public synchronized long getSize() throws IOException {
        return storage.getSize();
    }
    
    /**
     * Set the size in bytes
     * @param size the new size
     * @throws IOException if an error occurs
     */
    public synchronized void setSize(long size) throws IOException {
        storage.setSize(size);
    }
    
    /**
     * Read an unsigned byte value from the specified position.
     * @param pos the file position to read from
     * @return the unsigned byte value read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    public synchronized int read(long pos) throws IOException {
        return storage.read(pos);
    }
    
    /**
     * Read some data from the specified position.
     * @param pos the file position to read from
     * @param buf the buffer to receive the bytes read
     * @param off the buffer location to write the bytes to
     * @param len the number of bytes
     * @return the number of bytes read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    public synchronized int read(long pos, byte[] buf, int off, int len)
            throws IOException
    {
        return storage.read(pos, buf, off, len);
    }
    
    /**
     * Write an unsigned byte value to the specified position
     * @param pos the file position to write to
     * @param value the unsigned byte value to write
     * @throws IOException if an error occurs
     */
    public synchronized void write(long pos, int value) throws IOException {
        storage.write(pos, value);
    }
    
    /**
     * Write some data to the specified position
     * @param pos the file position to write to
     * @param buf contains the bytes to be written
     * @param off offset of first byte
     * @param len number of bytes
     * @throws IOException if an error occurs
     */
    public synchronized void write(long pos, byte[] buf, int off, int len)
            throws IOException
    {
        storage.write(pos, buf, off, len);
    }
    
}










