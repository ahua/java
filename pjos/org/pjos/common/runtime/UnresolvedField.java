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
 * Represents an unresolved field entry in the runtime constant pool
 */
public class UnresolvedField extends Abstract {
    
    /**
     * Create an unresolved field entry
     * @param name the field name
     * @param descriptor the field descriptor
     * @param classname the class name
     */
    UnresolvedField(String name, String descriptor, String classname) {
        super(UNRESOLVED_FIELD);
        this.name = name;
        this.descriptor = descriptor;
        this.classname = classname;
    }

    /**
     * @return the name of the class required to resolve this entry
     */
    public String getClassnameToResolve() {
        return classname;
    }

    /**
     * @param type the type
     * @return the resolved field object if available
     */
    public Entry resolve(Type type) {
        return (classname == type.name)
                ? (Entry) type.getField(name, descriptor)
                : this;
    }

    /**
     * @return a string description
     */
    public String toString() {
        return "Unresolved Field: " + classname + "." + name + descriptor;
    }

}

