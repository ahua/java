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
 * Represents a long constant entry in the runtime constant pool.
 */
public class LongConstant extends Entry {
    
    /** The value */
    long value;
    
    /**
     * Create a long constant
     * @param value the long value (represents either long or double)
     */
    LongConstant(long value) {
        super(LONG_CONSTANT);
        this.value = value;
    }
    
    /**
     * @return the value (can represent either long or double)
     */
    public long getValue() {
        return value;
    }
    
    /**
     * @return a string description
     */
    public String toString() {
        return "Long constant: 0x" + Long.toHexString(value);
    }
    
}


