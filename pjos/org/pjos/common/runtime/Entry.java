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
package org.pjos.common.runtime;

/**
 * Any object wishing to be an entry in a constant pool should
 * inherit from this class.
 */
public abstract class Entry implements Constants {
    
    /** The type entry id */
    public static final int TYPE = 1;
    
    /** The unresolved type entry id */
    public static final int UNRESOLVED_TYPE = 2;
    
    /** The field entry id */
    public static final int FIELD = 3;
    
    /** The unresolved field entry id */
    public static final int UNRESOLVED_FIELD = 4;
    
    /** The method entry id */
    public static final int METHOD = 5;
    
    /** The unresolved method entry id */
    public static final int UNRESOLVED_METHOD = 6;
    
    /** The integer constant entry id */
    public static final int INTEGER_CONSTANT = 7;
    
    /** The long constant entry id */
    public static final int LONG_CONSTANT = 8;
    
    /** The string constant entry id */
    public static final int STRING_CONSTANT = 9;
    
    /**
     * The id of this entry. This id is used by the runtime engine
     * to differentiate between different entry types.
     */
    int id;
    
    /**
     * Create an entry. The id passed to this constructor should
     * be one of the constants defined in this class.
     * @param id the id
     */
    Entry(int id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public final int getId() {
        return id;
    }
    
    /**
     * If this entry can be replaced with a resolved entry available
     * in the given type object, return the replacement, otherwise
     * just return this entry.
     * @param type the type
     * @return the resolved entry or this
     */
    public Entry resolve(Type type) {
        return this;
    }
    
    /**
     * If this entry requires a type to resolve it, return the
     * class name of that type, otherwise return null.
     * @return classname for resolution or null
     */
    public String getClassnameToResolve() {
        return null;
    }
    
    /**
     * Make sure this method is overridden by subclasses!
     * @return a string description
     */
    public String toString() {
        throw new UnsupportedOperationException(
                "toString() not implemented in " + getClass().getName());
    }
    
}

