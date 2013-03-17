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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A frame displaying the basic state of the engine.
 */
class Debugger extends JFrame {

    /** The monospaced font */
    static final Font MONOSPACED = new Font("Monospaced", Font.PLAIN, 12);
    
    /** The engine */
    private Engine engine;
    
    /** The notifier */
    private Notifier notifier;
    
    /** The file containing the memory data */
    private File memory;
    
    /** The file containing the floppy data */
    private File floppy;
    
    /** 
     * Create a debugger
     * @param engine the engine
     * @param memory the memory image
     * @param floppy the floppy image
     */
    Debugger(Engine engine, File memory, File floppy) {
        this.engine = engine;
        this.memory = memory;
        this.floppy = floppy;
        
        // start notifier
        notifier = new Notifier(engine);
        notifier.register(this);
        
        // init gui
        setTitle("Emulator");
        initLayout();
        initHandlers();
        pack();
        setResizable(false);
        setVisible(true);
    }
    
    /**
     * Initialise the layout of components within this frame
     */
    private void initLayout() {
        JPanel content = new JPanel(new BorderLayout(5, 5));
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(content);
        content.add(new RegisterPanel(engine), BorderLayout.WEST);
        content.add(new TracePanel(engine), BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.add(new ButtonPanel(engine, memory, floppy), BorderLayout.NORTH);
        south.add(new ConsoleTable(engine), BorderLayout.CENTER);
        JPanel mp = new JPanel(new GridLayout(1, 0, 5, 5));
        mp.add(new MemoryPanel(engine));
        mp.add(new MemoryPanel(engine));
        south.add(mp, BorderLayout.EAST);
        content.add(south, BorderLayout.SOUTH);
    }
    
    /**
     * Initialise event handlers
     */
    private void initHandlers() {
        // exit application when frame closed
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
        
}









