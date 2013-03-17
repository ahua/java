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

import java.lang.reflect.Method;

/**
 * This emulator implementation uses a class loader to dynamically
 * load the implementation from the package emulator.implementation.
 * This means code can be changed, recompiled and reloaded without
 * having to restart the debugger.
 */
public class ResetImplementation implements Engine {
    
    /** The directory */
    private String directory;
    
    /** The class loader */
    private ClassLoader loader;
    
    /** The underlying engine */
    private Engine engine;

    /**
     * Create a reset implementation which will load classes
     * from the specified directory.
     * @param directory the directory
     * @param memory the memory image
     * @param floppy the floppy image
     */
    public ResetImplementation(String directory, byte[] memory, byte[] floppy) {
        this.directory = directory;
        reset(memory, floppy);
    }
    
    /**
     * Reset the emulator and load the given memory image.
     * @param memory the memory image
     * @param floppy the floppy image
     */
    public synchronized void reset(byte[] memory, byte[] floppy) {
        System.out.println("Engine resetting");
        try {
            // dynamic equivalent of emulator = Implementation.get();
            loader = new ResetLoader(directory);
            Class implementation = loader.loadClass(
                    "org.pjos.emulator.engine.implementation.Implementation");
            Method method = implementation.getMethod("get", null);
            engine = (Engine) method.invoke(null, null);
            engine.reset(memory, floppy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @param address the address
     * @return the 32-bit value at the specified memory address
     */
    public int load(int address) {
        return engine.load(address);
    }
    
    /**
     * @param address the address
     * @return the 16-bit value at the specified memory address
     */
    public int loadShort(int address) {
        return engine.loadShort(address);
    }
    
    /**
     * @param address the address
     * @return the 8-bit value at the specified memory address
     */
    public int loadByte(int address) {
        return engine.loadByte(address);
    }
    
    /**
     * @return the value in the core register
     */
    public int getCore() {
        return engine.getCore();
    }
    
    /**
     * @return the value in the thread register
     */
    public int getThread() {
        return engine.getThread();
    }
    
    /**
     * @return the value in the frame register
     */
    public int getFrame() {
        return engine.getFrame();
    }
    
    /**
     * @return the value in the method register
     */
    public int getMethod() {
        return engine.getMethod();
    }
    
    /**
     * @return the value in the code register
     */
    public int getCode() {
        return engine.getCode();
    }
    
    /**
     * @return the value in the instruction register
     */
    public int getInstruction() {
        return engine.getInstruction();
    }
    
    /**
     * @return the value in the stack register
     */
    public int getStack() {
        return engine.getStack();
    }
    
    /**
     * @return the value in the locals register
     */
    public int getLocals() {
        return engine.getLocals();
    }
    
    /**
     * @return the value in the pool register
     */
    public int getPool() {
        return engine.getPool();
    }
    
    /**
     * @return the memory size in bytes
     */
    public int getMemorySize() {
        return engine.getMemorySize();
    }
    
    /**
     * @return the number of instructions executed since the last reset
     */
    public int getCounter() {
        return engine.getCounter();
    }
    
    /**
     * Execute one virtual machine instruction. If an exception is thrown,
     * during execution return it, otherwise return null.
     * @return an exception if thrown, null otherwise
     */
    public Exception step() {
        return engine.step();
    }
    
    /**
     * Set the debug mode
     * @param debug the new debug mode
     */
    public void setDebug(boolean debug) {
        engine.setDebug(debug);
    }
    
    /**
     * @return the debug mode
     */
    public boolean getDebug() {
        return engine.getDebug();
    }
    
    /**
     * @return the number of console rows
     */
    public int getRowCount() {
        return engine.getRowCount();
    }
    
    /**
     * @return the console row at the specified index
     * @param index the index
     */
    public String getRow(int index) {
        return engine.getRow(index);
    }

    /**
     * Simulate keyboard press
     * @param key the key code
     */
    public void pressKey(int key) {
        engine.pressKey(key);
    }
    
    /**
     * Simulate keyboard release
     * @param key the key code
     */
    public void releaseKey(int key) {
        engine.releaseKey(key);
    }
}









