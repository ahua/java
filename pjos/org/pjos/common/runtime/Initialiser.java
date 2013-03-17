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
 * This is the first thread to run and initialises the VM.
 */
class Initialiser extends Thread {

    /** The Strings to be interned (set during image creation) */
    private String[] toBeInterned;

    /**
     * The Classes to be initialised and registered
     * (set during image creation)
     */
    private Type[] toBeRegistered;

    /**
     * Create an initialiser thread (not used, created by system)
     */
    private Initialiser() {
        // this code should never be called
        super(null, null);
        throw new IllegalStateException();
    }

    /**
     * Do the initialisation
     */
    public void run() {
        try {
            // set thread name
            setName("Initialiser");
            
            // Run the class initialisers for all the loaded classes
            BootClassLoader bcl = BootClassLoader.get();
            for (int i = 0, n = toBeRegistered.length; i < n; i++) {
                Type type = toBeRegistered[i];
                Method clinit = type.getMethod("<clinit>", "()V");
                if (clinit != null) {
                    Core.executeStatic(clinit);
                }
                //Core.debug("registered type: " + type.getName());
                bcl.register(type);
            }
            Core.debug("Types registered and initialised");
            
            // Intern strings
            for (int i = 0, n = toBeInterned.length; i < n; i++) {
                String s = toBeInterned[i];
                s.intern();
                //Core.debug("string interned: " + s);
            }
            Core.debug("Strings interned");
            
            // Run platform initialiser
            Core core = Core.get();
            Method init = core.architectureType.getMethod("init", "()V");
            Core.executeStatic(init);
        } catch (Throwable t) {
            Core.debug("Uncaught exception in initialiser thread");
            if (System.err != null) {
                t.printStackTrace();
            } else {
                Core.debug(t.toString());
                StackTraceElement[] ste = t.getStackTrace();
                for (int i = 0, n = ste.length; i < n; i++) {
                    Core.debug("        at " + ste[i]);
                }
            }
        }
    }
                
}
