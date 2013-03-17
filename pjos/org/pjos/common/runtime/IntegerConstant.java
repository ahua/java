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
package org.pjos.common.runtime;

/**
 * Represents an integer constant entry in the runtime constant pool.
 */
public class IntegerConstant extends Entry {
    
    /** The value */
    int value;
    
    /**
     * Create an integer constant
     * @param value the constant value
     */
    IntegerConstant(int value) {
        super(INTEGER_CONSTANT);
        this.value = value;
    }
    
    /**
     * @return the value (can represent either int or float)
     */
    public int getValue() {
        return value;
    }
    
    /**
     * @return a string description
     */
    public String toString() {
        return "Integer constant: 0x" + Integer.toHexString(value);
    }

}


