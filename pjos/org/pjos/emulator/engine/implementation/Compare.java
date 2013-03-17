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
 * Contains implementations compare instructions.
 */
class Compare {
    
    /**
     * lcmp instruction
     */
    static void lcmp() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        int result = 0;
        if (value1 > value2) {
            result = 1;
        } else if (value1 < value2) {
            result = -1;
        }
        Stack.pushData(result);
        Reg.instruction += 1;
    }
    
    /**
     * fcmpl instruction
     */
    static void fcmpl() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        float f2 = Float.intBitsToFloat(value2);
        float f1 = Float.intBitsToFloat(value1);
        int k = -1;
        if (f1 > f2) {
            k = 1;
        } else if (f1 == f2) {
            k = 0;
        }
        Stack.pushData(k);
        Reg.instruction += 1;
    }
    
    /**
     * fcmpg instruction
     */
    static void fcmpg() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        float f2 = Float.intBitsToFloat(value2);
        float f1 = Float.intBitsToFloat(value1);
        int k = 1;
        if (f1 < f2) {
            k = -1;
        } else if (f1 == f2) {
            k = 0;
        }
        Stack.pushData(k);
        Reg.instruction += 1;
    }
    
    /**
     * dcmpl instruction
     */
    static void dcmpl() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        double d2 = Double.longBitsToDouble(value2);
        double d1 = Double.longBitsToDouble(value1);
        int k = -1;
        if (d1 > d2) {
            k = 1;
        } else if (d1 == d2) {
            k = 0;
        }
        Stack.pushData(k);
        Reg.instruction += 1;
    }
    
    /**
     * dcmpg instruction
     */
    static void dcmpg() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        double d2 = Double.longBitsToDouble(value2);
        double d1 = Double.longBitsToDouble(value1);
        int k = 1;
        if (d1 < d2) {
            k = 1;
        } else if (d1 == d2) {
            k = 0;
        }
        Stack.pushData(k);
        Reg.instruction += 1;
    }

}
