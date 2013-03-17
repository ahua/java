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
package org.pjos.common.fs.fat12;

/**
 * Used to calculate used space and free space in a directory entry list.
 *
 * Not safe for use by multiple threads.
 */
class Counter {
    
    /** The used space */
    private int used = 0;
    
    /** The offset after the final entry */
    private int end = 0;
    
    /**
     * Create a counter by counting the used space and free
     * space in the entry list starting at the given entry.
     * @param entry the entry
     */
    Counter(Entry entry) {
        Entry next = entry;
        while (next != null) {
            if (!next.isFree()) { used += 32; }
            end += 32;
            next = next.next();
        }
    }
    
    /** 
     * @return the amount of used space
     */
    int getUsed() {
        return used;
    }
    
    /**
     * @return the offset after the final entry
     */
    int getEnd() {
        return end;
    }

}
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 



