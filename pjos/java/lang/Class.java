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
package java.lang;

import java.io.InputStream;
import java.io.Serializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.net.URL;

import java.security.ProtectionDomain;

import org.pjos.common.runtime.Core;
import org.pjos.common.runtime.Entry;
import org.pjos.common.runtime.Frame;
import org.pjos.common.runtime.Statics;
import org.pjos.common.runtime.Type;

/**
 * Implementation of java.lang.Class based on Sun specification.
 */
public final class Class implements Serializable {
    
    /** The internal class representation */
    private Type type;
    
    /** The class name */
    private String name;
    
    /** The class loader */
    private ClassLoader loader;
    
    /** The super class (null for java/lang/Object and primitives) */
    private Class superclass;
    
    /** The interfaces */
    private Class[] interfaces;
    
    /** The component type (arrays only) */
    private Class componentClass;
    
    /** This flag is set once this class has been initialised */
    private boolean initialised;
    
    /**
     * Create a class object which uses the given type
     * object to retrieve its runtime information. This method
     * also resolves the class.
     */
    Class(Type type, ClassLoader loader) {
        try {
            this.type = type;
            this.name = type.getName().replace('/', '.').intern();
            this.loader = loader;

            // set the super class
            String superName = type.getSuperName();
            superclass = (superName != null)
                    ? loader.loadClass(superName.replace('/', '.'))
                    : null;
            Type superType = (superclass != null) ? superclass.type : null;

            // set the interface classes
            String[] interfaceNames = type.getInterfaceNames();
            int length = interfaceNames.length;
            interfaces = new Class[length];
            Type[] interfaceTypes = new Type[length];
            for (int i = 0; i < length; i++) {
                Class c = loader.loadClass(interfaceNames[i].replace('/', '.'));
                interfaces[i] = c;
                interfaceTypes[i] = c.type;
            }

            // set the component class
            String componentName = type.getComponentName();
            componentClass = (componentName != null)
                    ? loader.loadClass(componentName.replace('/', '.'))
                    : null;
            Type componentType = (componentClass != null)
                    ? componentClass.type
                    : null;
            
            // link type
            type.link(superType, interfaceTypes, componentType);
            type.setPeer(this);
            
            // resolve constant pool entries where possible
            resolve(loader);
            
            // create statics object
            Statics.create(type);
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            throw new ClassFormatError(e.getMessage());
        }
    }
    
    /**
     * Run the static initialiser
     */
    synchronized void initialise() {
        if (initialised) { return; }
        org.pjos.common.runtime.Method clinit;
        clinit = type.getMethod("<clinit>", "()V");
        if (clinit != null) {
            Core.executeStatic(clinit);
        }
        initialised = true;
    }
    
    /**
     * Resolve this class using the given class loader
     */
    private void resolve(ClassLoader loader) {
        Entry[] pool = type.getPool();
        for (int i = 0, n = pool.length; i < n; i++) {
            Entry entry = pool[i];
            String classname = (entry != null)
                    ? entry.getClassnameToResolve()
                    : null;
            Class c = (classname != null)
                    ? loader.findLoadedClass(classname)
                    : null;
            if (c != null) { pool[i] = entry.resolve(c.type); }
        }
    }
    
    /** 
     * @return a string describing this class
     */
    public String toString() {
        if (isPrimitive()) {
            return name;
        } else if (isInterface()) {
            return "interface " + name;
        } else {
            return "class " + name;
        }
    }

    /** 
     * @param name the class name
     * @return the class with the specified name, loading if necessary.
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class forName(String name) throws ClassNotFoundException {
        Frame frame = Frame.currentFrame().getReturnFrame();
        Class caller = frame.getMethod().getOwner().getPeer();
        return forName(name, true, caller.loader);
    }

    /**
     * Load a class using the given classloader
     * @param name the name of the class
     * @param initialize true if the class should be initialised
     * @param loader the class loader to use
     * @return the class
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class forName(
            String name,
            boolean initialize,
            ClassLoader loader)
            throws ClassNotFoundException
    {
        return (loader != null)
                ? loader.loadClass(name)
                : ClassLoader.getSystemClassLoader().loadClass(name);
    }

    /**
     * Create a new instance of this class
     * @return the new instance
     * @throws InstantiationException if unable to instantiate
     * @throws IllegalAccessException if not allowed to instantiate
     */
    public Object newInstance()
            throws InstantiationException,
            IllegalAccessException
    {
throw new UnsupportedOperationException();
    }

