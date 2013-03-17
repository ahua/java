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
 * Holds the data for a basic constant pool entry.
 */
class BasicEntry implements Constants {

    /** The tag */
    private int tag;

    /** The first value */
    private int first;

    /** The second value */
    private int second;
    
    /** The string value */
    private String stringValue;
    
    /** The integer value */
    private int intValue;
    
    /** The long value */
    private long longValue;
    
    /** The size (the number of entries used by this entry) */
    private int size = 1;
    
    /**
     * Create an instance
     */
    private BasicEntry() {
        // nothing to do here
    }

    /**
     * Read in the next constant pool entry from the given stream
     * @param in the input stream
     * @return the entry read
     * @throws IOException if an error occurs
     */
    static BasicEntry read(DataInput in) throws IOException {
        BasicEntry result = new BasicEntry();
        result.tag = in.readUnsignedByte();
        switch (result.tag) {
            // read in two values
            case CP_METHODREF:
            case CP_INTERFACEMETHODREF:
            case CP_FIELDREF:
            case CP_NAMEANDTYPE:
                result.first = in.readUnsignedShort();
                result.second = in.readUnsignedShort();
                break;

            // read in one value
            case CP_CLASS:
            case CP_STRING:
                result.first = in.readUnsignedShort();
                break;

            // read in string
            case CP_UTF8:
                result.stringValue = in.readUTF().intern();
                break;

            // read in 32 bit constant
            case CP_INTEGER:
            case CP_FLOAT:
                result.intValue = in.readInt();
                break;

            // read in 64 bit numeric constant
            case CP_LONG:
            case CP_DOUBLE:
                long high = (long) in.readInt();
                long low = (long) in.readInt();
                result.longValue = (high << 32) | (low & 0xffffffffL);
                result.size = 2; // these take up two entries
                break;
        }
        return result;
    }
    
    /**
     * @return the size (the number of entries used by this entry)
     */
    int getSize() {
        return size;
    }
    
    /**
     * @param pool the constant pool entries
     * @return an Entry instance to represent this basic entry. It is
     *         valid to return null if an entry is not required.
     */
    Entry createEntry(BasicEntry[] pool) {
        switch (tag) {
            case CP_STRING:
                return new StringConstant(pool[first].stringValue);
                
            case CP_UTF8:
                return new StringConstant(stringValue);

            case CP_CLASS:
                return new UnresolvedType(pool[first].stringValue);
            
            case CP_LONG:
            case CP_DOUBLE:
                return new LongConstant(longValue);
            
            case CP_INTEGER:
            case CP_FLOAT:
                return new IntegerConstant(intValue);
            
            case CP_METHODREF:
            case CP_INTERFACEMETHODREF:
            case CP_FIELDREF:
                return createMemberEntry(pool);
        }
        return null;
    }
    
    /**
     * Return a method or field entry representing this basic entry
     */
    private Entry createMemberEntry(BasicEntry[] pool) {
        BasicEntry classEntry = pool[first];
        String classname = pool[classEntry.first].stringValue;
        BasicEntry nameAndType = pool[second];
        String name = pool[nameAndType.first].stringValue;
        String descriptor = pool[nameAndType.second].stringValue;
        if (tag == CP_FIELDREF) {
            return new UnresolvedField(name, descriptor, classname);
        } else {
            return new UnresolvedMethod(name, descriptor, classname);
        }
    }
    
}

