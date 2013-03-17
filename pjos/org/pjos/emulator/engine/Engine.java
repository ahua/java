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

/**
 * An emulator for an abstract 32-bit computer.
 */
public interface Engine {
    
    /**
     * @param address the address
     * @return the 32-bit value at the specified memory address
     */
    int load(int address);
    
    /**
     * @param address the address
     * @return the 16-bit value at the specified memory address
     */
    int loadShort(int address);
    
    /**
     * @param address the address
     * @return the 8-bit value at the specified memory address
     */
    int loadByte(int address);
    
    /**
     * @return the value in the core register
     */
    int getCore();
    
    /**
     * @return the value in the thread register
     */
    int getThread();
    
    /**
     * @return the value in the frame register
     */
    int getFrame();
    
    /**
     * @return the value in the method register
     */
    int getMethod();
    
    /**
     * @return the value in the code register
     */
    int getCode();
    
    /**
     * @return the value in the instruction register
     */
    int getInstruction();
    
    /**
     * @return the value in the stack register
     */
    int getStack();
    
    /**
     * @return the value in the locals register
     */
    int getLocals();
    
    /**
     * @return the value in the pool register
     */
    int getPool();
    
    /**
     * @return the memory size in bytes
     */
    int getMemorySize();
    
    /**
     * @return the number of instructions executed since the last reset
     */
    int getCounter();
    
    /**
     * Execute one virtual machine instruction
     * @return the an exception is thrown, otherwise null
     */
    Exception step();
    
    /**
     * Reset the emulator and load the given memory and floppy images
     * @param memory the memory image
     * @param floppy the floppy image
     */
    void reset(byte[] memory, byte[] floppy);
    
    /**
     * Set the debug mode
     * @param debug the new mode
     */
    void setDebug(boolean debug);
    
    /**
     * @return the debug mode
     */
    boolean getDebug();
    
    /**
     * @return the number of console rows
     */
    int getRowCount();
    
    /**
     * @param index the index
     * @return the console row at the specified index
     */
    String getRow(int index);

    /**
     * Simulate keyboard press
     * @param key the key code
     */
    void pressKey(int key);
    
    /**
     * Simulate keyboard release
     * @param key the key code
     */
    void releaseKey(int key);
    
}









