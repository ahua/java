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

import java.awt.Graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A panel with buttons used to control the state of the engine
 */
class ButtonPanel extends JPanel {
    
    /** The engine */
    private Engine engine;
    
    /** The file containing the memory data */
    private File memory;
    
    /** The file containing the floppy data */
    private File floppy;
    
    /** The executor */
    private Executor executor;
    
    /** The reset button */
    private JButton resetButton = new JButton("Reset");
    
    /** The go button */
    private JToggleButton goButton = new JToggleButton(" Go ");
    
    /** The debug button */
    private JToggleButton debugButton = new JToggleButton("Debug");
    
    /** The limit button */
    private JToggleButton limitButton = new JToggleButton("Limit");
    
    /** The spinner model */
    private SpinnerNumberModel spinnerModel
            = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
    
    /** The spinner */
    private JSpinner spinner = new JSpinner(spinnerModel);
    
    /** The spinner label */
    private JLabel spinnerLabel = new JLabel("Set limit: ");
    
    /** 
     * Create a button panel
     * @param engine the engine
     * @param memory the memory image
     * @param floppy the floppy image
     */
    ButtonPanel(Engine engine, File memory, File floppy) {
        this.engine = engine;
        this.memory = memory;
        this.floppy = floppy;
        executor = new Executor(engine, this);
        initLayout();
        initHandlers();
    }
    
    /**
     * Layout internal components
     */
    private void initLayout() {
        add(resetButton);
        add(goButton);
        add(debugButton);
        add(limitButton);
        add(spinnerLabel);
        add(spinner);
    }
    
    /**
     * Initialise internal event handlers
     */
    private void initHandlers() {
        // allow debug button to switch debug mode off/on
        debugButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                engine.setDebug(debugButton.isSelected());
            }
        });
        
        // when the go button is pressed update the executor
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executor.setRunning(goButton.isSelected());
            }
        });
        
        // when the limit button is pressed update the executor
        limitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executor.limit(limitButton.isSelected(),
                        spinnerModel.getNumber().intValue());
                setStates();
            }
        });
        
        // when the reset button is pressed, reset the engine
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { reset(); }
        });

        // when the value in the spinner has changed, tell the executor
        spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                executor.limit(limitButton.isSelected(),
                        spinnerModel.getNumber().intValue());
            }
        });
    }
    
    /**
     * Set the button states based on the executor's state
     */
    private void setStates() {
        debugButton.setSelected(engine.getDebug());
        goButton.setSelected(executor.getRunning());
        boolean limited = executor.isLimited();
        spinner.setEnabled(limited);
        spinnerLabel.setEnabled(limited);
    }
    
    /**
     * Overridden to update button states before displaying
     * @param g the graphics
     */
    public void paint(Graphics g) {
        setStates();
        super.paint(g);
    }
    
    /**
     * Reset the engine
     */
    private void reset() {
        try {
            byte[] memoryData = Util.getData(memory);
            byte[] floppyData = Util.getData(floppy);
            engine.reset(memoryData, floppyData);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Util.displayError(sw.toString(), this);
        }
    }
    
}












