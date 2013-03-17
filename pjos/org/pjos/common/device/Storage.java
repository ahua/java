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
package org.pjos.common.device;

import java.io.IOException;

/**
 * Represents a collection of bytes that can be read and
 * modified (like a file for example).
 */
public interface Storage {
    
    /**
     * @return the storage size in bytes
     * @throws IOException if an error occurs
     */
    long getSize() throws IOException;
    
    /**
     * Set the size of this storage in bytes
     * @param size the new size
     * @throws IOException if an error occurs
     */
    void setSize(long size) throws IOException;
    
    /**
     * Read an unsigned byte value from the specified position.
     * @param pos the position to read from
     * @return the unsigned byte value read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    int read(long pos) throws IOException;
    
    /**
     * Read some data from the specified position.
     * @param pos the position to read from
     * @param buf the buffer to receive the bytes read
     * @param off the buffer location to write the bytes to
     * @param len the number of bytes
     * @return the number of bytes read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    int read(long pos, byte[] buf, int off, int len) throws IOException;
    
    /**
     * Write an unsigned byte value to the specified position
     * @param pos the position to write to
     * @param value the unsigned byte value to write
     * @throws IOException if an error occurs
     */
    void write(long pos, int value) throws IOException;
    
    /**
     * Write some data to the specified position
     * @param pos the position to write to
     * @param buf contains the bytes to be written
     * @param off offset of first byte
     * @param len number of bytes
     * @throws IOException if an error occurs
     */
    void write(long pos, byte[] buf, int off, int len)
            throws IOException;
    
}










