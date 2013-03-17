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
 * Initialises short array nodes
 */
class ShortArrayInitialiser extends AbstractInitialiser {
    
    /**
     * Create a short array initialiser
     * @param creator the creator
     */
    ShortArrayInitialiser(Creator creator) {
        super(creator);
    }
    
    /**
     * Initialise a node
     * @param node the node
     * @param key the key
     */
    public boolean init(Node node, Object key) {
        if (!(key instanceof short[])) { return false; }
        short[] data = (short[]) key;

        node.setHeader(HEADER_DATA_ARRAY);
        node.addPointer(getCreator().getType("[S"));
        node.addData(data.length);
        for (int i = 0, n = data.length; i < n; i += 2) {
            int value = get(i, data) << 16 | get(i + 1, data);
            node.add(new ShortSlot(value));
        }
        return true;
    }
    
    /**
     * Return the short value at the given index if it exists, otherwise zero
     */
    private int get(int index, short[] data) {
        if (index < data.length) {
            return data[index] & 0xffff;
        }
        return 0;
    }

}








