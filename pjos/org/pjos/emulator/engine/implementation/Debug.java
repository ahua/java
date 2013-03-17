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
 * Contains code used to debug the processor.
 */
class Debug implements Constants {
    
    /** Set when the processor is in debug mode */
    static boolean debug = true;
    
    /**
     * @param address the method address
     * @return a string representing the method at the given address
     */
    static String method(int address) {
        String name = string(Mem.load(address + 4 * ENTRY_NAME));
        String descriptor = string(Mem.load(address + 4 * ENTRY_DESCRIPTOR));
        String classname = string(Mem.load(address + 4 * ENTRY_CLASSNAME));
        return classname + "." + name + descriptor;
    }
    
    /**
     * @param address the string address
     * @return the string stored at the given memory location
     */
    static String string(int address) {
        if (address == NULL) {
            return "null";
        } else {
            int array = Mem.load(address + 4 * STRING_CHARS);
            int first = Mem.load(address + 4 * STRING_FIRST);
            int last = Mem.load(address + 4 * STRING_LAST);
            StringBuffer sb = new StringBuffer();
            for (int i = first; i < last; i++) {
                char c = (char) Mem.loadShort(array + 4 * ARRAY_DATA + 2 * i);
                sb.append(c);
            }
            return sb.toString();
        }
    }

}
