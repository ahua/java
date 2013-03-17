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
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A panel showing a portion of the emulator memory as hexadecimal values.
 */
class MemoryPanel extends JPanel {
    
    /** The address field */
    private HexField field = new HexField();
    
    /** The memory table */
    private MemoryTable table;
    
    /** The scroll pane for the table */
    private JScrollPane scroll;
    
    /** 
     * Create a memory panel
     * @param engine the engine
     */
    MemoryPanel(Engine engine) {
        table = new MemoryTable(engine);
        scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(150, 150));
        initLayout();
        initHandlers();
    }
    
    /**
     * Layout internal components
     */
    private void initLayout() {
        setLayout(new BorderLayout());
        add(field, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
    
    /**
     * Initialise internal event handlers
     */
    private void initHandlers() {
        field.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int address = field.getValue();
                scroll.getVerticalScrollBar().setValue(
                        (address & 0xFFFFFFFC) * 4);
            }
        });
    }
    
}












