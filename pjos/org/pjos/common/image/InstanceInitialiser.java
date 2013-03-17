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
import org.pjos.common.runtime.Type;

/**
 * Initialises instance nodes
 */
class InstanceInitialiser extends AbstractInitialiser {
    
    /**
     * Create an instance intialiser
     * @param creator the creator
     */
    InstanceInitialiser(Creator creator) {
        super(creator);
    }
    
    /**
     * Initialise a node
     * @param node the node
     * @param key the key
     * @return true if successful
     */
    public boolean init(Node node, Object key) {
        if (!(key instanceof Instance)) { return false; }
        Instance instance = (Instance) key;
        node.setHeader(HEADER_INSTANCE);
        Type type = getCreator().getType(instance.getClassname());
        node.addPointer(type);
        initSlots(type, node, instance);
        instance.checkUsed(); // check that all configured fields have been used
        return true;
    }
    
    /**
     * Initialise the slots for the given instance
     */
    private void initSlots(Type type, Node node, Instance instance) {
        // init super slots first
        Type superType = type.getSuperType();
        if (superType != null) { initSlots(superType, node, instance); }
        
        // add a slot for each instance field
        Field[] fields = type.getFields();
        for (int i = 0, n = fields.length; i < n; i++) {
            Field field = fields[i];
            InstanceField iField = instance.getField(field);
            if (iField != null) {
                // add a configured field slot
                node.add(iField.getSlot());
                iField.setUsed();
            } else if (field.isReference()) {
                // add null pointer slot
                node.addPointer(null);
            } else {
                // zero data slot (2 slots for doubles or longs)
                node.addData(0);
                if (field.getSize() == 2) { node.addData(0); }
            }
        }
    }
        
}








