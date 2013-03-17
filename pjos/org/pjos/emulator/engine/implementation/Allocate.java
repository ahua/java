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
 * Contains code used to allocate memory.
 */
class Allocate implements Constants {

    /**
     * @return percentage of memory used
     */
    static int used() {
        int next = Mem.load(Reg.core + 4 * CORE_NEXT);
        return (next - Reg.core) * 100 / Mem.LIMIT;
    }
    
    /**
     * Allocate space for an object. Return the address of the newly allocated
     * object. If there is not enough space, run garbage collection and return
     * null. The object header and size are initialised, and all words of the
     * object are set to zero.
     *
     * Code calling this method should check if null is returned, this means
     * garbage collection has taken place and all previous memory addresses
     * have become invalid.
     * @param numwords the number of 32-bit words required by the object
     * @param header the header for the new object
     * @return the address of the allocated space
     */
    static int allocate(int numWords, int header) {
        // find the next available address
        int numBytes = numWords * 4;
        int address = Mem.load(Reg.core + 4 * CORE_NEXT);
        int nextAddress = address + numBytes;
        
        // do garbage collection if not enough space...
        int result = NULL;
        if (nextAddress >= Reg.core + Mem.LIMIT) {
            Collector.gc();
        } else {
            // ...otherwise initialise
            Mem.store(header, address);
            Mem.store(numWords, address + 4);
            
            // initialise rest of object to zero/null
            for (int i = 2; i < numWords; i++) {
                Mem.store(0x00000000, address + (i * 4));
            }
            result = address;
            
            // reset next pointer to next free space
            Mem.store(nextAddress, Reg.core + 4 * CORE_NEXT);
        }
        return result;
    }
    
}
