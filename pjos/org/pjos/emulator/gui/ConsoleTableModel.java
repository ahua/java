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

import javax.swing.table.AbstractTableModel;

/**
 * A table model making the console text contents available.
 */
class ConsoleTableModel extends AbstractTableModel {
    
    /** The engine */
    private Engine engine;
    
    /** 
     * Create a console table model
     * @param engine the engine
     */
    ConsoleTableModel(Engine engine) {
        this.engine = engine;
    }
    
    /**
     * @param row the row
     * @param column the column
     * @return the value at the specified index
     */
    public Object getValueAt(int row, int column) {
        return engine.getRow(row);
    }
    
    /**
     * @return the number of rows
     */
    public int getRowCount() {
        return engine.getRowCount();
    }
    
    /**
     * @return the number columns
     */
    public int getColumnCount() {
        return 1;
    }
    
}












