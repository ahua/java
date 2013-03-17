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
 * Contains implementations for various maths instructions.
 */
class Math implements Constants {

    /**
     * iadd instruction
     */
    static void iadd() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        Stack.pushData(value1 + value2);
        Reg.instruction += 1;
    }

    /**
     * ladd instruction
     */
    static void ladd() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        Stack.pushLong(value1 + value2);
        Reg.instruction += 1;
    }

    /**
     * fadd instruction
     */
    static void fadd() {
        float value2 = Float.intBitsToFloat(Stack.popData());
        float value1 = Float.intBitsToFloat(Stack.popData());
        Stack.pushData(Float.floatToRawIntBits(value1 + value2));
        Reg.instruction += 1;
    }

    /**
     * dadd instruction
     */
    static void dadd() {
        double value2 = Double.longBitsToDouble(Stack.popLong());
        double value1 = Double.longBitsToDouble(Stack.popLong());
        Stack.pushLong(Double.doubleToRawLongBits(value1 + value2));
        Reg.instruction += 1;
    }

    /**
     * isub instruction
     */
    static void isub() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        Stack.pushData(value1 - value2);
        Reg.instruction += 1;
    }

    /**
     * lsub instruction
     */
    static void lsub() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        Stack.pushLong(value1 - value2);
        Reg.instruction += 1;
    }

    /**
     * fsub instruction
     */
    static void fsub() {
        float value2 = Float.intBitsToFloat(Stack.popData());
        float value1 = Float.intBitsToFloat(Stack.popData());
        Stack.pushData(Float.floatToRawIntBits(value1 - value2));
        Reg.instruction += 1;
    }

    /**
     * dsub instruction
     */
    static void dsub() {
        double value2 = Double.longBitsToDouble(Stack.popLong());
        double value1 = Double.longBitsToDouble(Stack.popLong());
        Stack.pushLong(Double.doubleToRawLongBits(value1 - value2));
        Reg.instruction += 1;
    }

    /**
     * imul instruction
     */
    static void imul() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        Stack.pushData(value1 * value2);
        Reg.instruction += 1;
    }

    /**
     * lmul instruction
     */
    static void lmul() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        Stack.pushLong(value1 * value2);
        Reg.instruction += 1;
    }

    /**
     * fmul instruction
     */
    static void fmul() {
        float value2 = Float.intBitsToFloat(Stack.popData());
        float value1 = Float.intBitsToFloat(Stack.popData());
        Stack.pushData(Float.floatToRawIntBits(value1 * value2));
        Reg.instruction += 1;
    }

    /**
     * dmul instruction
     */
    static void dmul() {
        double value2 = Double.longBitsToDouble(Stack.popLong());
        double value1 = Double.longBitsToDouble(Stack.popLong());
        Stack.pushLong(Double.doubleToRawLongBits(value1 * value2));
        Reg.instruction += 1;
    }

    /**
     * idiv instruction
     */
    static void idiv() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        if (value2 == 0) {
            Exceptions.throwException(CORE_THROW_ARITHMETIC);
            return;
        }
        Stack.pushData(value1 / value2);
        Reg.instruction += 1;
    }

    /**
     * ldiv instruction
     */
    static void ldiv() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        if (value2 == 0) {
            Exceptions.throwException(CORE_THROW_ARITHMETIC);
            return;
        }
        Stack.pushLong(value1 / value2);
        Reg.instruction += 1;
    }

    /**
     * fdiv instruction
     */
    static void fdiv() {
        float value2 = Float.intBitsToFloat(Stack.popData());
        float value1 = Float.intBitsToFloat(Stack.popData());
        Stack.pushData(Float.floatToRawIntBits(value1 / value2));
        Reg.instruction += 1;
    }

    /**
     * ddiv instruction
     */
    static void ddiv() {
        double value2 = Double.longBitsToDouble(Stack.popLong());
        double value1 = Double.longBitsToDouble(Stack.popLong());
        Stack.pushLong(Double.doubleToRawLongBits(value1 / value2));
        Reg.instruction += 1;
    }

    /**
     * irem instruction
     */
    static void irem() {
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        if (value2 == 0) {
            Exceptions.throwException(CORE_THROW_ARITHMETIC);
            return;
        }
        Stack.pushData(value1 % value2);
        Reg.instruction += 1;
    }

    /**
     * lrem instruction
     */
    static void lrem() {
        long value2 = Stack.popLong();
        long value1 = Stack.popLong();
        if (value2 == 0) {
            Exceptions.throwException(CORE_THROW_ARITHMETIC);
            return;
        }
        Stack.pushLong(value1 % value2);
        Reg.instruction += 1;
    }

    /**
     * frem instruction
     */
    static void frem() {
        float value2 = Float.intBitsToFloat(Stack.popData());
        float value1 = Float.intBitsToFloat(Stack.popData());
        Stack.pushData(Float.floatToRawIntBits(value1 % value2));
        Reg.instruction += 1;
    }

    /**
     * drem instruction
     */
    static void drem() {
        double value2 = Double.longBitsToDouble(Stack.popLong());
        double value1 = Double.longBitsToDouble(Stack.popLong());
        Stack.pushLong(Double.doubleToRawLongBits(value1 % value2));
        Reg.instruction += 1;
    }

    /**
     * ineg instruction
     */
    static void ineg() {
        Stack.pushData(-Stack.popData());
        Reg.instruction += 1;
    }

    /**
     * lneg instruction
     */
    static void lneg() {
        Stack.pushLong(-Stack.popLong());
        Reg.instruction += 1;
    }

    /**
     * fneg instruction
     */
    static void fneg() {
        float value = Float.intBitsToFloat(Stack.popData());
        Stack.pushData(Float.floatToRawIntBits(-value));
        Reg.instruction += 1;
    }

    /**
     * dneg instruction
     */
    static void dneg() {
        double value = Double.longBitsToDouble(Stack.popLong());
        Stack.pushLong(Double.doubleToRawLongBits(-value));
        Reg.instruction += 1;
    }

}
