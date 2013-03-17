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
 * Contains code to implement exceptions
 */
class Exceptions implements Constants {

    /**
     * athrow instruction
     */
    static void athrow() {
        // check for null pointer
        int exception = Stack.popPointer();
        if (exception == NULL) {
            throwException(CORE_THROW_NULL_POINTER);
            return;
        }

        // search for exception handler
        int exceptionType = Mem.load(exception + 4 * OBJECT_TYPE);
        while (Reg.frame != NULL) {
            // search in current method...
            if (searchForHandler(exception, exceptionType)) { return; }

            // ...otherwise go to previous method frame

            // if method was synchronized, unlock
            unlockIfSynchronized();

            // pop to previous frame
            int previous = Mem.load(Reg.frame + 4 * FRAME_RETURN_FRAME);
            Mem.store(previous, Reg.thread + 4 * THREAD_FRAME);
            if (previous != NULL) { Reg.load(); }
        }

        // No handler was found in any frame so exit thread
        Reg.thread = NULL;
        throw new IllegalStateException(
                "Exiting thread 0x" + Integer.toHexString(Reg.thread)
                + " no handler found for exception type 0x"
                + Integer.toHexString(exceptionType));
    }

    /**
     * Search for an appropriate handler in the current method
     * and return true if one is found, false otherwise.
     */
    private static boolean searchForHandler(int exception, int exceptionType) {
        // make sure exception table exists
        int exceptionTable = Mem.load(Reg.method + 4 * METHOD_EXCEPTIONS);
        if (exceptionTable == NULL) { return false; }

        // loop through exception table entries
        int pc = Reg.instruction - Reg.code;
        int length = Mem.load(exceptionTable + 4 * ARRAY_LENGTH);
        int entry = exceptionTable + 4 * ARRAY_DATA;
        int limit = entry + 2 * length;
        while (entry < limit) {
            // read table entries
            int startpc = Mem.loadShort(entry) & 0x0000ffff;
            int endpc = Mem.loadShort(entry + 2) & 0x0000ffff;
            int handlerpc = Mem.loadShort(entry + 4) & 0x0000ffff;
            int catchtype = Mem.loadShort(entry + 6) & 0x0000ffff;

            // check range
            int catchTypeEntry = Mem.load(Reg.pool + 4 * catchtype);
            boolean inRange = (pc >= startpc) && (pc < endpc);
            boolean handled = (catchtype == 0)
                    || isSubClassOf(exceptionType, catchTypeEntry);
            if (inRange && handled) {
                Reg.instruction = Reg.code + handlerpc;
                int frameSize = Mem.load(Reg.frame + 4 * OBJECT_SIZE) * 4;
                Reg.stack = Reg.frame + frameSize;
                Stack.pushPointer(exception);
                return true;
            }
            entry += 8; // check 4 shorts at a time
        }
        return false;
    }

    /**
     * If the current method is synchronized, unlock it
     */
    private static void unlockIfSynchronized() {
        int flags = Mem.load(Reg.method + 4 * ENTRY_FLAGS);
        if (Modifier.isSynchronized(flags)) {
            int lock = Mem.load(Reg.frame + 4 * OBJECT_LOCK);
            if (lock != NULL) { Monitor.unlock(lock); }
        }
    }

    /**
     * Return true if type a represents a subclass of type b.
     * b may be an unresolved type entry instead of a valid type.
     */
    private static boolean isSubClassOf(int a, int b) {
        // check if b is unresolved
        int entryId = Mem.load(b + 4 * ENTRY_ID);
        boolean unresolved = (entryId == Entry.UNRESOLVED_TYPE);
        int nameb = (unresolved) ? Mem.load(b + 4 * ENTRY_NAME) : NULL;

        // check a and all its superclasses against b
        int type = a;
        while (type != NULL) {
            if (type == b) { return true; }
            if (unresolved) {
                int name = Mem.load(type + 4 * TYPE_NAME);
                if (name == nameb) { return true; }
            }
            type = Mem.load(type + 4 * TYPE_SUPER_TYPE); // check super type
        }
        return false;
    }

    /**
     * This method is used wherever the system needs to
     * throw an exception while trying to execute a java machine instruction.
     * The given method offset indicates a field of the core object which
     * refers to a static method which throws the desired exception. A new
     * frame is created on top of the existing stack and this method is
     * executed.
     * @param offset the offset of the exception method in the core object
     */
    static void throwException(int offset) {
        int method = Mem.load(Reg.core + 4 * offset);
        int target = Mem.load(Reg.core + 4 * OBJECT_TYPE);
        Invoke.executeMethod(target, method, 0);
    }

}









