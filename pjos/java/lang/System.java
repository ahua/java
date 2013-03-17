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
import java.io.PrintStream;

import java.util.Properties;

/**
 * Implementation of java.lang.System based on Sun specification.
 */
public final class System {
    
    /** The standard input stream */
    public static final InputStream in = null;
    
    /** The standard output stream */
    public static final PrintStream out = null;
    
    /** The standard error stream */
    public static final PrintStream err = null;
    
    /** The system properties */
    private static Properties properties;
    
    /**
     * Reset the standard input stream
     * @param in the new input stream
     */
    public static native void setIn(InputStream in);
    
    /**
     * Reset the standard output stream
     * @param out the new output stream
     */
    public static native void setOut(PrintStream out);
    
    /**
     * Reset the standard error stream
     * @param out the new error stream
     */
    public static native void setErr(PrintStream out);
    
    /**
     * Initialise system properties
     */
    private static synchronized void createSystemProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("java.version", "undefined");
            properties.setProperty("java.vendor", "undefined");
            properties.setProperty("java.vendor.url", "undefined");
            properties.setProperty("java.home", "undefined");
            properties.setProperty(
                    "java.vm.specification.version", "undefined");
            properties.setProperty("java.vm.specification.name", "undefined");
            properties.setProperty("java.vm.version", "undefined");
            properties.setProperty("java.vm.vendor", "undefined");
            properties.setProperty("java.vm.name", "undefined");
            properties.setProperty("java.specification.version", "undefined");
            properties.setProperty("java.specification.vendor", "undefined");
            properties.setProperty("java.specification.name", "undefined");
            properties.setProperty("java.class.version", "undefined");
            properties.setProperty("java.class.path", "undefined");
            properties.setProperty("java.library.path", "floppy:/temp");
            properties.setProperty("java.io.tmpdir", "undefined");
            properties.setProperty("java.compiler", "none");
            properties.setProperty("java.ext.dirs", "undefined");
            properties.setProperty("os.name", "undefined");
            properties.setProperty("os.arch", "undefined");
            properties.setProperty("os.version", "undefined");
            properties.setProperty("file.separator", "/");
            properties.setProperty("path.separator", ";");
            properties.setProperty("line.separator", "\n");
            properties.setProperty("user.name", "undefined");
            properties.setProperty("user.home", "undefined");
            properties.setProperty("user.dir", "/floppy");
        }
    }
    
    /**
     * Set the system security manager
     * @param sm the security manager to use
     */
    public static void setSecurityManager(SecurityManager sm) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the system security manager
     */
    public static SecurityManager getSecurityManager() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the current time in milliseconds since 01-JAN-1970
     */
    public static native long currentTimeMillis();
    
    /**
     * Copy an array from the specified source to the specified destination
     * @param src the source array
     * @param srcPos location in source
     * @param dest the destination array
     * @param destPos location in dest
     * @param length number of elements to copy
     */
    public static native void arraycopy(
            Object src,
            int srcPos,
            Object dest,
            int destPos,
            int length);
    
    /**
     * @param o the object
     * @return the identity hash code of the given object
     */
    public static native int identityHashCode(Object o);
    
    /**
     * @return the current system properties
     */
    public static Properties getProperties() {
        if (properties == null) { createSystemProperties(); }
        return properties;
    }
    
    /**
     * Set the current system properties
     * @param props the properties
     */
    public static void setProperties(Properties props) {
        properties = props;
    }
    
    /**
     * Get the system property for the specified key
     * @param key the key
     * @return the property, or null if not set
     */
    public static String getProperty(String key) {
        if (properties == null) { createSystemProperties(); }
        return properties.getProperty(key);
    }
    
    /**
     * Get the system property for the specified key
     * @param key the key
     * @param def the default value
     * @return the property, or the default value if not set
     */
    public static String getProperty(String key, String def) {
        if (properties == null) { createSystemProperties(); }
        return properties.getProperty(key, def);
    }
    
    /**
     * Set the system property for the specified key
     * @param key the key
     * @param value the value
     * @return the property, or default value if not set
     */
    public static String setProperty(String key, String value) {
        if (properties == null) {
            createSystemProperties();
        }
        Object result = properties.setProperty(key, value);
        return (result != null) ? result.toString() : null;
    }

    /**
     * Return an environment variable (deprecated)
     * @param name the property name
     * @return the value
     */
    public static String getenv(String name) {
        return getProperty(name);
    }

    /**
     * Terminate the virtual machine
     * @param status the return status
     */
    public static void exit(int status) {
        Runtime.getRuntime().exit(status);
    }
    /**
     * Run the garbage collector
     */
    public static void gc() {
        Runtime.getRuntime().gc();
    }
    
    /**
     * Run the finalization methods of any objects pending finalization
     */
    public static void runFinalization() {
        Runtime.getRuntime().runFinalization();
    }
    
    /**
     * Set the run finalizers on exit flag. (deprecated)
     * @param value the flag
     */
    public static void runFinalizersOnExit(boolean value) {
         Runtime.runFinalizersOnExit(value);
    }
    
    /**
     * Load a file as a dynamic library
     * @param filename the file name
     */
    public static void load(String filename) {
         Runtime.getRuntime().load(filename);
    }
    
    /**
     * Load the system library of the specified name
     * @param libname the library name
     */
    public static void loadLibrary(String libname) {
        Runtime.getRuntime().loadLibrary(libname);
    }
    
    /**
     * Map a library name into a string representing a native library
     * @param libname the library name
     * @return the name
     */
    public static native String mapLibraryName(String libname);

}
