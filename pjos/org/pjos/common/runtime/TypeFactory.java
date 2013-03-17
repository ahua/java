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

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

import java.lang.reflect.Modifier;

/**
 * Contains methods for creating type objects
 */
public class TypeFactory {

    /** The primitive types */
    private static Type[] primitives;
    
    /**
     * Don't allow instantiation!
     */
    private TypeFactory() {
        // should never be called
        throw new IllegalStateException();
    }
    
    /**
     * Create a type instance by reading class data from the given byte array.
     * @param data the byte array
     * @param offset the class data starts here
     * @param length the size of the class data
     * @return the type instance
     */
    public static Type read(byte[] data, int offset, int length) {
        try {
            DataInputStream dis = new DataInputStream(
                    new ByteArrayInputStream(data, offset, length));
            Type result = read(dis);
            dis.close();
            return result;
        } catch (IOException e) {
            throw (ClassFormatError) new ClassFormatError().initCause(e);
        }
    }
    
    /**
     * Return a type instance to represent the class of the given name. This
     * method returns primitive and array types only. If no type can be
     * generated, null is returned.
     * @param name the class name
     * @return the type object
     */
    public static Type generate(String name) {
        return (name.startsWith("["))
                ? generateArray(name, extractComponentName(name))
                : getPrimitive(name);
    }
    
    /**
     * Extract the component name from the given array name and return.
     * @param name the array name
     * @return the component name
     */
    public static String extractComponentName(String name) {
        if (!name.startsWith("[")) { throw new IllegalArgumentException(name); }
        String rest = name.substring(1);
        
        // component type could be primitive (format not checked)...
        Type primitive = getPrimitive(rest);
        String result = (primitive != null) ? primitive.name : null;
        if (rest.startsWith("[")) {
            // ...or array...
            result = rest;
        } else if (rest.startsWith("L")) {
            // ...or class (strip 'L' from front, ';' from end)
            result = rest.substring(1, rest.length() - 1);
        }
        return result;
    }
    
    /**
     * Return a type instance representing the primitive type of the given name.
     * If the given name does not represent a valid primitive type name, return
     * null.
     */
    private static Type getPrimitive(String name) {
        if (primitives == null) { createPrimitives(); }
        name = name.intern();
        for (int i = 0, n = primitives.length; i < n; i++) {
            Type primitive = primitives[i];
            if (primitive.name == name || primitive.code == name) {
                return primitive;
            }
        }
        return null;
    }

    /**
     * Create the primitive types
     */
    private static synchronized void createPrimitives() {
        if (primitives == null) {
            primitives = new Type[] {
                generatePrimitive("boolean", "Z"),
                generatePrimitive("byte", "B"),
                generatePrimitive("char", "C"),
                generatePrimitive("short", "S"),
                generatePrimitive("int", "I"),
                generatePrimitive("long", "J"),
                generatePrimitive("float", "F"),
                generatePrimitive("double", "D"),
                generatePrimitive("void", "V"),
            };
        }
    }
    
    /**
     * Generate a primitive type
     */
    private static Type generatePrimitive(String name, String code) {
        Type result = new Type();
        result.name = name.intern();
        result.code = code.intern();
        result.flags = Modifier.PUBLIC;
        result.pool = new Entry[0];
        result.superName = null;
        result.superType = null;
        result.interfaceNames = new String[0];
        result.interfaces = new Type[0]; // linked
        result.methods = new Method[0];
        result.fields = new Field[0];
        result.instanceFieldCount = 0;
        result.staticFieldCount = 0;
        result.source = null;
        result.linked = true;
        result.componentName = null;
        result.componentType = null;
        result.width = 0;
        result.primitive = true;
        result.arrayType = null;
        result.instanceMap = new boolean[0];
        result.staticMap = new boolean[0];
        return result;
    }
    
