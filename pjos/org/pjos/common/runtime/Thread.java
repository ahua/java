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
package org.pjos.common.runtime;

/**
 * This object holds the state of a system thread.
 */
public class Thread {

    /** The java thread */
    java.lang.Thread thread;

    /** The current frame */
    Frame frame = null;

    /** Points to a lock if this thread is waiting to acquire it */
    Lock currentLock = null;

    /** Holds the number of times the lock needs to be acquired */
    int lockCount = 0;

    /** The next thread waiting on or for the same lock */
    Thread nextLock = null;

    /** The previous thread waiting on or for the same lock */
    Thread prevLock = null;

    /** The thread scheduled to run after this one */
    Thread nextRunning = null;

    /** The thread scheduled to run before this one */
    Thread prevRunning = null;

    /** The thread due to be woken up after this one */
    Thread nextSleeping = null;

    /** The thread due to be woken up before this one */
    Thread prevSleeping = null;

    /** The time this thread is due to wake up */
    long wakeUpTime = 0;

    /** The Name */
    String name = null;

    /** The Priority */
    int priority = 0;

    /** This flag is set when the thread is started */
    boolean started = false;

    /** This flag is set when the thread is suspended */
    boolean suspended = false;

    /**
     * Create a system thread for the given java thread
     * @param thread the java thread
     * @param name the name
     */
    public Thread(java.lang.Thread thread, String name) {
        this.thread = thread;
        this.name = name;
    }

    /**
     * Do the work of the thread
     */
    void run() {
        try {
            thread.run();
        } catch (Throwable t) {
            ThreadGroup group = (thread != null)
                    ? thread.getThreadGroup()
                    : null;
            if (group == null) {
                t.printStackTrace();
            } else {
                group.uncaughtException(thread, t);
            }
        }
    }

    /**
     * Start this thread
     */
    public synchronized void start() {
        if (started) { throw new IllegalThreadStateException(); }
        startHook();
    }

    /**
     * Native hook to start the thread
     */
    private native void startHook();

    /**
     * Suspend the thread
     */
    public native void suspend();

    /**
     * Resume the thread
     */
    public native void resume();

    /**
     * Set the name
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the priority
     * @param priority the new priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @return the current frame
     */
    public Frame getFrame() {
        return frame;
    }

    /**
     * @return the java thread
     */
    public java.lang.Thread getThread() {
        return thread;
    }

    /**
     * @return the currently executing thread
     */
    public static native Thread currentThread();

    /**
     * Sleep for the specified interval
     * @param millis the number of milliseconds
     * @param nanos the number o nanos
     */
    public static native void sleep(long millis, int nanos);

}
