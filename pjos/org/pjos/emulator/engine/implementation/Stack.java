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
 * Contains code used to manipulate the stack.
 */
class Stack implements Constants {
    
    /**
     * @return true if the value on the top of the stack is a pointer
     */
    static boolean pointerOnStack() {
        return Mem.load(Reg.stack + 4) == TRUE;
    }
    
    /**
     * Return the pointer value at the specified offset
     * from the top of the stack
     * @param offset distance of desired entry from top of stack
     */
    static int peekPointer(int offset) {
        int address = Reg.stack + 8 * offset;
        int value = Mem.load(address);
        int flag = Mem.load(address + 4);
        if (flag == FALSE) {
            throw new IllegalStateException("Data found at offset " + offset);
        }
        return value;
    }
    
    /**
     * Return the data value at the specified offset from the top of the stack
     */
    static int peekData(int offset) {
        int address = Reg.stack + 8 * offset;
        int value = Mem.load(address);
        int flag = Mem.load(address + 4);
        if (flag == TRUE) {
            throw new IllegalStateException("Data found at offset " + offset);
        }
        return value;
    }
    
    /**
     * @return the value popped off the stack
     */
    static int popPointer() {
        int result = Mem.load(Reg.stack);
        int flag = Mem.load(Reg.stack + 4);
        if (flag == FALSE) {
            throw new IllegalStateException("Stack contains data");
        }
        Mem.store(FALSE, Reg.stack + 4); // unused entry
        Reg.stack += 8;
        if (Debug.debug) {
            check();
            checkPointer(result);
        }
        return result;
    }
    
    /**
     * Pop a data value off the stack
     */
    static int popData() {
        int result = Mem.load(Reg.stack);
        int flag = Mem.load(Reg.stack + 4);
        if (flag == TRUE) {
            throw new IllegalStateException("Stack contains pointer");
        }
        Reg.stack += 8;
        if (Debug.debug) { check(); }
        return result;
    }
    
    /**
     * Push a data value onto the stack
     * @param value the value to be pushed
     */
    static void pushData(int value) {
        pushValue(value, false);
    }

    /**
     * Push a pointer value onto the stack
     * @param pointer the pointer to be pushed
     */
    static void pushPointer(int pointer) {
        pushValue(pointer, true);
    }
    
    /**
     * Push a value onto the stack
     */
    private static void pushValue(int value, boolean isReference) {
        if (Debug.debug && isReference) {
            checkPointer(value);
        }
        Reg.stack -= 8;
        Mem.store((isReference) ? TRUE : FALSE, Reg.stack + 4);
        Mem.store(value, Reg.stack);
        if (Debug.debug) {
            check();
        }
    }
    
    /**
     * Return the maximum value for the stack pointer
     */
    static int max() {
        int numWords = Mem.load(Reg.frame + 4);
        return Reg.frame + 4 * numWords;
    }
    
    /**
     * Return the minimum value for the stack pointer
     */
    static int min() {
        int frameWords = Mem.load(Reg.frame + 4);
        int stackEntries = Mem.load(Reg.method + 4 * METHOD_MAX_STACK);
        return Reg.frame + 4 * frameWords - 8 * stackEntries;
    }
    
    /**
     * Check that the given pointer value is null or within the current space
     * @param pointer the pointer
     */
    static void checkPointer(int pointer) {
        if (pointer == NULL) { return; }
        if (pointer < Reg.core || pointer >= Reg.core + Mem.LIMIT) {
            throw new IllegalStateException(
                    "Invalid pointer value: 0x"
                    + Integer.toHexString(pointer));
        }
    }
    
    /**
     * Check that the current stack pointer value is within the valid range
     */
    static void check() {
        int min = min();
        int max = max();
        if (Reg.stack < min || Reg.stack > max) {
            throw new IllegalStateException(
                    "Stack pointer value invalid: 0x"
                    + Integer.toHexString(Reg.stack)
                    + " [0x" + Integer.toHexString(min)
                    + ", 0x" + Integer.toHexString(max) + "]");
        }
    }
    
    /**
     * Push the given long value onto the stack
     * @param value the long value to push
     */
    static void pushLong(long value) {
        int highBytes = (int) (value >>> 32);
        int lowBytes = (int) value;
        pushData(highBytes);
        pushData(lowBytes);
    }
    
    /**
     * @return the long value popped from the stack
     */
    static long popLong() {
        long lowBytes = popData() & 0x00000000ffffffffL;
        long highBytes = popData() & 0x00000000ffffffffL;
        long result = (highBytes << 32) | lowBytes;
        return result;
    }
    
    /**
     * dup instruction
     */
    static void dup() {
        if (pointerOnStack()) {
            pushPointer(peekPointer(0));
        } else {
            pushData(peekData(0));
        }
        Reg.instruction += 1;
    }
    
