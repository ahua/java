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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class calls the repaint method of registered
 * components if they need to be updated.
 */
class Notifier implements Runnable {
    
    /** The registered components */
    private Set registry = new HashSet();
    
    /** The engine */
    private Engine engine;
    
    /** The counter value at last update */
    private int counter = -1;
    
    /** The interval to wait between notifications */
    private static final long INTERVAL = 200;
    
    /**
     * Create a notifier
     * @param engine the engine
     */
    Notifier(Engine engine) {
        this.engine = engine;
        Thread thread = new Thread(this, "Notifier");
        thread.start();
    }
    
    /**
     * Register a component
     * @param c component
     */
    synchronized void register(Component c) {
        registry.add(c);
    }
    
    /**
     * Deregister a component
     * @param c the component
     */
    synchronized void deregister(Component c) {
        registry.remove(c);
    }
    
    /**
     * Notify all the components
     */
    synchronized void notifyComponents() {
        int latest = engine.getCounter();
        if (latest != counter) {
            for (Iterator it = registry.iterator(); it.hasNext();) {
                Component next = (Component) it.next();
                next.repaint();
            }
            counter = latest;
        }
    }
    
    /**
     * Notify components at regular intervals
     */
    public void run() {
        while (true) {
            notifyComponents();
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}









