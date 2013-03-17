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

import org.pjos.common.runtime.Field;

/**
 * Initialises field nodes
 */
class FieldInitialiser extends AbstractInitialiser {
    
    /**
     * Create a field initialiser
     * @param creator the creator
     */
    FieldInitialiser(Creator creator) {
        super(creator);
    }
    
    /**
     * Initialise a node
     */
    public boolean init(Node node, Object key) {
        if (!(key instanceof Field)) { return false; }
        Field field = (Field) key;
        int isReference = (field.isReference()) ? TRUE : FALSE;

        node.setHeader(HEADER_INSTANCE);
        node.addPointer(getCreator().getType("org/pjos/common/runtime/Field"));
        node.addData(field.getId());
        node.addPointer(field.getName());
        node.addPointer(field.getDescriptor());
        node.addPointer(field.getClassname());
        node.addData(field.getFlags());
        node.addPointer(field.getOwner());
        node.addData(field.getIndex());
        node.addData(isReference);
        node.addData(field.getSize());
        node.addPointer(field.getConstantValue());
        return true;
    }

}








