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
 * Execute the GETFIELD instruction
 */
void op_getfield() {
    Ref field = resolve(u16(1), ID_FIELD);
    if (field == NULL) { return; } // rollback

    // check for null pointer
    Ref object = popRef();
    if (object == NULL) {
        throwException(CORE_THROW_NULL_POINTER);
        return;
    }

    // push value
    Int index = getInt(field, FIELD_INDEX) + OBJECT_FIELDS;
    Int flag = getInt(field, FIELD_REFERENCE_FLAG);
    Int size = getInt(field, FIELD_SIZE);
    if (flag) { pushRef(getRef(object, index)); }
    else { pushInt(getInt(object, index)); }
    if (size == 2) { pushInt(getInt(object, index + 1)); }
    pc += 3;
}

/*
 * Execute the PUTFIELD instruction
 */
void op_putfield() {
    Ref field = resolve(u16(1), ID_FIELD);
    if (field == NULL) { return; } // rollback

    // read values from stack
    Int size = getInt(field, FIELD_SIZE);
    Int flag = getInt(field, FIELD_REFERENCE_FLAG);
    Int value2 = (size == 2) ? popInt() : 0;
    Word value1;
    if (flag) { value1.r = popRef(); }
    else { value1.i = popInt(); }
    Ref object = popRef();

    // check for null pointer
    if (object == NULL) {
        throwException(CORE_THROW_NULL_POINTER);
        return;
    }

    // store the values
    Int index = getInt(field, FIELD_INDEX) + OBJECT_FIELDS;
    if (flag) { setRef(object, index, value1.r); }
    else { setInt(object, index, value1.i); }
    if (size == 2) { setInt(object, index + 1, value2); }
    pc += 3;
}

/*
 * Execute the PUTSTATIC instruction
 */
void op_putstatic() {
    Ref field = resolve(u16(1), ID_FIELD);
    if (field == NULL) { return; } // rollback
    
    // read field values
    Ref type = getRef(field, ENTRY_OWNER);
    Ref statics = getRef(type, TYPE_STATICS);
    Int index = getInt(field, FIELD_INDEX);
    Int flag = getInt(field, FIELD_REFERENCE_FLAG);
    Int size = getInt(field, FIELD_SIZE);
    int offset = STATICS_FIELDS + index;
    
    // pop values from stack and store
    if (size == 2) { setInt(statics, offset, popInt()); }
    if (flag) { setRef(statics, offset, popRef()); }
    else { setInt(statics, offset, popInt()); }
    pc += 3;
}

/*
 * Execute the GETSTATIC instruction
 */
void op_getstatic() {
    Ref field = resolve(u16(1), ID_FIELD);
    if (field == NULL) { return; } // rollback

    // read field values
    Ref type = getRef(field, ENTRY_OWNER);
    Ref statics = getRef(type, TYPE_STATICS);
    Int index = getInt(field, FIELD_INDEX);
    Int flag = getInt(field, FIELD_REFERENCE_FLAG);
    Int size = getInt(field, FIELD_SIZE);
    int offset = STATICS_FIELDS + index;

    // read values and push on stack
    if (flag) { pushRef(getRef(statics, offset)); }
    else { pushInt(getInt(statics, offset)); }
    if (size == 2) { pushInt(getInt(statics, offset + 1)); }
    pc += 3;
}

