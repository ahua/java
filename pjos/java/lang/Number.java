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
package java.lang;

import java.io.Serializable;

/**
 * Implementation of java.lang.Number based on Sun specification.
 */
public abstract class Number implements Serializable {
    
    /**
     * Create a number
     */
    public Number() {
        // default constructor
    }
    
    /**
     * @return value as int
     */
    public abstract int intValue();
    
    /**
     * @return value as long
     */
    public abstract long longValue();
    
    /**
     * @return value as float
     */
    public abstract float floatValue();
    
    /**
     * @return value as double
     */
    public abstract double doubleValue();
    
    /**
     * @return value as byte
     */
    public byte byteValue() {
        return (byte) intValue();
    }
    
    /**
     * @return value as short
     */
    public short shortValue() {
        return (short) intValue();
    }
    
}
