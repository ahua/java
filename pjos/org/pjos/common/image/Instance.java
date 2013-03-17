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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.pjos.common.runtime.Field;

/**
 * An instance represents an object instance to be created
 * and inserted into the binary image.
 */
class Instance {
    
    /** The creator */
    private Creator creator;
    
    /** The name */
    private String name;
    
    /** The classname */
    private String classname;
    
    /** The preset field values in this instance, indexed by 'classname:name' */
    private Map fields = new HashMap();
    
    /**
     * Create an instance
     * @param node the node
     * @param creator the creator
     */
    Instance(Node node, Creator creator) {
        this.creator = creator;
        name = Creator.getAttribute("name", node);
        classname = Creator.getAttribute("class", node);
        
        // read the fields
        NodeList children = node.getChildNodes();
        for (int i = 0, n = children.getLength(); i < n; i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals("field")) { addField(child); }
        }
    }
    
    /**
     * Add the field value represented by the given node
     */
    private void addField(Node node) {
        String fieldName = Creator.getAttribute("name", node);
        String className = Creator.getAttribute("class", node);
        String key = getKey(className, fieldName);
        String type = Creator.getAttribute("type", node);
        String value = Creator.getAttribute("value", node);
        InstanceField field = new InstanceField(type, value, creator);
        if (fields.containsKey(key)) {
            throw new IllegalStateException(
                    "Field " + key + " already defined");
        }
        fields.put(key, field);
    }
    
    /**
     * Check that all configured fields have been used
     */
    void checkUsed() {
        for (Iterator it = fields.entrySet().iterator(); it.hasNext();) {
            Map.Entry next = (Map.Entry) it.next();
            InstanceField iField = (InstanceField) next.getValue();
            String key = (String) next.getKey();
            if (!iField.getUsed()) {
                throw new IllegalStateException(
                        "Invalid instance field: " + key);
            }
        }
    }

    /**
     * @return the field value with the specified name and class, return
     *         null if none exists.
     */
    InstanceField getField(Field field) {
        String key = getKey(field.getClassname(), field.getName());
        return (InstanceField) fields.get(key);
    }
    
    /**
     * Return a suitable key for the given classname and field name
     */
    private String getKey(String classname, String fieldname) {
        return classname + "." + fieldname;
    }
    
    /**
     * @return the name
     */
    String getName() {
        return name;
    }
    
    /**
     * @return the class name
     */
    String getClassname() {
        return classname;
    }
    
    /**
     * @return a string description
     */
    public String toString() {
        return "instance: " + name + " {" + classname + "}";
    }

}








