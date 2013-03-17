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
 * A table model making the emulator memory available as hex values.
 */
class MemoryTableModel extends AbstractTableModel {
    
    /** The engine */
    private Engine engine;
    
    /** The number of rows */
    private int rowCount;
    
    /** 
     * Create a memory table model
     * @param engine the engine
     */
    MemoryTableModel(Engine engine) {
        this.engine = engine;
        // one row for each 32-bit memory word
        rowCount = engine.getMemorySize() / 4;
    }
    
    /**
     * Return the value at the specified index
     * @param row the row index
     * @param column the column index
     */
    public Object getValueAt(int row, int column) {
        int address = 4 * row;
        int value = engine.load(address);
        String hexAddress = Integer.toHexString(address);
        String hexValue = Integer.toHexString(value);
        return Util.prefix(hexAddress, '0', 8) + ": "
                + Util.prefix(hexValue, '0', 8);
    }
    
    /**
     * @return the number of rows
     */
    public int getRowCount() {
        return rowCount;
    }
    
    /**
     * @return the number columns
     */
    public int getColumnCount() {
        return 1;
    }
    
}












