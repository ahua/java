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

// Used to record the last bytecodes executed for debugging
unsigned char dbgCodes[50];
int dbgIndex = 0;

// these values are set by the platform specific code
char* heap;          // start of heap
char* heapLimit;     // end of heap

// the virtual registers for the VM
Ref core;            // reference to core object
Ref thread;          // reference to current thread
Ref frame;           // reference to current frame
Ref method;          // reference to current method
Ref pool;            // reference to constant pool
Var* locals;         // reference to locals of current frame
Var* stack;          // reference to current top of stack
unsigned char* code; // points to first byte code of current method
int pc;              // program counter

/*
 * Return the constant pool entry at the specified index
 */
Ref getPoolEntry(int index) {
    return getRef(pool, ARRAY_DATA + index);
}

/*
 * Load the values of the virtual registers using the
 * current value of the thread register.
 */
void loadRegisters() {
    frame = getRef(thread, THREAD_FRAME);
    method = getRef(frame, FRAME_METHOD);
    pool = getRef(method, METHOD_POOL);
    locals = (Var*) (frame + FRAME_LOCALS);
    int sp = getInt(frame, FRAME_SP);
    stack = (Var*) ((char*) frame + sp);
    code = (unsigned char*) (getRef(method, METHOD_CODE) + ARRAY_DATA);
    pc = getInt(frame, FRAME_PC);
}

/*
 * Save the values of the virtual registers to the
 * current thread structure.
 */
void saveRegisters() {
    setInt(frame, FRAME_PC, pc);
    int sp = (char*) stack - (char*) frame;
    setInt(frame, FRAME_SP, sp);
}

/*
 * Return the unsigned 8-bit index at the specified
 * offset from the current instruction.
 */
unsigned char u8(int offset) {
    return code[pc + offset];
}

/*
 * Return the signed 8-bit index at the specified
 * offset from the current instruction.
 */
char s8(int offset) {
    return (char) u8(offset);
}

/*
 * Return the unsigned 16-bit index at the specified
 * offset from the current instruction.
 */
unsigned short u16(int offset) {
    return (u8(offset) << 8)
            | u8(offset + 1);
}

/*
 * Return the signed 16-bit index at the specified
 * offset from the current instruction.
 */
short s16(int offset) {
    return (short) u16(offset);
}

/*
 * Return the signed 32-bit index at the specified
 * offset from the current instruction.
 */
int s32(int offset) {
    return (u8(offset) << 24)
            | (u8(offset + 1) << 16)
            | (u8(offset + 2) << 8)
            | u8(offset + 3);
}

/*
 * Retrieve the constant pool entry at the given index. If the id of the
 * entry does not match the given id call the resolve method of the core
 * object and return null. The code in the core object will replace
 * the placeholder entry with the resolved object.
 */
Ref resolve(int index, int id) {
    Ref entry = getPoolEntry(index);
    if (getInt(entry, ENTRY_ID) != id) {
        Ref type = getRef(core, OBJECT_TYPE);
        Ref method = getRef(core, CORE_RESOLVE_METHOD);
        executeMethod(type, method, 0);
        return NULL;
    }
    return entry;
}

/*
 * This function contains the main interpreter loop. Java
 * ByteCode instructions are executed one at a time. Before
 * calling this function the core register should contain
 * the correct value.
 */
execute() {
    // initialise register values
    printf("Interpreter started\n");
    initCollector();
    thread = getRef(core, CORE_RUNNING);
    loadRegisters();   

    // used to mimic a time slice
    int maxSize = 50;
    int count = 0;
    
    // execute the next instruction, indefinitely
    void (*implementation)();
    int index;
    unsigned char bytecode;
    for (;;) {
        bytecode = code[pc];
        dbgCodes[dbgIndex++] = bytecode;
        if (dbgIndex == 50) { dbgIndex = 0; }
        //printf("0x%X\n", bytecode);
        implementation = distributor[bytecode];
        implementation();
        count++;

        // check if another thread needs to be scheduled
        if (thread == NULL || count >= maxSize) {
            if (thread != NULL) { saveRegisters(); }
            scheduleNextThread();
            //wakeSleepingThreads();
            count = 0;
        }
    }
}
