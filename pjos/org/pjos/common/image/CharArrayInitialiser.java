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
package org.pjos.common.image;

/**
 * Initialises nodes for character arrays
 */
class CharArrayInitialiser extends AbstractInitialiser {
    
    /**
     * Create a char array initialiser
     * @param creator the creator
     */
    CharArrayInitialiser(Creator creator) {
        super(creator);
    }
    
    /**
     * Initialise a node
     * @param node the node
     * @param key the key
     * @return true if successful
     */
    public boolean init(Node node, Object key) {
        if (!(key instanceof char[])) { return false; }
        char[] data = (char[]) key;

        node.setHeader(HEADER_DATA_ARRAY);
        node.addPointer(getCreator().getType("[C"));             // type
        node.addData(data.length);                          // length
        for (int i = 0, n = data.length; i < n; i += 2) {
            int value = get(i, data) << 16 | get(i + 1, data);
            node.add(new ShortSlot(value));                 // 2 chars per int
        }
        return true;
    }
    
    /**
     * Return the char value at the given index if it exists, otherwise zero
     */
    private int get(int index, char[] c) {
        if (index < c.length) { return c[index]; }
        return 0;
    }

}








