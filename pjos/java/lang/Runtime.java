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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementation of java.lang.Runtime based on Sun specification.
 */
public class Runtime {
    
    /** The singleton runtime object */
    private static Runtime singleton;
    
    /**
     * Create the singleton runtime object
     */
    private static synchronized void createRuntime() {
        if (singleton == null) { singleton = new Runtime(); }
    }
    
    /**
     * Create an instance - should only be called internally
     */
    private Runtime() {
        // nothing to do here
    }
    
    /**
     * @return the runtime object associated with the current java application
     */
    public static Runtime getRuntime() {
        if (singleton == null) { createRuntime(); }
        return singleton;
    }
    
    /**
     * Terminate the virtual machine
     * @param status the return status
     */
    public void exit(int status) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Add a shutdown hook
     * @param hook the hook
     */
    public void addShutdownHook(Thread hook) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Remove a shutdown hook
     * @param hook the hook
     * @return tru if successful
     */
    public boolean removeShutdownHook(Thread hook) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Halt the virtual machine
     * @param status the return status
     */
    public void halt(int status) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Set flag to run finalizers on exit (deprecated)
     * @param value the flag
     */
    public static void runFinalizersOnExit(boolean value) {
        // not implemented
    }
    
    /**
     * Execute the specified string command in a separate process
     * @param cmd the command
     * @return the process
     * @throws IOException if an error occurs
     */
    public Process exec(String cmd) throws IOException {
        return exec(cmd, null, null);
    }
    
    /**
     * Execute the specified string command in a separate process
     * @param cmd the command
     * @param envp the environment variables
     * @return the process
     * @throws IOException if an error occurs
     */
    public Process exec(String cmd, String[] envp) throws IOException {
        return exec(cmd, envp, null);
    }
    
    /**
     * Execute the specified string command in a separate process
     * @param cmd the command
     * @param envp the environment variables
     * @param dir the directory
     * @return the process
     * @throws IOException if an error occurs
     */
    public Process exec(String cmd, String[] envp, File dir)
            throws IOException
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * Execute the specified command and arguments in a separate process
     * @param cmdarray the command and args
     * @return the process object
     * @throws IOException if an error occurs
     */
    public Process exec(String[] cmdarray) throws IOException {
        return exec(cmdarray, null, null);
    }
    
    /**
     * Execute the specified command and arguments in a separate process
     * @param cmdarray the command and args
     * @param envp the environment variables
     * @return the process object
     * @throws IOException if an error occurs
     */
    public Process exec(String[] cmdarray, String[] envp) throws IOException {
        return exec(cmdarray, envp, null);
    }
    
    /**
     * Execute the specified command and arguments in a separate process
     * @param cmdarray the command and args
     * @param envp the environment variables
     * @param dir the directory
     * @return the process object
     * @throws IOException if an error occurs
     */
    public Process exec(String[] cmdarray, String[] envp, File dir)
            throws IOException
    {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the number of available processors - only one supported!
     */
    public int availableProcessors() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the amount of free memory in the vm
     */
    public long freeMemory() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the total amount of memory available to the vm
     */
    public long totalMemory() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @return the maximum amount of memory the vm will attempt to use
     */
    public long maxMemory() {
throw new UnsupportedOperationException();
    }
    
    /**
     * Run the garbage collector
     */
    public void gc() {
        // donesn't do anything
    }
    
    /**
     * Run the finalization methods of any objects pending finalization
     */
    public void runFinalization() {
        // currently finalizers are not run
    }
    
    /**
     * Enable/disable tracing of instructions
     * @param on the trace flag
     */
    public void traceInstructions(boolean on) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Enable/disable tracing of method calls
     * @param on the trace flag
     */
    public void traceMethodCalls(boolean on) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Load the specified file as a dynamic library.
     * @param filename the filename
     */
    public void load(String filename) {
throw new UnsupportedOperationException();
    }
    
    /**
     * Load the dynamic library with the specified name
     * @param libname the library name
     */
    public void loadLibrary(String libname) {
// just ignore requests to load library for the moment
//throw new UnsupportedOperationException();
    }
    
    /**
     * @param in the input stream
     * @return a localised input stream (deprecated)
     */
    public InputStream getLocalizedInputStream(InputStream in) {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param out the output stream
     * @return the localised output stream (deprecated)
     */
    public OutputStream getLocalizedOutputStream(OutputStream out) {
throw new UnsupportedOperationException();
    }
    
}
