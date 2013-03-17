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

import javax.swing.JTextField;

/**
 * A text field with monospaced font
 */
class MonospacedField extends JTextField {
    
    /**
     * Create a monospaced field with the specfied number of columns
     * @param columns the columns
     */
    MonospacedField(int columns) {
        super(columns);
        setFont(Debugger.MONOSPACED);
    }
    
}












