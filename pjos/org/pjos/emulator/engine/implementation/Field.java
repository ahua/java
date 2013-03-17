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
 * Contains implementations for various field instructions.
 */
class Field implements Constants {
    
    /**
     * getfield instruction
     */
    static void getfield() {
        // resolve the field entry
        int index = Instruction.twoByteCodes(1);
        int entry = Resolve.resolve(index, Entry.FIELD);
        if (entry == NULL) { return; } // rollback because gc has been done

        // read field values
        int fieldIndex = Mem.load(entry + 4 * FIELD_INDEX);
        int object = Stack.popPointer();
        int isReference = Mem.load(entry + 4 * FIELD_REFERENCE_FLAG);
        int size = Mem.load(entry + 4 * FIELD_SIZE);

        // check pointer for null
        if (object == NULL) {
            Exceptions.throwException(CORE_THROW_NULL_POINTER);
            return;
        }

        // retrieve values
        int value1 = Mem.load(object + 4 * OBJECT_FIELDS + 4 * fieldIndex);
        int value2 = (size == 2)
                ? Mem.load(object + 4 * OBJECT_FIELDS + 4 * fieldIndex + 4)
                : 0;

        // push values
        if (isReference == 1) {
            Stack.pushPointer(value1);
        } else {
            Stack.pushData(value1);
        }
        if (size == 2) { Stack.pushData(value2); }
        
        Reg.instruction += 3;
    }

    /**
     * putfield instruction
     */
    static void putfield() {
        // resolve the field entry
        int index = Instruction.twoByteCodes(1);
        int entry = Resolve.resolve(index, Entry.FIELD);
        if (entry == NULL) { return; } // rollback because gc has been done

        // read field values
        int fieldIndex = Mem.load(entry + 4 * FIELD_INDEX);
        int isReference = Mem.load(entry + 4 * FIELD_REFERENCE_FLAG);
        int size = Mem.load(entry + 4 * FIELD_SIZE);

        // read values from stack
        int value2 = (size == 2) ? Stack.popData() : 0;
        int value1 = (isReference == 1) ? Stack.popPointer() : Stack.popData();
        int object = Stack.popPointer();

        // check for null pointer
        if (object == NULL) {
            Exceptions.throwException(CORE_THROW_NULL_POINTER);
            return;
        }

        // store the values
        Mem.store(value1, object + 4 * OBJECT_FIELDS + 4 * fieldIndex);
        if (size == 2) {
            Mem.store(value2, object + 4 * OBJECT_FIELDS + 4 * fieldIndex + 4);
        }
        
        Reg.instruction += 3;
    }
    
    /**
     * putstatic instruction
     */
    static void putstatic() {
        // resolve the field entry
        int index = Instruction.twoByteCodes(1);
        int entry = Resolve.resolve(index, Entry.FIELD);
        if (entry == NULL) { return; } // rollback because gc has been done

        // read field values
        int fieldIndex = Mem.load(entry + 4 * FIELD_INDEX);
        int owner = Mem.load(entry + 4 * ENTRY_OWNER);
        int statics = Mem.load(owner + 4 * TYPE_STATICS);
        int offset = STATICS_FIELDS + fieldIndex;
        int isReference = Mem.load(entry + 4 * FIELD_REFERENCE_FLAG);
        int size = Mem.load(entry + 4 * FIELD_SIZE);

        // pop values from stack
        int value2 = (size == 2) ? Stack.popData() : 0;
        int value1 = (isReference == 1) ? Stack.popPointer() : Stack.popData();

        // store values
        Mem.store(value1, statics + 4 * offset);
        if (size == 2) { Mem.store(value2, statics + 4 * offset + 4); }
        
        Reg.instruction += 3;
    }
    
    /**
     * getstatic instruction
     */
    static void getstatic() {
        // resolve the field entry
        int index = Instruction.twoByteCodes(1);
        int entry = Resolve.resolve(index, Entry.FIELD);
        if (entry == NULL) { return; } // rollback because gc has been done

        // read field values
        int fieldIndex = Mem.load(entry + 4 * FIELD_INDEX);
        int type = Mem.load(entry + 4 * ENTRY_OWNER);
        int statics = Mem.load(type + 4 * TYPE_STATICS);
        int offset = STATICS_FIELDS + fieldIndex;
        int isReference = Mem.load(entry + 4 * FIELD_REFERENCE_FLAG);
        int size = Mem.load(entry + 4 * FIELD_SIZE);

        // read values
        int value1 = Mem.load(statics + 4 * offset);
        int value2 = (size == 2) ? Mem.load(statics + 4 * offset + 4) : 0;

        // push values on stack
        if (isReference == 1) {
            Stack.pushPointer(value1);
        } else {
            Stack.pushData(value1);
        }
        if (size == 2) { Stack.pushData(value2); }

        Reg.instruction += 3;
    }
    
}
