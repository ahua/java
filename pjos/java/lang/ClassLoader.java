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
import java.io.IOException;

import java.net.URL;

import java.security.ProtectionDomain;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.pjos.common.runtime.BootClassLoader;
import org.pjos.common.runtime.Type;
import org.pjos.common.runtime.TypeFactory;

/**
 * Implementation of java.lang.Classloader based on Sun specification.
 */
public abstract class ClassLoader {
    
    /** The system class loader */
    private static ClassLoader system;
    
    /** The parent class loader */
    private ClassLoader parent;
    
    /** The map containing the classes that have been loaded */
    private Map classes;
    
    /**
     * Create a class loader with the given parent
     * @param parent the parent class loader
     */
    protected ClassLoader(ClassLoader parent) {
        this.parent = parent;
        classes = new HashMap();
    }
    
    /**
     * Create a class loader with the system class loader as its parent
     */
    protected ClassLoader() {
        this(null);
    }
    
    /**
     * @param name the primitive class name
     * @return the primitive class with the given name
     */
    static Class getPrimitiveClassFor(String name) {
        return BootClassLoader.get().getPrimitiveClass(name);
    }
    
    /**
     * Load the class with the specified name
     * @param name the name
     * @return the class
     * @throws ClassNotFoundException if the class could not be loaded
     */
    public Class loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }
    
    /**
     * Generate an array class
     */
    private Class generateArrayClass(String name) {
        Type type = TypeFactory.generate(name.replace('.', '/'));
        Class result = new Class(type, this);
        store(name, result);
        return result;
    }
    
    /**
     * Load the class with the specified name. Classes are resolved in
     * the class constructor so the resolve flag is ignored here.
     * @param name the name
     * @param resolve the resolve flag
     * @return the class
     * @throws ClassNotFoundException if the class could not be loaded
     */
    protected Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException
    {
        // check to see if the class has already been loaded
        Class result = findLoadedClass(name);
        if (result != null) { return result; }

        // try the parent loader
        try {
            if (parent != null) { result = parent.loadClass(name); }
        } catch (ClassNotFoundException e) {
            // just continue if not found
        }

        // try the system loader
        try {
            if (result == null && this != system) {
                result = getSystemClassLoader().loadClass(name);
            }
        } catch (ClassNotFoundException e) {
            // just continue if not found
        }

        // generate array classes
        if (result == null && name.startsWith("[")) {
            String componentName = TypeFactory.extractComponentName(name);
            Class component = loadClass(componentName);
            ClassLoader loader = component.getClassLoader();
            if (loader == null) { loader = getSystemClassLoader(); }
            result = loader.generateArrayClass(name);
        }

        // find the class
        if (result == null) { result = findClass(name); }
        
        // store the class for future reference
        if (result != null) {
            result = store(name, result);
            result.initialise();
            return result;
        }
        throw new ClassNotFoundException(name);
    }

    /**
     * Find the named class
     * @param name the name
     * @return the class
     * @throws ClassNotFoundException if the class is not found
     */
    protected Class findClass(String name) throws ClassNotFoundException {
        throw new ClassNotFoundException(name);
    }
    
    /**
     * Define a class (deprecated)
     * @param b the array containing the class definition
     * @param off the location of the class data
     * @param len the number of bytes in the class definition
     * @return the class
     */
    protected final Class defineClass(byte[] b, int off, int len) {
        Type type = TypeFactory.read(b, off, len);
        return new Class(type, this);
    }
    
    /**
     * Define a class using the class file data.
     * @param name the name
     * @param b the array containing the class definition
     * @param off the location of the class data
     * @param len the number of bytes in the class definition
     * @return the class
     */
    protected final Class defineClass(String name, byte[] b, int off, int len) {
        Class result = defineClass(b, off, len);
        if (!result.getName().equals(name)) {
            throw new ClassFormatError(name);
        }
        return result;
    }
    
    /**
     * Define a class with optional protection domain.
     * @param name the name
     * @param b the array containing the class definition
     * @param off the location of the class data
     * @param len the number of bytes in the class definition
     * @param pd the protection domain
     * @return the class
     */
    protected final Class defineClass(
            String name,
            byte[] b,
            int off,
            int len,
            ProtectionDomain pd)
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * Store the given class
     */
    private Class store(String name, Class c) {
        synchronized (classes) {
            Class result = (Class) classes.get(name);
            if (result == null) { result = c; }
            if (!name.equals(result.getName())) {
                throw new ClassFormatError(name);
            }
            classes.put(name, c);
            return result;
        }
    }
    
    /**
     * In this implementation classes are resolved in the class
     * constructor, so this method just does nothing.
     * @param c the class to resolve
     */
    protected final void resolveClass(Class c) {
        // do nothing
    }
    
    /**
     * @param name the name
     * @return the named system class
     * @throws ClassNotFoundException if the class is not found
     */
    protected final Class findSystemClass(String name)
            throws ClassNotFoundException
    {
        return getSystemClassLoader().loadClass(name);
    }
    
    /**
     * @return the parent class loader
     */
    public final ClassLoader getParent() {
        return parent;
    }
    
    /**
     * Set the signers of the given class
     * @param c the class
     * @param signers the signers
     */
    protected final void setSigners(Class c, Object[] signers) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param name the name
     * @return the named class, or null if it has not been loaded
     */
    protected final Class findLoadedClass(String name) {
        synchronized (classes) {
            return (Class) classes.get(name);
        }
    }
    
    /**
     * @param name the name
     * @return the named resource
     */
    public URL getResource(String name) {
        URL result = (parent != null)
                ? parent.getResource(name)
                : getSystemResource(name);
        if (result == null) { result = findResource(name); }
        return result;
    }
    
    /**
     * @param name the name
     * @return an enumeration of URLs representing the named resources
     * @throws IOException if an error occurs
     */
    public final Enumeration getResources(String name) throws IOException {
        Enumeration result = (parent != null)
                ? parent.getResources(name)
                : getSystemResources(name);
            if (result == null) { result = findResources(name); }
        return result;
    }
    
    /**
     * @param name the name
     * @return an enumeration of URLs representing the named resources
     * @throws IOException if an error occurs
     */
    protected Enumeration findResources(String name) throws IOException {
        Vector v = new Vector();
        return v.elements();
    }
    
    /**
     * @param name the name of the desired resource
     * @return a URL object for the named resource
     */
    protected URL findResource(String name) {
        return null;
    }
    
    /**
     * @param name the name of the desired resource
     * @return a URL object for the named resource
     */
    public static URL getSystemResource(String name) {
        return getSystemClassLoader().getResource(name);
    }
    
    /**
     * @param name the name
     * @return the system resources matching the given
     *         name as an enumeration of URLs.
     * @throws IOException if an error occurs
     */
    public static Enumeration getSystemResources(String name)
            throws IOException
    {
        return getSystemClassLoader().findResources(name);
    }
    
    /**
     * @param name the resource name
     * @return an input stream for reading the specified resource
     */
    public InputStream getResourceAsStream(String name) {
        try {
            URL url = getResource(name);
            return (url != null) ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * @param name the resource name
     * @return an input stream for reading the specified system resource
     */
    public InputStream getSystemResourceAsStream(String name) {
        try {
            URL url = getSystemResource(name);
            return (url != null) ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * @return the system class loader
     */
    public static ClassLoader getSystemClassLoader() {
        if (system == null) {
            system = BootClassLoader.get();
        }
        return system;
    }
    
    /**
     * Define a package
     * @param name the name
     * @param specTitle the spec title
     * @param specVersion the spec version
     * @param specVendor the spec vendor
     * @param implTitle the impl title
     * @param implVersion the impl version
     * @param implVendor the impl vendor
     * @param sealBase the seal base
     * @return the package object
     */
    protected Package definePackage(
            String name,
            String specTitle,
            String specVersion,
            String specVendor,
            String implTitle,
            String implVersion,
            String implVendor,
            URL sealBase)
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param name the name of the desired package
     * @return the package of the given name
     */
    protected Package getPackage(String name) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return all the packages defined by this class loader and its ancestors
     */
    protected Package[] getPackages() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param libname the library name
     * @return the absolute path of the native library
     */
    protected String findLibrary(String libname) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Set the default assertion status
     * @param enabled the status
     */
    public void setDefaultAssertionStatus(boolean enabled) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Set the default assertion status for the named package
     * @param packageName the name of the package
     * @param enabled the status
     */
    public void setDefaultAssertionStatus(String packageName, boolean enabled) {
throw new UnsupportedOperationException();
    }
    
    /** 
     * Set the assertion status for the named class
     * @param className the name of the class
     * @param enabled the status
     */
    public void setClassAssertionStatus(String className, boolean enabled) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Clear all assertion status settings
     */
    public void clearAssertionStatus() {
throw new UnsupportedOperationException();
    }
    
}
