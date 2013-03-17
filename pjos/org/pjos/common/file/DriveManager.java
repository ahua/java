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

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the different file systems and maps them
 * to drive names. Although this class only allows file systems
 * to be mounted to drive names there is nothing to stop a file
 * system itself transparently providing access to other filesystems.
 */
public class DriveManager extends FileSystem {

    /** The singleton drive manager */
    private static final DriveManager singleton = new DriveManager();

    /** The registered drives */
    private Map drives;

    /**
     * Create a drive manager
     */
    private DriveManager() {
        drives = new HashMap();
    }

    /**
     * @return the list of drive names
     */
    public static String[] list() {
        return singleton.listDrives();
    }

    /**
     * Add a drive
     * @param drive the drive name
     * @param fs the filesystem to mount on the drive
     * @throws IOException if an error occurs
     */
    public static void add(String drive, FileSystem fs) throws IOException {
        singleton.addDrive(drive, fs);
    }

    /**
     * Remove a drive
     * @param drive the drive name
     */
    public static void remove(String drive) {
        singleton.removeDrive(drive);
    }

    /**
     * @param path the file path
     * @return a file object for the given path
     * @throws IOException if an error occurs 
     */
    public static Broker getBroker(Path path) throws IOException {
        return singleton.get(path);
    }

    /**
     * @param path the file path
     * @return a file broker for the given path
     * @throws IOException if an error occurs
     */
    protected synchronized Broker get(Path path) throws IOException {
        // only absolute paths are accepted here
        if (!path.isAbsolute()) {
            throw new IllegalArgumentException("Path not absolute");
        }
        
        // return list of drives as root directory contents
        if (path == Path.ROOT) { return new DriveList(); }

        // wrap a file object from one of the drives
        String drive = path.get(0);
        FileSystem fs = getDrive(drive);
        if (fs == null) { throw new FileNotFoundException(); }
        Path drivePath = path.subPath(0, 1);
        Path filePath = path.subPath(1).makeAbsolute();
        Broker target = fs.get(filePath);
        return new MountBroker(drivePath, target);
    }

    /**
     * Add a drive
     */
    private synchronized void addDrive(String drive, FileSystem fs)
            throws IOException
    {
        if (drive.indexOf(Path.SEPARATOR) != -1) {
            throw new IllegalArgumentException("Drive specification invalid");
        } else if (fs == null) {
            throw new NullPointerException();
        } else if (drives.containsKey(drive)) {
            throw new IOException("Drive already exists: " + drive);
        }
        drives.put(drive, fs);
    }

    /**
     * Remove a drive
     */
    private synchronized void removeDrive(String drive) {
        drives.remove(drive);
    }

    /**
     * Return the list of drive names
     */
    private synchronized String[] listDrives() {
        int size = drives.size();
        return (String[]) drives.keySet().toArray(new String[size]);
    }

    /**
     * Return the file system mapped to the specfied drive
     */
    private synchronized FileSystem getDrive(String drive) {
        return (FileSystem) drives.get(drive);
    }

}










