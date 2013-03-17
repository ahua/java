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
 * Initialises nodes for byte arrays
 */
class ByteArrayInitialiser extends AbstractInitialiser {
    
    /**
     * Create a byte array initialiser
     * @param creator the creator
     */
    ByteArrayInitialiser(Creator creator) {
        super(creator);
    }
    
    /**
     * Initialise a node
     * @param node the node
     * @param key the key
     * @return true if successful
     */
    public boolean init(Node node, Object key) {
        if (!(key instanceof byte[])) { return false; }
        byte[] data = (byte[]) key;

        node.setHeader(HEADER_DATA_ARRAY);
        node.addPointer(getCreator().getType("[B"));             // type
        node.addData(data.length);                          // length
        for (int i = 0, n = data.length; i < n; i += 4) {
            int value = get(i, data) << 24
                    | get(i + 1, data) << 16
                    | get(i + 2, data) << 8
                    | get(i + 3, data);
            node.add(new ByteSlot(value));                  // 4 bytes per int
        }
        return true;
    }
    
    /**
     * Return the byte value at the given index
     */
    private int get(int index, byte[] b) {
        if (index < b.length) { return b[index] & 0xff; }
        return 0;
    }
    
}








