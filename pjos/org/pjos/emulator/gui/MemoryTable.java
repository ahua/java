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

import javax.swing.JTable;

/**
 * A table to display a part of the emulator memory as hex values.
 */
class MemoryTable extends JTable {
    
    /** 
     * Create a memory table
     * @param engine engine
     */
    MemoryTable(Engine engine) {
        super(new MemoryTableModel(engine));
        setFont(Debugger.MONOSPACED);
        setShowGrid(false);
        setTableHeader(null);
        getColumnModel().getColumn(0).setPreferredWidth(150);
        getColumnModel().getColumn(0).setMinWidth(150);
        getColumnModel().getColumn(0).setMaxWidth(150);
    }
    
}












