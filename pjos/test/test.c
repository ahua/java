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

#include <stdio.h>
#include <time.h>
#include "interpreter.h"

/*
 * Define the initial offset in bytes
 * of the core object. Zero should not be used
 * here to avoid conflicting with the value
 * for NULL.
 */
#define CORE_OFFSET 4
#define NULL_OFFSET 0

/*
 * The idle sleep parameters, 0.2 seconds
 */
static struct timespec interval = {0, 200000000};
static struct timespec remaining;

/*
 * Settings for java heap
 */
#define MEM_SIZE 8*1024*1024
static char mem[MEM_SIZE];     // java heap space

/*
 * Used by the floppy image methods
 */
static FILE* fimage;

/*
 * Convert the given offset to a reference
 */
static void convert(Word* pw) {
    int offset = pw->i;
    if (offset == NULL_OFFSET) {
        pw->r = NULL;
    } else {
        pw->r = (Word*) (heap + offset - CORE_OFFSET);

        // check pointer for validity
        int id = pw->r->i;
        switch (id) {
            case HEADER_INSTANCE:
            case HEADER_DATA_ARRAY:
            case HEADER_OBJECT_ARRAY:
            case HEADER_STATIC_FIELDS:
            case HEADER_STACK_FRAME:
                break;
            default:
                printf("convert(): Invalid object type at offset: 0x%X\n", offset);
                exit(1);
                break;
        }
    }
}

/*
 * Return a pointer to the first map byte from
 * the type at the given offset.
 */
static char* findMapByOffset(Ref type) {
    int mapOffset = type[TYPE_INSTANCE_MAP].i;
    int* map = (int*) (heap + mapOffset - CORE_OFFSET);
    return (char*) (map + ARRAY_DATA);
}

/*
 * Return a pointer to the first map byte from
 * the given type.
 */
static char* findMapByReference(Ref type) {
    Ref map = type[TYPE_INSTANCE_MAP].r;
    return (char*) (map + ARRAY_DATA);
}

/*
 * Convert all the pointers in the given instance
 */
static void convertInstance(Ref obj) {
    // find the map for this instance type
    int offset = ((char*) obj) - heap;
    int typeOffset = obj[OBJECT_TYPE].i;
    Ref type = (Ref) (heap + typeOffset - CORE_OFFSET);
    char* flag = (typeOffset > offset)
            ? findMapByOffset(type)
            : findMapByReference(type);

    // convert pointers indicated by the map flags
    int index = OBJECT_FIELDS;
    int size = obj[OBJECT_SIZE].i;
    for (; index < size; index++) {
        if (*(flag++)) { convert(obj + index); }
    }
}

/*
 * Convert all the pointers in the given object array
 */
static void convertObjectArray(Ref array) {
    Word* next = array + ARRAY_DATA;
    Word* max = array + array[OBJECT_SIZE].i;
    for (; next < max; next++) {
        convert(next);
    }
}

/*
 * Convert all the pointers in the static fields object
 */
static void convertStaticFields(Ref sf) {
    convert(sf + STATICS_MAP);
    Ref map = sf[STATICS_MAP].r;
    char* flag = (char*) (map + ARRAY_DATA);
    int index = STATICS_FIELDS;
    int size = sf[OBJECT_SIZE].i;
    for (; index < size; index++) {
        if (*(flag++)) { convert(sf + index); }
    }
}

/*
 * Convert all the pointers in the given stack frame
 */
static void convertStackFrame(Ref frame) {
    convert(frame + FRAME_RETURN_FRAME);
    convert(frame + FRAME_METHOD);

    // check tagged values
    int size = frame[OBJECT_SIZE].i;
    int index = FRAME_LOCALS;
    for (; index < size; index += 2) {
        if (frame[index + 1].i) { convert(frame + index); }
    }
}

/*
 * Convert all the pointers in the specified object
 * from relative values to physical values.
 */
static void convertObject(Ref obj) {
    switch (obj[OBJECT_ID].i) {
        case HEADER_INSTANCE:
            convertInstance(obj);
            break;
        case HEADER_DATA_ARRAY:
            // no more references to check
            break;
        case HEADER_OBJECT_ARRAY:
            convertObjectArray(obj);
            break;
        case HEADER_STATIC_FIELDS:
            convertStaticFields(obj);
            break;
        case HEADER_STACK_FRAME:
            convertStackFrame(obj);
            break;
        default:
            printf("object type not recognised: 0x%X\n", obj[OBJECT_ID].i);
            exit(1);
    }

    // convert lock and type (common to all objects)
    convert(obj + OBJECT_LOCK);
    convert(obj + OBJECT_TYPE);
    // Note: convert methods above rely on type being unconverted
    //       so they know what state the fields of type are in.
}