    /**
     * Generate an array type
     */
    private static Type generateArray(String name, String componentName) {
        Type result = new Type();
        result.name = name.intern();
        result.code = null;
        result.flags = Modifier.PUBLIC;
        result.pool = new Entry[0];
        result.superName = "java/lang/Object";
        result.superType = null; // not linked
        result.interfaceNames = new String[] {
            "java/lang/Cloneable",
            "java/io/Serializable"
        };
        result.interfaces = null; // not linked
        result.methods = new Method[] {
            createCloneMethod(result)
        };
        result.fields = new Field[] {
            createLengthField(result)
        };
        result.instanceFieldCount = 1;
        result.staticFieldCount = 0;
        result.source = null;
        result.linked = false;
        result.componentName = componentName.intern();
        result.componentType = null; // not linked
        result.width = getArrayWidth(componentName);
        result.primitive = false;
        result.arrayType = null;
        result.instanceMap = null; // not linked
        result.staticMap = null; // not linked
        return result;
    }

    /**
     * Return a clone method for the given array type
     */
    private static Method createCloneMethod(Type array) {
        return new Method(
            "clone",                                        // name
            "()V",                                          // descriptor
            array.name,                                     // class name
            Modifier.PUBLIC | Modifier.NATIVE,              // flags
            array                                           // owner
        );
    }

    /**
     * Create a length field for the given array type
     */
    private static Field createLengthField(Type array) {
        return new Field(
            "length",                                       // name
            "I",                                            // descriptor
            array.name,                                     // class name
            Modifier.PUBLIC | Modifier.FINAL,               // flags
            array,                                          // owner
            1                                               // size
        );
    }
    
    /**
     * Return the array width for the given component type name
     */
    private static int getArrayWidth(String name) {
        switch (name.charAt(0)) {
            case 'D':
            case 'J':
                return 8;
            case 'I':
            case 'F':
            case '[':
                return 4;
            case 'S':
            case 'C':
                return 2;
            case 'B':
            case 'Z':
                return 1;
        }
        return 4; // this is the default for object references
    }

    /**
     * Read a type from the class file data in the given stream.
     */
    private static Type read(DataInput in) throws IOException {
        Type result = new Type();

        // Check the magic number
        int k = in.readInt();
        if (k != 0xCAFEBABE) {
            throw new ClassFormatError("Wrong magic number");
        }

        // Ignore version numbers
        k = in.readUnsignedShort();
        k = in.readUnsignedShort();

        // Read in constant pool
        Entry[] pool = BasicPool.read(in);
        result.pool = pool;

        // Read access flags
        result.flags = in.readUnsignedShort();

        // class name
        UnresolvedType ut = (UnresolvedType) pool[in.readUnsignedShort()];
        result.name = ut.name;
        
        // super name
        ut = (UnresolvedType) pool[in.readUnsignedShort()];
        result.superName = (ut != null) ? ut.name : null;
        
        // interface names
        int count = in.readUnsignedShort();
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            ut = (UnresolvedType) pool[in.readUnsignedShort()];
            names[i] = ut.name;
        }
        result.interfaceNames = names;
        
        // sets fields, instanceFieldCount, staticFieldCount
        readFields(in, result);
        
        // methods
        result.methods = readMethods(in, result);
        
        // sets source (if attribute exists)
        readAttributes(in, result);
        
        // remaining fields
        result.code = null;
        result.superType = null; // not linked
        result.interfaces = null; // not linked
        result.linked = false;
        result.componentName = null;
        result.componentType = null;
        result.width = 0;
        result.primitive = false;
        return result;
    }


    /**
     * Read the fields
     */
    private static void readFields(DataInput in, Type type) throws IOException {
        int count = in.readUnsignedShort();
        Field[] fields = new Field[count];
        for (int i = 0; i < count; i++) {
            fields[i] = new Field(in, type);
        }
        type.fields = fields;
    }
    
    /**
     * Read the methods
     */
    private static Method[] readMethods(DataInput in, Type type)
            throws IOException
    {
        int count = in.readUnsignedShort();
        Method[] result = new Method[count];
        for (int i = 0; i < count; i++) {
            result[i] = new Method(in, type);
        }
        return result;
    }
    
    /**
     * Read the attributes
     */
    private static void readAttributes(DataInput in, Type type)
            throws IOException
    {
        int count = in.readUnsignedShort();
        Entry[] pool = type.pool;
        for (int i = 0; i < count; i++) {
            // read the source file attribute, skip the rest
            String id = pool[in.readUnsignedShort()].toString();
            if (id.equals("SourceFile")) {
                in.readInt(); // ignore length
                type.source = pool[in.readUnsignedShort()].toString();
            } else {
                in.skipBytes(in.readInt());
            }
        }
    }

}

