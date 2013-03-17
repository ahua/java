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

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

import org.pjos.common.device.Storage;

import org.pjos.common.file.Path;

/**
 * A manager provides filesystem access to a storage device
 * using the FAT12 file system format. This driver is only
 * designed to work with 1.4MB floppies, other sizes are not
 * supported.
 *
 * The manager object provides synchronization to protect the
 * other classes in this package which are not safe for use
 * by multiple threads.
 *
 * Safe for use by multiple threads.
 */
class Manager {

    /** The storage */
    private Storage storage;

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
        this.storage = storage;
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
        model = null;
    }
    
    /**
     * @return the volume label, or null if none is found
     * @throws IOException if an error occurs
     */
    synchronized String getLabel() throws IOException {
        if (root == null) { root = createRoot(); }
        Entry entry = root.getEntry();
        while (entry != null) {
            if (entry.isVolumeLabel()) {
                return entry.getRawShortName();
            }
            entry = entry.next();
        }
        throw new FileNotFoundException();
    }
    
    /**
     * Set the volume label. The given string must be 11 characters in length.
     * @param label the new label
     * @throws IOException if an error occurs
     */
    synchronized void setLabel(String label) throws IOException {
        if (label.length() != 11) { throw new IllegalArgumentException(); }

        // try to find an existing volume label entry
        if (root == null) { root = createRoot(); }
        Entry rootEntry = root.getEntry();
        Entry entry = rootEntry;
        while (entry != null) {
            if (entry.isVolumeLabel()) {
                entry.setRawShortName(label);
                model.getReservedData().set(label, 43);
                model.store();
                return;
            }
            entry = entry.next();
        }
        
        // need to create new volume label entry
        rootEntry.addVolumeLabelEntry(label);
        model.getReservedData().set(label, 43);
        model.store();
    }
    
    /**
     * Delete the file or directory matching the given path
     * @param path the file path
     * @return true if successful
     */
    synchronized boolean delete(Path path) {
        try {
            resolve(path);
            if (directory != null) {
                directory.delete();
            } else if (file != null) {
                file.delete();
            }
            model.store(); // save changes
            return true;
        } catch (IOException e) {
            reset(); // if an error occurs, make sure changes will not be saved
            return false;
        }
    }
    
    /**
     * Create a new file in this directory with the given name
     * @param path the directory path
     * @param longName the file name
     * @return the path of the new file, or null if unsuccessful
     */
    synchronized Path createFile(Path path, String longName) {
        try {
            resolveDirectory(path);
            directory.createFile(longName);
            model.store();
            return this.path.append(Path.create(longName));
        } catch (IOException e) {
            reset();
            return null;
        }
    }
    
    /**
     * Create a new directory in this directory with the given name
     * @param path the directory path
     * @param longName the file name
     * @return the path of the new directory, or null if unsuccessful
     */
    synchronized Path createDirectory(Path path, String longName) {
        try {
            resolveDirectory(path);
            directory.createDirectory(longName);
            model.store();
            return this.path.append(Path.create(longName));
        } catch (IOException e) {
            reset();
            return null;
        }
    }
    
    /**
     * @param path the file path
     * @return true if the specified path represents a valid file or directory
     */
    synchronized boolean isValid(Path path) {
        try {
            resolve(path);
            return (file != null || directory != null);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * @param path the file path
     * @return true if the specified path represents a valid directory
     */
    synchronized boolean isDirectory(Path path) {
        try {
            resolve(path);
            return (directory != null);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * @param path the file path
     * @return true if the specified path represents a valid file
     */
    synchronized boolean isFile(Path path) {
        try {
            resolve(path);
            return (file != null);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * No files or directories are hidden by this driver.
     * @return always return false
     */
    synchronized boolean isHidden(Path path) {
        return false;
    }
    
    /**
     * @param path the directory path
     * @return an array containing the filenames in a directory, or null
     *         if no directory is found
     */
    synchronized String[] list(Path path) {
        try {
            resolve(path);
            return (directory != null) ? directory.list() : null;
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * @param path the file path
     * @return the last modification timestamp from a file, or zero
     *         if not available
     */
    synchronized long getLastModified(Path path) {
        try {
            resolve(path);
            return (file != null) ? file.getLastModified() : 0L;
        } catch (IOException e) {
            return 0L;
        }
    }
    
    /**
     * Set the timestamp of the last modification
     * @param path the file path
     * @param timestamp the new timestamp
     */
    synchronized void setLastModified(Path path, long timestamp) {
        if (timestamp < 0) { throw new IllegalArgumentException(); }
        try {
            resolve(path);
            if (file != null) {
                file.setLastModified(timestamp);
                model.store();
            }
        } catch (IOException e) {
            // just ignore
        }
    }
    
    /**
     * @return the size of the file matching the given path, or
     *         zero if the path resolves to a directory.
     */
    synchronized int getSize(Path path) throws IOException {
        resolveFile(path);
        return file.getSize();
    }
    
    /**
     * Set the size of a file.
     * @param path the file path
     * @param size the new size
     * @throws IOException if an error occurs
     */
    synchronized void setSize(Path path, int size) throws IOException {
        resolveFile(path);
        file.setSize(size);
        file.setLastModified(System.currentTimeMillis());
        model.store();
    }
    
    /**
     * Read an unsigned byte value from the specified position.
     * @param path the file path
     * @param pos the file position to read from
     * @return the unsigned byte value read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    synchronized int read(Path path, int pos) throws IOException {
        resolveFile(path);
        return file.read(pos);
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
        resolveFile(path);
        int count = 0;
        int from = pos;
        int to = off;
        int k = file.read(from);
        while (count < len && k != -1) {
            buf[to] = (byte) (k & 0xff);
            count++;
            from++;
            to++;
            k = file.read(from);
        }
        return (count > 0) ? count : -1;
    }
    
    /**
     * Write an unsigned byte value to the specified file
     * @param path the file path
     * @param pos the file position to write to
     * @param value the unsigned byte value to write
     * @throws IOException if an error occurs
     */
    synchronized void write(Path path, int pos, int value) throws IOException {
        resolveFile(path);
        if (pos > file.getSize()) { file.setSize(pos + 1); }
        file.write(pos, value);
        file.setLastModified(System.currentTimeMillis());
        model.store();
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
        resolveFile(path);
        if (pos + len > file.getSize()) {
            file.setSize(pos + len);
        }
        int to = pos;
        int from = off;
        int max = off + len;
        while (from < max) {
            file.write(to, buf[from]);
            to++;
            from++;
        }
        file.setLastModified(System.currentTimeMillis());
        model.store();
    }

    /**
     * Resolve the given path to a file
     */
    private void resolveFile(Path p) throws IOException {
        resolve(p);
        if (file == null) { throw new FileNotFoundException(); }
    }
    
    /**
     * Resolve the given path to a directory
     */
    private void resolveDirectory(Path p) throws IOException {
        resolve(p);
        if (directory == null) { throw new FileNotFoundException(); }
    }
    
    /**
     * Find the directory or file matching the given path.
     * @param p the path
     * @return the canonical path
     * @throws IOException if an error occurs
     */
    synchronized Path resolve(Path p) throws IOException {
        // can't resolve null path
        if (p == null) { throw new NullPointerException(); }

        // if the given path is already resolved, no need to do anything
        if (p.equals(path)) { return path; }
        
        // can only resolve absolute paths
        if (!p.isAbsolute()) {
            throw new IllegalArgumentException("Path not absolute: " + p);
        }

        // unresolve in case an exception is thrown
        path = null;
        file = null;
        directory = null;

        // Load root directory if necessary
        if (root == null) { root = createRoot(); }
        
        if (p == Path.ROOT) {
            // resolve to root directory...
            directory = root;
            path = Path.ROOT;
        } else {
            // ...otherwise search for the desired file or directory
            search(p);
        }
        
        // Return the canonical path
        return path;
    }

    /**
     * Perform a recursive search for the file or directory with
     * the given path and resolve it.
     */
    private void search(Path p) throws IOException {
        // recurse through folders starting from root
        Directory folder = root;
        List names = new LinkedList();
        for (int i = 0, n = p.size() - 1; i < n; i++) {
            Entry entry = folder.resolve(p.get(i));
            if (!entry.isDirectory()) {
                throw new IOException("Invalid path");
            }
            names.add(entry.getName());
            folder = entry.getDirectory();
        }
        
        // match file or directory
        Entry resolved = folder.resolve(p.last());
        if (resolved.isDirectory()) {
            directory = resolved.getDirectory();
        } else {
            file = resolved.getFile();
        }
        names.add(resolved.getName());
        
        // set the path to the resolved path (canonical path)
        path = Path.create(names, true);
    }
    
    /**
     * Create the root directory object
     */
    private Directory createRoot() throws IOException {
        // create model if necessary
        if (model == null) { model = new Model(storage); }
        return new BlockDirectory(model.getRootData(), model);
    }
    
}










