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
package java.io;

/**
 * Implementation of java.io.ByteArrayInputStream based on Sun specification.
 */
public class ByteArrayInputStream extends InputStream {
    
    /** The buffer where the data is stored */
    protected byte[] buf;
    
    /** Index one greater than the last valid character in the buffer */
    protected int count;
    
    /** The currently marked position in the stream */
    protected int mark;
    
    /** The index of the next character to read */
    protected int pos;
    
    /** 
     * Create a byte array input stream
     * @param buf the buffer to use
     */
    public ByteArrayInputStream(byte[] buf) {
        this(buf, 0, buf.length);
    }
    
    /**
     * Create a byte array input stream
     * @param buf the buffer to use
     * @param offset the buffer location to read from
     * @param length the number of bytes that can be read
     */
    public ByteArrayInputStream(byte[] buf, int offset, int length) {
        this.buf = buf;
        pos = offset;
        count = offset + length;
        mark = offset;
    }
    
    /**
     * Read the next byte of data
     * @return the next unsigned byte value, or -1 for end of file
     */
    public int read() {
        return (pos < count) ? buf[pos++] & 0xff : -1;
    }
    
    /**
     * Read some data
     * @param b the buffer to write to
     * @param off the buffer location to write to
     * @param len the number of bytes to read
     * @return the number of bytes read, or -1 for end of file
     */
    public int read(byte[] b, int off, int len) {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (pos == count) { return -1; }
        int to = off;
        int max = off + len;
        while (to < max && pos < count) {
            b[to++] = buf[pos++];
        }
        return to - off;
    }
    
    /**
     * Skip over data
     * @param n the number of bytes to skip
     * @return the number of bytes skipped
     */
    public long skip(long n) {
        long k = count - pos;
        if (k > n) { k = n; }
        pos += (int) k;
        return k;
    }
    
    /**
     * @return the number of bytes available for reading
     */
    public int available() {
        return count - pos;
    }
    
    /**
     * @return true if mark/reset operations are permitted
     */
    public boolean markSupported() {
        return true;
    }
    
    /** 
     * Mark the current position in this stream
     * @param readAheadLimit the number of bytes that can be pre-read
     */
    public void mark(int readAheadLimit) {
        mark = pos;
    }
    
    /**
     * Reset the buffer to the marked position
     */
    public void reset() {
        pos = mark;
    }
    
    /**
     * Close this stream (does nothing)
     * @throws IOException if an error occurs
     */
    public void close() throws IOException {
        // don't do anything
    }
    
}















