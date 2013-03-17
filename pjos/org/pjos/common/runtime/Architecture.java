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
 * Sub-classes of this class provide platform-specific initialisation.
 * Each subclass must define a static method named "init" that returns
 * no arguments. This method must create an instance of the subclass
 * and assign it to the static field named "singleton" of this class.
 */
public abstract class Architecture {
    
    /**
     * The architecture instance for the current platform. This
     * field should be set by the init method of the subclass.
     */
    protected static Architecture singleton;
    
    /**
     * Sub-classes should define a method with the same signature
     * as this one to be called by the initialisation thread.
     * @throws Exception if an error occurs
     */
    public static void init() throws Exception {
        throw new IllegalStateException();
    }
    
    /**
     * @return the singleton instance
     */
    static Architecture get() {
        return singleton;
    }
    
    /**
     * @return the next interrupt key in the queue, or null if
     *         no interrupts are waiting.
     */
    public abstract Object getNextInterrupt();
    
}

