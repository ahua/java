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

import org.pjos.common.runtime.Type;

/**
 * Initialise type nodes
 */
class TypeInitialiser extends AbstractInitialiser {
    
    /**
     * Create a type initialiser
     * @param creator the creator
     */
    TypeInitialiser(Creator creator) {
        super(creator);
    }
    
    /**
     * Initialise a node
     * @param node the node
     * @param key the key
     * @return true if successful
     */
    public boolean init(Node node, Object key) {
        if (!(key instanceof Type)) { return false; }
        Type t = (Type) key;

        int isLinked = (t.isLinked()) ? TRUE : FALSE;
        int isPrimitive = (t.isPrimitive()) ? TRUE : FALSE;
        
        node.setHeader(HEADER_INSTANCE);
        node.addPointer(getCreator().getType("org/pjos/common/runtime/Type"));
        node.addData(t.getId());
        node.addPointer(getCreator().getPeer(t));
        node.addPointer(new Statics(t));
        node.addPointer(t.getName());
        node.addPointer(t.getCode());
        node.addData(t.getFlags());
        node.addPointer(t.getPool());
        node.addPointer(t.getSuperName());
        node.addPointer(t.getSuperType());
        node.addPointer(t.getInterfaceNames());
        node.addPointer(t.getInterfaces());
        node.addPointer(t.getMethods());
        node.addPointer(t.getFields());
        node.addData(t.getInstanceFieldCount());
        node.addData(t.getStaticFieldCount());
        node.addPointer(t.getSource());
        node.addData(isLinked);
        node.addPointer(t.getComponentName()); // name of component type
        node.addPointer(t.getComponentType()); // array component type
        node.addData(t.getWidth());            // array width
        node.addData(isPrimitive);
        node.addPointer(getArrayType(t));
        node.addPointer(t.getInstanceMap());
        node.addPointer(t.getStaticMap());
        return true;
    }
    
    /**
     * Return the array type for the given type, null for primitive arrays
     */
    private Type getArrayType(Type component) {
        String name = component.getName();
        if (component.isArray()) {
            return getCreator().getType("[" + name);
        }
        if (!component.isPrimitive()) {
            return getCreator().getType("[L" + name + ";");
        }
        return null;
    }

}








