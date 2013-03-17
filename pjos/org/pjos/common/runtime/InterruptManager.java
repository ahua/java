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

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages access to interrupts. Interrupts are
 * mapped to key objects according to the current architecture.
 */
public final class InterruptManager {
    
    /** The map containing the synchronizers */
    private static Map synchronizers = new HashMap();
    
    /**
     * Instances should not be created!
     */
    private InterruptManager() {
        throw new IllegalStateException();
    }
    
    /**
     * This method is called by a thread when it wants
     * to wait for an interrupt to occur. If the thread is
     * interrupted, return the exception to the caller.
     * @param key the interrupt key
     * @return null or the exception if interrupted
     */
    public static InterruptedException waitFor(Object key) {
        return getSynchronizerFor(key).queue();
    }
    
    /**
     * Return the lock mapped to the given key. Create a new
     * lock if none exists.
     */
    private static Synchronizer getSynchronizerFor(Object key) {
        synchronized (synchronizers) {
            Synchronizer result = (Synchronizer) synchronizers.get(key);
            if (result == null) {
                result = new Synchronizer();
                synchronizers.put(key, result);
            }
            return result;
        }
    }
    
    /**
     * Notify any threads waiting on a particular interrupt lock
     * @param key the interrupt key
     */
    public static void interrupt(Object key) {
        getSynchronizerFor(key).release();
    }
    
}








