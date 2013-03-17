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
 * Contains implementations for various array instructions.
 */
class Array implements Constants {
    
    /**
     * If the specified array is null, throw a null pointer exception, if
     * the index is invalid throw an array out of bounds exception. Return
     * true only if no exception was thrown.
     */
    private static boolean check(int array, int index) {
        if (array == NULL) {
            // check for null array
            Exceptions.throwException(CORE_THROW_NULL_POINTER);
            return false;
        } else if (index < 0 || index >= Mem.load(array + 4 * ARRAY_LENGTH)) {
            // check index
            Exceptions.throwException(CORE_THROW_ARRAY_INDEX);
            return false;
        }
        
        // no exceptions were thrown
        return true;
    }
    
    /**
     * arraylength instruction
     */
    static void arraylength() {
        int array = Stack.popPointer();
        if (array == NULL) {
            Exceptions.throwException(CORE_THROW_NULL_POINTER);
        } else {
            Stack.pushData(Mem.load(array + 4 * ARRAY_LENGTH));
            Reg.instruction += 1;
        }
    }
    
    /**
     * iaload instruction
     */
    static void iaload() {
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            int value = Mem.load(array + 4 * ARRAY_DATA + 4 * index);
            Stack.pushData(value);
            Reg.instruction += 1;
        }
    }
    
    /**
     * laload instruction
     */
    static void laload() {
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            long high = Mem.load(array + 4 * ARRAY_DATA + 4 * index);
            long low = Mem.load(array + 4 * ARRAY_DATA + 4 * index + 4)
                    & 0x00000000FFFFFFFFL;
            Stack.pushLong((high << 32) | low);
            Reg.instruction += 1;
        }
    }
    
    /**
     * faload instruction
     */
    static void faload() {
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            int value = Mem.load(array + 4 * ARRAY_DATA + 4 * index);
            Stack.pushData(value);
            Reg.instruction += 1;
        }
    }
    
    /**
     * daload instruction
     */
    static void daload() {
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            long high = Mem.load(array + 4 * ARRAY_DATA + 4 * index);
            long low = Mem.load(array + 4 * ARRAY_DATA + 4 * index + 4)
                    & 0x00000000FFFFFFFFL;
            Stack.pushLong((high << 32) | low);
            Reg.instruction += 1;
        }
    }
    
    /**
     * aaload instruction
     */
    static void aaload() {
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            int address = Mem.load(array + 4 * ARRAY_DATA + 4 * index);
            Stack.pushPointer(address);
            Reg.instruction += 1;
        }
    }

    /**
     * baload instruction
     */
    static void baload() {
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            int value = Mem.loadByte(array + 4 * ARRAY_DATA + index);
            // sign extend if necessary
            if (value > 127) { value = 0xffffff00 | value; }
            Stack.pushData(value);
            Reg.instruction += 1;
        }
    }
    
    /**
     * caload instruction
     */
    static void caload() {
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            int value = Mem.loadShort(array + 4 * ARRAY_DATA + 2 * index);
            Stack.pushData(value);
            Reg.instruction += 1;
        }
    }
    
    /**
     * saload instruction
     */
    static void saload() {
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            int value = Mem.loadShort(array + 4 * ARRAY_DATA + 2 * index);
            // sign extend if necessary
            if (value > 32767) { value = 0xffff0000 | value; }
            Stack.pushData(value);
            Reg.instruction += 1;
        }
    }
    
    /**
     * iastore instruction
     */
    static void iastore() {
        int value = Stack.popData();
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            Mem.store(value, array + 4 * ARRAY_DATA + 4 * index);
            Reg.instruction += 1;
        }
    }
    
    /**
     * lastore instruction
     */
    static void lastore() {
        long value = Stack.popLong();
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            int high = (int) (value >>> 32);
            int low = (int) value;
            Mem.store(high, array + 4 * ARRAY_DATA + 8 * index);
            Mem.store(low, array + 4 * ARRAY_DATA + 8 * index + 4);
            Reg.instruction += 1;
        }
    }
    
    /**
     * fastore instruction
     */
    static void fastore() {
        int value = Stack.popData();
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            Mem.store(value, array + 4 * ARRAY_DATA + 4 * index);
            Reg.instruction += 1;
        }
    }
    
    /**
     * dastore instruction
     */
    static void dastore() {
        long value = Stack.popLong();
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            int high = (int) (value >>> 32);
            int low = (int) value;
            Mem.store(high, array + 4 * ARRAY_DATA + 8 * index);
            Mem.store(low, array + 4 * ARRAY_DATA + 8 * index + 4);
            Reg.instruction += 1;
        }
    }
    
    /**
     * aastore instruction
     */
    static void aastore() {
        int pointer = Stack.popPointer();
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            Mem.store(pointer, array + 4 * ARRAY_DATA + 4 * index);
            Reg.instruction += 1;
        }
    }
    
    /**
     * bastore instruction
     */
    static void bastore() {
        int value = Stack.popData();
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            Mem.storeByte(value, array + 4 * ARRAY_DATA + index);
            Reg.instruction += 1;
        }
    }
    
    /**
     * castore instruction
     */
    static void castore() {
        int value = Stack.popData();
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            Mem.storeShort(value, array + 4 * ARRAY_DATA + 2 * index);
            Reg.instruction += 1;
        }
    }
    
    /**
     * sastore instruction
     */
    static void sastore() {
        int value = Stack.popData();
        int index = Stack.popData();
        int array = Stack.popPointer();
        if (check(array, index)) {
            Mem.storeShort(value, array + 4 * ARRAY_DATA + 2 * index);
            Reg.instruction += 1;
        }
    }
    
}
