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
package org.pjos.emulator.engine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class loader will load classes from the emulator.implementation
 * package from the specified directory. Other classes will be loaded
 * in the normal way.
 */
class ResetLoader extends ClassLoader {
    
    /** The directory */
    private String directory;
    
    /** The classes */
    private Map classes = new HashMap();
    
    /**
     * Create a reset loader
     * @param directory the directory
     */
    ResetLoader(String directory) {
        this.directory = directory;
    }
    
    /**
     * Load a class. This is overridden to skip the step where the
     * parent loader is asked for the class.
     * @param name the class name
     * @param resolve resolve flag (ignored)
     */
    protected synchronized Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException
    {
        return (name.startsWith("org.pjos.emulator.engine.implementation"))
                ? load(name)
                : super.loadClass(name, resolve);
    }
    
    /**
     * Load a class internally
     */
    private Class load(String name) throws ClassNotFoundException {
        // check if it has already been loaded
        Class result = (Class) classes.get(name);

        // load from file if necessary
        if (result == null) {
            try {
                File file = new File(directory, name.replace('.', '/')
                        + ".class");
                byte[] data = getData(file);
                result = defineClass(name, data, 0, data.length);
                classes.put(name, result);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ClassNotFoundException();
            }
        }

        // return the class
        return result;
    }
    
    /**
     * Load the data from the specified file and return as byte array
     * @param f the file
     * @throws IOException if an error occurs
     */
    static byte[] getData(File f) throws IOException {
        int size = (int) f.length();
        byte[] result = new byte[size];
        BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(f));
        int k = in.read(result);
        if (k != size) {
            throw new IOException("Unable to read file contents: "
                    + f.getName());
        }
        in.close();
        return result;
    }

}









