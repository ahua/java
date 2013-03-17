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

import java.util.List;
import java.util.Random;

/**
 * Represents a directory in the FAT12 filesystem.
 *
 * Not safe for use by multiple threads.
 */
abstract class Directory {
    
    /** The model */
    private Model model;

    /** The data */
    protected Data data;
    
    /** The root of the entry list */
    protected Entry entry;
    
    /**
     * Create a new directory object using the given data
     */
    Directory(Model model, Data data) throws IOException {
        this.model = model;
        this.data = data;
        entry = Entry.read(data, 0, model);
    }
    
    /**
     * Return a string array containing the names of all files
     * and directories in this directory.
     */
    String[] list() throws IOException {
        if (entry == null) { return new String[0]; }
        List names = entry.list();
        return (String[]) names.toArray(new String[names.size()]);
    }
    
    /**
     * Find the entry matching the given name in this directory
     */
    Entry resolve(String name) throws IOException {
        if (entry == null) { throw new FileNotFoundException(); }
        return entry.resolve(name);
    }

    /**
     * Delete this directory. If it contains valid
     * files or sub-directories, throw an io exception.
     */
    abstract void delete() throws IOException;
    
    /**
     * Set last modified information
     */
    abstract void setLastModified(long millis) throws IOException;
    
    /**
     * Return the first cluster number of this directory, or zero
     * if this directory is the root directory.
     */
    abstract int getFirstClusterNumber();
    
    /**
     * Add a file entry to this directory. Assume the given names are valid.
     */
    void createFile(String longName) throws IOException {
        String shortName = generateShortName(longName);
        int additional = calculateEntriesRequired(longName);
        consolidate(additional);
        entry.addFileEntries(shortName, longName);
    }
    
    /**
     * Add a directory entry to this directory.
     * Assume the given names are valid.
     * @param longName the long filename
     * @throws IOException if an error occurs
     */
    void createDirectory(String longName) throws IOException {
        String shortName = generateShortName(longName);
        int additional = calculateEntriesRequired(longName);
        consolidate(additional);
        entry.addDirectoryEntries(
                shortName,
                longName,
                getFirstClusterNumber());
    }
    
    /**
     * Return the number of long entries required to store the
     * given long file name.
     */
    private int calculateEntriesRequired(String longName) {
        // 13 characters per entry so divide and round up
        return (longName.length() + 12) / 13;
    }
    
    /**
     * Consolidate this entry list if space can be saved. Room must
     * be left for the additional number of entries specified.
     */
    void consolidate(int additional) throws IOException {
        int size = data.getSize();
        Counter counter = new Counter(entry);
        int extra = additional * 32;
        int required = counter.getUsed() + extra;
        
        // can't be resized
        if (!isResizable()) {
            if (required > size) {
                throw new IOException("Directory full");
            } else if (size - counter.getEnd() < additional) {
                entry.compact(0);
            }
        } else if (required > model.getBytesPerCluster()) {
            // can be resized so try to conserve space
            entry.compact(0);
            setSize(required);
        }
    }
    
    /**
     * Return true if this directory can be resized
     */
    abstract boolean isResizable();
    
    /**
     * Resize this directory if possible
     */
    abstract void setSize(int size) throws IOException;
    
    /**
     * Return the first entry in this directory
     */
    Entry getEntry() {
        return entry;
    }

    /** 
     * Generate a short name based on the given long name appropriate for
     * the current directory.
     * @param longName the long filename
     * @return the short filename
     * @throws IOException if the given long name is invalid
     */
    String generateShortName(String longName) throws IOException {
        checkLongName(longName);
        StringBuffer buf = new StringBuffer(longName.toUpperCase());
        boolean periodFound = false;
        boolean lossyConversion = false;
        String special = "$%'-_@~`!(){}^#& ";
        for (int i = buf.length() - 1; i >= 0; i--) {
            char c = buf.charAt(i);
            if (c == '.') {
                // remove all but the last period
                if (periodFound) {
                    buf.deleteCharAt(i);
                } else {
                    periodFound = true;
                }
            } else if (c == ' ') {
                // remove spaces
                buf.deleteCharAt(i);
            } else if ((c < 'A' || c > 'Z') && (special.indexOf(c) == -1)) {
                buf.setCharAt(i, '_'); // replace invalid chars with '_'
                lossyConversion = true;
            }
        }

        // split into base and extension
        String base = buf.toString();
        int period = base.indexOf('.');
        String extension = (period != -1) ? base.substring(period + 1) : "";
        if (extension.length() > 3) { extension = extension.substring(0, 3); }
        String basis = (period != -1) ? base.substring(0, period) : base;
        if (basis.length() > 8) { basis = basis.substring(0, 8); }
        String name = (extension.equals("")) ? basis : basis + "." + extension;
        if (name.equals("") || name.startsWith(".")) {
            throw new IOException("Invalid name");
        }
        
        // generate numeric tail
        if (checkShortName(name)) {
            return name;
        } else {
            Random random = new Random();
            for (int i = 1; i < 999999; i += random.nextInt(10) + 1) {
                name = addTail(basis, extension, i);
                if (checkShortName(name)) { return name; }
            }
        }
        throw new IOException("Unable to generate suitable short name");
    }
    
    /**
     * Add the given numeric table to the short name supplied
     */
    private String addTail(String basis, String extension, int value) {
        String tail = "~" + value;
        int allowed = 8 - tail.length();
        basis = (basis.length() > allowed)
                ? basis.substring(0, allowed)
                : basis;
        return (extension.equals(""))
                ? basis + tail
                : basis + tail + "." + extension;
    }
    
    /**
     * Throw an io exception if the given long name is invalid
     */
    private void checkLongName(String longName) throws IOException {
        // check for leading or trailing spaces
        if (longName.startsWith(" ") || longName.endsWith(" ")) {
            throw new IOException("Invalid file name");
        }
        
        // check for invalid characters
        String disallowed = "+,;=[]";
        for (int i = 0, n = longName.length(); i < n; i++) {
            if (disallowed.indexOf(longName.charAt(i)) != -1) {
                throw new IOException("Invalid file name");
            }
        }
    }
    
    /**
     * Return true if the given short name does not match any entries in
     * the current directory.
     */
    private boolean checkShortName(String shortName) {
        Entry next = entry;
        while (next != null) {
            if (shortName.equalsIgnoreCase(next.getShortName())) {
                return false;
            }
            String ln = next.getLongName();
            if (ln != null && shortName.equalsIgnoreCase(ln)) {
                return false;
            }
            next = next.next();
        }
        return true;
    }
    
}
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 



