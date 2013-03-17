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
package org.pjos.common.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.StringTokenizer;

import org.pjos.common.runtime.Type;
import org.pjos.common.runtime.TypeFactory;

/**
 * Used to load type objects from classfiles stored in the file system.
 */
class TypeLoader {
    
    /** The directories to search when looking for class files */
    private String[] dirs;
    
    /**
     * Create a type loader
     * @param classpath the classpath
     */
    TypeLoader(String classpath) {
        // split classpath into directory entries
        StringTokenizer st = new StringTokenizer(classpath, ";");
        int length = st.countTokens();
        dirs = new String[length];
        for (int i = 0; i < length; i++) {
            dirs[i] = st.nextToken();
        }
    }

    /**
     * Load the type of the given name
     * @param name the name
     * @throws IOException if an error occurs
     */
    Type load(String name) throws IOException {
        Type result = TypeFactory.generate(name);
        if (result != null) {
            return result;
        } else {
            return loadFromFile(name);
        }
    }
    
    /**
     * Load the type of the given name
     * @param name the name
     * @throws IOException if an error occurs
     */
    Type loadFromFile(String name) throws IOException {
        for (int i = 0, n = dirs.length; i < n; i++) {
            File file = new File(dirs[i], name + ".class");
            if (file.exists()) {
                byte[] data = getData(file);
                return TypeFactory.read(data, 0, data.length);
            }
        }
        throw new IllegalStateException("No file found for type: " + name);
    }
    
    /**
     * Load the binary data from the specified file
     */
    private static byte[] getData(File file) throws IOException {
        int size = (int) file.length();
        byte[] result = new byte[size];
        BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(file));
        int k = in.read(result);
        if (k != size) { throw new IOException(file.getAbsolutePath()); }
        in.close();
        return result;
    }

}
