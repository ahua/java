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
 * Holds class information that can be accessed at runtime. The public
 * methods of this class return the actual objects, so internal arrays
 * can be manipulated externally. This is to avoid creating duplicate
 * arrays in a runtime environment, but not particularly good OO style!
 */
public class Type extends Entry {

    /** The peer class */
    private Class peer;
    
    /** The static field values, for use internally by system */
    private Statics statics;

    /** The Name */
    String name;
    
    /** The code (used for primitive types) */
    String code;

    /** The Flags */
    int flags;

    /** The Runtime Constant Pool */
    Entry[] pool;
    
    /** The name of the super class */
    String superName;
    
    /** The type representing the super class (set during linking) */
    Type superType;

    /** The indices of the implemented interfaces */
    String[] interfaceNames;
    
    /** The interface types */
    Type[] interfaces;
    
    /** The methods */
    Method[] methods;
    
    /** The fields */
    Field[] fields;
    
    /** The number of instance fields */
    int instanceFieldCount;
    
    /** The number of static fields */
    int staticFieldCount;
    
    /** The source (eg. name of source file) */
    String source;
    
    /** Set after the type is linked */
    boolean linked;
    
    /** The component type name (arrays only) */
    String componentName;
    
    /** The component type (arrays only) */
    Type componentType;
    
    /** The array width (arrays only) */
    int width;
    
    /** Primitive type flag */
    boolean primitive;
    
    /** The array type */
    Type arrayType;
    
    /** The pointer map for instance fields */
    boolean[] instanceMap;
    
    /** The pointer map for static fields */
    boolean[] staticMap;
    
    /**
     * Create a type instance (used by TypeFactory methods)
     */
    Type() {
        super(TYPE);
    }
    
    /**
     * Set the peer class. This is not used internally to the runtime
     * package and may be used by external code to associate class and
     * type objects.
     * @param peer the new peer
     */
    public void setPeer(Class peer) {
        this.peer = peer;
    }
    
    /**
     * @return the peer class (will be null if nothing has been set).
     */
    public Class getPeer() {
        return peer;
    }
    
    /**
     * Link this type loading any necessary types using the type loader.
     * A type should only be linked once!
     * @param superType the super type
     * @param interfaces the implemented interfaces
     * @param componentType the array component type
     */
    public synchronized void link(
            Type superType,
            Type[] interfaces,
            Type componentType)
    {
        if (linked) { throw new IllegalStateException("Already linked"); }
        setSuperType(superType);
        setInterfaces(interfaces);
        setComponentType(componentType);

        // set field indices and field counts
        int instanceIndex = (superType != null)
                ? superType.instanceFieldCount
                : 0;
        int staticIndex = 0;
        for (int i = 0, n = fields.length; i < n; i++) {
            Field field = fields[i];
            if (field.isStatic()) {
                field.index = staticIndex;
                staticIndex += field.size;
            } else {
                field.index = instanceIndex;
                instanceIndex += field.size;
            }
        }
        instanceFieldCount = instanceIndex;
        staticFieldCount = staticIndex;
        createReferenceMaps();
        linked = true;
    }
    
    /**
     * Create the reference maps
     */
    private void createReferenceMaps() {
        instanceMap = new boolean[instanceFieldCount];
        staticMap = new boolean[staticFieldCount];
        if (superType != null) {
            boolean[] superMap = superType.getInstanceMap();
            for (int i = 0, n = superMap.length; i < n; i++) {
                instanceMap[i] = superMap[i];
            }
        }
        for (int i = 0, n = fields.length; i < n; i++) {
            Field field = fields[i];
            int index = field.getIndex();
            boolean isRef = field.isReference();
            if (field.isStatic()) {
                staticMap[index] = isRef;
            } else {
                instanceMap[index] = isRef;
            }
        }
    }
    
    /**
     * Set the super type
     */
    private void setSuperType(Type t) {
        if (superName != null) {
            if (t != null && t.linked && t.name == superName) {
                superType = t;
            } else {
                throw new LinkageError(superName);
            }
        }
    }
    
    /**
     * Set the interfaces
     */
    private void setInterfaces(Type[] types) {
        int length = interfaceNames.length;
        interfaces = new Type[length];
        for (int i = 0; i < length; i++) {
            Type t = types[i];
            if (t != null && t.linked && t.name == interfaceNames[i]) {
                interfaces[i] = t;
            } else {
                throw new LinkageError(interfaceNames[i]);
            }
        }
    }
    
    /**
     * Set the component type
     */
    private void setComponentType(Type t) {
        if (componentName != null) {
            if (t != null && t.linked && t.name == componentName) {
                componentType = t;
            } else {
                throw new LinkageError(name);
            }
        }
    }
        
