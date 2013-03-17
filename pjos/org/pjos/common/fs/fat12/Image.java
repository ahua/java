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

import java.io.File;
import java.io.RandomAccessFile;

import org.pjos.common.device.FileStorage;

/**
 * A tool used to create an empty floppy disk image.
 *
 * Safe for use by multiple threads.
 */
public class Image {
    
    /**
     * Run from the command line
     * @param args usage: java org.pjos.common.fs.fat12.Image"
     *             &lt;image&gt; &lt;label&gt;
     * @throws Exception if an error occurs
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println(
                    "usage: java org.pjos.common.fs.fat12.Image"
                    + " <image> <label>");
            System.exit(1);
        }

        // Don't create an image if the file already exists
        File image = new File(args[0]);
        if (image.exists()) { return; }

        // Create the image
        RandomAccessFile raf = new RandomAccessFile(image, "rw");
        raf.setLength(1024 * 1440);
        FileStorage storage = new FileStorage(raf);
        Format.format(storage, args[1]);
        raf.close();
        System.out.println("Created floppy image " + image.getPath());
    }

}










