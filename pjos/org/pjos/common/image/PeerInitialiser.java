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
 * Initialises nodes for peer objects. Peer objects are
 * placeholders for java.lang.Class instances.
 */
class PeerInitialiser extends AbstractInitialiser {
    
    /**
     * Create a peer initialiser
     * @param creator the creator
     */
    PeerInitialiser(Creator creator) {
        super(creator);
    }
    
    /**
     * Initialise a node
     * @param node the node
     * @param key the key
     * @return true if successful
     */
    public boolean init(Node node, Object key) {
        if (!(key instanceof Peer)) { return false; }
        Peer peer = (Peer) key;
        Type type = peer.getType();
        String typename = peer.getType().getName();
        String classname = typename.replace('/', '.');
        
        node.setHeader(HEADER_INSTANCE);
        node.addPointer(getCreator().getType("java/lang/Class"));
        node.addPointer(type); // type <classname>
        node.addPointer(classname);
        node.addPointer(null); // class loader (null = boot loader)
        node.addPointer(getCreator().getPeer(type.getSuperType()));
        node.addPointer(getInterfacePeers(type));
        node.addPointer(getCreator().getPeer(type.getComponentType()));
        node.addData(TRUE); // initialised
        return true;
    }

    /**
     * Return an array containing the peers for the interfaces
     * implemented by the given type.
     */
    private Peer[] getInterfacePeers(Type type) {
        Type[] interfaceTypes = type.getInterfaces();
        int length = interfaceTypes.length;
        Peer[] result = new Peer[length];
        for (int i = 0; i < length; i++) {
            result[i] = getCreator().getPeer(interfaceTypes[i]);
        }
        return result;
    }
    
}








