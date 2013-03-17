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
 * Check that the specified offset is a valid location
 * in the given object, and is not a local variable
 * or stack frame slot. The object id and size are also
 * checked here, they are written to directly by the
 * allocator and collector.
 */
static void checkOffset(Ref obj, int offset) {
    // check offset is within bounds
    int size = obj[OBJECT_SIZE].i;
    if (offset <= OBJECT_SIZE || offset >= size) {
        printf("Invalid offset: %d (%d, %d)\n", offset, OBJECT_SIZE, size);
        debugTrace();
        exit(1);
    }

    // check header
    int id = obj[OBJECT_ID].i;
    switch (id) {
        case HEADER_INSTANCE:
        case HEADER_OBJECT_ARRAY:
        case HEADER_STATIC_FIELDS:
        case HEADER_DATA_ARRAY:
            break;
        case HEADER_STACK_FRAME:
            if (offset >= FRAME_LOCALS) {
                printf("Invalid attempt to access frame values\n");
                debugTrace();
                exit(1);
            }
            break;
        default:
            printf("Invalid id: 0x%X\n", id);
            exit(1);
            break;
    }
}

/*
 * Return true if the specified offset can hold a reference,
 * false otherwise.
 */
static int referenceAt(Ref obj, int offset) {
    checkOffset(obj, offset); // make sure offset is in valid range

    // check header
    switch (offset) {
        case OBJECT_TYPE:
        case OBJECT_LOCK:
            return TRUE;
        case OBJECT_HASHCODE:
            return FALSE;
    }

    // object type dependent mappings
    Ref map, type;
    char* flags;
    switch (obj[OBJECT_ID].i) {
        case HEADER_INSTANCE:
            type = obj[OBJECT_TYPE].r;
            map = type[TYPE_INSTANCE_MAP].r;
            flags = (char*) (map + ARRAY_DATA);
            return flags[offset - OBJECT_FIELDS];
        case HEADER_OBJECT_ARRAY:
            if (offset == ARRAY_LENGTH) { return FALSE; }
            return TRUE;
        case HEADER_STATIC_FIELDS:
            if (offset == STATICS_MAP) { return TRUE; }
            map = obj[STATICS_MAP].r;
            flags = (char*) (map + ARRAY_DATA);
            return flags[offset - STATICS_FIELDS];
        case HEADER_DATA_ARRAY:
            return FALSE;
        case HEADER_STACK_FRAME:
            if (offset < FRAME_LOCALS) {
                return offset == FRAME_RETURN_FRAME
                        || offset == FRAME_METHOD;
            }
            break;
    }
    printf("object id: 0x%X\n", obj[OBJECT_ID].i);
    printf("offset: %d\n", offset);
    printf("Error. This statement should not be reached!\n");
    exit(1);
}

/*
 * Check that the field at the specified offset can contain
 * a value of the type indicated by the "isReference" flag.
 */
static void checkField(Ref obj, int offset, int isReference) {
    if (referenceAt(obj, offset) != isReference) {
        debugTrace();
        if (isReference) { printf("Memory violation: Not reference\n"); }
        else { printf("Memory violation: Reference\n"); }
        printf("obj: %p\n", obj);
        printf("offset: %d\n", offset);
        exit(1);
    }
}

/*
 * Return an integer value from the specified object field
 */
Int getInt(Ref obj, int offset) {
    checkField(obj, offset, FALSE);
    return obj[offset].i;
}

/*
 * Return a float value from the specified object field
 */
Float getFloat(Ref obj, int offset) {
    checkField(obj, offset, FALSE);
    return obj[offset].f;
}

/*
 * Return a reference from the specified object field
 */
Ref getRef(Ref obj, int offset) {
    checkField(obj, offset, TRUE);
    Ref result = obj[offset].r;
    checkReference(result);
    return result;
}

/*
 * Store an integer value to an object field
 */
void setInt(Ref obj, int offset, Int value) {
    checkField(obj, offset, FALSE);
    obj[offset].i = value;
}

/*
 * Store a float value to an object field
 */
void setFloat(Ref obj, int offset, Float value) {
    checkField(obj, offset, FALSE);
    obj[offset].f = value;
}

/*
 * Store a reference value to an object field
 */
void setRef(Ref obj, int offset, Ref ref) {
    checkField(obj, offset, TRUE);
    checkReference(ref);
    obj[offset].r = ref;
}

