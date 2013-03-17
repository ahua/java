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
 * A typesafe enum for the different types of slots. Slots will
 * be written to memory in different ways depending on the type
 * of slot and the byte order of the underlying architecture.
 */
class SlotType {

    /** The 32-bit word slot type */
    public static final SlotType WORD = new SlotType("word");
    
    /** The 16-bit short slot type */
    public static final SlotType SHORT = new SlotType("short");
    
    /** The 8-bit byte slot type */
    public static final SlotType BYTE = new SlotType("byte");

    /** The name */
    private final String name;
    
    /**
     * Create a slot type
     */
    private SlotType(String name) {
        this.name = name;
    }
    
    /**
     * @return the name by default
     */
    public String toString() {
        return name;
    }
    
}





