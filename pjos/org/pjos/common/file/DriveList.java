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

/**
 * Provides root directory access to the drive manager
 * and is therefore the root directory of the entire
 * file system.
 */
class DriveList extends Broker {
    
    /**
     * Create a drive list
     */
    DriveList() {
        super(Path.ROOT);
    }

    /**
     * Always valid
     */
    public boolean isValid() { 
        return true;
    }
    
    /**
     * Behaves like a directory
     */
    public boolean isDirectory() { 
        return true;
    }
    
    /**
     * Not a file
     */
    public boolean isFile() {
        return false;
    }
    
    /**
     * Not hidden
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * Not available
     */
    public long getLastModified() {
        return 0L;
    }
    
    /**
     * Does nothing
     */
    public void setLastModified(long timestamp) {
        // nothing to do here
    }

    /**
     * Return the list of drive names
     */
    public String[] list() {
        return DriveManager.list();
    }
    
    /**
     * Can't be deleted
     */
    public boolean delete() {
        return false;
    }
    
    /**
     * Cannot contain files
     */
    public Broker createFile(String name) {
        return null;
    }
    
    /**
     * Drives can only be added or removed using the drive manager
     */
    public Broker createDirectory(String name) {
        return null;
    }
    
    /**
     * Nothing to sync.
     */
    public void sync() {
        // nothing to do here
    }

}










