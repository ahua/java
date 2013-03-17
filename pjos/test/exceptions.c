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
 * This method is used whenever the system needs to throw an exception
 * while trying to execute a java bytecode instruction. The given method
 * offset indicates a field of the core object referring to a static
 * method which throws the desired exception. A new frame is created on
 * top of the existing stack and this method is executed.
 */
void throwException(int offset) {
    Ref method = getRef(core, offset);
    Ref coreType = getRef(core, OBJECT_TYPE);
    executeMethod(coreType, method, 0);
}

/*
 * Return true if type A represents a subclass of type B.
 * B may be an unresolved type entry instead of a valid type.
 */
static int isSubClassOf(Ref a, Ref b) {
    // check if b is unresolved
    int id = getInt(b, ENTRY_ID);
    int unresolved = (id == ID_UNRESOLVED_TYPE);
    Ref bname = (unresolved) ? getRef(b, ENTRY_NAME) : NULL;

    // check a and all its superclasses against b
    Ref type = a;
    while (type != NULL) {
        if (type == b) { return TRUE; }
        if (unresolved) {
            Ref name = getRef(type, TYPE_NAME);
            if (name == bname) { return TRUE; }
        }
        type = getRef(type, TYPE_SUPER_TYPE); // check super type
    }
    return FALSE;
}

/*
 * Search for an appropriate handler in the current method
 * and return true if one is found, false otherwise.
 */
static int searchForHandler(Ref exception, Ref exceptionType) {
    // make sure exception table exists
    Ref exceptionTable = getRef(method, METHOD_EXCEPTIONS);
    if (exceptionTable == NULL) { return FALSE; }

    // loop through exception table entries
    int length = getInt(exceptionTable, ARRAY_LENGTH);
    unsigned short* ps = (unsigned short*) (exceptionTable + ARRAY_DATA);
    int i = 0;
    for (; i < length; i += 4) {
        // read table entry
        int start_pc = ps[i];
        int end_pc = ps[i + 1];
        int handler_pc = ps[i + 2];
        int catch_type = ps[i + 3];

        // check range
        Ref catchTypeEntry = getPoolEntry(catch_type);
        int inRange = (pc >= start_pc) && (pc < end_pc);
        int handled = (catch_type == 0)
                || isSubClassOf(exceptionType, catchTypeEntry);
        if (inRange && handled) {
            pc = handler_pc;
            int frameSize = frame[OBJECT_SIZE].i;

            // clear stack
            Var* max = (Var*) (frame + frameSize);
            for (; stack < max; stack++) { stack->flag = FALSE; }
            pushRef(exception);
            return TRUE;
        }
    }
    return FALSE;
}

/*
 * If the current method is synchronized, unlock it
 */
static void unlockIfSynchronized() {
    int flags = getInt(method, ENTRY_FLAGS);
    if (flags && ACC_SYNCHRONIZED) {
        Ref lock = getRef(frame, OBJECT_LOCK);
        if (lock != NULL) { unlockLock(lock); }
    }
}

/*
 * Execute the ATHROW instruction
 */
void op_athrow() {
    // check for null pointer
    Ref exception = popRef();
    if (exception == NULL) {
        throwException(CORE_THROW_NULL_POINTER);
        return;
    }

    // search for exception handler
    Ref exceptionType = getRef(exception, OBJECT_TYPE);
    while (frame != NULL) {
        // search in current method...
        if (searchForHandler(exception, exceptionType)) { return; }

        // ...otherwise pop to previous method frame
        unlockIfSynchronized();
        Ref previous = getRef(frame, FRAME_RETURN_FRAME);
        setRef(thread, THREAD_FRAME, previous);
        if (previous != NULL) { loadRegisters(); }
    }

    // no handler was found in any frame so exit thread
    thread = NULL;
    printf("Illegal State: thread exiting\n");
    exit(1);
}

