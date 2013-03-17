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
 * Implementation of java.io.ByteArrayOutputStream based on Sun specification.
 */
public class ByteArrayOutputStream extends OutputStream {
    
    /** The buffer where the data is stored */
    protected byte[] buf;
    
    /** The number of valid bytes in the buffer */
    protected int count = 0;
    
    /**
     * Create a byte array output stream
     */
    public ByteArrayOutputStream() {
        this(32);
    }
    
    /**
     * Create a byte array output stream with the specified capacity
     *
     * @param size the desired capacity
     */
    public ByteArrayOutputStream(int size) {
        buf = new byte[size];
    }
    
    /**
     * Write a byte to this output stream
     *
     * @param b the unsigned byte value to be written
     */
    public synchronized void write(int b) {
        ensureCapacity(count + 1);
        buf[count++] = (byte) b;
    }

    /**
     * Write data to this output stream
     *
     * @param b the bytes to write
     * @param off the offset of the first byte to write
     * @param len the number of bytes to write
     */
    public synchronized void write(byte[] b, int off, int len) {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        for (int from = off, i = 0; i < len; i++) {
            buf[count++] = b[from++];
        }
    }
    
    /**
     * Write contents to the given output stream
     * @param out the stream to write to
     * @throws IOException if unable to perform write
     */
    public synchronized void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }
    
    /**
     * Reset count to zero
     */
    public synchronized void reset() {
        count = 0;
    }
    
    /**
     * Return a byte array containing the current buffer contents
     * @return a byte array containing the contents
     */
    public synchronized byte[] toByteArray() {
        byte[] result = new byte[count];
        for (int i = 0; i < count; i++) {
            result[i] = buf[i];
        }
        return result;
    }
    
    /**
     * Return the current size
     * @return the size
     */
    public synchronized int size() {
        return count;
    }
    
    /**
     * Convert to a string
     * @return the string representation
     */
    public String toString() {
throw new UnsupportedOperationException();
    }
    
    /**
     * @param enc the encoding to use
     * @return the string representation using the given encoding
     * @throws UnsupportedEncodingException if not supported
     */
    public String toString(String enc) throws UnsupportedEncodingException {
throw new UnsupportedOperationException();
    }
    
    /**
     * Convert to a string using the given hibyte (deprecated)
     * @param hibyte the high byte to use in the conversion
     * @return the string representation
     */
    public synchronized String toString(int hibyte) {
        StringBuffer sb = new StringBuffer(count);
        hibyte = (hibyte & 0xff) << 8;
        for (int i = 0; i < count; i++) {
            int value = (int) (buf[i] & 0xff);
            sb.append((char) (hibyte | value));
        }
        return sb.toString();
    }
    
    /**
     * Do nothing!
     * @throws IOException this exception is not thrown
     */
    public void close() throws IOException {
        // don't do anything
    }
    
    /**
     * Ensure that the buffer can handle the specified capacity
     */
    private void ensureCapacity(int minimumCapacity) {
        int max = buf.length;
        if (max < minimumCapacity) {
            // try doubling the current capacity,
            // if not enough use suggested minimum
            max = max * 2;
            if (max < minimumCapacity) { max = minimumCapacity; }

            // allocate larger internal array
            byte[] newBuf = new byte[max];
            for (int i = 0; i < count; i++) { newBuf[i] = buf[i]; }
            buf = newBuf;
        }
    }
    
}















