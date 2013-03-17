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

import org.pjos.common.file.Broker;
import org.pjos.common.file.DriveManager;
import org.pjos.common.file.Path;

/**
 * Implementation of java.io.FileDescriptor based on Sun specification.
 */
public final class FileDescriptor {

    /** The standard input stream */
    public static final FileDescriptor in = null;

    /** The standard output stream */
    public static final FileDescriptor out = null;

    /** The standard error stream */
    public static final FileDescriptor err = null;

    /** The broker */
    private Broker broker;

    /**
     * Create an invalid file descriptor
     */
    public FileDescriptor() {
        broker = null;
    }

    /**
     * Create a file descriptor with the given path. The given path
     * is assumed to represent an absolute, canonical path.
     * @param path the absolute, canonical path
     */
    FileDescriptor(Path path) throws IOException {
        try {
            broker = DriveManager.getBroker(path);
        } catch (FileNotFoundException e) {
            String s = path.toString();
            FileNotFoundException wrap = new FileNotFoundException(s);
            throw (FileNotFoundException) wrap.initCause(e);
        }
    }

    /**
     * @return true if this descriptor is valid, false otherwise
     */
    public boolean valid() {
        return (broker != null) && broker.isValid();
    }

    /**
     * Sync underlying system buffers
     * @throws SyncFailedException if unable to sync
     */
    public void sync() throws SyncFailedException {
        if (broker != null) { broker.sync(); }
    }
    
    /**
     * Return the broker
     */
    Broker getBroker() {
        return broker;
    }
    
}
