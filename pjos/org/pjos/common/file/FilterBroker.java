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
package org.pjos.common.file;

import java.io.IOException;
import java.io.SyncFailedException;

/**
 * A filter broker is used to filter method calls to another broker.
 * This implementation simply passes all calls on.
 */
public class FilterBroker extends Broker {
    
    /** The broker object being filtered */
    protected final Broker broker;
    
    /**
     * Create a filter broker wrapped around the specified broker
     * @param path the path
     * @param broker the broker to be wrapped
     */
    protected FilterBroker(Path path, Broker broker) {
        super(path);
        this.broker = broker;
    }
    
    /**
     * @return true if this broker represents a valid resource
     */
    public boolean isValid() {
        return broker.isValid();
    }
    
    /**
     * @return true if this broker represents a directory
     */
    public boolean isDirectory() {
        return broker.isDirectory();
    }
    
    /**
     * @return true if this broker represents a file
     */
    public boolean isFile() {
        return broker.isFile();
    }
    
    /**
     * @return true if this broker represents a hidden resource
     */
    public boolean isHidden() {
        return broker.isHidden();
    }
    
    /**
     * @return the timestamp of the last modification or zero if unavailable
     */
    public long getLastModified() {
        return broker.getLastModified();
    }
    
    /**
     * Set the timestamp of the last modification
     * @param timestamp the timestamp
     */
    public void setLastModified(long timestamp) {
        broker.setLastModified(timestamp);
    }
    
    /**
     * @return the list of names of files within this directory
     */
    public String[] list() {
        return broker.list();
    }
    
    /**
     * Delete this file or directory
     * @return true if successful
     */
    public boolean delete() {
        return broker.delete();
    }
    
    /**
     * Create a new file
     * @param name the name to give the new file
     * @return the broker for the new file
     */
    public Broker createFile(String name) {
        return broker.createFile(name);
    }
    
    /**
     * Create a new directory
     * @param name the name to give the new directory
     * @return the broker for the new directory
     */
    public Broker createDirectory(String name) {
        return broker.createDirectory(name);
    }
    
    /**
     * Sync any system buffers
     * @throws SyncFailedException if an error occurs
     */
    public void sync() throws SyncFailedException {
        broker.sync();
    }

    /**
     * @return the file size in bytes
     * @throws IOException if an error occurs
     */
    public long getSize() throws IOException {
        return broker.getSize();
    }
    
    /**
     * Set the size of this file in bytes
     * @param size the new size
     * @throws IOException if an error occurs
     */
    public void setSize(long size) throws IOException {
        broker.setSize(size);
    }
    
    /**
     * Read an unsigned byte value from the specified position.
     * @param pos the file position to read from
     * @return the unsigned byte value read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    public int read(long pos) throws IOException {
        return broker.read(pos);
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
        return broker.read(pos, buf, off, len);
    }
    
    /**
     * Write an unsigned byte value to the specified position
     * @param pos the file position to write to
     * @param value the unsigned byte value to write
     * @throws IOException if an error occurs
     */
    public void write(long pos, int value) throws IOException {
        broker.write(pos, value);
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
        broker.write(pos, buf, off, len);
    }

}