    /**
     * @param o the object to check
     * @return true if the given object is assignment compatible with this class
     */
    public boolean isInstance(Object o) {
        return (o != null && isAssignableFrom(o.getClass()));
    }

    /**
     * @param c the class to check
     * @return true if a reference of the same type as this class can be
     *         assigned an instance of the type of the given class.
     */
    public boolean isAssignableFrom(Class c) {
throw new UnsupportedOperationException();
    }

    /**
     * @return true if this class represents an interface type
     */
    public boolean isInterface() {
        return Modifier.isInterface(type.getFlags());
    }

    /**
     * @return true if this class represents an array type
     */
    public boolean isArray() {
        return type.isArray();
    }

    /**
     * @return true if this class represents a primitive type
     */
    public boolean isPrimitive() {
        return type.isPrimitive();
    }

    /**
     * @return the name of this class
     */
    public String getName() {
        return name;
    }

    /**
     * @return the class loader, or null for boot class loader
     */
    public ClassLoader getClassLoader() {
        return loader;
    }

    /**
     * @return the super class
     */
    public Class getSuperclass() {
        return superclass;
    }
    
    /**
     * @return the package
     */
    public Package getPackage() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the interfaces implemented by this class
     */
    public Class[] getInterfaces() {
        int length = interfaces.length;
        Class[] result = new Class[length];
        for (int i = 0; i < length; i++) {
            result[i] = interfaces[i];
        }
        return result;
    }
    
    /**
     * @return the component type if this is an array class, null otherwise
     */
    public Class getComponentType() { return componentClass; }
    
    /**
     * @return the modifiers
     */
    public int getModifiers() {
        return type.getFlags();
    }
    
    /**
     * @return the signers
     */
    public Object[] getSigners() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the declaring class
     */
    public Class getDeclaringClass() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return all member classes
     */
    public Class[] getClasses() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the fields
     */
    public Field[] getFields() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the methods
     */
    public Method[] getMethods() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the constructors
     */
    public Constructor[] getConstructors() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param name the name of the field
     * @return the field with the specified name
     * @throws NoSuchFieldException if the field is not found
     */
    public Field getField(String name) throws NoSuchFieldException {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param name the method name
     * @param parameterTypes an array containing the parameter classes
     * @return the method with the specified name and parameter types.
     * @throws NoSuchMethodException if the method is not found
     */
    public Method getMethod(String name, Class[] parameterTypes)
            throws NoSuchMethodException
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param parameterTypes an array containing the parameter classes
     * @return the constructor matching the given array of parameter types.
     * @throws NoSuchMethodException if the constructor is not found
     */
    public Constructor getConstructor(Class[] parameterTypes)
            throws NoSuchMethodException
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return declared member classes
     */
    public Class[] getDeclaredClasses() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return declared fields
     */
    public Field[] getDeclaredFields() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return declared methods
     */
    public Method[] getDeclaredMethods() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the declared constructors
     */
    public Constructor[] getDeclaredConstructors() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param name the name of the field
     * @param parameterTypes an array containing the parameter classes
     * @return the declared field with the specified name
     * @throws NoSuchFieldException if the field is not found
     */
    public Field getDeclaredField(String name, Class[] parameterTypes)
            throws NoSuchFieldException
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param name the name of the method
     * @param parameterTypes an array containing the parameter classes
     * @return the declared method with the specified name
     * @throws NoSuchMethodException if the method is not found
     */
    public Method getDeclaredMethod(String name, Class[] parameterTypes)
            throws NoSuchMethodException
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param parameterTypes an array containing the parameter classes
     * @return the declared constructor matching the given parameter types
     * @throws NoSuchMethodException if the constructor is not found
     */
    public Constructor getDeclaredConstructor(Class[] parameterTypes)
            throws NoSuchMethodException
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param name the name
     * @return the resource with the specified name
     */
    public InputStream getResourceAsStream(String name) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param name the name
     * @return the resource with the specified name
     */
    public URL getResource(String name) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the protection domain of this class
     */
    public ProtectionDomain getProtectionDomain() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the desired assertion status
     */
    public boolean desiredAssertionStatus() {
throw new UnsupportedOperationException();
    }

}

