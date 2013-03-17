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
import java.io.PrintStream;

import java.nio.ByteOrder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A web is used to allocate and keep track of nodes before creating
 * an executable image. Each node in the web is associated with a
 * key object and kept in a map. 
 */
class Web {
    
    /** The map of nodes */
    private Map nodes = new HashMap();
    
    /** The executable image */
    private Image image = null;
    
    /** The physical memory address */
    private int physical;
    
    /** The byte order for this image */
    private ByteOrder order;
    
    /** The initialisers */
    private Initialiser[] initialisers;
    
    /** The generated image */
    private byte[] generated = null;
    
    /** Used to write entries to the log after allocation */
    private PrintStream log;

    /**
     * Create a web to generate an image with the specified
     * byte order and physical address.
     * @param order the byte order
     * @param physical the physical address
     * @param intialisers the initialiser objects
     * @param log used to write log entries
     */
    Web(ByteOrder order,
            int physical,
            Initialiser[] initialisers,
            PrintStream log)
    {
        if (order == null || physical < 0 || initialisers == null) {
            throw new IllegalArgumentException();
        }
        this.order = order;
        this.physical = physical;
        this.initialisers = initialisers;
        this.log = log;
    }
    
    /**
     * @param key the key
     * @return a node for the given object, create one if necessary
     */
    synchronized Node get(Object key) {
        if (key == null) { throw new NullPointerException(); }
        Node result = (Node) nodes.get(key);
        if (result == null) {
            if (image != null) {
                throw new IllegalArgumentException(
                        "Nodes have already been allocated objects");
            }
            result = new Node(this, key);
            nodes.put(key, result);
            initialise(result, key);
        }
        return result;
    }
    
    /**
     * Initialise the node using the given key
     */
    private void initialise(Node node, Object key) {
        for (int i = 0, n = initialisers.length; i < n; i++) {
            if (initialisers[i].init(node, key)) { return; }
        }
        throw new IllegalStateException(
                "No initialiser for key: " + key
                + " {" + key.getClass().getName() + "}");
    }
    
    /**
     * Creates the image and allocates objects for each node, allocating
     * the node with the given key at the beginning.
     * @param key the key
     */
    synchronized void allocate(Object key) {
        if (image != null) {
            throw new IllegalStateException("Already allocated");
        }
        if (key == null) { throw new NullPointerException(); }
        image = new Image(physical, order);
        Node first = (Node) nodes.get(key);
        if (first == null) {
            throw new IllegalStateException("Invalid root key: " + key);
        }
        first.allocate(image);
        log.println(Integer.toHexString(first.address()) + ": " + first);
        for (Iterator it = nodes.values().iterator(); it.hasNext(); ) {
            Node next = (Node) it.next();
            next.allocate(image);
            log.println(Integer.toHexString(next.address()) + ": " + next);
        }
    }
    
    /**
     * @return the address immediately following the last allocation
     */
    synchronized int next() {
        if (image == null) {
            throw new IllegalStateException("Image not allocated");
        }
        return image.next();
    }
    
    /**
     * Generate image as a byte array and return
     * @return the generated array
     */
    synchronized byte[] generate() throws IOException {
        if (image == null) {
            throw new IllegalStateException("Image not allocated");
        }
        if (generated == null) { generated = image.generate(); }
        return generated;
    }
    
    /**
     * @return a set containing all keys of the specified class
     */
    synchronized Set getMatchingKeys(Class c) {
        Set result = new HashSet();
        for (Iterator it = nodes.keySet().iterator(); it.hasNext();) {
            Object next = it.next();
            if (next.getClass().equals(c)) { result.add(next); }
        }
        return result;
    }
    
}





