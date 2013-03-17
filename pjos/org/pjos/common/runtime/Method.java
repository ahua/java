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

import java.lang.reflect.Modifier;

/**
 * Holds method information to be accessed at runtime. Internal arrays
 * accessed through the public methods can be altered externally. They
 * are made available to prevent duplication in a runtime environment.
 */
public final class Method extends Member {

    /** The constant pool */
    Entry[] pool;
    
    /** The max number of stack entries */
    int maxStack;
    
    /** The max number local variables */
    int maxLocals;

    /** The number of arguments */
    int argCount;

    /** The byte code */
    byte[] code;

    /** The exceptions table */
    short[] exceptions;
    
    /** The line number table */
    short[] lineNumbers;
    
    /**
     * The magic number (zero for native methods,
     * non-zero for magic methods)
     */
    int magic;
    
    /**
     * Create a method by reading the information
     * in from the given stream.
     * @param in the input stream
     * @param owner the ownere (the parent type)
     * @throws IOException if an error occurs
     */
    Method(DataInput in, Type owner) throws IOException {
        super(METHOD);
        this.owner = owner;
        classname = owner.name;
        read(in);
    }
    
    /**
     * Create a method using the given information. This should only be used
     * to create native or magic methods because there is no way of setting
     * the fields necessary for real methods.
     * @param name the name
     * @param descriptor the descriptor
     * @param classname the class name
     * @param flags the flags
     * @param owner the owner (the parent type)
     */
    Method(String name,
            String descriptor,
            String classname,
            int flags,
            Type owner)
    {
        super(METHOD);
        this.name = name;
        this.descriptor = descriptor;
        this.classname = classname;
        this.flags = flags;
        this.owner = owner;
        pool = owner.pool;
        countArguments();
    }
    
    /**
     * Count the number of 32 bit arguments that would be passed
     * to this method. 64 bit arguments are counted as two 32 bit
     * arguments. If this method is an instance method, the reference
     * to the object must also be passed, this counts as one 32 bit
     * argument.
     */
    private void countArguments() {
        String d = descriptor;
        int count = 0;
        if (!isStatic()) { count++; } // count instance reference
        int pos = 1;
        char c = d.charAt(pos);
        while (c != ')') {
            switch (c) {
                // 64 bit primitives
                case 'D': case 'J':
                    count++; // these take up an extra space
                    break;

                // object reference
                case 'L':
                    while (d.charAt(pos) != ';') { pos++; }
                    break;

                // array reference
                case '[':
                    while (d.charAt(pos) == '[') { pos++; }
                    if (d.charAt(pos) == 'L') {
                        while (d.charAt(pos) != ';') { pos++; }
                    }
                    break;
            }
            count++;
            c = d.charAt(++pos);
        }
        argCount = count;
    }

    /**
     * Read in the information for this method
     */
    private void read(DataInput in) throws IOException {
        // read flags, name and descriptor
        pool = owner.pool;
        flags = in.readUnsignedShort();
        name = pool[in.readUnsignedShort()].toString();
        descriptor = pool[in.readUnsignedShort()].toString();

        // read attributes
        int count = in.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            // read in the method code, ignore others
            String id = pool[in.readUnsignedShort()].toString();
            if (id.equals("Code")) {
                readCode(in);
            } else {
                in.skipBytes(in.readInt());
            }
        }
        countArguments();
    }
    
    /**
     * Read in the bytecode and exception table
     */
    private void readCode(DataInput in) throws IOException {
        in.readInt(); // skip length
        maxStack = in.readUnsignedShort();
        maxLocals = in.readUnsignedShort();

        // byte codes
        int count = in.readInt();
        code = new byte[count];
        for (int i = 0; i < count; i++) {
            code[i] = in.readByte();
        }

        // exception table
        count = in.readUnsignedShort() * 4;
        exceptions = new short[count];
        for (int i = 0; i < count; i++) {
            exceptions[i] = in.readShort();
        }

        // read code attributes
        count = in.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            String id = pool[in.readUnsignedShort()].toString();
            
            // read line number table, ignore others
            if (id.equals("LineNumberTable")) {
                readLineNumbers(in);
            } else {
                in.skipBytes(in.readInt());
            }
        }
    }

    /**
     * Read in the line number table
     */
    private void readLineNumbers(DataInput in) throws IOException {
        in.readInt(); // skip length
        int count = in.readUnsignedShort() * 2; // length of line number table
        lineNumbers = new short[count];
        for (int i = 0; i < count; i++) {
            lineNumbers[i] = in.readShort();
        }
    }

    /**
     * @return the number of stack entries
     */
    public int getMaxStack() {
        return maxStack;
    }
    
    /**
     * @return the number of local entries
     */
    public int getMaxLocals() {
        return maxLocals;
    }
    
    /**
     * @return the number of arguments
     */
    public int getArgCount() {
        return argCount;
    }
    
    /**
     * @return the byte codes
     */
    public byte[] getCode() {
        return code;
    }
    
    /**
     * @return the exception table
     */
    public short[] getExceptions() {
        return exceptions;
    }

    /**
     * @return the line number table
     */
    public short[] getLineNumbers() {
        return lineNumbers;
    }
    
    /**
     * @return the constant pool
     */
    public Entry[] getPool() {
        return pool;
    }
    
    /**
     * @return a string description
     */
    public String toString() {
        return "Method: " + classname + "." + name + descriptor;
    }

    /**
     * @return true if this field is native, false otherwise
     */
    public boolean isNative() {
        return Modifier.isNative(flags);
    }
    
    /**
     * Set the magic number
     * @param magic the magic number
     */
    public void setMagic(int magic) {
        this.magic = magic;
    }
    
    /**
     * @return the magic number
     */
    public int getMagic() {
        return magic;
    }
    
}

