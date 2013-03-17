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
 * Represents an unresolved method entry in the runtime constant pool
 */
public class UnresolvedMethod extends Abstract {
    
    /**
     * Create an unresolved method entry
     * @param name the method name
     * @param descriptor the method descriptor
     * @param classname the class name
     */
    UnresolvedMethod(String name, String descriptor, String classname) {
        super(UNRESOLVED_METHOD);
        this.name = name;
        this.descriptor = descriptor;
        this.classname = classname;
    }

    /**
     * @return the class name required to resolve this method
     */
    public String getClassnameToResolve() {
        return classname;
    }

    /**
     * @param type the type
     * @return the resolved method object if available
     */
    public Entry resolve(Type type) {
        return (type.name == classname)
                ? (Entry) type.getMethod(name, descriptor)
                : this;
    }

    /**
     * @return a string description
     */
    public String toString() {
        return "Unresolved Method: " + classname + "." + name + descriptor;
    }

}