/*
 * Parse the java heap and set all the pointers to hold
 * physical addresses instead of relative values. Return
 * a pointer to the next available object location.
 */
static void convertPointers() {
    Ref next = core;
    while (next[OBJECT_ID].i != HEADER_END) {
        convertObject(next);
        next += next[OBJECT_SIZE].i;
    }
    core[CORE_NEXT].i = next - core;
}

/*
 * Load the file "memory.bin" into the java heap.
 */
static void loadMemoryImage() {
    FILE* file;
    file = fopen("memory.bin", "r");
    if (file == NULL) {
        fprintf(stderr, "Can't find 'memory.bin'\n");
        exit(1);
    } else {
        int value;
        char* next = mem;
        while ((value = getc(file)) != EOF) {
            *(next++) = value;
        }
        printf("loaded %d bytes into java heap\n", next - mem);
        int* after = (int*) next;
        *after = HEADER_END;         // write zero after last object
    }
}

/*
 * Open the floppy image file
 */
static void openFloppy() {
    fimage = fopen("floppy.bin", "rw");
    if (fimage == NULL) {
        fprintf(stderr, "Can't find 'floppy.bin'\n");
        debugTrace();
        exit(1);
    }
}

/*
 * Read an unsigned byte value from the floppy image
 */
int readFromFloppy(int pos) {
    if (fimage == NULL) { openFloppy(); }
    int k = fseek(fimage, pos, SEEK_SET);
    if (k > 0) {
        printf("fseek returned: %d\n");
        exit(1);
    }
    int result = getc(fimage) & 0xFF;
    //printf("readFromFloppy(%d): 0x%X\n", pos, result);
    return result;
}

/*
 * Read an unsigned 8-bit ascii char from the console
 */
char readFromConsole() {
    return getc(stdin);
}

/*
 * Sleep for a short time
 */
void idleSleep() {
    nanosleep(&interval, &remaining);
}

/*
 * Print a debug line
 */
void debugLine(Ref string) {
    debugString(string);
    printf("\n");
}

/*
 * Print the contents of a java string
 */
void debugString(Ref string) {
    int length = string[STRING_LENGTH].i;
    char* c = ((char*) (string[STRING_CHARS].r + ARRAY_DATA));
    int i = 0;
    while (i++ < length) {
        printf("%c", *c);
        c += 2;
    }
}

/*
 * Print a stack trace of the current thread for debug purposes
 */
void debugTrace() {
    Ref sf = frame;
    for (; sf != NULL; sf = sf[FRAME_RETURN_FRAME].r) {
        printf("  at ");
        Ref method = sf[FRAME_METHOD].r;
        Ref type = method[ENTRY_OWNER].r;
        debugString(type[TYPE_NAME].r);
        printf(".");
        debugString(method[ENTRY_NAME].r);
        printf("(");
        Ref source = type[TYPE_SOURCE].r;
        if (source == NULL) {
            printf("Unknown Source");
        } else {
            debugString(source);
            int pc = sf[FRAME_PC].i;
            Ref lnt = method[METHOD_LINE_NUMBERS].r;
            unsigned short* table = (unsigned short*) (lnt + ARRAY_DATA);
            if (table != NULL) {
                int length = lnt[ARRAY_LENGTH].i;
                int line = -1;
                int index = 0;
                while (index < length) {
                    unsigned short startEntry = table[index++];
                    unsigned short lineEntry = table[index++];
                    if (pc >= startEntry) { line = lineEntry; }
                    if (pc < startEntry) { break; }
                }
                if (line >= 0) { printf(":%d", line); }
            }
        }
        printf(")\n");
    }
}

/*
 * Start the test VM
 */
int main() {
    // check type sizes
    Byte b;
    Short s;
    Char c;
    Int i;
    long long int l;
    Float f;
    double d;
    printf("Byte: %d\n", sizeof(b));
    printf("Short: %d\n", sizeof(s));
    printf("Char: %d\n", sizeof(c));
    printf("Int: %d\n", sizeof(i));
    printf("long long int: %d\n", sizeof(l));
    printf("Float: %d\n", sizeof(f));
    printf("double: %d\n", sizeof(d));

    // start interpreter
    heap = mem;
    heapLimit = heap + MEM_SIZE;
    loadMemoryImage();
    core = (Ref) heap;
    convertPointers();
    execute();
    return 0;
}
