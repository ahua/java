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
package org.pjos.emulator.engine.implementation;

/**
 * Uses the data in a file to represent a virtual floppy disk drive
 */
class Floppy {

    /** The number of bytes in a floppy */
    private static final int LENGTH = 1440 * 1024;

    /** The data */
    private static byte[] data;
    
    /**
     * Reset the floppy data
     * @param b the new data
     */
    static synchronized void reset(byte[] b) {
        if (b.length != LENGTH) {
            throw new IllegalArgumentException();
        }
        data = b;
    }
    
    /**
     * Write an unsigned byte value to the disk
     * @param value the value
     * @param pos the position on the disk
     */
    static synchronized void write(int value, int pos) {
        if (pos < 0 || pos >= LENGTH) {
            throw new IllegalArgumentException();
        }
        data[pos] = (byte) (value & 0xff);
    }
    
    /**
     * Read an unsigned byte value from the disk 
     * @param pos the position on the disk
     * @return the value at the given position
     */
    static synchronized int read(int pos) {
        if (pos < 0 || pos >= LENGTH) {
            throw new IllegalArgumentException();
        }
        return data[pos] & 0xff;
    }

}









