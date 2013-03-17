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

import org.pjos.common.device.OperationNotAvailableException;
import org.pjos.common.device.Storage;

/**
 * Provides access to file and directory information for a specified path. 
 */
public abstract class Broker implements Storage {
    
    /** The path to the underlying resource */
    private final Path path;
    
    /**
     * Create a broker for the specified path
     * @param path the path for this broker
     * @throws IllegalArgumentException if the given path is not absolute 
     */
    protected Broker(Path path) {
        if (!path.isAbsolute()) {
            throw new IllegalArgumentException("Path not absolute: " + path);
        }
        this.path = path;
    }
    
    /**
     * @return the path
     */
    protected Path getPath() {
        return path;
    }
    
    /**
     * @return true if this broker represents a valid file or directory
     */
    public abstract boolean isValid();
    
    /**
     * @return true if this broker represents a directory
     */
    public abstract boolean isDirectory();
    
    /**
     * @return true if this broker represents a file
     */
    public abstract boolean isFile();
    
    /**
     * @return true if this broker represents a hidden resource
     */
    public abstract boolean isHidden();
    
    /**
     * This implementation always returns 0L.
     * @return the timestamp of the last modification, or zero if unavailable
     */
    public long getLastModified() {
        return 0L;
    }
    
    /**
     * Set the timestamp of the last modification.
     * This implementation does nothing.
     * @param timestamp the desired timestamp
     */
    public void setLastModified(long timestamp) {
        // do nothing
    }
    
    /**
     * This implementation always returns null
     * @return the list of names of files within this directory, or null
     *         if this broker doesn't represent a directory.
     */
    public String[] list() {
        return null;
    }
    
    /**
     * Delete this file or directory, this implementation returns false
     * @return true if successful
     */
    public boolean delete() {
        return false;
    }
    
    /**
     * Create a file within this directory.
     * This implementation always returns null.
     * @param name the name of the new file
     * @return the broker for the new file
     */
    public Broker createFile(String name) {
        return null;
    }
    
    /**
     * Create a directory within this directory.
     * This implementation always returns null.
     * @param name the name of the new directory
     * @return the broker for the new directory
     */
    public Broker createDirectory(String name) {
        return null;
    }
    
    /**
     * Guarantees that any system buffers have been synchronised with
     * the underlying device. This implementation does nothing.
     * @throws SyncFailedException if an error occurs
     */
    public void sync() throws SyncFailedException {
        // do nothing
    }

    /**
     * @return the path to this file or directory.
     */
    public final Path path() {
        return path;
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @return n/a
     * @throws IOException always
     */
    public long getSize() throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param size ignored
     * @throws IOException always
     */
    public void setSize(long size) throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param pos ignored
     * @return n/a
     * @throws IOException always
     */
    public int read(long pos) throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param pos ignored
     * @param buf ignored
     * @param off ignored
     * @param len ignored
     * @return n/a
     * @throws IOException always
     */
    public int read(long pos, byte[] buf, int off, int len) throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param pos ignored
     * @param value ignored
     * @throws IOException always
     */
    public void write(long pos, int value) throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param pos ignored
     * @param buf ignored
     * @param off ignored
     * @param len ignored
     * @throws IOException always
     */
    public void write(long pos, byte[] buf, int off, int len)
            throws IOException
    {
        throw new OperationNotAvailableException();
    }
    
}










