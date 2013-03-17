


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

import java.io.IOException;

import org.pjos.common.device.Device;
import org.pjos.common.device.Storage;

/**
 * A 1440 floppy disk device driver for the x86 platform.
 */
class FloppyDevice extends Device implements Storage, Runnable {

    /** The size of a floppy disk */
    private static final long SIZE = 1474560L;

    /** The amount of time to wait before writing changes */
    private static final long INTERVAL = 2000;

    /** Provides access to the data */
    private Floppy floppy = new Floppy();

    /** The current sector data */
    private byte[] data = null;

    /** The current sector index */
    private int index = -1;

    /**
     * Create a floppy device
     */
    FloppyDevice() {
        // nothing to do here
    }

    /**
     * Reset the floppy device
     */
    private synchronized void reset() {
        X86Architecture.out(0, 0x3f2);
    }

    /**
     * @return the number of bytes on a 1.4M floppy disk
     */
    public long getSize() throws IOException {
        return SIZE;
    }

    /**
     * The 1.4M floppy is a fixed size 
     * @param size ignored
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
        if (pos < 0 || pos >= SIZE) {
            throw new IOException("Invalid position");
        }
        
        // check that data array exists
        if (data == null) { data = new byte[512]; }

        // load the sector data if necessary
        int p = (int) pos;
        int sector = p / 512;
        if (index != sector) {
            floppy.readSector(sector, data);
            index = sector;
        }

        // return the byte value as unsigned integer value
        int offset = p % 512;
        int result = (int) data[offset] & 0xff;
        return result;
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
        int to = off;
        while (count < len && from < SIZE) {
            buf[to++] = (byte) read(from++);
            count++;
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

    /**
     * Check periodically to make sure the drive motor is not on when
     * not needed.
     */
    private synchronized void maintain() {
        // NEED TO IMPLEMENT
    }


    /**
     * Run in the background and make sure the floppy drive is looked after
     */
    public void run() {
        maintain();
    }

}










