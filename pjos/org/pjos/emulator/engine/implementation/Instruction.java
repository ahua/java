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
 * Contains code used to manipulate byte code instructions.
 */
class Instruction implements Constants {
    
    /**
     * Return the code byte at the given offset from the current instruction
     * @param offset the offset
     * @return the unsigned byte value
     */
    static int byteCode(int offset) {
        return Mem.loadByte(Reg.instruction + offset);
    }
    
    /**
     * Read the byte code at the given index, sign extend
     * it to an integer value and return.
     * @param offset the offset
     * @return the signed byte value
     */
    static int signByteCode(int offset) {
        return signExtendByte(byteCode(offset));
    }
    
    /**
     * Read two code bytes at the given index, combine to
     * unsigned integer and return.
     * @param offset the offset
     * @return the unsigned 16-bit integer
     */
    static int twoByteCodes(int offset) {
        return (byteCode(offset) << 8) | byteCode(offset + 1);
    }
    
    /**
     * Read the two code bytes at the given index and combine
     * into a signed integer
     * @param offset the offset
     * @return the signed 16-bit integer 
     */
    static int signTwoByteCodes(int offset) {
        return signExtendShort(twoByteCodes(offset));
    }
    
    /**
     * @param value the value
     * @return the given byte sign extended
     */
    static int signExtendByte(int value) {
        return (value > 127) ? (value | 0xffffff00) : value;
    }
    
    /**
     * @param value the value
     * @return the given short value sign extended
     */
    static int signExtendShort(int value) {
        return (value > 32767) ? (value | 0xffff0000) : value;
    }
    
    /**
     * Read four byte codes at the given offset and combine
     * into a signed integer.
     * @param offset the offset
     * @return the signed 32-bit integer
     */
    static int signFourByteCodes(int offset) {
        return (byteCode(offset) << 24)
            | (byteCode(offset + 1) << 16)
            | (byteCode(offset + 2) << 8)
            | byteCode(offset + 3);
    }

}
