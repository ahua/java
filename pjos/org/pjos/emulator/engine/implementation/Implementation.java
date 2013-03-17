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
package org.pjos.emulator.engine.implementation;

import org.pjos.emulator.engine.Engine;

import java.util.LinkedList;

/**
 * Provides an implementation of the abstract 32-bit computer
 * defined by the Emulator interface. Requests from external
 * objects to this package are synchronised here.
 */
public class Implementation implements Engine, Constants {
    
    /** The singleton implementation */
    private static final Implementation singleton = new Implementation();
    
    /** The number of console lines */
    private static final int LINES = 31;
    
    /** The console lines */
    private String[] lines = new String[LINES];
    
    /** Counts the number of instructions executed since the last reset */
    static int counter = 0;
    
    /** The queue of interrupt values */
    static LinkedList interrupts = new LinkedList();
    
    /** The queue of keyboard values */
    static LinkedList keys = new LinkedList();
    
    /**
     * Create an implementation
     */
    private Implementation() {
        clearLines();
        Thread thread = new Thread(new Timer(this));
        thread.start();
    }

    /**
     * @return the singleton implementation
     */
    public static Implementation get() {
        return singleton;
    }
    
    /**
     * @param address the memory address
     * @return the 32-bit value at the specified memory address
     */
    public int load(int address) {
        return Mem.load(address);
    }
    
    /**
     * @param address the memory address
     * @return the 16-bit value at the specified memory address
     */
    public int loadShort(int address) {
        return Mem.loadShort(address);
    }
    
    /**
     * @param address the memory address
     * @return the 8-bit value at the specified memory address
     */
    public int loadByte(int address) {
        return Mem.loadByte(address);
    }
    
    /**
     * @return the value in the core register
     */
    public int getCore() {
        return Reg.core;
    }
    
    /**
     * @return the value in the thread register
     */
    public int getThread() {
        return Reg.thread;
    }
    
    /**
     * @return the value in the frame register
     */
    public int getFrame() {
        return Reg.frame;
    }
    
    /**
     * @return the value in the method register
     */
    public int getMethod() {
        return Reg.method;
    }
    
    /**
     * @return the value in the code register
     */
    public int getCode() {
        return Reg.code;
    }
    
    /**
     * @return the value in the instruction register
     */
    public int getInstruction() {
        return Reg.instruction;
    }
    
    /**
     * @return the value in the stack register
     */
    public int getStack() {
        return Reg.stack;
    }
    
    /**
     * @return the value in the locals register
     */
    public int getLocals() {
        return Reg.locals;
    }
    
    /**
     * @return the value in the pool register
     */
    public int getPool() {
        return Reg.pool;
    }

    /**
     * @return the memory size in bytes
     */
    public int getMemorySize() {
        return Mem.SIZE;
    }
    
    /**
     * @return the number of instructions executed since the last reset
     */
    public int getCounter() {
        return counter;
    }
    
    /**
     * Execute one virtual machine instruction. If an exception is thrown,
     * during execution return it, otherwise return null.
     * @return the exception if thrown or null
     */
    public synchronized Exception step() {
        try {
            // execute the next instruction
            Distributor.execute();
            counter++;
            
            // if the current thread exits, yield to next
            if (Reg.thread == NULL) { tick(); }
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    /**
     * This method will be called by the system timer to allow
     * the currently running thread to be changed.
     */
    synchronized void tick() {
        if (Reg.thread != NULL) {
            // save execution state of running thread
            Reg.save();
        }
        Threads.scheduleNextThread();
        Sleep.wakeSleepingThreads();
    }
    
    /**
     * Reset the emulator. This method will write the contents of the
     * given array into memory and zero any extra space. The core
     * register is set to zero and the other registers are reset from
     * the data in the core object.
     * @param image the new memory image
     * @param floppy the new floppy image
     */
    public synchronized void reset(byte[] image, byte[] floppy) {
        Mem.reset(image);
        Reg.reset();
        counter = 0;
        Floppy.reset(floppy);
        clearLines();
    }
    
    /**
     * Set the debug mode
     * @param debug the new debug mode
     */
    public synchronized void setDebug(boolean debug) {
        Debug.debug = debug;
    }
    
    /**
     * @return the debug mode
     */
    public boolean getDebug() {
        return Debug.debug;
    }
    
    /**
     * @return the number of console rows
     */
    public int getRowCount() {
        return LINES;
    }
    
    /**
     * @param index the index
     * @return the console row at the specified index
     */
    public String getRow(int index) {
        if (index < 0 || index >= LINES) {
            throw new IllegalArgumentException();
        }
        return lines[index];
    }
    
    /**
     * Simulate keyboard press
     * @param key the key code
     */
    public synchronized void pressKey(int key) {
        keys.addLast(new Integer(KEY_PRESS));
        keys.addLast(new Integer(key));
        interrupts.addLast(new Integer(KEYBOARD_INTERRUPT));
        scheduleInterruptHandler();
    }
    
    /**
     * Simulate keyboard release
     * @param key the key code
     */
    public synchronized void releaseKey(int key) {
        keys.addLast(new Integer(KEY_RELEASE));
        keys.addLast(new Integer(key));
        interrupts.addLast(new Integer(KEYBOARD_INTERRUPT));
        scheduleInterruptHandler();
    }
    
    /**
     * Schedule the interrupt handler thread
     */
    private synchronized void scheduleInterruptHandler() {
        int notifier = Mem.load(Reg.core + 4 * CORE_NOTIFIER);
        int suspended = Mem.load(notifier + 4 * THREAD_SUSPENDED);
        if (suspended == TRUE) {
            Threads.resume(notifier);
        }
    }

    /**
     * Write a character to the console
     * @param c the character
     */
    void toConsole(char c) {
        // backspace
        int last = LINES - 1;
        int length = lines[last].length();
        if (c == '\u0008' && length > 0) {
            String s = lines[last];
            lines[last] = s.substring(0, length - 1);
        } else {
            // scroll
            if (c == '\n' || length >= 70) {
                for (int i = 0; i < last; i++) {
                    lines[i] = lines[i + 1];
                }
                lines[last] = "";
            }
        
            // append char to last line
            if (c != '\n' && c != '\u0008') {
                if (Character.isISOControl(c)) { c = '.'; }
                lines[last] += c;
            }
        }
    }

    /**
     * Clear the console lines
     */
    private void clearLines() {
        for (int i = 0; i < LINES; i++) {
            lines[i] = "";
        }
    }

}









