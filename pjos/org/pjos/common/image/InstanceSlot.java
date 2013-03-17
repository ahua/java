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
 * Resolves to an instance pointer
 */
class InstanceSlot extends Slot {
    
    /** The creator */
    private Creator creator;
    
    /** The instance */
    private String instance;
    
    /**
     * Create an instance slot
     * @param creator the creator
     * @param instance the instance
     */
    InstanceSlot(Creator creator, String instance) {
        this.creator = creator;
        this.instance = instance;
    }
    
    /**
     * Set the value
     */
    void resolve() {
        Instance i = creator.getInstance(instance);
        Node node = creator.load(i);
        value = node.address();
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





