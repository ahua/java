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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Manage instances for a binary image
 */
class InstanceManager {

    /** The creator */
    private Creator creator;
    
    /** Contains the instances, indexed by name */
    private Map instanceMap = new HashMap();
    
    /**
     * Create an instance manager
     * @param creator the creator
     */
    InstanceManager(Creator creator) {
        this.creator = creator;
    }
    
    /**
     * Read the instance entries and initialise them
     * @param the document holding the configuration info
     */
    void loadInstances(Document doc) {
        // create the instances from the xml configuration
        NodeList instanceNodes = doc.getElementsByTagName("instance");
        for (int i = 0, n = instanceNodes.getLength(); i < n; i++) {
            addInstance(new Instance(instanceNodes.item(i), creator));
        }
        
        // load the instances into the web
        for (Iterator it = instanceMap.values().iterator(); it.hasNext();) {
            Instance next = (Instance) it.next();
            creator.load(next);
        }
    }
    
    /**
     * Add an instance
     */
    private void addInstance(Instance i) {
        String name = i.getName();
        if (instanceMap.containsKey(name)) {
            throw new IllegalStateException("Instance already exists: " + name);
        }
        instanceMap.put(name, i);
    }
    
    /**
     * @param name the name
     * @return the instance with the given name
     */
    Instance get(String name) {
        Instance result = (Instance) instanceMap.get(name);
        if (result == null) {
            throw new IllegalStateException("No instance defined: " + name);
        }
        return result;
    }
    
}
