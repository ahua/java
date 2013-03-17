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
package org.pjos.common.fs.ext2;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.pjos.common.file.Broker;
import org.pjos.common.file.FileSystem;
import org.pjos.common.file.Path;

import org.pjos.common.device.FileStorage;
import org.pjos.common.device.Storage;

/**
 * This is an implementation of the ext2 file system.
 * Based on Dave Poirier's documentation:
 *     http://www.nongnu.org/ext2-doc/ext2.html
 *
 * Safe for use by multiple threads.
 */
public class Ext2 extends FileSystem {
    
    /** The manager */
    private Manager manager;
    
    /**
     * Create an Ext2 filesystem object which uses the
     * given storage device.
     * @param storage the underlying storage
     * @throws IOException if an error occurs
     */
    public Ext2(Storage storage) throws IOException {
        if (storage == null) { throw new NullPointerException(); }
        manager = new Manager(storage);
    }

    /**
     * @param path the file path
     * @return a broker for the given path, or null if none is available
     * @throws IOException if an error occurs
     */
    public Broker get(Path path) throws IOException {
        return new BrokerImpl(manager, path);
    }
    
    /**
     * @return the volume label
     * @throws IOException if an error occurs
     */
    public String getLabel() throws IOException {
        return manager.getLabel();
    }
    
    /**
     * Run from the command line for testing purposes
     * @param args usage: java org.pjos.common.fs.fat12.Ext2 
     *             &lt;image&gt; &lt;file/dir&gt;
     * @throws Exception if an error occurs
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("usage: "
                    + "java org.pjos.common.fs.fat12.Ext2 "
                    + "<image> <file/dir>");
            System.exit(1);
        }
        
        // create the filesystem object
        RandomAccessFile raf = new RandomAccessFile(args[0], "r");
        Ext2 fs = new Ext2(new FileStorage(raf));
        
        // try to read the volume label
        try {
            System.out.println("Volume label: " + fs.getLabel());
            System.out.println();
        } catch (IOException e) {
            System.out.println("No volume label");
            e.printStackTrace();
        }

        // either list files in directory, or output contents of file
        Path path = Path.create(args[1]);
        Broker broker = fs.get(path);
        if (broker.isDirectory()) {
            System.out.println("Directory " + path + " contains: ");
            String[] names = broker.list();
            for (int i = 0, n = names.length; i < n; i++) {
                System.out.println("\t[" + names[i] + "]");
            }
        } else {
            int size = (int) broker.getSize();
            System.out.println("File " + path
                    + " contains: [" + size + " bytes]");
            for (int i = 0; i < size; i++) {
                char c = (char) broker.read(i);
                if (Character.isISOControl(c)) { c = '.'; }
                System.out.print(c);
            }
            System.out.println();        
        }
        System.out.println();        
    }
    
}










