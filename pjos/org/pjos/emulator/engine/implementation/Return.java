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

/**
 * Contains code to implement return instructions.
 */
class Return implements Constants {

    /**
     * ireturn instruction
     */
    static void ireturn() {
        returnValue();
    }
    
    /**
     * lreturn instruction
     */
    static void lreturn() {
        returnLong();
    }
    
    /**
     * freturn instruction
     */
    static void freturn() {
        returnValue();
    }
    
    /**
     * dreturn instruction
     */
    static void dreturn() {
        returnLong();
    }
    
    /**
     * areturn instruction
     */
    static void areturn() {
        int pointer = Stack.popPointer();
        previousFrame();
        Stack.pushPointer(pointer);
    }
            
    /**
     * return instruction
     */
    static void op_return() {
        previousFrame();
    }

    /**
     * Return a 32 bit data value
     */
    private static void returnValue() {
        int value = Stack.popData();
        previousFrame();
        Stack.pushData(value);
    }
    
    /**
     * Return a 64 bit data value
     */
    private static void returnLong() {
        long value = Stack.popLong();
        previousFrame();
        Stack.pushLong(value);
    }
    
    /**
     * Point current thread to previous frame
     */
    static void previousFrame() {
        // check synchronization
        int flags = Mem.load(Reg.method + 4 * ENTRY_FLAGS);
        if (Modifier.isSynchronized(flags)) {
            int lock = Mem.load(Reg.frame + 4 * OBJECT_LOCK);
            if (lock != NULL) { Monitor.unlock(lock); }
        }
        
        // Point thread to previous frame
        int previous = Mem.load(Reg.frame + 4 * FRAME_RETURN_FRAME);
        Mem.store(previous, Reg.thread + 4 * THREAD_FRAME);
        if (previous == NULL) {
            // current thread has finished execution so unschedule it
            Threads.unschedule(Reg.thread);
        } else {
            int returnpc = Mem.load(Reg.frame + 4 * FRAME_RETURN_PC);
            Mem.store(returnpc, previous + 4 * FRAME_PC);
            Reg.load(); // load registers with values from previous frame
        }
    }

}









