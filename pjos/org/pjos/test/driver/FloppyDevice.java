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
package org.pjos.test.driver;

import java.io.IOException;

import org.pjos.common.device.Device;
import org.pjos.common.device.Storage;

/**
 * A 1440 floppy disk device driver for the x86 platform.
 */
class FloppyDevice extends Device implements Storage {

    /** The size of a floppy disk */
    private static final long SIZE = 1474560L;

    /** The current sector data */
    private byte[] data;

    /** The current sector index */
    private int index;

    /**
     * Create a floppy device
     */
    FloppyDevice() {
        data = null;
        index = -1;
    }

    /**
     * @return the size in bytes
     * @throws IOException if an error occurs
     */
    public long getSize() throws IOException {
        return SIZE;
    }

    /**
     * Floppy disk size is fixed at 1.4M
     * @param size the new size
     * @throws IOException always
     */
    public void setSize(long size) throws IOException {
        throw new IOException("Illegal Operation");
    }

    /**
     * Read an unsigned byte value from the specified position.
     * @param pos the position to read from
     * @return the unsigned byte value read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    public synchronized int read(long pos) throws IOException {
        if (pos < 0 || pos > SIZE) {
            throw new IOException("Invalid position");
        }
        return TestArchitecture.readFromFloppy((int) pos);
    }

    /**
     * Read some data from the specified position.
     * @param pos the position to read from
     * @param buf the buffer to receive the bytes read
     * @param off the buffer location to write the bytes to
     * @param len the number of bytes
     * @return the number of bytes read, or -1 for end of file.
     * @throws IOException if an error occurs
     */
    public synchronized int read(long pos, byte[] buf, int off, int len)
            throws IOException
    {
        if (pos < 0 || pos + len > SIZE) {
            throw new IOException("Invalid position");
        }
        int count = 0;
        long from = pos;
        for (int to = off; count < len && from < SIZE; to++, count++, from++) {
            buf[to] = (byte) read(from);
        }
        return count;
    }

    /**
     * Write an unsigned byte value to the specified position
     * @param pos the position to write to
     * @param value the unsigned byte value to write
     * @throws IOException if an error occurs
     */
    public synchronized void write(long pos, int value) throws IOException {
        if (pos < 0 || pos > SIZE) {
            throw new IOException("Invalid position");
        }
throw new IOException("FloppyDevice.write(long, int) not implemented");
    }

    /**
     * Write some data to the specified position
     * @param pos the position to write to
     * @param buf contains the bytes to be written
     * @param off offset of first byte
     * @param len number of bytes
     * @throws IOException if an error occurs
     */
    public synchronized void write(long pos, byte[] buf, int off, int len)
            throws IOException
    {
        if (pos < 0 || pos + len > SIZE) {
            throw new IOException("Invalid position");
        }
        int from = off;
        for (long to = pos, n = pos + len; to < n; to++, from++) {
            int value = (int) (buf[from] & 0xff);
            write(to, value);
        }
    }

}










