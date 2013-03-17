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
package java.lang;

/**
 * Implementation of java.lang.Thread based on Sun specification.
 */
public class Thread implements Runnable {
    
    /** The minimum priority */
    public static final int MIN_PRIORITY = 1;
    
    /** The normal priority */
    public static final int NORM_PRIORITY = 5;
    
    /** The maximum priority */
    public static final int MAX_PRIORITY = 10;
    
    /** The thread counter */
    private static int counter = 1;
    
    /** The target */
    private Runnable target;
    
    /** The thread group */
    private ThreadGroup group;
    
    /** The daemon flag */
    private boolean daemon = false;
    
    /** The underlying system thread */
    private org.pjos.common.runtime.Thread system;
    
    /**
     * Generate a new thread name
     */
    private static synchronized String nextName() {
        return "Thread-" + (counter++);
    }
    
    /**
     * Create a thread
     */
    public Thread() {
        this(null, null, nextName());
    }
    
    /**
     * Create a thread
     * @param target the target
     */
    public Thread(Runnable target) {
        this(null, target, nextName());
    }
    
    /**
     * Create a thread
     * @param group the group
     * @param target the target
     */
    public Thread(ThreadGroup group, Runnable target) {
        this(group, target, nextName());
    }
    
    /**
     * Create a thread
     * @param name the name
     */
    public Thread(String name) {
        this(null, null, name);
    }
    
    /**
     * Create a thread
     * @param group the group
     * @param name the name
     */
    public Thread(ThreadGroup group, String name) {
        this(group, null, name);
    }
    
    /**
     * Create a thread
     * @param target the target
     * @param name the name
     */
    public Thread(Runnable target, String name) {
        this(null, target, name);
    }
    
    /**
     * Create a thread, ignore the stack size
     * @param group the group
     * @param target the target
     * @param name the name
     */
    public Thread(ThreadGroup group, Runnable target, String name) {
        if (group == null) {
            Thread thread = Thread.currentThread();
            if (thread != null) { group = thread.getThreadGroup(); }
        }
        if (group == null) { group = ThreadGroup.getRootGroup(); }
        this.group = group;
        this.target = target;
        system = new org.pjos.common.runtime.Thread(this, name);
    }
    
    /**
     * Create a thread, ignore the stack size
     * @param group the group
     * @param target the target
     * @param name the name
     * @param stackSize ignored
     */
    public Thread(
            ThreadGroup group,
            Runnable target,
            String name,
            long stackSize)
    {
        this(group, target, name);
    }
    
    /**
     * @return a reference to the currently executing thread
     */
    public static Thread currentThread() {
        return org.pjos.common.runtime.Thread.currentThread().getThread();
    }
    
    /**
     * Pause this thread and allow others to execute (not implemented)
     */
    public static void yield() {
        // no plans to implement, yielding happens automatically anyway
    }
    
    /**
     * Put the currently executing thread to sleep
     * @param millis the number of milliseconds to sleep
     * @throws InterruptedException if interrupted
     */
    public static void sleep(long millis) throws InterruptedException {
        sleep(millis, 0);
    }
    
