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

import java.io.DataInput;
import java.io.IOException;

/**
 * Represents a basic runtime constant pool. This class is used
 * to read constant pool information and produce an array of
 * Entry objects to represent the pool.
 */
class BasicPool {

    /** The entries in the pool */
    private BasicEntry[] entries;

    /**
     * Read in the list of constant pool entries from the given input stream
     * @param in the input stream
     * @return the array of entries
     * @throws IOException if an error occurs
     */
    static Entry[] read(DataInput in) throws IOException {
        BasicPool pool = new BasicPool(in);
        return pool.createEntries();
    }
        
    /**
     * Create a basic constant pool by reading from the given input stream
     */
    private BasicPool(DataInput in) throws IOException {
        int size = in.readUnsignedShort();
        entries = new BasicEntry[size];
        entries[0] = null;              // first entry is always null
        for (int i = 1; i < size; ) {
            BasicEntry entry = BasicEntry.read(in);
            entries[i] = entry;
            i += entry.getSize();
        }
    }
    
    /**
     * Create entries to match the ones in this pool
     */
    private Entry[] createEntries() {
        int length = entries.length;
        Entry[] result = new Entry[length];
        for (int i = 0; i < length; i++) {
            BasicEntry be = entries[i];
            if (be != null) {
                result[i] = be.createEntry(entries);
            }
        }
        return result;
    }
    
    /**
     * @param index the index
     * @return the entry at the given index
     */
    BasicEntry get(int index) {
        return entries[index];
    }
    
}

