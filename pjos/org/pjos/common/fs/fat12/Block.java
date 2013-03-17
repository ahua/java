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
 * A block is a section of data contained in a number of consecutive sectors.
 *
 * Not safe for use by multiple threads.
 */
class Block extends Data {
    
    /** The model */
    private Model model;
    
    /** The sectors */
    private Sector[] sectors;
    
    /** The size (number of bytes in this block) */
    private int size;

    /** The number of bytes per sector */
    private int bytesPerSector;
    
    /**
     * Create a block consisting of the given sectors
     */
    Block(Model model, Sector[] sectors) {
        this.model = model;
        this.sectors = sectors;
        bytesPerSector = model.getBytesPerSector();
        size = sectors.length * bytesPerSector;
    }

    /**
     * Return the size (number of bytes) of this data.
     */
    int getSize() { return size; }

    /**
     * Set the unsigned 8-bit value at the specified offset
     */
    void set8(int value, int offset) throws IOException {
        if (offset < 0 || offset > size) {
            throw new IllegalArgumentException("Invalid offset: " + offset);
        }
        Sector sector = sectors[offset / bytesPerSector];
        // make sure sector is loaded before trying to access!
        if (!sector.present()) { sector.load(); }
        sector.set8(value, offset % bytesPerSector);
    }
    
    /**
     * Return the unsigned 8-bit value at the specified offset
     */
    int get8(int offset) throws IOException {
        if (offset < 0 || offset > size) {
            throw new IllegalArgumentException("Invalid offset: " + offset);
        }
        Sector sector = sectors[offset / bytesPerSector];
        // make sure sector is loaded before trying to access!
        if (!sector.present()) { sector.load(); }
        return sector.get8(offset % bytesPerSector);
    }
    
    
}










