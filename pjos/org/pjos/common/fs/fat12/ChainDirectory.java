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
 * Represents a directory whose entries are stored in a cluster chain.
 *
 * Not safe for use by multiple threads.
 */
class ChainDirectory extends Directory {
    
    /** The chain */
    private Chain chain;
    
    /** The parent entry */
    private Entry parent;
    
    /**
     * Create a new chain directory object
     */
    ChainDirectory(Chain chain, Model model, Entry parent) throws IOException {
        super(model, chain);
        this.chain = chain;
        this.parent = parent;
    }
    
    /**
     * Delete this directory. If it contains valid
     * files or sub-directories, throw an io exception.
     */
    void delete() throws IOException {
        if (list().length > 0) {
            throw new IOException("Directory not empty");
        }
        parent.delete();
    }
    
    /**
     * Set last modified information
     */
    void setLastModified(long millis) throws IOException {
        parent.setLastModified(millis);
    }

    /**
     * Can be resized
     */
    boolean isResizable() { return true; }
    
    /**
     * Resize this directory's chain
     */
    void setSize(int size) throws IOException { chain.setSize(size); }

    /**
     * Return the first cluster number of this directory.
     */
    int getFirstClusterNumber() {
        return chain.getFirstClusterNumber();
    }

}
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 



