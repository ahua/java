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

import java.lang.reflect.Modifier;

import org.pjos.common.runtime.Entry;

/**
 * Contains code to implement the instructions which create object instances
 */
class Instance implements Constants {
    
    /**
     * multianewarray instruction
     */
    static void multianewarray() {
throw new UnsupportedOperationException("Don't hold your breath!");
    }
    
    /**
     * anewarray instruction
     */
    static void anewarray() {
        // look up component type
        int index = Instruction.twoByteCodes(1);
        int entry = Resolve.resolve(index, Entry.TYPE);
        if (entry == NULL) { return; } // rollback because gc has been done
        int arrayType = Mem.load(entry + 4 * TYPE_ARRAY_TYPE);
        
        // resolve array type
        if (arrayType == NULL) {
            int resolveMethod = Mem.load(Reg.core + 4 * CORE_RESOLVE_METHOD);
            int target = Mem.load(Reg.core + 4 * OBJECT_TYPE);
            Invoke.executeMethod(target, resolveMethod, 0);
            return; // rollback
        }

        // figure out required size
        int length = Stack.peekData(0); // don't pop off yet in case gc runs
        int numWords = ARRAY_DATA + length;

        // allocate space
        int address = Allocate.allocate(numWords, HEADER_OBJECT_ARRAY);
        if (address == NULL) { return; } // roll back because gc has been done
        
        // initialise as array object
        Mem.store(address, address + 4 * OBJECT_HASHCODE);
        Mem.store(NULL, address + 4 * OBJECT_LOCK);
        Mem.store(arrayType, address + 4 * OBJECT_TYPE);
        Mem.store(length, address + 4 * ARRAY_LENGTH);

        // set default values to null
        for (int i = 0; i < length; i++) {
            Mem.store(NULL, address + 4 * ARRAY_DATA + 4 * i);
        }

        // pop length
        Stack.popData();
        
        // push address of new array
        Stack.pushPointer(address);
        Reg.instruction += 3;
    }
    
    /**
     * newarray instruction
     */
    static void newarray() {
        // figure out required width
        int atype = Instruction.byteCode(1);
        int width = getWidth(atype);

        // figure out size
        int length = Stack.peekData(0);
        int numBytes = length * width;
        int extra = (numBytes % 4 == 0) ? 0 : 1;
        int numDataWords = (numBytes / 4) + extra;
        int numWords = numDataWords + ARRAY_DATA;

        // allocate space
        int address = Allocate.allocate(numWords, HEADER_DATA_ARRAY);
        if (address == NULL) { return; } // roll back because gc has been done
        
        // initialise as array object
        int type = Mem.load(Reg.core + 4 * CORE_ARRAYS + 4 * atype);
        Mem.store(address, address + 4 * OBJECT_HASHCODE);
        Mem.store(NULL, address + 4 * OBJECT_LOCK);
        Mem.store(type, address + 4 * OBJECT_TYPE);
        Mem.store(length, address + 4 * ARRAY_LENGTH);

        // pop length
        Stack.popData();
        
        // push address of new array
        Stack.pushPointer(address);
        Reg.instruction += 2;
    }
    
    /**
     * new instruction
     */
    static void op_new() {
        // resolve entry
        int index = Instruction.twoByteCodes(1);
        int entry = Resolve.resolve(index, Entry.TYPE);
        if (entry == NULL) { return; } // rollback because gc has been done
        
        // calculate required size
        int numFields = Mem.load(entry + 4 * TYPE_INSTANCE_FIELD_COUNT);
        int numWords = numFields + OBJECT_FIELDS;
        
        // allocate space
        int address = Allocate.allocate(numWords, HEADER_INSTANCE);
        if (address == NULL) { return; } // rollback because gc has been done

        // initialise pointer fields to null
        // do this before setting the class, or it will also be set to null!!
        int current = entry;
        while (current != NULL) {
            int fieldsAddress = Mem.load(current + 4 * TYPE_FIELDS);
            int fieldCount = Mem.load(fieldsAddress + 4 * ARRAY_LENGTH);
            for (int i = 0; i < fieldCount; i++) {
                int fieldAddress = Mem.load(
                        fieldsAddress + 4 * ARRAY_DATA + 4 * i);
                int isReference =
                        Mem.load(fieldAddress + 4 * FIELD_REFERENCE_FLAG);
                int fieldIndex = Mem.load(fieldAddress + 4 * FIELD_INDEX);
                int flags = Mem.load(fieldAddress + 4 * ENTRY_FLAGS);
                if (isReference == TRUE && !Modifier.isStatic(flags)) {
                    Mem.store(NULL,
                            address + 4 * OBJECT_FIELDS + 4 * fieldIndex);
                }
            }
            
            // check fields defined in parent class
            current = Mem.load(current + 4 * TYPE_SUPER_TYPE);
        }

        // set class, lock and id hash code
        Mem.store(address, address + 4 * OBJECT_HASHCODE);
        Mem.store(NULL, address + 4 * OBJECT_LOCK);
        Mem.store(entry, address + 4 * OBJECT_TYPE);

        // push address
        Stack.pushPointer(address);
        
        Reg.instruction += 3;
    }

    /**
     * Return the width of an array instruction based on the given type
     */
    private static int getWidth(int type) {
        switch (type) {
            case T_BOOLEAN:
            case T_BYTE:
                return 1;
                
            case T_SHORT:
            case T_CHAR:
                return 2;
                
            case T_FLOAT:
            case T_INT:
                return 4;
                
            case T_DOUBLE:
            case T_LONG:
                return 8;
        }
        throw new IllegalStateException("Invalid array type: " + type);
    }
    
}









