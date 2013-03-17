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
 * Return the lock for the specified object. Allocate a new lock object
 * if necessary. If this allocation is interrupted by the garbage
 * collector, return null.
 */
Ref getLock(Ref object) {
    Ref result = getRef(object, OBJECT_LOCK);
    if (result == NULL) {
        result = allocate(LOCK_WAIT_TAIL + 1, HEADER_INSTANCE);
        if (result == NULL) { return NULL; } // rollback, gc done

        // initialise
        Ref lockType = getRef(core, CORE_LOCK_TYPE);
        int hash = (char*) result - (char*) core;
        setRef(result, OBJECT_TYPE, lockType);
        setInt(result, OBJECT_HASHCODE, hash);
        // all fields in new lock are initially null

        // point object to its new lock
        setRef(object, OBJECT_LOCK, result);
    }
    return result;
}

/*
 * Called when the given thread wants to acquire the specified
 * lock. If the thread must wait in the queue it is unscheduled.
 * The lock count is incremented by the specified amount
 * after acquisition.
 */
void acquireLock(Ref thread, Ref lock, int increment) {
    // either the lock currently has no owner...
    Ref owner = getRef(lock, LOCK_OWNER);
    if (owner == NULL) {
        setRef(lock, LOCK_OWNER, thread);
        setInt(lock, LOCK_COUNT, increment);
    }

    // ...or the current thread already holds the lock...
    else if (owner == thread) {
        Int count = getInt(lock, LOCK_COUNT);
        setInt(lock, LOCK_COUNT, count + increment);
    }

    // ...or the current thread has to wait in line to obtain the lock
    else {
        setInt(thread, THREAD_LOCK_COUNT, increment);
        addToLockQueue(thread, lock);
        unschedule(thread);
    }
}

/*
 * Relinquish control of the given lock to the next thread in the queue
 */
static void relinquishLock(Ref lock) {
    // set ownership to next in queue...
    Ref thread = getRef(lock, LOCK_LOCK_HEAD);
    if (thread != NULL) {
        // move thread at head of queue to owner
        setRef(lock, LOCK_OWNER, thread);
        Int count = getInt(thread, THREAD_LOCK_COUNT);
        setInt(lock, LOCK_COUNT, count);

        // point head of queue to next in line
        Ref next = getRef(thread, THREAD_NEXT_LOCK);
        setRef(lock, LOCK_LOCK_HEAD, next);
        if (next == NULL) { setRef(lock, LOCK_LOCK_TAIL, NULL); }
        else { setRef(next, THREAD_PREV_LOCK, NULL); }

        // thread no longer in queue, schedule new owner
        setRef(thread, THREAD_NEXT_LOCK, NULL);
        setRef(thread, THREAD_LOCK, NULL);
        schedule(thread);
    }

    // ...unless there's nothing waiting
    else {
        setRef(lock, LOCK_OWNER, NULL);
        setInt(lock, LOCK_COUNT, 0);
    }
}

/*
 * Unlock the given lock
 */
void unlockLock(Ref lock) {
    Int count = getInt(lock, LOCK_COUNT);
    if (count > 1) { setInt(lock, LOCK_COUNT, --count); }
    else { relinquishLock(lock); }
}

/*
 * Add the specified thread to the lock queue of the given lock
 */
void addToLockQueue(Ref thread, Ref lock) {
    // either the queue is empty...
    Ref lockTail = getRef(lock, LOCK_LOCK_TAIL);
    if (lockTail == NULL) {
        setRef(lock, LOCK_LOCK_HEAD, thread);
    }
    
    // ...or the thread can be appended
    else {
        setRef(lockTail, THREAD_NEXT_LOCK, thread);
        setRef(thread, THREAD_PREV_LOCK, lockTail);
    }

    // either way the thread becomes the new tail
    setRef(lock, LOCK_LOCK_TAIL, thread);
    setRef(thread, THREAD_LOCK, lock);
}

/*
 * Execute the MONITORENTER instruction
 */
void op_monitorenter() {
    Ref object = peekRef(); // don't pop in case gc runs
    Ref lock = getLock(object);
    if (lock == NULL) { return; } // roll back, gc done
    popRef(); // can pop now, no turning back
    pc++;
    acquireLock(thread, lock, 1);
}

/*
 * Execute the MONITOREXIT instruction
 */
void op_monitorexit() {
    // assume the current thread owns the monitor
    // assume lock was created when monitorenter was called
    Ref object = popRef();
    Ref lock = getRef(object, OBJECT_LOCK);
    if (lock == NULL) {
        printf("Illegal State, no lock found\n");
        debugTrace();
        exit(1);
    }
    unlockLock(lock);
    pc++;
}

