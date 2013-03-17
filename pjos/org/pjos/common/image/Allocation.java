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
package org.pjos.common.image;

import java.io.IOException;
import java.io.OutputStream;

import org.pjos.common.runtime.Constants;

/**
 * Represents an object loaded into an executable memory image.
 * If slots are not set, they are assumed to be zero value
 * data slots. Null pointers are set to zero.
 */
class Allocation implements Constants {
    
    /** The image */
    private Image image;
    
    /** The address */
    private int address;
    
    /** The number of words */
    private int numWords;
    
    /** The slots */
    private Slot[] slots;
    
    /**
     * Create an allocation of the specified size
     * @param image the image
     * @param address the address
     * @param slots the slots
     */
    Allocation(Image image, int address, Slot[] slots) {
        if (image == null || address < 0 || slots == null) {
            throw new IllegalArgumentException();
        }
        this.image = image;
        this.address = address;
        this.slots = slots;
        numWords = slots.length;
    }
    
    /**
     * @return the number of bytes required by this object
     */
    int numBytes() {
        return numWords * 4;
    }
    
    /**
     * @return the number of words required by this object
     */
    int numWords() {
        return numWords;
    }
    
    /**
     * Write a binary representation to the given output stream. Slots are
     * resolved before their values are written.
     * @param out the output stream
     * @throws IOException if an error occurs
     */
    synchronized void writeTo(OutputStream out) throws IOException {
        for (int i = 0, n = slots.length; i < n; i++) {
            Slot slot = slots[i];
            slot.resolve();
            image.writeSlot(slot, out);
        }
    }
    
    /**
     * @return the address
     */
    int address() {
        return address;
    }
    
}





