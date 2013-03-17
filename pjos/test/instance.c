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

#include "interpreter.h"

/*
 * Return true if type b is amoung the listed interfaces of type a
 */
static int isDeclaredImplementationOf(Ref a, Ref b) {
    Ref interfaces = getRef(a, TYPE_INTERFACE_TYPES);
    if (interfaces != NULL) {
        Int length = getInt(interfaces, ARRAY_LENGTH);
        int i = 0;
        for (; i < length; i++) {
            Ref next = getRef(interfaces, ARRAY_DATA + i);
            if (next == b) { return TRUE; }
        }
    }
    return FALSE;
}

/*
 * Return true if type a is either a subclass or an implementation
 * of type b.
 */
static int isSubClassOrImplementationOf(Ref a, Ref b) {
    while (a != NULL) {
        // simple checks
        if (a == b) { return TRUE; }
        if (isDeclaredImplementationOf(a, b)) { return TRUE; }

        // array types - check components
        Ref ca = getRef(a, TYPE_COMPONENT_TYPE);
        if (ca != NULL) {
            a = ca;
            b = getRef(b, TYPE_COMPONENT_TYPE);
            if (b == NULL) { return FALSE; } // b is not an array
            // next iteration will compare component types
        }

        // next iteration will compare super type of a to b
        else { a = getRef(a, TYPE_SUPER_TYPE); }
    }
    return FALSE;
}

/*
 * Return the width of an array instruction based on the given atype
 */
static int getWidth(int atype) {
    switch (atype) {
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
    printf("Invalid array type encountered: %d\n", atype);
    exit(1);
}

/*
 * Execute the MULTIANEWARRAY instruction
 */
void op_multianewarray() {
	printf("multianewarray not implemented!!!\n");
    exit(1);
}

/*
 * Execute the ANEWARRAY instruction
 */
void op_anewarray() {
    Ref type = resolve(u16(1), ID_TYPE);
    if (type == NULL) { return; } // rollback, gc done
    Ref arrayType = getRef(type, TYPE_ARRAY_TYPE);

    // resolve array type
    if (arrayType == NULL) {
        Ref target = getRef(core, OBJECT_TYPE);
        Ref method = getRef(core, CORE_RESOLVE_METHOD);
        executeMethod(target, method, 0);
        return; // rollback
    }

    // allocate space
    int length = peekInt(); // don't pop yet in case gc runs
    int numWords = ARRAY_DATA + length;
    Ref array = allocate(numWords, HEADER_OBJECT_ARRAY);
    if (array == NULL) { return; } // rollback, gc done
    popInt(); // pop length

    // initialise array object and push on stack
    int hash = (char*) array - (char*) core;
    setInt(array, OBJECT_HASHCODE, hash);
    setRef(array, OBJECT_TYPE, arrayType);
    setInt(array, ARRAY_LENGTH, length);
    pushRef(array);
    pc += 3;
}

/*
 * Execute the NEWARRAY instruction
 */
void op_newarray() {
    // figure out the required size
    int atype = u8(1);
    int width = getWidth(atype);
    int length = peekInt(); // don't pop in case rolled back later
    int numBytes = length * width;
    int extra = ((numBytes % 4) == 0) ? 0 : 1;
    int numDataWords = (numBytes / 4) + extra;
    int numWords = numDataWords + ARRAY_DATA;

    // allocate space
    Ref array = allocate(numWords, HEADER_DATA_ARRAY);
    if (array == NULL) { return; } // rollback, gc done
    popInt(); // can pop the length now

    // initialise new array and push address on stack
    int hash = (char*) array - (char*) core;
    Ref type = getRef(core, CORE_ARRAYS + atype);
    setInt(array, OBJECT_HASHCODE, hash);
    setRef(array, OBJECT_TYPE, type);
    setInt(array, ARRAY_LENGTH, length);
    pushRef(array);
    pc += 2;
}

/*
 * Execute the NEW instruction
 */
void op_new() {
    // resolve the type
    Ref type = resolve(u16(1), ID_TYPE);
    if (type == NULL) { return; } // rollback, gc done

    // allocate space
    int numFields = getInt(type, TYPE_INSTANCE_FIELD_COUNT);
    int numWords = OBJECT_FIELDS + numFields;
    Ref object = allocate(numWords, HEADER_INSTANCE);
    if (object == NULL) { return; } // rollback, gc done
    
    // set headers and push on stack
    int hash = (char*) object - (char*) core;
    setInt(object, OBJECT_HASHCODE, hash);
    setRef(object, OBJECT_TYPE, type);
    pushRef(object);
    pc += 3;
}

/*
 * Execute the CHECKCAST instruction
 */
void op_checkcast() {
    Ref entry = getPoolEntry(u16(1));
    Ref object = peekRef();
    if (object != NULL) {
        Ref type = getRef(object, OBJECT_TYPE);
        if (!isSubClassOrImplementationOf(type, entry)) {
            throwException(CORE_THROW_CLASS_CAST);
            return;
        }
    }
    pc += 3;
}

/*
 * Execute the INSTANCEOF instruction
 */
void op_instanceof() {
    Ref entry = getPoolEntry(u16(1));
    Ref object = popRef();
    int value = FALSE;
    if (object != NULL) {
        Ref type = getRef(object, OBJECT_TYPE);
        value = isSubClassOrImplementationOf(type, entry);
    }
    pushInt(value);
    pc += 3;
}