    /**
     * dup_x1 instruction
     */
    static void dup_x1() {
        // pop first value
        boolean pointer1 = pointerOnStack();
        int value1 = (pointer1) ? popPointer() : popData();

        // pop second value
        boolean pointer2 = pointerOnStack();
        int value2 = (pointer2) ? popPointer() : popData();

        // push first then second then first
        pushValue(value1, pointer1);
        pushValue(value2, pointer2);
        pushValue(value1, pointer1);
        Reg.instruction += 1;
    }
    
    /**
     * dup_x2 instruction
     */
    static void dup_x2() {
        // pop first value
        boolean pointer1 = pointerOnStack();
        int value1 = (pointer1) ? popPointer() : popData();
        
        // pop second value
        boolean pointer2 = pointerOnStack();
        int value2 = (pointer2) ? popPointer() : popData();
        
        // pop third value
        boolean pointer3 = pointerOnStack();
        int value3 = (pointer3) ? popPointer() : popData();
        
        // push first, third, second, first values
        pushValue(value1, pointer1);
        pushValue(value3, pointer3);
        pushValue(value2, pointer2);
        pushValue(value1, pointer1);
        Reg.instruction += 1;
    }
    
    /**
     * bipush instruction
     */
    static void bipush() {
        int value = Instruction.signByteCode(1);
        Stack.pushData(value);
        Reg.instruction += 2;
    }
    
    /**
     * sipush instruction
     */
    static void sipush() {
        int value = Instruction.signTwoByteCodes(1);
        Stack.pushData(value);
        Reg.instruction += 3;
    }
    
    /**
     * pop instruction
     */
    static void pop() {
        if (pointerOnStack()) {
            popPointer();
        } else {
            popData();
        }
        Reg.instruction += 1;
    }
    
    /**
     * pop2 instruction
     */
    static void pop2() {
        if (pointerOnStack()) {
            popPointer();
        } else {
            popData();
        }
        if (pointerOnStack()) {
            popPointer();
        } else {
            popData();
        }
        Reg.instruction += 1;
    }
    
    /**
     * dup2 instruction
     */
    static void dup2() {
        // could be made more efficient by not popping off the stack,
        // just peeking, but pointerOnStack() method doesn't support
        // this yet.
        boolean pointer1 = pointerOnStack();
        int value1 = (pointer1) ? popPointer() : popData();
        boolean pointer2 = pointerOnStack();
        int value2 = (pointer2) ? popPointer() : popData();
        pushValue(value2, pointer2);
        pushValue(value1, pointer1);
        pushValue(value2, pointer2);
        pushValue(value1, pointer1);
        Reg.instruction += 1;
    }
    
    /**
     * dup2_x1 instruction
     */
    static void dup2_x1() {
        // could be made more efficient by not popping off the stack,
        // just peeking, but pointerOnStack() method doesn't support
        // this yet.
        boolean pointer1 = pointerOnStack();
        int value1 = (pointer1) ? popPointer() : popData();
        boolean pointer2 = pointerOnStack();
        int value2 = (pointer2) ? popPointer() : popData();
        boolean pointer3 = pointerOnStack();
        int value3 = (pointer3) ? popPointer() : popData();
        pushValue(value2, pointer2);
        pushValue(value1, pointer1);
        pushValue(value3, pointer3);
        pushValue(value2, pointer2);
        pushValue(value1, pointer1);
        Reg.instruction += 1;
    }
    
    /**
     * dup2_x2 instruction
     */
    static void dup2_x2() {
        // could be made more efficient by not popping off the stack,
        // just peeking, but pointerOnStack() method doesn't support
        // this yet.
        boolean pointer1 = pointerOnStack();
        int value1 = (pointer1) ? popPointer() : popData();
        boolean pointer2 = pointerOnStack();
        int value2 = (pointer2) ? popPointer() : popData();
        boolean pointer3 = pointerOnStack();
        int value3 = (pointer3) ? popPointer() : popData();
        boolean pointer4 = pointerOnStack();
        int value4 = (pointer4) ? popPointer() : popData();
        pushValue(value2, pointer2);
        pushValue(value1, pointer1);
        pushValue(value4, pointer4);
        pushValue(value3, pointer3);
        pushValue(value2, pointer2);
        pushValue(value1, pointer1);
        Reg.instruction += 1;
    }
    
    /**
     * swap instruction
     */
    static void swap() {
        boolean pointer1 = pointerOnStack();
        int value1 = (pointer1) ? popPointer() : popData();
        boolean pointer2 = pointerOnStack();
        int value2 = (pointer2) ? popPointer() : popData();
        pushValue(value1, pointer1);
        pushValue(value2, pointer2);
        Reg.instruction += 1;
    }
}
