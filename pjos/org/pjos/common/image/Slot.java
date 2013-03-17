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

import org.pjos.common.runtime.Constants;

/**
 * Represents a data or pointer slot of an allocation
 */
abstract class Slot implements Constants {
    
    /** The value */
    protected int value = 0;
    
    /** Set when resolved */
    protected boolean resolved = false;
    
    /**
     * Create a slot
     */
    Slot() {
        // nothing to do here
    }
    
    /**
     * Resolve this field. Subclasses can override
     * this method to set the value and resolved
     * flag here if they can't set them in the constructor.
     */
    void resolve() {
        // nothing to do here
    }
    
    /**
     * @return the 32 bit value to be written into
     *         the allocation at this field's location
     */
    int getValue() {
        if (!resolved) {
            throw new IllegalStateException("Slot not resolved");
        }
        return value;
    }
    
    /**
     * Return the slot type. The default slot type is
     * a 32-bit word, but this method may be overridden
     * for special types of slots.
     * @return SlotType.WORD
     */
    SlotType getType() {
        return SlotType.WORD;
    }
    
}