    /**
     * Put the currently executing thread to sleep
     * @param millis the number of milliseconds to sleep
     * @param nanos the number of nanoseconds to sleep
     * @throws InterruptedException if interrupted
     */
    public static void sleep(long millis, int nanos)
            throws InterruptedException
    {
        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException();
        }
        org.pjos.common.runtime.Thread.sleep(millis, nanos);
    }
    
    /**
     * Start execution of this thread
     */
    public void start() {
        // call start hook of system implementation
        system.start();
    }
    
    /**
     * Do the work of this thread
     */
    public void run() {
        if (target != null) { target.run(); }
    }
    
    /**
     * Stop this thread (deprecated, not implemented)
     */
    public final void stop() {
        // no plans to implement
    }
    
    /**
     * Stop this thread by throwing an exception (deprecated, not implemented)
     * @param t the exception
     */
    public final void stop(Throwable t) {
        // no plans to implement
    }
    
    /**
     * Interrupt this thread
     */
    public void interrupt() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return true if the current thread has been interrupted
     */
    public static boolean interrupted() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return true if this thread has been interrupted
     */
    public boolean isInterrupted() {
throw new UnsupportedOperationException();
    }
    
    /**
     * Destroy this thread (not implemented)
     */
    public void destroy() {
        // no plans to implement
    }
    
    /**
     * @return true if this thread is alive
     */
    public final boolean isAlive() {
throw new UnsupportedOperationException();
    }
    
    /**
     * Suspend this thread (deprecated)
     */
    public final void suspend() {
        system.suspend();
    }
    
    /**
     * Resume this thread (deprecated)
     */
    public final void resume() {
        system.resume();
    }
    
    /**
     * Set the priority of this thread
     * @param newPriority the new priority
     */
    public final void setPriority(int newPriority) {
        //checkAccess();
        if (newPriority < MIN_PRIORITY || newPriority > MAX_PRIORITY) {
            throw new IllegalArgumentException();
        }
        int priority = Math.min(newPriority, group.getMaxPriority());
        system.setPriority(priority);
    }
    
    /**
     * @return the priority of this thread
     */
    public final int getPriority() {
        return system.getPriority();
    }
    
    /**
     * Set the name of this thread
     * @param name the new name
     */
    public final void setName(String name) {
        checkAccess();
        system.setName(name);
    }
    
    /**
     * @return the name
     */
    public final String getName() {
        return system.getName();
    }
    
    /**
     * @return the thread group
     */
    public ThreadGroup getThreadGroup() {
        return group;
    }
    
    /**
     * @return the number of active threads in the current thread's group
     */
    public static int activeCount() {
        return currentThread().getThreadGroup().activeCount();
    }
    
    /**
     * Copy active threads into the given array
     * @param tarray the array
     * @return the number of threads copied into the array
     */
    public static int enumerate(Thread[] tarray) {
        return currentThread().getThreadGroup().enumerate(tarray);
    }
    
    /**
     * @return the number of stack frames (deprecated)
     */
    public int countStackFrames() {
        return system.getFrame().getStackSize();
    }
    
    /**
     * Wait for this thread to die
     * @param millis the number of milliseconds to wait
     * @throws InterruptedException if interrupted
     */
    public final void join(long millis) throws InterruptedException {
        join(millis, 0);
    }
    
    /**
     * Wait for this thread to die
     * @param millis the number of milliseconds to wait
     * @param nanos the number of nanoseconds to wait
     * @throws InterruptedException if interrupted
     */
    public final void join(long millis, int nanos) throws InterruptedException {
        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException();
        }
throw new UnsupportedOperationException();
    }
    
    /**
     * Wait for this thread to die
     * @throws InterruptedException if interrupted
     */
    public final void join() throws InterruptedException {
        join(0, 0);
    }
    
    /**
     * Print a stack trace of the current thread
     * (for debugging purposes only)
     */
    public static void dumpStack() {
        try {
            throw new Exception("Dumping thread stack for dubugging");
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
    
    /**
     * Set daemon status
     * @param daemon the status flag
     */
    public final void setDaemon(boolean daemon) {
        checkAccess();
        this.daemon = daemon;
    }
    
    /**
     * @return true if this thread is a daemon
     */
    public final boolean isDaemon() {
        return daemon;
    }
    
    /**
     * Determine if the currently executing thread
     * has permission to modify this thread.
     */
    public final void checkAccess() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return a string representation of this thread
     */
    public String toString() {
        return "Thread " + getName() + ", priority " + getPriority()
                + ", group " + group;
    }
    
    /**
     * @return the context class loader for this thread
     */
    public ClassLoader getContextClassLoader() {
throw new UnsupportedOperationException();
    }
    
    /**
     * Set the context class loader for this thread
     * @param cl the class loader
     */
    public void setContextClassLoader(ClassLoader cl) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param o the object
     * @return true if this thread holds the monitor lock
     *         on the specified object
     */
    public static boolean holdsLock(Object o) {
        if (o == null) { throw new NullPointerException(); }
throw new UnsupportedOperationException();
    }
    
}
