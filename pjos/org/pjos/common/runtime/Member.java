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

import java.lang.reflect.Modifier;

/**
 * Parent class for methods and fields.
 */
public abstract class Member extends Abstract {
    
    /** The modifiers */
    int flags;
    
    /** The type this member belongs to */
    Type owner;
    
    /**
     * Create a member
     * @param type the type
     */
    Member(int type) {
        super(type);
    }
    
    /**
     * @return true if this field is static, false otherwise
     */
    public boolean isStatic() {
        return Modifier.isStatic(flags);
    }
    
    /**
     * @return the modifier flags
     */
    public int getFlags() {
        return flags;
    }
    
    /**
     * @return the owner type
     */
    public Type getOwner() {
        return owner;
    }
    
}

