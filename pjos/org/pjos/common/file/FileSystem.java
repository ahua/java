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
package org.pjos.common.file;

import java.io.IOException;

/**
 * Subclasses of FileSystem provide the VM access to
 * a tree structure of files that will be made available
 * to applications through java.io.File and related classes.
 */
public abstract class FileSystem {
    
    /**
     * Create a file system
     */
    protected FileSystem() {
        // nothing to do here
    }
    
    /**
     * Return a broker object for the given path, null if no file or
     * directory exists which matches the supplied path. If the given
     * path is the empty path, return an object representing the root
     * directory of this file system.
     * @param path the file or directory path
     * @return the broker
     * @throws IOException if an error occurs
     */
    protected abstract Broker get(Path path) throws IOException;
    
}










