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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * The boot class loader for the vm
 */
public final class BootClassLoader extends ClassLoader {

    /** The singleton instance */
    private static BootClassLoader singleton;

    /**
     * @return the singleton instance
     */
    public static synchronized BootClassLoader get() {
        if (singleton == null) {
            singleton = new BootClassLoader();
        }
        return singleton;
    }

    /** The classes */
    private Map classes;

    /**
     * Create a boot class loader
     */
    private BootClassLoader() {
        classes = new HashMap();
    }

    /**
     * Load the class with the specified name
     * @param name the name
     * @return the class loaded
     * @throws ClassNotFoundException if not found
     */
    protected synchronized Class findClass(String name)
            throws ClassNotFoundException
    {
        // get registered class...
        Class result = (Class) classes.get(name);
        if (result != null) { return result; }
        
        // ...or load from file
        try {
            System.out.println("loading class from file: " + name);
            // load bytes from file
            String filename = name.replace('.', '/') + ".class";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileInputStream in = new FileInputStream(filename);
            byte[] buf = new byte[64];
            int k = in.read(buf, 0, 64);
            while (k != -1) {
                out.write(buf, 0, k);
                k = in.read(buf, 0, 64);
            }
            byte[] data = out.toByteArray();

            // define class
            result = defineClass(name, data, 0, data.length);
            //System.out.println("Loaded class from file: " + filename);
            return result;
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    /**
     * Register the class for the given type - this method is called
     * by the initialiser thread.
      * @param type the type to be registered
     */
    synchronized void register(Type type) {
        Class c = type.getPeer();
        classes.put(c.getName(), c);
    }

    /**
     * @param name the name
     * @return the primitive class of the given type
     */
    public Class getPrimitiveClass(String name) {
        try {
            return loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new InternalError("Primitive class not found: " + name);
        }
    }

}
