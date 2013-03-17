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
 * Contains implementations for various bitwise instructions.
 */
class Bit {
    
    /**
     * ishl instruction
     */
    static void ishl() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        Stack.pushData(value1 << value2);
        Reg.instruction += 1;
    }
    
    /**
     * lshl instruction
     */
    static void lshl() {
        int value2 = Stack.popData();
        long value1 = Stack.popLong();
        Stack.pushLong(value1 << value2);
        Reg.instruction += 1;
    }

    /**
     * ishr instruction
     */
    static void ishr() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        Stack.pushData(value1 >> value2);
        Reg.instruction += 1;
    }
    
    /**
     * lshr instruction
     */
    static void lshr() {
        int value2 = Stack.popData();
        long value1 = Stack.popLong();
        Stack.pushLong(value1 >> value2);
        Reg.instruction += 1;
    }

    /**
     * iushr instruction
     */
    static void iushr() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        Stack.pushData(value1 >>> value2);
        Reg.instruction += 1;
    }
    
    /**
     * lushr instruction
     */
    static void lushr() {
        int value2 = Stack.popData();
        long value1 = Stack.popLong();
        Stack.pushLong(value1 >>> value2);
        Reg.instruction += 1;
    }

    /**
     * iand instruction
     */
    static void iand() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        Stack.pushData(value1 & value2);
        Reg.instruction += 1;
    }
    
    /**
     * land instruction
     */
    static void land() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        Stack.pushLong(value1 & value2);
        Reg.instruction += 1;
    }
    
    /**
     * ior instruction
     */
    static void ior() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        Stack.pushData(value1 | value2);
        Reg.instruction += 1;
    }
    
    /**
     * lor instruction
     */
    static void lor() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        Stack.pushLong(value1 | value2);
        Reg.instruction += 1;
    }
    
    /**
     * ixor instruction
     */
    static void ixor() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        Stack.pushData(value1 ^ value2);
        Reg.instruction += 1;
    }
    
    /**
     * lxor instruction
     */
    static void lxor() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        Stack.pushLong(value1 ^ value2);
        Reg.instruction += 1;
    }
    
}
