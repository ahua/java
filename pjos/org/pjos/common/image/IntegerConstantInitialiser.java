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

import org.pjos.common.runtime.IntegerConstant;

/**
 * Initialises integer constant nodes
 */
class IntegerConstantInitialiser extends AbstractInitialiser {
    
    /**
     * Create an integer constant initialiser
     */
    IntegerConstantInitialiser(Creator creator) {
        super(creator);
    }
    
    /**
     * Initialise a node
     * @param node the node
     * @param key the key
     * @return true if successful
     */
    public boolean init(Node node, Object key) {
        if (!(key instanceof IntegerConstant)) { return false; }
        IntegerConstant ic = (IntegerConstant) key;
        node.setHeader(HEADER_INSTANCE);
        node.addPointer(getCreator().getType(
                "org/pjos/common/runtime/IntegerConstant"));
        node.addData(ic.getId());
        node.addData(ic.getValue());
        return true;
    }

}








