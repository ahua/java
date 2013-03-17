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
 * Contains implementations for the numerical conversion instructions
 */
class Convert {
    
    /**
     * i2l instruction
     */
    static void i2l() {
        int value = Stack.popData();
        Stack.pushLong((long) value);
        Reg.instruction += 1;
    }

    /**
     * i2f instruction
     */
    static void i2f() {
        int value = Stack.popData();
        Stack.pushData(Float.floatToRawIntBits((float) value));
        Reg.instruction += 1;
    }
    
    /**
     * i2d instruction
     */
    static void i2d() {
        int value = Stack.popData();
        Stack.pushLong(Double.doubleToRawLongBits((double) value));
        Reg.instruction += 1;
    }
    
    /**
     * l2i instruction
     */
    static void l2i() {
        long value = Stack.popLong();
        Stack.pushData((int) value);
        Reg.instruction += 1;
    }
    
    /**
     * l2f instruction
     */
    static void l2f() {
        long value = Stack.popLong();
        Stack.pushData(Float.floatToRawIntBits((float) value));
        Reg.instruction += 1;
    }
    
    /**
     * l2d instruction
     */
    static void l2d() {
        long value = Stack.popLong();
        Stack.pushLong(Double.doubleToRawLongBits((double) value));
        Reg.instruction += 1;
    }
    
    /**
     * f2i instruction
     */
    static void f2i() {
        float value = Float.intBitsToFloat(Stack.popData());
        Stack.pushData((int) value);
        Reg.instruction += 1;
    }
    
    /** 
     * f2l instruction
     */
    static void f2l() {
        float value = Float.intBitsToFloat(Stack.popData());
        Stack.pushLong((long) value);
        Reg.instruction += 1;
    }
    
    /**
     * f2d instruction
     */
    static void f2d() {
        float value = Float.intBitsToFloat(Stack.popData());
        Stack.pushLong(Double.doubleToRawLongBits((double) value));
        Reg.instruction += 1;
    }
    
    /**
     * d2i instruction
     */
    static void d2i() {
        double value = Double.longBitsToDouble(Stack.popLong());
        Stack.pushData((int) value);
        Reg.instruction += 1;
    }
    
    /**
     * d2l instruction
     */
    static void d2l() {
        double value = Double.longBitsToDouble(Stack.popLong());
        Stack.pushLong((long) value);
        Reg.instruction += 1;
    }
    
    /**
     * d2f instruction
     */
    static void d2f() {
        double value = Double.longBitsToDouble(Stack.popLong());
        Stack.pushData(Float.floatToRawIntBits((float) value));
        Reg.instruction += 1;
    }
    
    /**
     * i2b instruction
     */
    static void i2b() {
        byte value = (byte) Stack.popData();
        Stack.pushData((int) value);
        Reg.instruction += 1;
    }
    
    /**
     * i2c instruction
     */
    static void i2c() {
        int value = Stack.popData();
        Stack.pushData(value & 0x0000ffff);
        Reg.instruction += 1;
    }
    
    /** 
     * i2s instruction
     */
    static void i2s() {
        short value = (short) Stack.popData();
        Stack.pushData((int) value);
        Reg.instruction += 1;
    }
    
}
