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
 * Implementation of java.lang.Object based on Sun specification.
 */
public class Object {
    
    /**
     * Construct an object
     */
    public Object() {
        // default constructor
    }
    
    /**
     * @return the class of this object
     */
    public final native Class getClass();
    
    /**
     * @return the hash code
     */
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    /**
     * @param o the object to test
     * @return true if this object is equal to the one given
     */
    public boolean equals(Object o) {
        return this == o;
    }
    
    /**
     * Clone this object
     * @return the cloned object
     * @throws CloneNotSupportedException if not supported
     */
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    /**
     * @return a string representing this object
     */
    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode());
    }
    
    /**
     * Wake up a single thread waiting on this object's monitor
     */
    public final native void notify();
    
    /**
     * Wake up all threads waiting on this object's monitor
     */
    public final native void notifyAll();
    
    /**
     * Wait for this object's monitor, or until the specified time has elapsed
     * @param timeout the number of milliseconds to wait
     * @throws InterruptedException if interrupted
     */
    public final void wait(long timeout) throws InterruptedException {
        wait(timeout, 0);
    }
    
    /**
     * Wait for this object's monitor, or until the specified time has elapsed
     * @param timeout the number of milliseconds to wait
     * @param nanos the number of nanoseconds to wait
     * @throws InterruptedException if interrupted
     */
    public final native void wait(long timeout, int nanos)
            throws InterruptedException;
    
    /**
     * Wait for this object's monitor
     * @throws InterruptedException if interrupted
     */
    public final void wait() throws InterruptedException {
        wait(0, 0);
    }
    
    /**
     * Can be overidden to perform finalization actions
     * @throws Throwable any exceptions thrown will be ignored
     */
    protected void finalize() throws Throwable {
        // don't do anything by default
    }
    
}
