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

import org.pjos.common.runtime.Constants;
import org.pjos.common.runtime.Opcodes;

import org.pjos.emulator.engine.Engine;

import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A panel displaying the values stored in the emulator's registers
 */
class RegisterPanel extends JPanel implements Constants {
    
    /** The engine */
    private Engine engine;
    
    /** The core field */
    private JTextField coreField = new MonospacedField(12);
    
    /** The thread field */
    private JTextField threadField = new MonospacedField(12);
    
    /** The thread name field */
    private JTextField threadNameField = new MonospacedField(12);
    
    /** The frame field */
    private JTextField frameField = new MonospacedField(12);
    
    /** The method field */
    private JTextField methodField = new MonospacedField(12);
    
    /** The code field */
    private JTextField codeField = new MonospacedField(12);
    
    /** The instruction field */
    private JTextField instructionField = new MonospacedField(12);
    
    /** The stack field */
    private JTextField stackField = new MonospacedField(12);
    
    /** The pc field */
    private JTextField pcField = new MonospacedField(12);
    
    /** The pool field */
    private JTextField poolField = new MonospacedField(12);
    
    /** The counter field */
    private JTextField counterField = new MonospacedField(12);
    
    /** The next instruction field */
    private JTextField nextField = new MonospacedField(21);
    
    /** The top of stack field */
    private JTextField topField = new MonospacedField(12);
    
    /** 
     * Create a register panel
     * @param engine the engine
     */
    RegisterPanel(Engine engine) {
        this.engine = engine;
        setValues();
        initLayout();
    }
    
    /**
     * Set the values of the internal components
     */
    private void setValues() {
        try {
            coreField.setText(Util.hex(engine.getCore()));
            int thread = engine.getThread();
            threadField.setText(Util.hex(thread));
            int threadName = engine.load(thread + 4 * THREAD_NAME);
            threadNameField.setText(Util.safeReadString(threadName, engine));
            frameField.setText(Util.hex(engine.getFrame()));
            methodField.setText(Util.hex(engine.getMethod()));
            codeField.setText(Util.hex(engine.getCode()));
            instructionField.setText(Util.hex(engine.getInstruction()));
            stackField.setText(Util.hex(engine.getStack()));
            poolField.setText(Util.hex(engine.getPool()));
            pcField.setText(String.valueOf(
                    engine.getInstruction() - engine.getCode()));
            counterField.setText(String.valueOf(engine.getCounter()));
            int byteCode = engine.loadByte(engine.getInstruction());
            nextField.setText(Opcodes.getName(byteCode)
                    + " [0x" + Integer.toHexString(byteCode) + "]");
            int value = engine.load(engine.getStack());
            topField.setText(Util.hex(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Layout internal components
     */
    private void initLayout() {
        setLayout(new GridLayout(0, 2, 5, 5));
        add(new JLabel("Core: "));
        add(coreField);
        add(new JLabel("Thread: "));
        add(threadField);
        add(new JLabel("Thread Name: "));
        add(threadNameField);
        add(new JLabel("Frame: "));
        add(frameField);
        add(new JLabel("Method: "));
        add(methodField);
        add(new JLabel("Code: "));
        add(codeField);
        add(new JLabel("Instruction: "));
        add(instructionField);
        add(new JLabel("Stack: "));
        add(stackField);
        add(new JLabel("Pool: "));
        add(poolField);
        add(new JLabel("pc: "));
        add(pcField);
        add(new JLabel("counter: "));
        add(counterField);
        add(new JLabel("next: "));
        add(nextField);
        add(new JLabel("top of stack: "));
        add(topField);
    }
    
    /**
     * Paint this panel. This is overridden to first
     * set the field states correctly before displaying them.
     * @param g the graphics
     */
    public void paint(Graphics g) {
        setValues();
        super.paint(g);
    }
    
}









