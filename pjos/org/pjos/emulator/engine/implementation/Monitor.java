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
 * Contains code to implement synchronization, waiting and notification.
 */
class Monitor implements Constants {

    /**
     * monitorenter instruction
     */
    static void monitorenter() {
        int object = Stack.peekPointer(0); // don't pop yet in case gc runs
        
        // Get the lock object
        int lock = getLock(object);
        if (lock == NULL) { return; } // roll back, gc done
        
        // gc cannot interrupt now
        Stack.popPointer(); // pop object off stack
        Reg.instruction += 1;
        acquire(Reg.thread, lock, 1);
    }
    
    /**
     * monitorexit instruction
     */
    static void monitorexit() {
        // assume that the current running thread owns the monitor
        // assume lock was created when monitorenter was called...
        int object = Stack.popPointer();
        int lock = Mem.load(object + 4 * OBJECT_LOCK);
        if (lock == NULL) {
            throw new IllegalStateException("No lock found");
        }
        unlock(lock);
        Reg.instruction += 1;
    }
    
    /**
     * Called when the current thread wants to wait on the specified object
     * for the specified amount of time.
     * @param object the object
     * @param timeout the max amount of time to wait or zero for indefinite
     */
    static void wait(int object, long timeout) {
        // assume thread is already owner of lock
        int lock = getLock(object);
        if (lock == NULL) {
            throw new UnsupportedOperationException(
                    "Trying to wait on unsynchronized object");
        }

        // set the lock count in the thread
        int count = Mem.load(lock + 4 * LOCK_COUNT);
        Mem.store(count, Reg.thread + 4 * THREAD_LOCK_COUNT);
        Mem.store(lock, Reg.thread + 4 * THREAD_LOCK);

        // sit in wait queue
        addToWaitQueue(lock);
        relinquish(lock);
        if (timeout > 0) { Sleep.addToSleepQueue(timeout); }
        Threads.unschedule(Reg.thread);
    }
    
    /**
     * Called when all threads waiting on the specified object have
     * been notified.
     * @param object the object
     */
    static void notifyAll(int object) {
        int lock = getLock(object);
        if (lock == NULL) {
            throw new UnsupportedOperationException(
                    "Trying to notify on unsynchronized object");
        }
        
        // notify all threads in wait queue
        int thread = Mem.load(lock + 4 * LOCK_WAIT_HEAD);
        while (thread != NULL) {
            notifyThread(thread, lock);
            thread = Mem.load(lock + 4 * LOCK_WAIT_HEAD);
        }
    }
    
    /**
     * Notify the first thread waiting on the specified object
     * @param object the object
     */
    static void notify(int object) {
        int lock = getLock(object);
        if (lock == NULL) {
            throw new UnsupportedOperationException(
                    "Trying to notify on unsynchronized object");
        }
        int thread = Mem.load(lock + 4 * LOCK_WAIT_HEAD);
        if (thread != NULL) {
            notifyThread(thread, lock);
        }
    }
    
    /**
     * Remove the specified thread from the wait queue and from 
     * the sleep queue if necessary and append to lock queue.
     * @param thread the thread
     * @param lock the lock
     */
    static void notifyThread(int thread, int lock) {
        removeFromWaitQueue(thread, lock);
        if (Sleep.isSleeping(thread)) {
            Sleep.removeFromSleepQueue(thread);
        }
        addToLockQueue(thread, lock);
    }
    
    /**
     * Remove the specified thread from the wait queue of the given lock
     * @param thread the thread
     * @param lock the lock
     */
    static void removeFromWaitQueue(int thread, int lock) {
        int waitHead = Mem.load(lock + 4 * LOCK_WAIT_HEAD);
        int waitTail = Mem.load(lock + 4 * LOCK_WAIT_TAIL);
        int next = Mem.load(thread + 4 * THREAD_NEXT_LOCK);
        int prev = Mem.load(thread + 4 * THREAD_PREV_LOCK);
        if (waitHead == thread) {
            // either the thread is at the head of the wait queue...
            Mem.store(next, lock + 4 * LOCK_WAIT_HEAD);
            if (next == NULL) {
                Mem.store(NULL, lock + 4 * LOCK_WAIT_TAIL);
            } else {
                Mem.store(NULL, next + 4 * THREAD_PREV_LOCK);
            }
        } else if (waitTail == thread) {
            // ...or at the tail...
            // note: thread is tail but not head, ie. prev is not null
            Mem.store(prev, lock + 4 * LOCK_WAIT_TAIL);
            Mem.store(NULL, prev + 4 * THREAD_NEXT_LOCK);
        } else {
            // ...or somewhere in the middle
            Mem.store(next, prev + 4 * THREAD_NEXT_LOCK); // prev -> next
            Mem.store(prev, next + 4 * THREAD_PREV_LOCK); // prev <- next
        }
        
        // thread no longer in wait queue
        Mem.store(NULL, thread + 4 * THREAD_NEXT_LOCK);
        Mem.store(NULL, thread + 4 * THREAD_PREV_LOCK);
        Mem.store(NULL, thread + 4 * THREAD_LOCK);
    }
    
