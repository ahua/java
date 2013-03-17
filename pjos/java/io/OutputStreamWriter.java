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
 * Temporary implementation of output stream writer to avoid having
 * to load encoding classes.
 */
public class OutputStreamWriter extends Writer {
    
    /** The output stream */
    private OutputStream out;
    
    /**
     * Wrap the given output stream
     * @param out the stream to be wrapped
     */
    public OutputStreamWriter(OutputStream out) {
        this.out = out;
    }
    
    /**
     * Write the given characters
     * @param buf the character buffer to read from
     * @param off the offset of the first character to write
     * @param len the number of characters to write
     * @throws IOException if an error occurs
     */
    public void write(char[] buf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            out.write(buf[off + i]);
        }
    }
    
    /**
     * Flush the underlying stream
     * @throws IOException if an error occurs
     */
    public void flush() throws IOException {
        out.flush();
    }
    
    /**
     * Close the underlying stream
     * @throws IOException if an error occurs
     */
    public void close() throws IOException {
        out.close();
    }
    
}

