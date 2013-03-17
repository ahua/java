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
package org.pjos.emulator.engine.implementation;

/**
 * Contains implementations for various const instructions.
 */
class Const implements Constants {
    
    /**
     * aconst_null instruction
     */
    static void aconst_null() {
        Stack.pushPointer(NULL);
        Reg.instruction += 1;
    }
    
    /**
     * iconst_x instruction
     * @param value the value
     */
    static void iconst_x(int value) {
        Stack.pushData(value);
        Reg.instruction += 1;
    }
    
    /**
     * lconst_x instruction
     * @param value the value
     */
    static void lconst_x(long value) {
        Stack.pushLong(value);
        Reg.instruction += 1;
    }
    
    /**
     * fconst_x instruction
     * @param value the value
     */
    static void fconst_x(float value) {
        Stack.pushData(Float.floatToRawIntBits(value));
        Reg.instruction += 1;
    }

}
