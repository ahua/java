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
 * Wake up any sleeping threads if their timeouts
 * have expired.
 */
void wakeSleepingThreads() {
    printf("wakeSleepingThreads() not implemented!!!\n");
    exit(1);
}

/*
 * Schedule the next thread in line
 */
void scheduleNextThread() {
    // get the address of the next thread
    Ref running = getRef(core, CORE_RUNNING);

    // schedule the idle thread...
    if (running == NULL) { thread = getRef(core, CORE_IDLE); }

    // ... or the next thread in line
    else {
        thread = getRef(running, THREAD_NEXT_RUNNING);
        setRef(core, CORE_RUNNING, thread);
    }

    // set the registers for next thread
    loadRegisters();
}

/*
 * Start a new thread. Note: The start hook method
 * is not a native method!
 */
void startNewThread(int pcOffset) {
    // calculate required size for stack frame
    Ref method = getRef(core, CORE_THREAD_RUN_METHOD);
    Int maxStack = getInt(method, METHOD_MAX_STACK);
    Int maxLocals = getInt(method, METHOD_MAX_LOCALS);
    int numWords = FRAME_LOCALS + 2*maxStack + 2*maxLocals;

    // allocate space for new frame
    Ref newFrame = allocate(numWords, HEADER_STACK_FRAME);
    if (newFrame == NULL) { return; } // rollback, gc done

    // initialise new frame
    Ref frameType = getRef(frame, OBJECT_TYPE);
    Ref thread = popRef();
    setRef(newFrame, OBJECT_TYPE, frameType);
    setRef(newFrame, FRAME_RETURN_FRAME, NULL);
    setRef(newFrame, FRAME_METHOD, method);
    setInt(newFrame, FRAME_PC, 0);
    setInt(newFrame, FRAME_SP, 4*numWords);
    setLocalRef(0, thread, newFrame);

    // update thread object and schedule
    setRef(thread, THREAD_FRAME, newFrame);
    setInt(thread, THREAD_STARTED, TRUE);
    pc += pcOffset;
    schedule(thread);
}
	
/*
 * Insert the given thread into the queue of running threads.
 * Assume the given thread is not currently scheduled, insert
 * it directly before the first thread in the queue.
 */
void schedule(Ref t) {
    // if the thread is suspended, do nothing
    if (getInt(t, THREAD_SUSPENDED)) { return; }

    // currently no threads in running queue
    Ref running = getRef(core, CORE_RUNNING);
    if (running == NULL) {
        setRef(core, CORE_RUNNING, t);
        setRef(t, THREAD_NEXT_RUNNING, t);
        setRef(t, THREAD_PREV_RUNNING, t);
    }

    // was previous <-> running
    // now previous <-> t <-> running
    else {
        Ref previous = getRef(running, THREAD_PREV_RUNNING);
        setRef(t, THREAD_PREV_RUNNING, previous);  // previous <- thread
        setRef(previous, THREAD_NEXT_RUNNING, t);    // previous -> thread
        setRef(running, THREAD_PREV_RUNNING, t);   // thread <- running
        setRef(t, THREAD_NEXT_RUNNING, running);   // thread -> running
    }
}

/*
 * Unschedule the specified thread by removing it from the queue
 * of running threads. The idle thread should not be unscheduled!
 */
void unschedule(Ref t) {
    Ref previous = getRef(t, THREAD_PREV_RUNNING);
    Ref next = getRef(t, THREAD_NEXT_RUNNING);

    // this might be the only thread in the queue...
    if (t == next) {
        setRef(core, CORE_RUNNING, NULL);
    }

    // ...otherwise remove it from the queue
    else {
        // change the running thread if necessary
        Ref running = getRef(core, CORE_RUNNING);
        if (t == running) { setRef(core, CORE_RUNNING, next); }

        // was previous <-> t <-> next
        // now previous <-> next
        setRef(previous, THREAD_NEXT_RUNNING, next); // previous -> next
        setRef(next, THREAD_PREV_RUNNING, previous); // previous <- next
    }

    // thread now has no next or previous
    setRef(t, THREAD_PREV_RUNNING, NULL);
    setRef(t, THREAD_NEXT_RUNNING, NULL);

    // if the current thread is being unscheduled, set registers accordingly
    if (t == thread) {
        saveRegisters();
        thread = NULL;
    }
}
	
	/**
	 * Suspend the specified thread
	 */
//	static void suspend(int thread) {
//		// set the suspended flag in the thread
//		Mem.store(TRUE, thread + 4*THREAD_SUSPENDED);
//		
//		// unschedule thread if necessary
//		int next = Mem.load(thread + 4*THREAD_NEXT_RUNNING);	// next is non-null if thread is scheduled
//		if (next != NULL) unschedule(thread);
//	}
	
	/**
	 * Resume the specified thread
	 */
//	static void resume(int thread) {
//		// clear the suspended flag in the thread
//		Mem.store(FALSE, thread + 4*THREAD_SUSPENDED);
//		
//		// schedule thread if necessary
//		int next = Mem.load(thread + 4*THREAD_NEXT_RUNNING);	// next is non-null if thread is scheduled
//		if (next == NULL) schedule(thread);
//	}
	


