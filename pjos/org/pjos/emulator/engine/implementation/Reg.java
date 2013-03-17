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
 * Defines the registers for the emulator implementation.
 */
class Reg implements Constants {
    
    /**
     * The core register. Points to the core object at the head
     * of the active java space
     */
    static int core;
    
    /** The thread register. Points to the current thread */
    static int thread;
    
    /** The frame register. Points to the current frame */
    static int frame;
    
    /** The method register. Points to the current method */
    static int method;
    
    /**
     * The code register. Points to the byte array containing
     * the current method code
     */
    static int code;
    
    /** The instruction register. Points to the current instruction */
    static int instruction;
    
    /** The stack register. Points to the value on top of the stack */
    static int stack;
    
    /** The locals register. Points to local variable zero */
    static int locals;
    
    /**
     * The pool register. Points to the pool from the class
     * of the current method
     */
    static int pool;
    
    /**
     * Reset the registers. The core object is assumed to be at location
     * zero, and the others are calculated from the core.
     */
    static void reset() {
        core = Mem.OFFSET;
        thread = Mem.load(core + 4 * CORE_RUNNING);
        load();
    }
    
    /**
     * Load the correct register values for the current thread register
     */
    static void load() {
        frame = Mem.load(thread + 4 * THREAD_FRAME);
        method = Mem.load(frame + 4 * FRAME_METHOD);
        code = Mem.load(method + 4 * METHOD_CODE) + 4 * ARRAY_DATA;
        instruction = Mem.load(frame + 4 * FRAME_PC) + code;
        stack = Mem.load(frame + 4 * FRAME_SP) + frame;
        locals = frame + 4 * FRAME_LOCALS;
        pool = Mem.load(method + 4 * METHOD_POOL) + 4 * ARRAY_DATA;
    }
    
    /**
     * Save the execution state to the current frame
     */
    static void save() {
        Mem.store(instruction - code, frame + 4 * FRAME_PC);
        Mem.store(stack - frame, frame + 4 * FRAME_SP);
    }
    
}









