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

import java.io.IOException;

import org.pjos.common.device.Storage;

/**
 * Used to format a floppy disk.
 *
 * Safe for use by multiple threads.
 */
class Format {
    
    /**
     * Format the given storage device in fat12 format
     * with the specified volume label.
     */
    static void format(Storage storage, String label) throws IOException {
        label = toLabel(label);
        Data disk = new StorageData(storage);
        int size = 1024 * 1440;
        if (disk.getSize() != size) {
            throw new IOException("Storage size invalid");
        }

        // first zero the whole lot
        for (int i = 0; i < size; i++) { disk.set8(0, i); }

        // write the FAT settings to the boot sector
        disk.set("MSWIN4.1", 3);  // for maximum compatibility
        disk.set16(512, 11);   // bytes per sector
        disk.set8(1, 13);      // sectors per cluster
        disk.set16(1, 14);     // reserved sector count
        disk.set8(2, 16);      // number of fat tables
        disk.set16(224, 17);   // number of entries in root directory
        disk.set16(2880, 19);  // 16-bit sector count
        disk.set8(0xf0, 21);   // set to 0xF0 for "removable media"
        disk.set16(9, 22);     // number of sectors in each fat table
        disk.set16(18, 24);    // sectors per track
        disk.set16(2, 26);     // number of heads
        disk.set32(0, 28);     // number of hidden sectors
        disk.set32(0, 32);     // 32-bit sector count (not used)
        disk.set8(0, 36);      // int 0x13 drive number, set to 0 for floppy
        disk.set8(0, 37);      // reserved
        disk.set8(0x29, 38);   // indicate following 3 fields are present
        disk.set32((int) System.currentTimeMillis(), 39); // arbitrary volume id
        disk.set(label, 43);               // volume label
        disk.set("FAT12   ", 54);          // file system type
        disk.set16(0xaa55, 510);           // magic number

        // write first two fat entries for each table
        disk.set8(0xf0, 512);
        disk.set8(0xff, 513);
        disk.set8(0xff, 514);
        disk.set8(0xf0, 5120);
        disk.set8(0xff, 5121);
        disk.set8(0xff, 5122);

        // write the volume label entry to the root directory
        int root = 512 * 19; // root dir located after boot sector and fats
        disk.set(label, root);
        disk.set8(0x08, root + 11);             // attributes for volume id
        disk.set8(0, root + 12);                // reserved
        disk.set8(0, root + 13);                // millisecond creation time
        disk.set16(0, root + 14);               // creation time
        disk.set16(0, root + 16);               // creation date
        disk.set16(0, root + 18);               // last access date
        disk.set16(0, root + 20);               // high word of cluster number
        long millis = System.currentTimeMillis();
        disk.set16(Util.timeStamp(millis), root + 22); // last write time
        disk.set16(Util.dateStamp(millis), root + 24); // last write date
        disk.set16(0, root + 26);               // low word of cluster number
        disk.set32(0, root + 28);               // file size
    }

    /**
     * Trim the given string, set to upper case and pad with spaces
     * or truncate to 11 characters long.
     */
    private static String toLabel(String s) {
        String result = s.trim().toUpperCase();
        while (result.length() < 11) { result += " "; }
        return result.substring(0, 11);
    }

}










