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

import org.pjos.emulator.engine.Engine;

import java.awt.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Some utility methods for this package
 */
class Util implements Constants {
    
    /**
     * Return a hex string representation of the given value in the
     * form 0xHHHHHHHH where H represents a hex digit.
     * @param value the value
     * @return the hex string representation
     */
    static String hex(int value) {
        String hexValue = Integer.toHexString(value);
        return "0x" + prefix(hexValue, '0', 8);
    }
    
    /**
     * Prefix the given string with the specified character to fill
     * it to the specified length
     * @param s the string
     * @param c the character
     * @param length the desired length
     */
    static String prefix(String s, char c, int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = length - s.length(); i < n; i++) {
            sb.append(c);
        }
        sb.append(s);
        return sb.toString();
    }
    
    /**
     * Load the data from the specified file and return as byte array.
     * @param f the file
     * @throws IOException if an error occurs
     */
    static byte[] getData(File f) throws IOException {
        int size = (int) f.length();
        byte[] result = new byte[size];
        BufferedInputStream in = new BufferedInputStream(
                new FileInputStream(f));
        int k = in.read(result);
        if (k != size) {
            throw new IOException("Unable to read file contents: "
                    + f.getName());
        }
        in.close();
        return result;
    }

    /**
     * Display an error dialog with the given message for the given component
     * @param message the error message
     * @param component the parent component
     */
    static void displayError(final String message, final Component component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(
                        component,
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * @param address the address of the string
     * @param engine the engine
     * @return the string stored at the given memory location.
     */
    static String readString(int address, Engine engine) {
        if (address == NULL) {
            return "null";
        } else {
            int array = engine.load(address + 4 * STRING_CHARS);
            int first = engine.load(address + 4 * STRING_FIRST);
            int last = engine.load(address + 4 * STRING_LAST);
            StringBuffer sb = new StringBuffer();
            for (int i = first; i < last; i++) {
                char c = (char) engine.loadShort(
                        array + 4 * ARRAY_DATA + 2 * i);
                sb.append(c);
            }
            return sb.toString();
        }
    }
    
    /**
     * Return the string stored at the given memory location, if there
     * is an error just return the string "[error]".
     * @param address the address of the string
     * @param engine the engine
     * @return the string value
     */
    static String safeReadString(int address, Engine engine) {
        try {
            return readString(address, engine);
        } catch (Exception e) {
            return "[error]";
        }
    }

}









