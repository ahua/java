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

static Word* current;           // the start of the current space
static Word* currentLimit;      // the end of the current space
static Word* other;             // the start of the unused space
static int size;                // the number of slots in a space
static int count;               // the number of objects evacuated
static Word* next;              // points to next free space
static Ref scan;                // points to next object to be scanned

/*
 * Initialise settings for object allocation/gc.
 */
void initCollector() {
    current = (Word*) heap;
    Word* limit = (Word*) heapLimit;
    size = (limit - current) / 2;
    other = current + size;
    currentLimit = other;
}

/*
 * Return only if the given pointer points
 * to an object with a valid object header
 */
static void checkHeader(Ref r) {
    int id = r[OBJECT_ID].i;
    switch (id) {
        case HEADER_INSTANCE:
        case HEADER_OBJECT_ARRAY:
        case HEADER_STATIC_FIELDS:
        case HEADER_STACK_FRAME:
        case HEADER_DATA_ARRAY:
        case HEADER_FORWARD:
            return;
        
        default:
            printf("Invalid object id: 0x%X\n", id);
            exit(1);
    }
}

/*
 * Return only if the given reference refers to
 * a valid object.
 */
void checkReference(Ref r) {
    if (r == NULL) { return; }
    checkHeader(r);
    Ref next = r + r[OBJECT_SIZE].i;
    if (next->i != HEADER_END) { checkHeader(next); }
}

/*
 * Evacuate the specified object
 */
static Ref evacuate(Ref object) {
    if (object == NULL) {
        printf("evacuate() called with NULL\n");
        exit(1);
    }
    checkReference(object);

    // copy object, set forwarding header and pointer
    int size = object[OBJECT_SIZE].i;
    Word* from = object;
    Word* to = next;
    Word* max = object + size;
    while (from < max) { *(to++) = *(from++); }
    object[OBJECT_ID].i = HEADER_FORWARD;
    object[OBJECT_SIZE].r = next; // forwarding pointer

    // update count and next pointer
    Ref result = next;
    next += size;
    next->i = HEADER_END; // mark the last entry
    count++;
    return result;
}

/*
 * Check the pointer at the specified index within the current scan
 * object. Evacuate the target object if it has not already been moved,
 * otherwise just update the pointer value to the new location. Ignore
 * null pointer values.
 */
static void check(int index) {
    Ref ref = scan[index].r;
    if (ref == NULL) { return; }
    Ref evacuated = (ref[OBJECT_ID].i == HEADER_FORWARD)
            ? ref[OBJECT_SIZE].r // forwarding pointer
            : evacuate(ref);
    scan[index].r = evacuated;
}

/*
 * Check the references in an object array
 */
static void checkObjectArray() {
    //printf("checkObjectArray()\n");
    int size = scan[OBJECT_SIZE].i;
    int i = ARRAY_DATA;
    while (i < size) {
        check(i++);
    }
}

/*
 * Check the fields of an instance
 */
static void checkInstanceFields() {
    //printf("checkInstanceFields()\n");
    Ref type = getRef(scan, OBJECT_TYPE);
    Ref map = getRef(type, TYPE_INSTANCE_MAP);
    char* flag = (char*) (map + ARRAY_DATA);
    int size = scan[OBJECT_SIZE].i;
    int i = OBJECT_FIELDS;
    for (; i < size; i++) {
        if (*(flag++)) { check(i); }
    }
}

/*
 * Check the fields of a static fields object
 */
static void checkStaticFields() {
    //printf("checkStaticFields()\n");
    check(STATICS_MAP);
    Ref map = getRef(scan, STATICS_MAP);
    char* flag = (char*) (map + ARRAY_DATA);
    int size = scan[OBJECT_SIZE].i;
    int i = STATICS_FIELDS;
    for (; i < size; i++) {
        if (*(flag++)) { check(i); }
    }
}

/*
 * Check the fields of a stack frame
 */
static void checkStackFrame() {
    //printf("checkStackFrame()\n");
    check(FRAME_RETURN_FRAME);
    check(FRAME_METHOD);

    // check tagged values
    int i = FRAME_LOCALS;
    int size = scan[OBJECT_SIZE].i;
    for (; i < size; i += 2) {
        if (scan[i + 1].i) { check(i); }
    }
}

/*
 * Return the percentage of memory used
 */
static int used() {
    return (core[CORE_NEXT].i * 100) / size;
}

/*
 * Run the garbage collector.
 *
 * This is a copy collector which divides the heap into 2 spaces
 * of equal size.
 */
static void gc() {
    // save execution state and flip spaces
    saveRegisters();
    Word* temp = current;
    current = other;
    other = temp;
    currentLimit = current + size;

    // initialise next and scan pointers, restart count
    next = current;
    scan = next;
    count = 0;

    // start by evacuating core object
    evacuate(core);
    core = current;

    // scan evacuated objects until all live objects have been evacuated.
    // (ie. until scan pointer catches up to next pointer)
    while (scan < next) {
        //printf("scan: 0x%X\n", scan[OBJECT_HASHCODE].i);
        check(OBJECT_LOCK);
        check(OBJECT_TYPE);
        int id = scan[OBJECT_ID].i;
        switch (id) {
            case HEADER_INSTANCE:       checkInstanceFields(); break;
            case HEADER_OBJECT_ARRAY:   checkObjectArray();    break;
            case HEADER_STATIC_FIELDS:  checkStaticFields();   break;
            case HEADER_STACK_FRAME:    checkStackFrame();     break;
            case HEADER_DATA_ARRAY:                            break;
            default:
                printf("gc(): Invalid object id: 0x%X\n", id);
                exit(1);
                break;
        }
        scan += scan[OBJECT_SIZE].i;    // set scan to next object
    }
    core[CORE_NEXT].i = next - current;
    printf("gc() - %d objects evacuated, %d%% used\n", count, used());
    thread = core[CORE_RUNNING].r;
    loadRegisters();
}

/*
 * Allocate space for a new object on the heap and return a reference to it.
 * If not enough memory is available, run the garbage collector and return
 * null. The object header and size are initialised, and all words in the
 * object are set to zero.
 *
 * Code calling this method should check if null is returned, this means
 * garbage collection has taken place and all previous memory addresses
 * have become invalid.
 */
Ref allocate(int numWords, int id) {
    // find the next available address, run gc if not enough space
    Word* result = current + core[CORE_NEXT].i;
    Word* next = result + numWords;
    if (next >= currentLimit) {
        gc();
        return NULL;
    }

    // otherwise initialise the new object
    result[OBJECT_ID].i = id;
    result[OBJECT_SIZE].i = numWords;
    Word* pw = result + OBJECT_HASHCODE;
    for (; pw < next; pw++) {
        pw->i = 0x00; // rest of object gets zeroed
    }

    // update core object to point to next free space
    core[CORE_NEXT].i = next - current;
    next->i = HEADER_END; // mark last object
    return result;
}

