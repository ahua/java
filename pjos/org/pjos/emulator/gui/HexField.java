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

import java.awt.Toolkit;

import javax.swing.JTextField;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This field is used to display and edit a hexadecimal string value
 * of the format 0xHHHHHHHH where H represents a hexadecimal digit.
 */
class HexField extends JTextField {

    /**
     * Create a hex field
     */
    HexField() {
        super(new HexDocument(), "0", 10);
        setFont(Debugger.MONOSPACED);
    }
    
    /**
     * Set the field contents to the given value
     * @param value the value
     */
    void setValue(int value) {
        setText(convert(value));
    }
    
    /**
     * @return the value representing the current field contents
     */
    int getValue() {
        return convert(getText());
    }
    
    /**
     * Convert a text value to an integer value
     */
    private static int convert(String text) {
        return (!text.equals("")) ? Integer.decode("0x" + text).intValue() : 0;
    }
    
    /**
     * Convert an integer value to a text value
     * @param value the integer value
     * @return the hex representation as a string
     */
    private static String convert(int value) {
        return Integer.toHexString(value);
    }
    
    /**
     * Models a binary address field in the format 0xHHHHHHHH
     * where H represents a hexadecimal digit.
     */
    static class HexDocument extends PlainDocument {
        
        /**
         * Make sure the changes are valid
         * @param offs the offset
         * @param str the string
         * @param a the attribute set
         * @throws BadLocationException if the offset is invalid
         */
        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException
        {
            int length = getLength();
            String desired = getText(0, offs) + str
                    + getText(offs, length - offs);
            try {
                // if the desired string can be successfully converted to an
                // integer, allow the insert
                int k = convert(desired);
                if (k >= 0 && desired.length() <= 8) {
                    super.insertString(offs, str, a);
                    return;
                }
            } catch (NumberFormatException e) {
                // just ignore format exceptions
            }
            Toolkit.getDefaultToolkit().beep();
        }
    
    }
    
}
