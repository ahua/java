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
package org.pjos.common.fs.fat12;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.StringTokenizer;

import org.pjos.common.device.FileStorage;

import org.pjos.common.file.Broker;
import org.pjos.common.file.Path;

/**
 * A tool used to import files into a floppy
 * disk image.
 *
 * Safe for use by multiple threads.
 */
public class Import {
    
    /**
     * Run from the command line. Only files which have been
     * modified since the last modification date of the
     * image will be imported. The search path argument must
     * be a list of directories separated by the ';' character.
     * @param args usage: java org.pjos.common.fs.fat12.Import
     *             &lt;image&gt; &lt;searchpath&gt;
     *             &lt;file1&gt; &lt;file2&gt;...
     * @throws Exception if an error occurs
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("usage: java org.pjos.common.fs.fat12.Import "
                    + "<image> <searchpath> <file1> <file2>...");
            System.exit(1);
        }

        // open the image file
        File image = new File(args[0]);
        long imageModified = image.lastModified();
        RandomAccessFile raf = new RandomAccessFile(image, "rw");
        FileStorage storage = new FileStorage(raf);
        Floppy1440 floppy = new Floppy1440(storage);

        // extract the search path
        StringTokenizer st = new StringTokenizer(args[1], ";");
        String[] dirs = new String[st.countTokens()];
        for (int i = 0, n = dirs.length; i < n; i++) {
            dirs[i] = st.nextToken();
        }

        // check each file in the argument list
        for (int i = 2, n = args.length; i < n; i++) {
            String path = args[i];
            File next = findFile(dirs, path);

            if (next.lastModified() > imageModified) {
                // import if modified more recently than image
                writeFile(floppy, path, next);
            } else {
                // otherwise only import if not present in image
                try {
                    floppy.get(Path.create(path));
                } catch (IOException e) {
                    writeFile(floppy, path, next);
                }
            }
        }

        // clean up
        raf.close();
    }

    /**
     * Search in all the directories in the given path to find
     * the specified file.
     */
    private static File findFile(String[] dirs, String path)
            throws FileNotFoundException
    {
        for (int i = 0, n = dirs.length; i < n; i++) {
            File file = new File(dirs[i], path);
            if (file.isFile()) { return file; }
        }
        throw new FileNotFoundException(path);
    }
    
    /**
     * Write the specified file to the given floppy. Use the directory
     * names from the file object. Load the file contents relative to the
     * given base directory.
     */
    private static void writeFile(Floppy1440 floppy, String filepath, File from)
            throws IOException
    {
        System.out.println("Importing: " + filepath);
        Path path = Path.create(filepath);
        
        // create the directories in the path
        Broker broker = floppy.get(Path.ROOT); // start at root directory
        for (int i = 0, n = path.size(); i < n; i++) {
            Path dir = path.subPath(0, i);
            try {
                broker = floppy.get(dir);
            } catch (IOException e) { 
                broker = broker.createDirectory(dir.last());
            }
        }
        
        // create the file
        try {
            broker = floppy.get(path);
        } catch (IOException e) {
            broker = broker.createFile(path.last());
        }
        
        // read file data
        int size = (int) from.length();
        byte[] data = new byte[size];
        BufferedInputStream in =
                new BufferedInputStream(new FileInputStream(from));
        int k = in.read(data);
        if (k != size) {
            throw new IOException("Unable to read file fully: "
                    + from.getPath());
        }
        in.close();
        
        // write data to floppy
        broker.write(0, data, 0, size);
    }
    
}










