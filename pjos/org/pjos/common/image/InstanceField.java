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

import java.util.StringTokenizer;

import org.pjos.common.runtime.Constants;
import org.pjos.common.runtime.Method;
import org.pjos.common.runtime.Type;

/**
 * Represents a configured field value for an instance
 */
class InstanceField implements Constants {

    /** The creator */
    private Creator creator;
    
    /** The type */
    private String type;
    
    /** The value */
    private String value;
    
    /** This flag can be set to indicate that this field has been used */
    private boolean used = false;
    
    /**
     * Create an instance field
     * @param type the type
     * @param value the value
     * @param creator the creator
     */
    InstanceField(String type, String value, Creator creator) {
        this.type = type.intern();
        this.value = value;
        this.creator = creator;
    }
    
    /**
     * @return the slot which represents this field value
     */
    Slot getSlot() {
        if (type == "instance") {
            return get(creator.getInstance(value));
        } else if (type == "type") {
            return get(getType(value));
        } else if (type == "int") {
            return getInteger();
        } else if (type == "frame") {
            return get(creator.getFrame(value));
        } else if (type == "next") {
            return new NextSlot(creator);
        } else if (type == "boolean") {
            return getBoolean();
        } else if (type == "method") {
            return getMethod();
        } else if (type == "strings") {
            return get(creator.getStrings());
        } else if (type == "types") {
            return get(creator.getTypes());
        }
        throw new IllegalStateException(
                "Instance type not recognised: " + type);
    }
    
    /**
     * Return a slot representing a boolean value
     */
    private Slot getBoolean() {
        int bool = (value.equals("true")) ? TRUE : FALSE;
        return new DataSlot(bool);
    }
    
    /**
     * Return a slot representing a method
     */
    private Slot getMethod() {
        StringTokenizer st = new StringTokenizer(value, ":");
        if (st.countTokens() != 3) {
            throw new IllegalStateException(
                    "Invalid method specification: " + value);
        }
        String classname = st.nextToken();
        String name = st.nextToken();
        String descriptor = st.nextToken();
        Method method = getType(classname).getMethod(name, descriptor);
        if (method == null) {
            throw new IllegalStateException(
                    "Invalid method specification: " + value);
        }
        return creator.getPointerTo(method);
    }
    
    /**
     * Return the type represented by the given string
     */
    private Type getType(String name) {
        return creator.getType(name);
    }
    
    /**
     * Return a pointer to the allocation for the given object
     */
    private Slot get(Object o) {
        return creator.getPointerTo(o);
    }
    
    /**
     * Return a slot representing an integer value
     */
    private Slot getInteger() {
        int i = Integer.decode(value).intValue();
        return new DataSlot(i);
    }
    
    /**
     * @return the used flag
     */
    boolean getUsed() {
        return used;
    }
    
    /**
     * Set the used flag
     */
    void setUsed() {
        used = true;
    }
    
}








