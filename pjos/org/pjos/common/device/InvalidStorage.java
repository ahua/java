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
 * An implementation of storage that throws an exception for every operation.
 *
 * Safe for use by multiple threads.
 */
public final class InvalidStorage implements Storage {
    
    /**
     * Create an invalid storage object
     */
    public InvalidStorage() {
        // nothing to do here
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @return n/a
     * @throws IOException always
     */
    public long getSize() throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param size ignored
     * @throws IOException always
     */
    public void setSize(long size) throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param pos ignored
     * @return n/a
     * @throws IOException always
     */
    public int read(long pos) throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param pos ignored
     * @param buf ignored
     * @param off ignored
     * @param len ignored
     * @return n/a
     * @throws IOException always
     */
    public int read(long pos, byte[] buf, int off, int len) throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param pos ignored
     * @param value ignored
     * @throws IOException always
     */
    public void write(long pos, int value) throws IOException {
        throw new OperationNotAvailableException();
    }
    
    /**
     * Always throws OperationNotAvailableException
     * @param pos ignored
     * @param buf ignored
     * @param off ignored
     * @param len ignored
     * @throws IOException always
     */
    public void write(long pos, byte[] buf, int off, int len)
            throws IOException
    {
        throw new OperationNotAvailableException();
    }
    
}










