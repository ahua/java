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

/**
 * A data slot that resolves to the next available memory location
 */
class NextSlot extends Slot {
    
    /** The creator */
    private Creator creator;
    
    /**
     * Create a next slot for the given web
     * @param creator the creator
     */
    NextSlot(Creator creator) {
        this.creator = creator;
    }
    
    /**
     * Set the value
     */
    void resolve() {
        value = creator.next();
        resolved = true;
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
    
}