    /**
     * Add the specified thread to the lock queue of the given lock
     * @param thread the thread
     * @param lock the lock
     */
    static void addToLockQueue(int thread, int lock) {
        int lockTail = Mem.load(lock + 4 * LOCK_LOCK_TAIL);
        if (lockTail == NULL) {
            // either the queue is empty...
            Mem.store(thread, lock + 4 * LOCK_LOCK_HEAD);
        } else {
            // ...or the thread can be appended
            Mem.store(thread, lockTail + 4 * THREAD_NEXT_LOCK);
            Mem.store(lockTail, thread + 4 * THREAD_PREV_LOCK);
        }
        
        // either way the thread becomes the new tail
        Mem.store(thread, lock + 4 * LOCK_LOCK_TAIL);
        Mem.store(lock, thread + 4 * THREAD_LOCK);
    }
    
    /**
     * Add the current thread to the wait queue of the specified lock
     * @param lock the lock
     */
    static void addToWaitQueue(int lock) {
        int waitTail = Mem.load(lock + 4 * LOCK_WAIT_TAIL);
        if (waitTail == NULL) {
            // either the queue is empty...
            Mem.store(Reg.thread, lock + 4 * LOCK_WAIT_HEAD);
        } else {
            // ...or the thread can be appended
            Mem.store(Reg.thread, waitTail + 4 * THREAD_NEXT_LOCK);
            Mem.store(waitTail, Reg.thread + 4 * THREAD_PREV_LOCK);
        }
        
        // either way the thread becomes the new tail
        Mem.store(Reg.thread, lock + 4 * LOCK_WAIT_TAIL);
    }
    
    /**
     * Called when the specified thread wants to acquire the
     * specified lock. If the thread must wait in the queue
     * it is unscheduled. The lock count is incremented by
     * the specified amount after acquisition.
     * @param thread the thread
     * @param lock the lock
     * @param increment the amount to increment the lock count
     */
    static void acquire(int thread, int lock, int increment) {
        int owner = Mem.load(lock + 4 * LOCK_OWNER);
        if (owner == NULL) {
            // if lock currently has no owner...
            Mem.store(thread, lock + 4 * LOCK_OWNER);
            Mem.store(increment, lock + 4 * LOCK_COUNT);

        } else if (owner == thread) {
            // ...if the current thread already holds the lock...
            int count = Mem.load(lock + 4 * LOCK_COUNT);
            Mem.store(count + increment, lock + 4 * LOCK_COUNT);

        } else {
            // ...if the current thread has to wait in line to obtain the lock
            Mem.store(increment, thread + 4 * THREAD_LOCK_COUNT);
            addToLockQueue(thread, lock);
            Threads.unschedule(thread);
        }
    }
    
    /**
     * Increment the count of the given lock by the given amount
     * @param lock the lock
     * @param amount the amount to increment
     */
    static void increment(int lock, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid amount " + amount);
        }
        int count = Mem.load(lock + 4 * LOCK_COUNT);
        Mem.store(count + amount, lock + 4 * LOCK_COUNT);
    }
    
    /**
     * Unlock the specified lock.
     * @param lock the lock
     */
    static void unlock(int lock) {
        int count = Mem.load(lock + 4 * LOCK_COUNT);
        if (count > 1) {
            // just decrement count if higher than 1...
            Mem.store(count - 1, lock + 4 * LOCK_COUNT);
        } else {
            // ...otherwise relinquish to next thread in queue
            relinquish(lock);
        }
    }
    
    /**
     * Relinquish control of the specified lock to the next thread in the queue
     */
    private static void relinquish(int lock) {
        // set ownership to next in queue...
        int thread = Mem.load(lock + 4 * LOCK_LOCK_HEAD);
        if (thread != NULL) {
            // move thread at head of queue to owner
            int count = Mem.load(thread + 4 * THREAD_LOCK_COUNT);
            Mem.store(thread, lock + 4 * LOCK_OWNER);
            Mem.store(count, lock + 4 * LOCK_COUNT);

            // point head of queue to next in line
            int next = Mem.load(thread + 4 * THREAD_NEXT_LOCK);
            Mem.store(next, lock + 4 * LOCK_LOCK_HEAD);
            if (next == NULL) {
                Mem.store(NULL, lock + 4 * LOCK_LOCK_TAIL);
            } else {
                Mem.store(NULL, next + 4 * THREAD_PREV_LOCK);
            }
            
            // thread is now no longer in queue
            Mem.store(NULL, thread + 4 * THREAD_NEXT_LOCK);
            Mem.store(NULL, thread + 4 * THREAD_LOCK);
            
            // schedule the new owner
            Threads.schedule(thread);
        } else {
            // ...unless there's nothing waiting
            Mem.store(NULL, lock + 4 * LOCK_OWNER);
            Mem.store(0, lock + 4 * LOCK_COUNT);
        }
    }
    
    /**
     * Return the lock for the specified object. Allocate a new lock object
     * if necessary. If the garbage collector interrupts this allocation,
     * return null.
     * @param object the object
     * @return the lock
     */
    static int getLock(int object) {
        int result = Mem.load(object + 4 * OBJECT_LOCK);
        
        // allocate new lock and assign to object
        if (result == NULL) {
            result = Allocate.allocate(LOCK_WAIT_TAIL + 1, HEADER_INSTANCE);
            if (result == NULL) { return NULL; } // gc done
            
            // initialise header
            int lockType = Mem.load(Reg.core + 4 * CORE_LOCK_TYPE);
            Mem.store(lockType, result + 4 * OBJECT_TYPE);
            Mem.store(NULL, result + 4 * OBJECT_LOCK);
            Mem.store(result, result + 4 * OBJECT_HASHCODE);
            
            // all fields in new object are initially null
            
            // point object to its new lock
            if (result != NULL) {
                Mem.store(result, object + 4 * OBJECT_LOCK);
            }
        }
        
        return result;
    }
    
}