//	/**
//	 * Called when the current thread wants to wait on the specified object
//	 * for the specified amount of time.
//	 */
//	static void wait(int object, long timeout) {
//		// assume thread is already owner of lock
//		int lock = getLock(object);
//		if (lock == NULL) throw new UnsupportedOperationException("Trying to wait on unsynchronized object");
//
//		// set the lock count in the thread
//		int count = Mem.load(lock + 4*LOCK_COUNT);
//		Mem.store(count, Reg.thread + 4*THREAD_LOCK_COUNT);
//		Mem.store(lock, Reg.thread + 4*THREAD_LOCK);
//
//		// sit in wait queue
//		addToWaitQueue(lock);
//		relinquish(lock);
//		if (timeout > 0) Sleep.addToSleepQueue(timeout);
//		Threads.unschedule(Reg.thread);
//	}
//	
//	/**
//	 * Called when all threads waiting on the specified object have been notified.
//	 */
//	static void notifyAll(int object) {
//		int lock = getLock(object);
//		if (lock == NULL) throw new UnsupportedOperationException("Trying to notify on unsynchronized object");
//		
//		// notify all threads in wait queue
//		int thread = Mem.load(lock + 4*LOCK_WAIT_HEAD);
//		while (thread != NULL) {
//			notifyThread(thread, lock);
//			thread = Mem.load(lock + 4*LOCK_WAIT_HEAD);
//		}
//	}
//	
//	/**
//	 * Notify the first thread waiting on the specified object
//	 */
//	static void notify(int object) {
//		int lock = getLock(object);
//		if (lock == NULL) throw new UnsupportedOperationException("Trying to notify on unsynchronized object");
//		int thread = Mem.load(lock + 4*LOCK_WAIT_HEAD);
//		if (thread != NULL) notifyThread(thread, lock);
//	}
//	
//	/**
//	 * Remove the specified thread from the wait queue and from 
//	 * the sleep queue if necessary and append to lock queue.
//	 */
//	static void notifyThread(int thread, int lock) {
//		removeFromWaitQueue(thread, lock);
//		if (Sleep.isSleeping(thread)) Sleep.removeFromSleepQueue(thread);
//		addToLockQueue(thread, lock);
//	}
//	
//	/**
//	 * Remove the specified thread from the wait queue of the given lock
//	 */
//	static void removeFromWaitQueue(int thread, int lock) {
//		int waitHead = Mem.load(lock + 4*LOCK_WAIT_HEAD);
//		int waitTail = Mem.load(lock + 4*LOCK_WAIT_TAIL);
//		int next = Mem.load(thread + 4*THREAD_NEXT_LOCK);
//		int prev = Mem.load(thread + 4*THREAD_PREV_LOCK);
//
//		// either the thread is at the head of the wait queue...
//		if (waitHead == thread) {
//			Mem.store(next, lock + 4*LOCK_WAIT_HEAD);
//			if (next == NULL) Mem.store(NULL, lock + 4*LOCK_WAIT_TAIL);
//			else Mem.store(NULL, next + 4*THREAD_PREV_LOCK);
//		}
//		
//		// ...or at the tail...
//		else if (waitTail == thread) {
//			// note: thread is tail but not head, ie. prev is not null
//			Mem.store(prev, lock + 4*LOCK_WAIT_TAIL);
//			Mem.store(NULL, prev + 4*THREAD_NEXT_LOCK);
//		}
//		
//		// ...or somewhere in the middle
//		else {
//			Mem.store(next, prev + 4*THREAD_NEXT_LOCK); // prev -> next
//			Mem.store(prev, next + 4*THREAD_PREV_LOCK); // prev <- next
//		}
//		
//		// thread no longer in wait queue
//		Mem.store(NULL, thread + 4*THREAD_NEXT_LOCK);
//		Mem.store(NULL, thread + 4*THREAD_PREV_LOCK);
//		Mem.store(NULL, thread + 4*THREAD_LOCK);
//	}
//	
//	/**
//	 * Add the current thread to the wait queue of the specified lock
//	 */
//	static void addToWaitQueue(int lock) {
//		int waitTail = Mem.load(lock + 4*LOCK_WAIT_TAIL);
//		
//		// either the queue is empty...
//		if (waitTail == NULL) Mem.store(Reg.thread, lock + 4*LOCK_WAIT_HEAD);
//		
//		// ...or the thread can be appended
//		else {
//			Mem.store(Reg.thread, waitTail + 4*THREAD_NEXT_LOCK);
//			Mem.store(waitTail, Reg.thread + 4*THREAD_PREV_LOCK);
//		}
//		
//		// either way the thread becomes the new tail
//		Mem.store(Reg.thread, lock + 4*LOCK_WAIT_TAIL);
//	}
//	
//	/**
//	 * Increment the count of the given lock by the given amount
//	 */
//	static void increment(int lock, int amount) {
//		if (amount < 0) throw new IllegalArgumentException("Invalid amount " + amount);
//		int count = Mem.load(lock + 4*LOCK_COUNT);
//		Mem.store(count + amount, lock + 4*LOCK_COUNT);
//	}
//	
