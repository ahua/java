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

/**
 * Represents a file.
 *
 * Not safe for use by multiple threads.
 */
class File {
    
    /** The parent entry */
    private Entry parent;
    
    /** The chain containing the data */
    private Chain chain;

    /** The size of this file */
    private int size;
    
    /**
     * Create a new file object using the given chain.
     */
    File(Chain chain, Entry parent) throws IOException {
        this.chain = chain;
        this.parent = parent;
        size = parent.getSize();
        if (chain.getSize() < size) {
            throw new IOException("Invalid format");
        }
    }
    
    /**
     * Return the size of this file
     */
    int getSize() { return size; }
    
    /**
     * Set the size of this file
     */
    void setSize(int size) throws IOException {
        // zero any values in the last cluster not currently used by the file
        int parentSize = parent.getSize();
        int chainSize = chain.getSize();
        if (size > parentSize) {
            for (int i = parentSize; i < chainSize; i++) {
                chain.set8(0, i);
            }
        }
        chain.setSize(size); // change size of chain
        parent.setSize(size); // change size in parent entry
        this.size = size;
    }
    
    /**
     * Return the byte at the specified position
     */
    int read(int pos) throws IOException {
        if (pos >= size) {
            return -1;
        } else {
            return chain.get8(pos);
        }
    }
    
    /**
     * Write the given unsigned byte value to the specified location in the file
     */
    void write(int pos, int value) throws IOException {
        chain.set8(value, pos);
    }
    
    /**
     * Set the last modified timestamp for this file to value given
     */
    void setLastModified(long timestamp) throws IOException {
        parent.setLastModified(timestamp);
    }
    
    /**
     * Return the last modified timestamp for this file
     */
    long getLastModified() throws IOException {
        return parent.getLastModified();
    }
    
    /**
     * Delete this file
     */
    void delete() throws IOException {
        setSize(0); // free clusters first
        parent.delete(); // mark directory entry as free
    }
    
}










