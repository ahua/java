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

import java.nio.channels.FileChannel;

/**
 * Implementation of java.io.FileInputStream based on Sun specification.
 */
public class FileInputStream extends InputStream {

    /** The file descriptor */
    private FileDescriptor fd;

    /** The position */
    private long pos = 0;

    /**
     * Create a file input stream
     * @param name the name of the file to read from
     * @throws FileNotFoundException if the file is not found
     */
    public FileInputStream(String name) throws FileNotFoundException {
        this(new File(name));
    }

    /**
     * Create a file input stream
     * @param file the file to read from
     * @throws FileNotFoundException if the file is not found
     */
    public FileInputStream(File file) throws FileNotFoundException {
        if (file == null || !file.exists() || file.isDirectory()) {
            //System.out.println("file: " + file);
            throw new FileNotFoundException();
        }
        fd = file.getFileDescriptor();
    }

    /**
     * Create a file input stream
     * @param fd the file descriptor
     */
    public FileInputStream(FileDescriptor fd) {
        this.fd = fd;
    }

    /**
     * Read a byte
     * @return the next unsigned byte value, or -1 for end of file.
     * @throws IOException if unable to read
     */
    public int read() throws IOException {
        return fd.getBroker().read(pos++);
    }

    /**
     * Read bytes into the given array
     * @param b the buffer to write the bytes to
     * @return the number of bytes read
     * @throws IOException if an error occurs
     */
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Read bytes into the given array
     * @param b the buffer to write the bytes to
     * @param off the buffer location of the first byte
     * @param len the number of bytes to read
     * @return the number of bytes read
     * @throws IOException if an error occurs while reading
     */
    public int read(byte[] b, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        int result = fd.getBroker().read(pos, b, off, len);
        if (result > 0) {
            pos += result;
        }
        return result;
    }

    /**
     * Skip over n bytes
     * @param n the number of bytes to skip
     * @return the number of bytes skipped
     * @throws IOException if an error occurs while skipping
     */
    public long skip(long n) throws IOException {
        return super.skip(n);
    }

    /**
     * @return the number of bytes available
     * @throws IOException if an error occurs
     */
    public int available() throws IOException {
        return (int) (fd.getBroker().getSize() - pos);
    }

    /**
     * Close this stream
     * @throws IOException if an error occurs
     */
    public void close() throws IOException {
        fd = null;
    }

    /**
     * @return the file descriptor
     * @throws IOException if an error occurs
     */
    public final FileDescriptor getFD() throws IOException {
        return fd;
    }

    /**
     * @return the file channel
     */
    public FileChannel getChannel() {
throw new UnsupportedOperationException();
    }

    /**
     * Finalise this stream
     * @throws IOException if an error occurs
     */
    protected void finalize() throws IOException {
        close();
    }

}
