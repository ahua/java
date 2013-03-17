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

import org.pjos.common.file.Broker;
import org.pjos.common.file.Path;

/**
 * Provides access to the file or directory located at the
 * specified path. Multiple brokers may be created for the
 * same path.
 *
 * Safe for use by multiple threads.
 */
class FloppyBroker extends Broker {
    
    /** The manager */
    private Manager manager;
    
    /**
     * Create a floppy broker for the given path to use the given manager.
     * @param manager the manager
     * @param the path to the resource
     * @throws IOException if an error occurs
     */
    FloppyBroker(Manager manager, Path path) throws IOException {
        super(manager.resolve(path));
        this.manager = manager;
    }

    /**
     * @return true if this broker represents a valid file or directory
     */
    public boolean isValid() {
        return manager.isValid(getPath());
    }
    
    /**
     * @return true if this broker represents a valid file
     */
    public boolean isFile() {
        return manager.isFile(getPath());
    }
    
    /**
     * @return true if this broker represents a valid directory
     */
    public boolean isDirectory() {
        return manager.isDirectory(getPath());
    }
    
    /**
     * @return true if this broker represents a hidden resource
     */
    public boolean isHidden() {
        return manager.isHidden(getPath());
    }
    
    /**
     * @return the timestamp of the last modification or zero if unavailable.
     */
    public long getLastModified() {
        return manager.getLastModified(getPath());
    }
    
    /**
     * Set the timestamp of the last modification
     * @param timestamp the new timestamp
     */
    public void setLastModified(long timestamp) {
        manager.setLastModified(getPath(), timestamp);
    }

    /**
     * @return the list of names of files within this directory
     */
    public String[] list() {
        return manager.list(getPath());
    }
    
    /**
     * Delete this file or directory
     * @return true if successful
     */
    public boolean delete() {
        return manager.delete(getPath());
    }
    
    /**
     * Create a new file within this directory
     * @name the name for the new file
     * @return the broker for the new file
     */
    public Broker createFile(String name) {
        try {
            Path created = manager.createFile(getPath(), name);
            return (created != null)
                    ? new FloppyBroker(manager, created)
                    : null;
        } catch (IOException e) {
            return null;
        }
    }
    
    /** 
     * Create a new directory within this directory
     * @name the name for the new directory
     * @return the broker for the new directory
     */
    public Broker createDirectory(String name) {
        try {
            Path created = manager.createDirectory(getPath(), name);
            return (created != null)
                    ? new FloppyBroker(manager, created)
                    : null;
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * No buffers to sync
     */
    public void sync() {
        // nothing to do here
    }

    /**
     * @return the storage size in bytes
     * @throws IOException if an error occurs
     */
    public long getSize() throws IOException {
        return (long) manager.getSize(getPath());
    }
    
    /**
     * Set the size of this storage in bytes
     * @param size the new size
     * @throws IOException if an error occurs
     */
    public void setSize(long size) throws IOException {
        if (size < 0 || size > Integer.MAX_VALUE) {
            throw new IOException("Invalid size");
        }
        manager.setSize(getPath(), (int) size);
    }
    
    /**
     * Read an unsigned byte value from the specified position.
     * @param pos the file position to read from
     * @return the unsigned byte value read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    public int read(long pos) throws IOException {
        int p = (int) pos;
        if (p < 0) {
            throw new IOException("Invalid position");
        }
        return manager.read(getPath(), (int) pos);
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
    public int read(long pos, byte[] buf, int off, int len) throws IOException {
        if (pos < 0 || pos + len > Integer.MAX_VALUE) {
            throw new IOException("Invalid position");
        }
        return manager.read(getPath(), (int) pos, buf, off, len);
    }
    
    /**
     * Write an unsigned byte value to the specified position
     * @param pos the file position to write to
     * @param value the unsigned byte value to write
     * @throws IOException if an error occurs
     */
    public void write(long pos, int value) throws IOException {
        if (pos < 0 || pos >= Integer.MAX_VALUE) {
            throw new IOException("Invalid position");
        }
        manager.write(getPath(), (int) pos, value);
    }
    
    /**
     * Write some data to the specified position
     * @param pos the file position to write to
     * @param buf contains the bytes to be written
     * @param off offset of first byte
     * @param len number of bytes
     * @throws IOException if an error occurs
     */
    public void write(long pos, byte[] buf, int off, int len)
            throws IOException
    {
        if (pos < 0 || pos + len > Integer.MAX_VALUE) {
            throw new IOException("Invalid position");
        }
        manager.write(getPath(), (int) pos, buf, off, len);
    }

}



