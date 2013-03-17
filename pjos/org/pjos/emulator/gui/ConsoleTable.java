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

import java.awt.Color;
import java.awt.Component;

import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JTable;

import javax.swing.table.TableCellRenderer;

/**
 * A table used to display console text
 */
class ConsoleTable extends JTable {
    
    /** The engine */
    private Engine engine;

    /** The label used to render the cells */
    private JLabel label = new JLabel();
    
    /** 
     * Create a console table
     * @param engine the engine
     */
    ConsoleTable(Engine engine) {
        super(new ConsoleTableModel(engine));
        this.engine = engine;
        setShowGrid(false);
        setTableHeader(null);
        setBackground(Color.black);
        setForeground(Color.white);
        setSelectionBackground(Color.black);
        setSelectionForeground(Color.white);
        setFont(Debugger.MONOSPACED);
        label.setFont(Debugger.MONOSPACED);
        label.setBackground(Color.black);
        label.setForeground(Color.white);
    }
    
    /**
     * Pass keyboard events on to the engine
     * @param e the key event
     */
    protected void processKeyEvent(KeyEvent e) {
        int id = e.getID();
        int code = e.getKeyCode();
        if (id == KeyEvent.KEY_PRESSED) {
            engine.pressKey(code);
        } else if (id == KeyEvent.KEY_RELEASED) {
            engine.releaseKey(code);
        }
        super.processKeyEvent(e);
    }

    /**
     * Prepare the cell renderer. This is overridden to
     * avoid the selection rectangle around the cell.
     * @param tcr the renderer
     * @param row the row index
     * @param col the column index
     */
    public Component prepareRenderer(TableCellRenderer tcr, int row, int col) {
        Object value = getModel().getValueAt(row, col);
        label.setText(String.valueOf(value));
        return label;
    }
    
}












