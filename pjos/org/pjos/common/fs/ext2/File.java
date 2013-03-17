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

import java.io.IOException;

/**
 * Represents a file.
 *
 * Not safe for use by multiple threads.
 */
class File {
    
    /**
     * Return the size of this file
     */
    int getSize() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Set the size of this file
     */
    void setSize(int size) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Return the byte at the specified position
     */
    int read(int pos) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Write the given unsigned byte value to the specified location in the file
     */
    void write(int pos, int value) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Set the last modified timestamp for this file to value given
     */
    void setLastModified(long timestamp) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Return the last modified timestamp for this file
     */
    long getLastModified() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Delete this file
     */
    void delete() throws IOException {
        throw new UnsupportedOperationException();
    }
    
}










