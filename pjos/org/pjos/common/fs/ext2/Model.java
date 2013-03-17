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

import org.pjos.common.device.Storage;

/**
 * This object manages file system settings.
 *
 * Not safe for use by multiple threads.
 */
class Model implements Constants {
    
    /** The underlying storage */
    private Storage storage;
    
    /** The superblock */
    private byte[] superblock = null;
    
    /**
     * Create a model
     * @param storage the underlying storage
     */
    Model(Storage storage) {
        this.storage = storage;
    }
    
    /**
     * @return the volume label
     */
    String getLabel() throws IOException {
        loadSuperblock();
        throw new UnsupportedOperationException();
    }
    
    /**
     * Reset the model. All settings are reloaded as
     * necessary. (eg. new floppy disk inserted)
     */
    void reset() {
        superblock = null;
    }
    
    /**
     * Load the superblock data into the cache if it
     * has not already been loaded.
     */
    private void loadSuperblock() throws IOException {
        superblock = new byte[SUPERBLOCK_SIZE];
        int k = storage.read(SUPERBLOCK_OFFSET, superblock, 0, SUPERBLOCK_SIZE);
        if (k != SUPERBLOCK_SIZE) {
            throw new IOException("Unable to read superblock");
        }
    }

}










