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
 * Represents a directory whose entries are stored in a fixed size block.
 * (ie. the root directory)
 *
 * Not safe for use by multiple threads.
 */
class BlockDirectory extends Directory {
    
    /**
     * Create a new directory object using the given data
     */
    BlockDirectory(Data data, Model model) throws IOException {
        super(model, data);
    }
    
    /**
     * A block directory can't be deleted
     */
    void delete() throws IOException {
        throw new IOException("Unable to delete block directory");
    }
    
    /**
     * Set last modified information
     */
    void setLastModified(long millis) {
        throw new IllegalStateException(); // block directories have no parent
    }

    /**
     * Not resizable
     */
    boolean isResizable() { return false; }
    
    /**
     * Illegal operation
     */
    void setSize(int size) { throw new IllegalStateException(); }
    
    /**
     * Return 0 for root directory
     */
    int getFirstClusterNumber() { return 0; }

}
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 



