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
 * This thread is notified by the system whenever an
 * interrupt occurs. It will use the interrupt manager
 * to notify any threads waiting for that interrupt.
 */
class Notifier extends Thread {
    
    /**
     * Create a notifier thread (not used, created by system).
     */
    private Notifier() {
        // this code should never be called
        super(null, null);
        throw new IllegalStateException();
    }
    
    /**
     * When an interrupt occurs, notify listening threads
     */
    public void run() {
        // set the name, because constructor is not actually run
        setName("Notifier");
        
        // don't proceed until the architecture is set
        Architecture arch = Architecture.get();
        while (arch == null) {
            suspend();
            arch = Architecture.get();
        }
        
        // process interrupts forever, suspending when there are none
        while (true) {
            Object key = arch.getNextInterrupt();
            while (key == null) {
                suspend();
                key = arch.getNextInterrupt();
            }
            InterruptManager.interrupt(key);
        }
    }
    
}

