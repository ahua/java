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
 * Big-endian memory for the virtual machine implementation.
 */
class Mem {
    
    /**
     * The location of the first java space. The pointer value 0x0 is
     * reserved for null pointers so the first java space cannot reside
     * at that address.
     */
    static final int OFFSET = 0x4;
    
    /**
     * The size of a java space
     */
    static final int LIMIT = 4 * 1024 * 1024; // 4MB per java space
    
    /** The memory size in bytes */
    static final int SIZE = OFFSET + (2 * LIMIT); // just over 8MB
    
    /** The memory */
    private static byte[] memory = new byte[SIZE];
    
    /**
     * Store an 8-bit value at the given address
     * @param value the value
     * @param address the address
     */
    public static void storeByte(int value, int address) {
        if (address < OFFSET || address >= SIZE) {
            throw new IllegalArgumentException(
                    "Invalid address: 0x" + Integer.toHexString(address));
        }
        memory[address] = (byte) (value & 0xff);
    }

    /**
     * Store a 16-bit value at the given address
     * @param value the value
     * @param address the address
     */
    public static void storeShort(int value, int address) {
        storeByte(value >>> 8, address);
        storeByte(value, address + 1);
    }

    /**
     * Store a 32-bit value at the given address
     * @param value the value
     * @param address the address
     */
    public static void store(int value, int address) {
        storeByte(value >>> 24, address);
        storeByte(value >>> 16, address + 1);
        storeByte(value >>> 8, address + 2);
        storeByte(value, address + 3);
    }

    /**
     * Retrieve an 8-bit value from the given address
     * @param address the address
     * @return the value
     */
    public static int loadByte(int address) {
        if (address < 0 || address >= SIZE) {
            throw new IllegalArgumentException(
                    "Invalid address: 0x" + Integer.toHexString(address));
        }
        return (int) (memory[address] & 0xff);
    }

    /**
     * Retrieve a 16-bit value from the given address
     * @param address the address
     * @return the value
     */
    public static int loadShort(int address) {
        return loadByte(address) << 8
            | loadByte(address + 1);
    }

    /**
     * Retrieve a 32-bit value from the given address
     * @param address the address
     * @return the value
     */
    public static int load(int address) {
        return loadByte(address) << 24
            | loadByte(address + 1) << 16
            | loadByte(address + 2) << 8
            | loadByte(address + 3);
    }

    /**
     * Reset the memory. The given image is loaded into the first java space.
     * The rest of memory is zeroed.
     * @param image the memory image
     */
    static void reset(byte[] image) {
        int to = 0;
        int from = 0;
        int max = image.length;
        while (to < OFFSET) {
            memory[to++] = 0; // zero to offset
        }
        while (from < max && to < SIZE) {
            memory[to++] = image[from++]; // load image
        }
        while (to < SIZE) {
            memory[to++] = 0; // zero to end of memory
        }
    }
     
}









