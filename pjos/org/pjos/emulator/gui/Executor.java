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
package org.pjos.emulator.gui;

import org.pjos.emulator.engine.Engine;

import java.awt.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Manages the running state of the emulator and provides the
 * thread within which the emulator instructions are executed.
 */
class Executor implements Runnable {
    
    /** The engine */
    private Engine engine;
    
    /** This component is used to show error messages */
    private Component component;
    
    /** The running state */
    private boolean running = false;
    
    /** The limited state. This flag is set if instructions are limited */
    private boolean limited = false;
    
    /** The number of instructions executed if instructions are limited */
    private int count;
    
    /** The current limit. */
    private int limit;
    
    /**
     * Create an executor
     * @param engine the engine
     * @param component the component
     */
    Executor(Engine engine, Component component) {
        this.engine = engine;
        this.component = component;
        Thread thread = new Thread(this, "Executor");
        thread.start();
    }
    
    /**
     * Set the running state
     * @param running the new state
     */
    synchronized void setRunning(boolean running) {
        this.running = running;
        count = 0;
        notifyAll();
    }
    
    /**
     * @return the running state
     */
    boolean getRunning() {
        return running;
    }
    
    /**
     * Set the limited state
     * @param limited the new state
     * @param limit the max number of instructions to set
     */
    synchronized void limit(boolean limited, int limit) {
        if (limit <= 0) { throw new IllegalArgumentException(); }
        this.limited = limited;
        this.limit = limit;
        count = 0;
    }
    
    /**
     * @return true if the limited flag is set, false otherwise
     */
    boolean isLimited() {
        return limited;
    }
    
    /**
     * Notify components at regular intervals
     */
    public void run() {
        while (true) {
            waitUntilRunning();
            executeInstruction();
        }
    }
    
    /**
     * Wait until the running state is set to true
     */
    private synchronized void waitUntilRunning() {
        while (!running) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Execute an instruction
     */
    private synchronized void executeInstruction() {
        Exception e = engine.step();
        if (limited && ++count >= limit) { setRunning(false); }
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Util.displayError(sw.toString(), component);
            setRunning(false);
        }
    }
    
}









