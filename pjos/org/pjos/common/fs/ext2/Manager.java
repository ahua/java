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
package org.pjos.common.fs.ext2;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

import org.pjos.common.device.Storage;

import org.pjos.common.file.Path;

/**
 * This manager provides filesystem access to a storage device
 * using the ext2 file system format.
 *
 * The manager object provides synchronization to protect the
 * other classes in this package which are not safe for use
 * by multiple threads.
 *
 * Safe for use by multiple threads.
 */
class Manager {

    /** Models the physical data on the floppy */
    private Model model;
    
    /** The root directory */
    private Directory root = null;
    
    /** The current path */
    private Path path = null;
    
    /** The file matching the current path */
    private File file = null;
    
    /** The directory matching the current path */
    private Directory directory = null;
    
    /**
     * Create a manager for the given storage device
     */
    Manager(Storage storage) {
        model = new Model(storage);
    }
    
    /**
     * Reset internal state and discard any cached data
     * by replacing the model.
     */
    synchronized void reset() {
        root = null;
        path = null;
        file = null;
        directory = null;
        model.reset();
    }
    
    /**
     * @return the volume label, or null if none is found
     * @throws IOException if an error occurs
     */
    synchronized String getLabel() throws IOException {
        return model.getLabel();
    }
    
    /**
     * Set the volume label. The given string must be 11 characters in length.
     * @param label the new label
     * @throws IOException if an error occurs
     */
    synchronized void setLabel(String label) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Delete the file or directory matching the given path
     * @param path the file path
     * @return true if successful
     */
    synchronized boolean delete(Path path) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Create a new file in this directory with the given name
     * @param path the directory path
     * @param longName the file name
     * @return the path of the new file, or null if unsuccessful
     */
    synchronized Path createFile(Path path, String longName) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Create a new directory in this directory with the given name
     * @param path the directory path
     * @param longName the file name
     * @return the path of the new directory, or null if unsuccessful
     */
    synchronized Path createDirectory(Path path, String longName) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @param path the file path
     * @return true if the specified path represents a valid file or directory
     */
    synchronized boolean isValid(Path path) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @param path the file path
     * @return true if the specified path represents a valid directory
     */
    synchronized boolean isDirectory(Path path) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @param path the file path
     * @return true if the specified path represents a valid file
     */
    synchronized boolean isFile(Path path) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * No files or directories are hidden by this driver.
     * @return always return false
     */
    synchronized boolean isHidden(Path path) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @param path the directory path
     * @return an array containing the filenames in a directory, or null
     *         if no directory is found
     */
    synchronized String[] list(Path path) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @param path the file path
     * @return the last modification timestamp from a file, or zero
     *         if not available
     */
    synchronized long getLastModified(Path path) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Set the timestamp of the last modification
     * @param path the file path
     * @param timestamp the new timestamp
     */
    synchronized void setLastModified(Path path, long timestamp) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @return the size of the file matching the given path, or
     *         zero if the path resolves to a directory.
     */
    synchronized int getSize(Path path) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Set the size of a file.
     * @param path the file path
     * @param size the new size
     * @throws IOException if an error occurs
     */
    synchronized void setSize(Path path, int size) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Read an unsigned byte value from the specified position.
     * @param path the file path
     * @param pos the file position to read from
     * @return the unsigned byte value read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    synchronized int read(Path path, int pos) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /** 
     * Read data from a file
     * @param path the file path
     * @param pos the file position to read from
     * @param buf the buffer to receive the bytes read
     * @param off the buffer location to write the bytes to
     * @param len the number of bytes
     * @return the number of bytes read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    synchronized int read(Path path, int pos, byte[] buf, int off, int len)
            throws IOException
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Write an unsigned byte value to the specified file
     * @param path the file path
     * @param pos the file position to write to
     * @param value the unsigned byte value to write
     * @throws IOException if an error occurs
     */
    synchronized void write(Path path, int pos, int value) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Write data to a file
     * @param path the file path
     * @param pos the file position to write to
     * @param buf contains the bytes to be written
     * @param off offset of first byte
     * @param len number of bytes
     * @throws IOException if an error occurs
     */
    synchronized void write(Path path, int pos, byte[] buf, int off, int len)
            throws IOException
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Find the directory or file matching the given path.
     * @param p the path
     * @return the canonical path
     * @throws IOException if an error occurs
     */
    synchronized Path resolve(Path p) throws IOException {
        throw new UnsupportedOperationException();
    }

}










