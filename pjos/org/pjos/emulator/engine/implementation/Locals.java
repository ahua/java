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
 * Contains code used to manipulate the local variables.
 */
class Locals implements Constants {
    
    /**
     * Get the pointer value contained in the local variable
     * with the given index.
     * @param index the index
     * @return the pointer
     */
    static int getPointerFromLocal(int index) {
        if (Debug.debug) { checkLocalIndex(index); }
        int address = Reg.locals + 8 * index;
        int result = Mem.load(address);
        int flag = Mem.load(address + 4);
        if (flag == FALSE) {
            throw new IllegalStateException("Data found at index " + index);
        }
        return result;
    }

    /**
     * Get the data value contained in the local variable
     * with the given index.
     * @param index the index
     * @return the value
     */
    static int getDataFromLocal(int index) {
        if (Debug.debug) { checkLocalIndex(index); }
        int address = Reg.locals + 8 * index;
        int result = Mem.load(address);
        int flag = Mem.load(address + 4);
        if (flag == TRUE) {
            throw new IllegalStateException("Pointer found at index " + index);
        }
        return result;
    }

    /**
     * Check that the specified local variable index is valid
     * @param index the index
     */
    static void checkLocalIndex(int index) {
        int max = Mem.load(Reg.method + 4 * METHOD_MAX_LOCALS);
        if (index < 0 || index >= max) {
            throw new IllegalStateException(
                    "Local index " + index + " out of range [0, " + max + ")");
        }
    }

    /**
     * Store the given data value to the local variable
     * at the given index.
     * @param value the data value
     * @param index the index
     */
    static void storeDataToLocal(int value, int index) {
        if (Debug.debug) { checkLocalIndex(index); }
        int address = Reg.locals + 8 * index;
        Mem.store(value, address);
        Mem.store(FALSE, address + 4);
    }

    /**
     * Store the given pointer value to the local variable
     * at the given index.
     * @param pointer the pointer value
     * @param index the index
     */
    static void storePointerToLocal(int pointer, int index) {
        if (Debug.debug) { checkLocalIndex(index); }
        int address = Reg.locals + 8 * index;
        Mem.store(pointer, address);
        Mem.store(TRUE, address + 4);
    }

    /**
     * Store the given long value to the local variable with
     * the given index.
     * 
     * Long values are stored high:low in local variables so
     * that they can be copied in reverse order directly from
     * the stack during method invocation.
     * @param value the long value
     * @param index the index
     */
    static void storeLongToLocal(long value, int index) {
        int highBytes = (int) (value >>> 32);
        int lowBytes = (int) value;
        storeDataToLocal(highBytes, index);
        storeDataToLocal(lowBytes, index + 1);
    }
    
    /**
     * Get the long value contained in the local variable
     * with the given index
     */
    static long getLongFromLocal(int index) {
        long highBytes = getDataFromLocal(index) & 0x00000000ffffffffL;
        long lowBytes = getDataFromLocal(index + 1) & 0x00000000ffffffffL;
        long result = (highBytes << 32) | lowBytes;
        return result;
    }

    /**
     * iinc instruction
     */
    static void iinc() {
        int index = Instruction.byteCode(1);
        int amount = Instruction.signByteCode(2);
        int value = getDataFromLocal(index);
        value += amount;
        storeDataToLocal(value, index);
        Reg.instruction += 3;
    }
    
}
