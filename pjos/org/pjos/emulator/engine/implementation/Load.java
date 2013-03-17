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

import org.pjos.common.runtime.Entry;

/**
 * Contains implementations for various load instructions.
 */
class Load implements Constants {
    
    /**
     * ldc instruction
     */
    static void ldc() {
        int index = Instruction.byteCode(1);
        loadConstant(index);
        Reg.instruction += 2;
    }
    
    /**
     * ldc_w instruction
     */
    static void ldc_w() {
        int index = Instruction.twoByteCodes(1);
        loadConstant(index);
        Reg.instruction += 3;
    }
    
    /**
     * Load the constant pool entry with the given index (single word)
     */
    private static void loadConstant(int index) {
        int entry = Mem.load(Reg.pool + 4 * index);
        int id = Mem.load(entry + 4 * ENTRY_ID);
        if (id == Entry.STRING_CONSTANT) {
            int pointer = Mem.load(entry + 4 * CONSTANT_FIRST);
            Stack.pushPointer(pointer);
        } else {
            int value = Mem.load(entry + 4 * CONSTANT_FIRST);
            Stack.pushData(value);
        }
    }
    
    /**
     * ldc2_w instruction
     */
    static void ldc2_w() {
        int index = Instruction.twoByteCodes(1);
        int entry = Mem.load(Reg.pool + 4 * index);
        int highBytes = Mem.load(entry + 4 * CONSTANT_FIRST);
        int lowBytes = Mem.load(entry + 4 * CONSTANT_SECOND);
        Stack.pushData(highBytes);
        Stack.pushData(lowBytes);
        Reg.instruction += 3;
    }
    
    /**
     * iload instruction
     */
    static void iload() {
        int index = Instruction.byteCode(1);
        Stack.pushData(Locals.getDataFromLocal(index));
        Reg.instruction += 2;
    }
    
    /**
     * iload_x instruction
     * @param index the index
     */
    static void iload_x(int index) {
        Stack.pushData(Locals.getDataFromLocal(index));
        Reg.instruction += 1;
    }
    
    /**
     * lload instruction
     */
    static void lload() {
        int index = Instruction.byteCode(1);
        Stack.pushLong(Locals.getLongFromLocal(index));
        Reg.instruction += 2;
    }
    
    /**
     * lload_x instruction
     * @param index the index
     */
    static void lload_x(int index) {
        Stack.pushLong(Locals.getLongFromLocal(index));
        Reg.instruction += 1;
    }
    
    /**
     * fload instruction
     */
    static void fload() {
        int index = Instruction.byteCode(1);
        Stack.pushData(Locals.getDataFromLocal(index));
        Reg.instruction += 2;
    }
    
    /**
     * fload_x instruction
     * @param index the index
     */
    static void fload_x(int index) {
        Stack.pushData(Locals.getDataFromLocal(index));
        Reg.instruction += 1;
    }
    
    /**
     * dload instruction
     */
    static void dload() {
        int index = Instruction.byteCode(1);
        Stack.pushLong(Locals.getLongFromLocal(index));
        Reg.instruction += 2;
    }

    /**
     * dload_x instruction
     */
    static void dload_x(int index) {
        Stack.pushLong(Locals.getLongFromLocal(index));
        Reg.instruction += 1;
    }
    
    /**
     * aload instruction
     */
    static void aload() {
        int index = Instruction.byteCode(1);
        Stack.pushPointer(Locals.getPointerFromLocal(index));
        Reg.instruction += 2;
    }
    
    /**
     * aload_x instruction
     */
    static void aload_x(int index) {
        Stack.pushPointer(Locals.getPointerFromLocal(index));
        Reg.instruction += 1;
    }
    
}
