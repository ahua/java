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

import java.util.List;
import java.util.Random;

/**
 * Represents a directory in the FAT12 filesystem.
 *
 * Not safe for use by multiple threads.
 */
abstract class Directory {
    
    /**
     * Return a string array containing the names of all files
     * and directories in this directory.
     */
    String[] list() throws IOException {
        throw new UnsupportedOperationException();
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
     * Return true if this directory can be resized
     */
    abstract boolean isResizable();
    
    /**
     * Resize this directory if possible
     */
    abstract void setSize(int size) throws IOException;
    
}
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 



