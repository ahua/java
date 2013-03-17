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
package org.pjos.x86.driver;

import java.io.InputStream;

import org.pjos.common.runtime.InterruptManager;

/**
 * A test keyboard handler that just prints information to the system out
 */
class KeyboardHandler implements Runnable {

    /** Will be used as the system input stream */
    private KeyboardInputStream kis;
    
    /**
     * Create a keyboard handler
     */
    KeyboardHandler() {
        kis = new KeyboardInputStream();
    }

    /**
     * @return the input stream to read typed characters
     */
    InputStream getInputStream() {
        return kis;
    }
    
    /**
     * Wait for keyboard events and print information
     */
    public void run() {
        // initialise keyboard state
        int special = 0;
        boolean escape = false;
        boolean lshift = false;
        boolean rshift = false;

        Object key = X86Architecture.getIrqKey(1);
        while (true) {
            InterruptManager.waitFor(key);
            
            // read characters until status indicates
            // there are none left to read
            int status = X86Architecture.in(0x64);
            while ((status & 0x01) != 0) {
                int code = X86Architecture.in(0x60);
                // apparently for some keyboards (AT?) need
                // to send ACK high and low here...
                // might depend on keyboard controller...

                if (special > 0) {
                    // ignore special characters for the moment (pause key)
                    special--;
                } else if (code == 0xe0) {
                    // check for escape
                    escape = true;
                } else if (code == 0xe1) {
                    // check for special escape
                    special = 2;
                } else {
                    // check for shift keys
                    if (code == 0xb6) {
                        rshift = false;
                    } else if (code == 0xaa) {
                        lshift = false;
                    } else if (code == 0x2a) {
                        lshift = true;
                    } else if (code == 0x36) {
                        rshift = true;
                    } else if ((code & 0x80) == 0) {
                        // key presses are from 0 to 127, ignore releases
                        if (!escape) {
                            int k = keys[code];

                            // do shift conversions
                            if (lshift || rshift) {
                                 // letters to upper case
                                if (k >= 0x61 && k <= 0x7a) {
                                    k -= 0x20;
                                } else if (k >= 0x30 && k <= 0x39) {
                                    k = digitsShifted[k - 0x30];
                                }
                            }
                            kis.write(k);
                        }
                    }
                    
                    status = X86Architecture.in(0x64);
                }
            }
        }
    }
    
    /**
     * The key mappings
     */
    private int[] keys = new int[] {
        // 0     1     2     3     4     5     6     7
                // 8    9     a     b     c     d     e     f
        0x00, 0x00, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36,
                0x37, 0x38, 0x39, 0x30, 0x00, 0x00, 0x08, 0x00, // 0
        0x71, 0x77, 0x65, 0x72, 0x74, 0x79, 0x75, 0x69,
                0x6f, 0x70, 0x00, 0x00, 0x0a, 0x00, 0x61, 0x73, // 1
        0x64, 0x66, 0x67, 0x68, 0x6a, 0x6b, 0x6c, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x7a, 0x78, 0x63, 0x76, // 2
        0x62, 0x6e, 0x6d, 0x2c, 0x2e, 0x00, 0x00, 0x00,
                0x00, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 3
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 4
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 5
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 6
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00  // 7
    };

    /**
     * The digits shifted
     */
    private int[] digitsShifted = new int[] {
        ')', '!', '@', '#', '$', '%', '^', '&', '*', '('
    };

}

