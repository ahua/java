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
 * Implementation of java.lang.ThreadGroup based on Sun specification.
 */
public class ThreadGroup {

    /** The root thread group */
    private static ThreadGroup root;

    /** The name */
    private String name;

    /** The parent */
    private ThreadGroup parent;

    /** The maximum priority */
    private int maxPriority = Thread.MAX_PRIORITY;

    /** The daemon flag */
    private boolean daemon = false;

    /**
     * Create the root thread group
     */
    private static synchronized void createRootGroup() {
        if (root == null) {
            root = new ThreadGroup();
            root.name = "Root";
        }
    }

    /**
     * @return the root thread group
     */
    static ThreadGroup getRootGroup() {
        if (root == null) { createRootGroup(); }
        return root;
    }

    /**
     * Create a thread group (for root group only)
     */
    private ThreadGroup() {
        // nothing to do here
    }

    /**
     * Create a thread group
     * @param name the name
     */
    public ThreadGroup(String name) {
        this(Thread.currentThread().getThreadGroup(), name);
    }

    /**
     * Create a thread group
     * @param parent the parent group
     * @param name the name
     */
    public ThreadGroup(ThreadGroup parent, String name) {
        checkAccess();
        if (parent == null) { throw new NullPointerException(); }
        this.parent = parent;
        this.name = name;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @return the parent thread group
     */
    public final ThreadGroup getParent() {
        if (parent != null) { parent.checkAccess(); }
        return parent;
    }

    /**
     * @return the maximum priority for this group
     */
    public final int getMaxPriority() {
        return maxPriority;
    }

    /**
     * @return the daemon status
     */
    public final boolean isDaemon() {
        return daemon;
    }

    /**
     * @return true if this thread group has been destroyed
     */
    public boolean isDestroyed() {
throw new UnsupportedOperationException();
    }

    /**
     * Set the daemon status
     * @param daemon new status
     */
    public final void setDaemon(boolean daemon) {
        checkAccess();
        this.daemon = daemon;
    }

    /**
     * Set the max priority for the group
     * @param max the next max priority
     */
    public final void setMaxPriority(int max) {
        checkAccess();
        if (max < Thread.MIN_PRIORITY || max > Thread.MAX_PRIORITY) { return; }
        if (parent != null && max >= parent.getMaxPriority()) { return; }
        maxPriority = max;
    }

    /**
     * @param group the group
     * @return true if this thread group is equal to the given group
     *              or is one of its ancestors 
     */
    public final boolean parentOf(ThreadGroup group) {
        if (this == group) { return true; }
        if (group == null) { return false; }
        return parentOf(group.parent);
    }

    /**
     * Determine if the currently running thread has
     * permission to modify this thread group.
     */
    public final void checkAccess() {
throw new UnsupportedOperationException();
    }

    /**
     * @return the active thread count
     */
    public int activeCount() {
throw new UnsupportedOperationException();
    }

    /**
     * Copy active threads into array
     * @param list the array
     * @return the number of threads copied
     */
    public int enumerate(Thread[] list) {
        return enumerate(list, true);
    }

    /**
     * Copy active threads into array
     * @param list the array
     * @param recurse the recurse flag
     * @return the number of threads copied
     */
    public int enumerate(Thread[] list, boolean recurse) {
        checkAccess();
throw new UnsupportedOperationException();
    }

    /**
     * @return the number of active groups in this group
     */
    public int activeGroupCount() {
throw new UnsupportedOperationException();
    }

    /**
     * Copy active thread groups into array
     * @param list the array
     * @return the number of thread groups copied
     */
    public int enumerate(ThreadGroup[] list) {
        return enumerate(list, true);
    }

    /**
     * Copy active thread groups into array
     * @param list the array
     * @param recurse the recurse flag
     * @return the number of thread groups copied
     */
    public int enumerate(ThreadGroup[] list, boolean recurse) {
throw new UnsupportedOperationException();
    }

    /**
     * Stop threads and sub groups (deprecated, not implemented)
     */
    public final void stop() {
        // no plans to implement
    }

    /**
     * Interrupt threads and sub groups
     */
    public final void interrupt() {
        checkAccess();
throw new UnsupportedOperationException();
    }

    /**
     * Suspend threads and sub groups (deprecated, not implemented)
     */
    public final void suspend() {
        // no plans to implement
    }

    /**
     * Resume threads and sub groups (deprecated, not implemented)
     */
    public final void resume() {
        // no plans to implement
    }

    /**
     * Destroy this group and sub groups
     */
    public final void destory() {
        checkAccess();
throw new UnsupportedOperationException();
    }

    /**
     * Print debug information to standard out (for debugging purposes only)
     */
    public void list() {
throw new UnsupportedOperationException();
    }

    /**
     * Handle an uncaught exception
     * @param t the thread throwing the exception
     * @param e the exception
     */
    public void uncaughtException(Thread t, Throwable e) {
        if (parent != null) {
            parent.uncaughtException(t, e);
        } else if (!(e instanceof ThreadDeath)) {
            e.printStackTrace();
        }
    }

    /**
     * Allow thread suspension (deprecated)
     * @param b the flag (ignored)
     * @return false
     */
    public boolean allowThreadSuspension(boolean b) {
        return false;
    }

    /**
     * @return a string representation of this thread group
     */
    public String toString() {
        return "ThreadGroup " + name;
    }

}
