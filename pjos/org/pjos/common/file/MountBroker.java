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
 * This is a filter broker that mounts another broker onto a base path.
 */
public class MountBroker extends FilterBroker {
    
    /** The base path */
    protected final Path base;
    
    /**
     * Mount the given broker on the specified base path
     * @param base the path to mount the broker on
     * @param broker the broker to mount
     */
    protected MountBroker(Path base, Broker broker) {
        super(base.append(broker.getPath()), broker);
        this.base = base;
    }
    
    /**
     * Create a new file
     * @param name the name for the new file
     * @return the broker for the new file, or null if not successful
     */
    public Broker createFile(String name) {
        Broker created = broker.createFile(name);
        return (created != null) ? new MountBroker(base, created) : null;
    }
    
    /**
     * Create a new directory
     * @param name the name for the new directory
     * @return the broker for the new directory, or null if not successful
     */
    public Broker createDirectory(String name) {
        Broker created = broker.createDirectory(name);
        return (created != null) ? new MountBroker(base, created) : null;
    }
    
}










