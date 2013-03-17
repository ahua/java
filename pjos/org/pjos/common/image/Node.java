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

import java.util.LinkedList;
import java.util.List;

import org.pjos.common.runtime.Constants;

/**
 * A Node is part of a web that can be linked to other nodes. Each
 * node is used to generate an object that can be part of an executable
 * image.
 */
class Node implements Constants {
    
    /** The key */
    private Object key;
    
    /** The web */
    private Web web;
    
    /** The slots */
    private List slots = new LinkedList();
    
    /** The allocation generated for this node */
    private Allocation allocation = null;
    
    /** Set after resolution */
    private boolean resolved = false;
    
    /** The header value */
    private int header = 0;
    
    /**
     * Create a node for the given web
     * @param web the web
     * @param key the key
     */
    Node(Web web, Object key) {
        this.web = web;
        this.key = key;
        add(new HeaderSlot(this));
        add(new SizeSlot(this));
        add(new HashSlot(this));
        addPointer(null);               // lock
    }
    
    /**
     * Add a data slot
     * @param value the value
     */
    synchronized void addData(int value) {
        add(new DataSlot(value));
    }
    
    /**
     * Add a pointer slot (null also allowed)
     * @param key the key
     */
    synchronized void addPointer(Object key) {
        add(new PointerSlot(key, web));
    }
    
    /**
     * Add a slot
     * @param slot the slot
     */
    synchronized void add(Slot slot) {
        if (allocation != null) {
            throw new IllegalStateException(
                    "Object already allocated: " + this);
        }
        if (slot == null) { throw new NullPointerException(); }
        slots.add(slot);
    }
    
    /**
     * Allocate an object using the given image
     * @param image the image to use
     */
    synchronized void allocate(Image image) {
        if (allocation != null) { return; } // already allocated
        Slot[] slotArray = (Slot[]) slots.toArray(new Slot[slots.size()]);
        allocation = image.allocate(slotArray);
    }
    
    /**
     * @return the address of this node's allocation
     */
    synchronized int address() {
        if (allocation == null) {
            throw new IllegalStateException("Not allocated yet: " + this);
        }
        return allocation.address();
    }
    
    /**
     * @return the number of words in this node's allocation
     */
    synchronized int numWords() {
        if (allocation == null) {
            throw new IllegalStateException("Not allocated yet: " + this);
        }
        return allocation.numWords();
    }
    
    /**
     * Set the header
     * @param header the new header
     */
    synchronized void setHeader(int header) {
        this.header = header;
    }
    
    /**
     * @return the header
     */
    synchronized int getHeader() {
        if (header == 0) {
            throw new IllegalStateException("Header not set");
        }
        return header;
    }
    
    /**
     * @return a string description using the key
     */
    public String toString() {
        return key.toString();
    }
    
}





