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
package org.pjos.emulator.driver;

import java.io.InputStream;

import org.pjos.common.runtime.InterruptManager;

/**
 * A simple keyboard handler that allows typed keys to be
 * read from the system input stream.
 */
class KeyboardHandler implements Runnable, Constants {
    
    /** Characters processed by this handler can be read from this stream */
    private KeyboardInputStream kis;

    /** Reflects the current state of the shift key */
    private boolean shift;

    /** The special characters */
    private char[] special;

    /**
     * Create a keyboard handler
     */
    KeyboardHandler() {
        kis = new KeyboardInputStream();
        special = new char[] {
            ')', '!', '@', '#', '$', '%', '^', '&', '*', '('
        };
    }

    /**
     * @return the input stream from which typed characters can be read
     */
    InputStream getInputStream() {
        return kis;
    }
    
    /**
     * Wait for keyboard events and print information
     */
    public void run() {
        while (true) {
            InterruptManager.waitFor(KEYBOARD_INTERRUPT_KEY);
            int value = Emulator.readFromKeyboard();
            while (value != -1) {
                int next = Emulator.readFromKeyboard();
                switch (value) {
                    // pass the key on to the system input stream
                    case KEY_PRESS:
                        if (next == 16) {
                            shift = true;
                        } else {
                            kis.write(getChar(next));
                        }
                        value = Emulator.readFromKeyboard();
                        break;

                    // ignore key releases (except for shift)
                    case KEY_RELEASE:
                        if (next == 16) { shift = false; }
                        value = Emulator.readFromKeyboard();
                        break;

                    default:
                        value = next;
                }
            }
        }
    }

    /**
     * Return an unsigned byte value to represent the character
     * typed using the given key code and the current shift value.
     */
    private int getChar(int code) {
        // handle letter
        char c = (char) code;
        if (c >= 'A' && c <= 'Z') {
            return (shift) ? c : Character.toLowerCase(c);
        }

        // handle digit
        if (c >= '0' && c <= '9') {
            return (shift) ? special[c - '0'] : c;
        }

        // handle colon/semicolon
        if (c == ';') { return (shift) ? ':' : ';'; }

        // otherwise just return the given code
        return code;
    }
    
}