    /**
     * Set the array type
     * @param arrayType the array type
     */
    public void setArrayType(Type arrayType) {
        if (this.arrayType != null) { return; }
        if (!arrayType.linked || (arrayType.componentType.name != name)) {
            throw new LinkageError();
        }
        this.arrayType = arrayType;
    }
    
    /**
    * @return the name
    */
    public String getName() {
        return name;
    }
    
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }
    
    /**
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }
    
    /**
     * @return a copy of the constant pool
     */
    public Entry[] getPool() {
        return pool;
    }
    
    /**
     * @return the name of the super class
     */
    public String getSuperName() {
        return superName;
    }
    
    /**
     * @return the super type
     */
    public Type getSuperType() {
        if (!linked) { throw new IllegalStateException("Not linked"); }
        return superType;
    }
    
    /**
     * @return the interface name array
     */
    public String[] getInterfaceNames() {
        return interfaceNames;
    }
    
    /**
     * @return the method array
     */
    public Method[] getMethods() {
        return methods;
    }
    
    /**
     * @return the field array
     */
    public Field[] getFields() {
        return fields;
    }
    
    /**
     * @return the instance field count
     */
    public int getInstanceFieldCount() {
        if (!linked) { throw new IllegalStateException("Not linked"); }
        return instanceFieldCount;
    }
    
    /**
     * @return the static field count
     */
    public int getStaticFieldCount() {
        if (!linked) { throw new IllegalStateException("Not linked"); }
        return staticFieldCount;
    }
    
    /**
     * @return the source string
     */
    public String getSource() {
        return source;
    }
    
    /**
     * @return true if this type has already been linked
     */
    public boolean isLinked() {
        return linked;
    }
    
    /**
     * @return true if this type represents a primitive type
     */
    public boolean isPrimitive() {
        return primitive;
    }
    
    /**
     * Return the method matching the given name and descriptor.
     * Search super types recursively.
     * @param name the method name
     * @param descriptor the method descriptor
     * @return the matching method
     */
    public Method getMethod(String name, String descriptor) {
        if (!linked) { throw new IllegalStateException("Not linked"); }
        Method result = (Method) getMember(methods, name, descriptor);
        if (result == null) {
            result = getInterfaceMethod(name, descriptor);
        }
        if (result == null && superType != null) {
            result = superType.getMethod(name, descriptor);
        }
        return result;
    }
    
    /**
     * Return the interface method matching the given name and descriptor.
     */
    private Method getInterfaceMethod(String name, String descriptor) {
        for (int i = 0, n = interfaces.length; i < n; i++) {
            Method method = interfaces[i].getMethod(name, descriptor);
            if (method != null) { return method; }
        }
        return null;
    }
    
    /**
     * Find the field matching the given name and descriptor.
     * Search super types recursively.
     * @param name the field name
     * @param descriptor the field descriptor
     * @return the matching field
     */
    public Field getField(String name, String descriptor) {
        if (!linked) { throw new IllegalStateException("Not linked"); }
        Field result = (Field) getMember(fields, name, descriptor);
        if (result == null && superType != null) {
            result = superType.getField(name, descriptor);
        }
        return result;
    }
    
    /**
     * Return the member matching the given name and descriptor
     */
    private Member getMember(Member[] members, String name, String descriptor) {
        for (int i = 0, n = members.length; i < n; i++) {
            Member m = members[i];
            if (m.getName().equals(name)
                    && m.getDescriptor().equals(descriptor))
            {
                return m;
            }
        }
        return null;
    }
    
    /**
     * @return a copy of the interfaces array
     */
    public Type[] getInterfaces() {
        if (!linked) { throw new IllegalStateException("Not linked"); }
        return interfaces;
    }
    
    /**
     * @return true if this type represents an array
     */
    public boolean isArray() {
        return name.startsWith("[");
    }
    
    /**
     * @return the component type name
     */
    public String getComponentName() {
        return componentName;
    }
    
    /**
     * @return the component type
     */
    public Type getComponentType() {
        if (!linked) { throw new IllegalStateException("Not linked"); }
        return componentType;
    }
    
    /**
     * @return the width (used by array types)
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * @return a string description
     */
    public String toString() {
        return "type: " + name;
    }
    
    /**
     * @return the array type (if set)
     */
    public Type getArrayType() {
        return arrayType;
    }
    
    /**
     * @return the instance field map
     */
    public boolean[] getInstanceMap() {
        if (!linked) { throw new IllegalStateException("Not linked"); }
        return instanceMap;
    }
    
    /**
     * @return the static field reference map
     */
    public boolean[] getStaticMap() {
        if (!linked) { throw new IllegalStateException("Not linked"); }
        return staticMap;
    }

}

