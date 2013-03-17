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
 * Contains implementations for the checkcast and instanceof instructions.
 */
class Cast implements Constants {

    /**
     * instanceof instruction
     */
    static void op_instanceof() {
        // read args
        int index = Instruction.twoByteCodes(1);
        int entry = Mem.load(Reg.pool + 4 * index);
        int objectAddress = Stack.popPointer();

        // fail by default
        int value = 0;

        // check the object type and its super types
        if (objectAddress != NULL) {
            int type = Mem.load(objectAddress + 4 * OBJECT_TYPE);
            if (isSubClassOrImplementationOf(type, entry)) { value = 1; }
        }
        Stack.pushData(value);
        Reg.instruction += 3;
    }
    
    /**
     * checkcast instruction
     */
    static void checkcast() {
        // read args
        int index = Instruction.twoByteCodes(1);
        int entry = Mem.load(Reg.pool + 4 * index);
        int objectAddress = Stack.peekPointer(0);

        // throw an exception if the cast cannot be allowed
        if (objectAddress != NULL) {
            int type = Mem.load(objectAddress + 4 * OBJECT_TYPE);
            if (!isSubClassOrImplementationOf(type, entry)) {
                Exceptions.throwException(CORE_THROW_CLASS_CAST);
                return;
            }
        }
        Reg.instruction += 3;
    }
    
    /**
     * Return true if typea is either a subclass or
     * an implementation of typeB.
     */
    private static boolean isSubClassOrImplementationOf(int typea, int typeb) {
        int a = typea;
        int b = typeb;
        while (a != NULL) {
            // simple checks
            if (a == b) { return true; }
            if (isDeclaredImplementationOf(a, b)) { return true; }
            
            // handle array types
            int compa = Mem.load(a + 4 * TYPE_COMPONENT_TYPE);
            if (compa != NULL) {
                a = compa;
                b = Mem.load(b + 4 * TYPE_COMPONENT_TYPE);
                if (b == NULL) { return false; } // b is not an array
                // next loop iteration will compare component type of a
                // with component type of b
            } else {
                // next loop iteration will compare super type of a with b
                a = Mem.load(a + 4 * TYPE_SUPER_TYPE);
            }
        }
        return false;
    }
    
    /**
     * @return true if typeb is amoung the listed interfaces of typea
     */
    private static boolean isDeclaredImplementationOf(int typea, int typeb) {
        int interfaces = Mem.load(typea + 4 * TYPE_INTERFACE_TYPES);
        if (interfaces != NULL) {
            int length = Mem.load(interfaces + 4 * ARRAY_LENGTH);
            for (int i = 0; i < length; i++) {
                int interfaceType = Mem.load(
                        interfaces + 4 * ARRAY_DATA + 4 * i);
                if (interfaceType == typeb) { return true; }
            }
        }
        return false;
    }
     
}
