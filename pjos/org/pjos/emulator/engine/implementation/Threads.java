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
 * Contains code to implement threading
 */
class Threads implements Constants {
    
    /**
     * Schedule the next running thread
     */
    static void scheduleNextThread() {
        // get the address of the next thread
        int running = Mem.load(Reg.core + 4 * CORE_RUNNING);
        if (running == NULL) {
            // schedule idle thread
            Reg.thread = Mem.load(Reg.core + 4 * CORE_IDLE);
        } else {
            // schedule next thread
            Reg.thread = Mem.load(running + 4 * THREAD_NEXT_RUNNING);
            Mem.store(Reg.thread, Reg.core + 4 * CORE_RUNNING);
        }
        
        // set other registers based on running thread
        Reg.load();
    }
    
    /**
     * Start a new thread.
     * @param pcOffset the program counter offset
     */
    static void startNewThread(int pcOffset) {
        // note: method is not native!
        
        // calculate required size for stack frame
        int method = Mem.load(Reg.core + 4 * CORE_THREAD_RUN_METHOD);
        int maxStack = Mem.load(method + 4 * METHOD_MAX_STACK);
        int maxLocals = Mem.load(method + 4 * METHOD_MAX_LOCALS);
        int numWords = FRAME_LOCALS + 2 * maxStack + 2 * maxLocals;
        
        // allocate space for new frame
        int address = Allocate.allocate(numWords, HEADER_STACK_FRAME);
        if (address == NULL) { return; } // roll back, gc done
        
        // initialise new frame
        int frameType = Mem.load(Reg.frame + 4 * OBJECT_TYPE);
        int thread = Stack.popPointer();
        Mem.store(frameType, address + 4 * OBJECT_TYPE);
        Mem.store(NULL, address + 4 * FRAME_RETURN_FRAME);
        Mem.store(method, address + 4 * FRAME_METHOD);
        Mem.store(0, address + 4 * FRAME_PC);
        Mem.store(4 * numWords, address + 4 * FRAME_SP);
        Mem.store(thread, address + 4 * FRAME_LOCALS); // arg 0 address
        Mem.store(TRUE, address + 4 * FRAME_LOCALS + 4); // reference flag

        // set new frame in thread object and set thread state to running
        Mem.store(address, thread + 4 * THREAD_FRAME);
        Mem.store(TRUE, thread + 4 * THREAD_STARTED);
        
        // point to next instruction in current frame
        Reg.instruction += pcOffset;

        // schedule thread to run
        schedule(thread);
    }
    
    /**
     * Insert the given thread into the queue of running threads.
     * It is assumed the given thread is not currently scheduled.
     * The given thread will be inserted directly before the first
     * thread in the queue.
     * @param thread the thread to be scheduled
     */
    static void schedule(int thread) {
        // if the thread is suspended, do nothing
        int suspended = Mem.load(thread + 4 * THREAD_SUSPENDED);
        if (suspended == TRUE) { return; }
        
        // currently no threads in running queue
        int running = Mem.load(Reg.core + 4 * CORE_RUNNING);
        if (running == NULL) {
            Mem.store(thread, Reg.core + 4 * CORE_RUNNING);
            Mem.store(thread, thread + 4 * THREAD_NEXT_RUNNING);
            Mem.store(thread, thread + 4 * THREAD_PREV_RUNNING);
        } else {
            // was previous <-> running
            // now previous <-> thread <-> running
            int previous = Mem.load(running + 4 * THREAD_PREV_RUNNING);
            Mem.store(previous, thread + 4 * THREAD_PREV_RUNNING);
            Mem.store(thread, previous + 4 * THREAD_NEXT_RUNNING);
            Mem.store(thread, running + 4 * THREAD_PREV_RUNNING);
            Mem.store(running, thread + 4 * THREAD_NEXT_RUNNING);
        }
    }
    
    /**
     * Unschedule the specified thread by removing it from the
     * queue of running threads. The idle thread should not be unscheduled!
     * @param thread the thread to be unscheduled
     */
    static void unschedule(int thread) {
        int previous = Mem.load(thread + 4 * THREAD_PREV_RUNNING);
        int next = Mem.load(thread + 4 * THREAD_NEXT_RUNNING);
        if (thread == next) {
            // if this is the only thread in the running queue...
            Mem.store(NULL, Reg.core + 4 * CORE_RUNNING);
        } else {
            // ...otherwise remove it from the queue
            // change the running thread if necessary
            int running = Mem.load(Reg.core + 4 * CORE_RUNNING);
            if (thread == running) {
                Mem.store(next, Reg.core + 4 * CORE_RUNNING);
            }
            
            // was previous <-> thread <-> next
            // now previous <-> next
            Mem.store(next, previous + 4 * THREAD_NEXT_RUNNING);
            Mem.store(previous, next + 4 * THREAD_PREV_RUNNING);
        }
        
        // thread now has no next or previous
        Mem.store(NULL, thread + 4 * THREAD_PREV_RUNNING);
        Mem.store(NULL, thread + 4 * THREAD_NEXT_RUNNING);
        
        // If the current thread is being unscheduled, set registers accordingly
        if (Reg.thread == thread) {
            Reg.save();
            Reg.thread = NULL;
        }
    }
    
    /**
     * Suspend the specified thread
     * @param thread the thread to suspend
     */
    static void suspend(int thread) {
        // set the suspended flag in the thread
        Mem.store(TRUE, thread + 4 * THREAD_SUSPENDED);
        
        // unschedule thread if necessary
        int next = Mem.load(thread + 4 * THREAD_NEXT_RUNNING);
        if (next != NULL) { unschedule(thread); }
    }
    
    /**
     * Resume the specified thread
     * @param thread the thread to resume
     */
    static void resume(int thread) {
        // clear the suspended flag in the thread
        Mem.store(FALSE, thread + 4 * THREAD_SUSPENDED);
        
        // schedule thread if necessary
        int next = Mem.load(thread + 4 * THREAD_NEXT_RUNNING);
        if (next == NULL) { schedule(thread); }
    }
    
}









