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

import java.io.DataInput;
import java.io.IOException;

/**
 * Holds field information to be accessed at runtime.
 */
public class Field extends Member {

    /** The index (set during linking) */
    int index = -1;
    
    /** This flag is set if the field is a reference */
    boolean isReference;
    
    /** The size, 1 for 32 bits, 2 for 64 bits */
    int size;
    
    /** The constant value (used for some static fields only) */
    Entry constantValue;

    /**
     * Create a field by reading the information
     * in from the given stream.
     * @param in the input stream
     * @param owner the owner (parent type)
     * @throws IOException if an error occurs
     */
    Field(DataInput in, Type owner) throws IOException {
        super(FIELD);
        this.owner = owner;
        classname = owner.name;
        read(in);
    }
    
    /**
     * Create a field with the given values (used for array length field)
     * @param name the name
     * @param descriptor the descriptor
     * @param classname the class name
     * @param flags the flags
     * @param owner the owner (parent type)
     * @param size the variable size (1 for 32-bit, 2 for 64-bit)
     */
    Field(String name,
            String descriptor,
            String classname,
            int flags,
            Type owner,
            int size)
    {
        super(FIELD);
        this.name = name;
        this.descriptor = descriptor;
        this.classname = classname;
        this.flags = flags;
        this.owner = owner;
        this.size = size;
    }
    
    /**
     * Read in the information for this field
     */
    private void read(DataInput in) throws IOException {
        // read flags
        flags = in.readUnsignedShort();
        
        // read name
        Entry[] pool = owner.pool;
        name = pool[in.readUnsignedShort()].toString();
        
        // read descriptor
        descriptor = pool[in.readUnsignedShort()].toString();
        
        // calculate size
        size = (descriptor.startsWith("J") || descriptor.startsWith("D"))
                ? 2
                : 1;
        
        // set reference flag
        isReference = descriptor.startsWith("[") || descriptor.startsWith("L");
        
        // read attributes
        int count = in.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            String id = pool[in.readUnsignedShort()].toString();
            
            if (id.equals("ConstantValue")) {
                // should really handle constant values somehow...
                in.readInt();
                int k = in.readUnsignedShort();
                if (isStatic()) { constantValue = pool[k]; }
            } else {
                // ignore all others
                in.skipBytes(in.readInt());
            }
        }
    }
    
    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * @return the reference flag
     */
    public boolean isReference() {
        return isReference;
    }
    
    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }
    
    /**
     * @return the constant value
     */
    public Entry getConstantValue() {
        return constantValue;
    }
    
    /**
     * @return a string description
     */
    public String toString() {
        return "Field: " + classname + "." + name + "." + descriptor;
    }
    
}

