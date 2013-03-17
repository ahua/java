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
 * Contains code to implement thread sleeping.
 */
class Sleep implements Constants {

    /**
     * Wake up any sleeping threads that are due
     */
    static void wakeSleepingThreads() {
        long now = System.currentTimeMillis();
        int asleep = Mem.load(Reg.core + 4 * CORE_SLEEPING);
        while (asleep != NULL && getWakeup(asleep) <= now) {
            wakeThread(asleep);
            asleep = Mem.load(Reg.core + 4 * CORE_SLEEPING);
        }
    }
    
    /**
     * @param thread the thread to be checked
     * @return true if the specified thread is sleeping
     */
    static boolean isSleeping(int thread) {
        return getWakeup(thread) != 0;
    }
    
    /**
     * Wake up the given thread
     * @param thread the thread to wake
     */
    static void wakeThread(int thread) {
        removeFromSleepQueue(thread);
        Threads.schedule(thread);
        
        // if thread was put to sleep by wait method, it
        // will need to reacquire the lock
        int lock = Mem.load(thread + 4 * THREAD_LOCK);
        if (lock != NULL) {
            Monitor.removeFromWaitQueue(thread, lock);
            Monitor.acquire(thread, lock, 0);
        }
    }
    
    /**
     * Add the current thread to the sleep queue for the
     * specified number of milliseconds.
     * @param millis the number of milliseconds to sleep for
     */
    static void addToSleepQueue(long millis) {
        // find out when the thread should wake up
        long wakeup = millis + System.currentTimeMillis();
        setWakeup(Reg.thread, wakeup);
        int sleeping = Mem.load(Reg.core + 4 * CORE_SLEEPING);
        if (sleeping == NULL) {
            // the sleep queue may be empty...
            Mem.store(Reg.thread, Reg.core + 4 * CORE_SLEEPING);
            Mem.store(NULL, Reg.thread + 4 * THREAD_NEXT_SLEEPING);
            Mem.store(NULL, Reg.thread + 4 * THREAD_PREV_SLEEPING);
        } else if (wakeup < getWakeup(sleeping)) {
            // ...or the new thread needs to be inserted at the front...
            Mem.store(Reg.thread, Reg.core + 4 * CORE_SLEEPING);
            Mem.store(sleeping, Reg.thread + 4 * THREAD_NEXT_SLEEPING);
            Mem.store(NULL, Reg.thread + 4 * THREAD_PREV_SLEEPING);
            Mem.store(Reg.thread, sleeping + 4 * THREAD_PREV_SLEEPING);
        } else {
            // ...or need to find the two threads in the queue between
            // which the new thread will be inserted...
            int current = sleeping;
            int next = Mem.load(current + 4 * THREAD_NEXT_SLEEPING);
            while (next != NULL && wakeup >= getWakeup(next)) {
                current = next;
                next = Mem.load(current + 4 * THREAD_NEXT_SLEEPING);
            }

            // insert the thread in queue
            Mem.store(Reg.thread, current + 4 * THREAD_NEXT_SLEEPING);
            Mem.store(current, Reg.thread + 4 * THREAD_PREV_SLEEPING);
            Mem.store(next, Reg.thread + 4 * THREAD_NEXT_SLEEPING);           
            if (next != NULL) {
                Mem.store(Reg.thread, next + 4 * THREAD_PREV_SLEEPING);
            }
        }
    }
    
    /**
     * @return the wakeup time for the specified thread
     */
    static long getWakeup(int thread) {
        long high = Mem.load(thread + 4 * THREAD_WAKEUP) & 0x00000000ffffffffL;
        long low = Mem.load(thread + 4 * THREAD_WAKEUP + 4)
                & 0x00000000ffffffffL;
        return (high << 32) | low;
    }
    
    /**
     * Set the wakeup time for the specified thread
     */
    private static void setWakeup(int thread, long wakeup) {
        int high = (int) (wakeup >>> 32);
        int low = (int) wakeup;
        Mem.store(high, thread + 4 * THREAD_WAKEUP);
        Mem.store(low, thread + 4 * THREAD_WAKEUP + 4);
    }
    
    /**
     * Remove the specified thread from the sleep queue
     * @param thread the thread to remove
     */
    static void removeFromSleepQueue(int thread) {
        int prev = Mem.load(thread + 4 * THREAD_PREV_SLEEPING);
        int next = Mem.load(thread + 4 * THREAD_NEXT_SLEEPING);
        
        // either thread is at head of sleep queue...
        int sleeping = Mem.load(Reg.core + 4 * CORE_SLEEPING);
        if (sleeping == thread) {
            Mem.store(next, Reg.core + 4 * CORE_SLEEPING);
            if (next != NULL) {
                Mem.store(NULL, next + 4 * THREAD_PREV_SLEEPING);
            }
        }
        
        // was prev <-> thread <-> next, now prev <-> next
        if (next != NULL) { Mem.store(prev, next + 4 * THREAD_PREV_SLEEPING); }
        if (prev != NULL) { Mem.store(next, prev + 4 * THREAD_NEXT_SLEEPING); }
        
        // thread is no longer sleeping
        Mem.store(NULL, thread + 4 * THREAD_NEXT_SLEEPING);
        Mem.store(NULL, thread + 4 * THREAD_PREV_SLEEPING);
        setWakeup(thread, 0);
    }
    
}









