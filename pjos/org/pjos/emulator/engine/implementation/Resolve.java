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
 * Contains code to resolve constant pool entries
 */
class Resolve implements Constants {
    
    /**
     * Retrieve the constant pool entry at the given index. If the id of the
     * entry does not match the given id call the resolve method of the core
     * object and return null.
     * @param index the index
     * @param id the id of the desired entry
     * @return a reference to the entry
     */
    static int resolve(int index, int id) {
        int entry = Mem.load(Reg.pool + 4 * index);
        int entryId = Mem.load(entry + 4 * ENTRY_ID);
        if (entryId != id) {
            int resolveMethod = Mem.load(Reg.core + 4 * CORE_RESOLVE_METHOD);
            int target = Mem.load(Reg.core + 4 * OBJECT_TYPE);
            Invoke.executeMethod(target, resolveMethod, 0);
            return NULL;
        }
        return entry;
    }

}









