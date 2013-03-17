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

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.pjos.common.runtime.Entry;
import org.pjos.common.runtime.Method;
import org.pjos.common.runtime.Type;

/**
 * Loads and manages type instances for a binary image
 */
class TypeManager {

    /** The creator */
    private Creator creator;
    
    /** Contains the types, indexed by name */
    private Type[] types;
    
    /** Contains the types, indexed by name */
    private Map typeMap = new HashMap();
    
    /**
     * Create a type manager for the given image
     * @param creator the creator
     */
    TypeManager(Creator creator) {
        this.creator = creator;
    }
    
    /**
     * Load and resolve types and related objects
     * @param doc the document
     * @param classpath the classpath
     */
    void loadTypes(Document doc, String classpath) throws IOException {
        // load unlinked types into map
        TypeLoader loader = new TypeLoader(classpath);
        NodeList classNodes = doc.getElementsByTagName("class");
        int length = classNodes.getLength();
        types = new Type[length];
        for (int i = 0; i < length; i++) {
            Node node = classNodes.item(i);
            String name = Creator.getAttribute("name", node);
            Type type = loader.load(name);
            types[i] = type;
            typeMap.put(name, type);
        }
        
        // Each of the following steps must be done separately
        for (int i = 0; i < length; i++) { link(types[i]); }
        readMagicMethods(doc);
        for (int i = 0; i < length; i++) { resolve(types[i]); }
        for (int i = 0; i < length; i++) { creator.load(types[i]); }
    }
    
    /**
     * Read the magic method configuration and mark the method objects
     */
    private void readMagicMethods(Document doc) throws IOException {
        NodeList magicNodes = doc.getElementsByTagName("magic");
        for (int i = 0, n = magicNodes.getLength(); i < n; i++) {
            Node node = magicNodes.item(i);
            String attribute = Creator.getAttribute("name", node);
            String classname = extractClassname(attribute);
            String name = extractName(attribute);
            String descriptor = extractDescriptor(attribute);
            int id = Integer.parseInt(Creator.getAttribute("id", node));
            Type type = get(classname);
            if (type == null) {
                throw new IllegalStateException("Type not found: " + classname);
            }
            Method method = type.getMethod(name, descriptor);
            if (method == null) {
                throw new IllegalStateException(
                        "Method not found: " + classname + "."
                        + name + descriptor);
            }
            if (!method.isNative()) {
                throw new IllegalStateException("Method not native: " + method);
            }
            method.setMagic(id);
        }
    }
    
    /**
     * Extract the classname from string of form:
     * <classname>.<name><descriptor>
     */
    private String extractClassname(String s) {
        return s.substring(0, s.indexOf('.'));
    }
    
    /**
     * Extract the name from string of form: <classname>.<name><descriptor>
     */
    private String extractName(String s) {
        int dot = s.indexOf('.');
        int bracket = s.indexOf('(');
        return s.substring(dot + 1, bracket);
    }
    
    /**
     * Extract the descriptor from string of form:
     * <classname>.<name><descriptor>
     */
    private String extractDescriptor(String s) {
        return s.substring(s.indexOf('('));
    }
    
    /**
     * Resolve all the entries in the runtime constant pool of the given type
     */
    private void resolve(Type type) {
        Entry[] pool = type.getPool();
        for (int i = 0, n = pool.length; i < n; i++) {
            Entry entry = pool[i];
            Type t = (entry != null)
                    ? get(entry.getClassnameToResolve())
                    : null;
            if (t != null) { pool[i] = entry.resolve(t); }
        }
    }
    
    /**
     * Link type and resolve its constant pool entries
     */
    private Type link(Type type) {
        if (type == null || type.isLinked()) { return type; }

        // link to super, interface and component types
        Type superType = link(get(type.getSuperName()));
        Type[] interfaces = getLinkedTypes(type.getInterfaceNames());
        Type componentType = link(get(type.getComponentName()));
        type.link(superType, interfaces, componentType);
        
        // resolve and return
        return type;
    }
    
    /**
     * Return an array of linked types matching the given names
     */
    private Type[] getLinkedTypes(String[] names) {
        int length = names.length;
        Type[] result = new Type[length];
        for (int i = 0; i < length; i++) {
            result[i] = link(get(names[i]));
        }
        return result;
    }

    /**
     * @param name the name
     * @return the type with the given name
     */
    Type get(String name) {
        return (Type) typeMap.get(name);
    }
    
    /**
     * @return an array containing all loaded types
     */
    Type[] getAll() {
        return types;
    }
    
}
