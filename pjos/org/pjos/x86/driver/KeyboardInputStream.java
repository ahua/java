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

/**
 * This stream is used to read typed characters from the keyboard.
 */
class KeyboardInputStream extends InputStream {
    
    /** The buffer size */
    private static final int MAX = 20;

    /** The buffer holding characters */
    private char[] buffer;

    /** The index of the next character to be read */
    private int from;

    /** The index of the next character to be written */
    private int to;

    /** The number of characters in the buffer */
    private int count;
    
    /**
     * Create a keyboard input stream
     */
    KeyboardInputStream() {
        buffer = new char[MAX];
        from = 0;
        to = 0;
        count = 0;
    }

    /**
     * Write a character to the buffer
     * @param c the character
     */
    void write(int c) {
        synchronized (buffer) {
            while (count >= MAX) {
                try {
                    buffer.wait();
                } catch (InterruptedException e) {
                    // just ignore interruptions
                }
            }
            buffer[to++] = (char) c;
            if (to >= MAX) { to = 0; }
            count++;
            buffer.notifyAll();
        }
    }

    /**
     * Read a character from the buffer
     * @return the character read
     */
    public int read() {
        synchronized (buffer) {
            while (count <= 0) {
                try {
                    buffer.wait();
                } catch (InterruptedException e) {
                    // just ignore interruptions
                }
            }
            int result = buffer[from++];
            if (from >= MAX) { from = 0; }
            count--;
            buffer.notifyAll();
            return result;
        }
    }

    /**
     * @return the number of characters available to be read
     */
    public int available() {
        return count;
    }
    
}

