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

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a 32-byte FAT12 directory entry.
 *
 * Not safe for use by multiple threads.
 */
class Entry {

    /**
     * Contains the offsets for the characters of
     * a long directory entry in reverse order
     */
    private static final int[] longCharOffsets = new int[] {
            30, 28, 24, 22, 20, 18, 16, 14, 9, 7, 5, 3, 1 };

    /** Read only attribute */
    private static final int ATTR_READ_ONLY = 0x01;

    /** Hidden attribute */
    private static final int ATTR_HIDDEN = 0x02;

    /** System attribute */
    private static final int ATTR_SYSTEM = 0x04;

    /** Volume ID attribute */
    private static final int ATTR_VOLUME_ID = 0x08;

    /** Directory attribute */
    private static final int ATTR_DIRECTORY = 0x10;

    /** Archive attribute */
    private static final int ATTR_ARCHIVE = 0x20;

    /** Long name attribute */
    private static final int ATTR_LONG_NAME
            = ATTR_READ_ONLY
            | ATTR_HIDDEN
            | ATTR_SYSTEM
            | ATTR_VOLUME_ID;

    /**
     * Long name mask (used to determine whether an entry
     * is a long-name sub-component
     */
    private static final int ATTR_LONG_NAME_MASK = ATTR_READ_ONLY
                                                    | ATTR_HIDDEN
                                                    | ATTR_SYSTEM
                                                    | ATTR_VOLUME_ID
                                                    | ATTR_DIRECTORY
                                                    | ATTR_ARCHIVE;

    /** Last long entry mask */
    private static final int LAST_LONG_ENTRY = 0x40;

    /** The data */
    private Data data;

    /** The offset of this entries data */
    private int offset;

    /** The disk model */
    private Model model;

    /** The next entry */
    private Entry next;

    /** The initial byte */
    private int initial;

    /** The attributes byte */
    private int attributes;

    /** Set if this entry is free */
    private boolean isFree = false;

    /** Set if this entry is long */
    private boolean isLong = false;

    /** Set if this entry represents a file */
    private boolean isFile = false;

    /** Set if this entry represents a directory */
    private boolean isDirectory = false;

    /** Set if this entry is a volume label */
    private boolean isVolumeLabel = false;

    /** The raw format short name */
    private String rawShortName;

    /** The short name */
    private String shortName;

    /** The directory this entry represents */
    private Directory directory = null;

    /** The file this entry represents */
    private File file;

    /** The chain this entry represents */
    private Chain chain = null;

    /** The cluster number of the first cluster */
    private int cluster;

    /** The checksum */
    private int checksum;

    /** The portion of the long file name contained in this entry */
    private String longNamePortion;

    /** The long file name for this entry */
    private String longName;

    /** The name for this entry */
    private String name;

    /** The file size */
    private int size;

    /**
     * Read the next entry from the specified offset of the given data
     * and return. Return null if no more entries can be read.
     */
    static Entry read(Data data, int offset, Model model) throws IOException {
        // check for end of data or null entry
        if (offset >= data.getSize() || data.get8(offset) == 0x00) {
            return null;
        }
        return new Entry(data, offset, model);
    }

    /**
     * Create an entry by reading from the specified
     * offset of the given data object
     */
    Entry(Data data, int offset, Model model) throws IOException {
        // set fields
        this.data = data;
        this.offset = offset;
        this.model = model;

        // initialise object by reading entry data
        initial = data.get8(offset);
        attributes = data.get8(offset + 11);
        if (initial == 0xe5) {
            initFree();
        } else if ((attributes & ATTR_LONG_NAME_MASK) == ATTR_LONG_NAME) {
            initLong();
        } else {
            initShort();
        }

        // read next entry
        next = read(data, offset + 32, model);

        // if this is the first of a set of long entries, calculate name
        if (isLong && ((initial & LAST_LONG_ENTRY) != 0)) {
            readLongName();
        }
    }

    /**
     * Initialise as free entry
     */
    private void initFree() { isFree = true; }

    /**
     * Initialise as long entry
     */
    private void initLong() throws IOException {
        isLong = true;
        checksum = data.get8(offset + 13);
        readLongNamePortion();
    }


    /**
     * Initialise as short entry
     */
    private void initShort() throws IOException {
        // find out what sort of entry this is
        int test = attributes & (ATTR_DIRECTORY | ATTR_VOLUME_ID);
        if (test == 0x00) {
            isFile = true;
        } else if (test == ATTR_DIRECTORY) {
            isDirectory = true;
        } else if (test == ATTR_VOLUME_ID) {
            isVolumeLabel = true;
        }

        // set other values
        cluster = (data.get16(offset + 20) << 16) | data.get16(offset + 26);
        size = data.get32(offset + 28);
        readShortName();
        checksum = calculateChecksum(rawShortName);
    }

    /**
     * Recurse through the entries in the list starting with this
     * object and return a list containing all the names (as Strings)
     * of valid files and directories found.
     */
    List list() throws IOException {
        List result = (next != null) ? next.list() : new LinkedList();
        if (isFile || isDirectory) {
            if (!name.equals(".") && !name.equals("..")) {
                result.add(0, name);
            }
        }
        return result;
    }

    /**
     * Add a file entry to the list of entries beginning with this one.
     * Return the short entry created.
     */
    void addFileEntries(String shortName, String longName) throws IOException {
        if (next != null) {
            next.addFileEntries(shortName, longName);
        } else {
            addEntries(shortName, longName, 0, System.currentTimeMillis());
        }
    }

    /**
     * Add a directory entry to the list of entries beginning with this one.
     * Return the short entry created.
     */
    void addDirectoryEntries(
            String shortName,
            String longName,
            int parentCluster)
            throws IOException
    {
        if (next != null) {
            next.addDirectoryEntries(shortName, longName, parentCluster);
        } else {
            // write the entries
            long millis = System.currentTimeMillis();
            Entry dir = addEntries(shortName, longName, ATTR_DIRECTORY, millis);
            Chain ch = dir.getChain();

            // entry for .
            ch.set(".          ", 0);                       // DIR_Name
            ch.set8(ATTR_DIRECTORY, 11);                    // DIR_attr
            ch.set8(0, 12);                                 // DIR_NTRes
            ch.set8(0, 13);                                 // DIR_CrtTimeTenth
            ch.set16(0, 14);                                // DIR_CrtTime
            ch.set16(0, 16);                                // DIR_CrtDate
            ch.set16(0, 18);                                // DIR_LstAccDate
            ch.set16(0, 20);                                // DIR_FstClusHI
            ch.set16(Util.timeStamp(millis), 22);           // DIR_WrtTime
            ch.set16(Util.dateStamp(millis), 24);           // DIR_WrtDate
            ch.set16(ch.getFirstClusterNumber(), 26);       // DIR_FstClusLO
            ch.set32(0, 28);                                // DIR_FileSize

            // entry for ..
            ch.set("..         ", 32 + 0);                  // DIR_Name
            ch.set8(ATTR_DIRECTORY, 32 + 11);               // DIR_attr
            ch.set8(0, 32 + 12);                            // DIR_NTRes
            ch.set8(0, 32 + 13);                            // DIR_CrtTimeTenth
            ch.set16(0, 32 + 14);                           // DIR_CrtTime
            ch.set16(0, 32 + 16);                           // DIR_CrtDate
            ch.set16(0, 32 + 18);                           // DIR_LstAccDate
            ch.set16(0, 32 + 20);                           // DIR_FstClusHI
            ch.set16(Util.timeStamp(millis), 32 + 22);      // DIR_WrtTime
            ch.set16(Util.dateStamp(millis), 32 + 24);      // DIR_WrtDate
            ch.set16(parentCluster, 32 + 26);               // DIR_FstClusLO
            ch.set32(0, 32 + 28);                           // DIR_FileSize
        }
    }

    /**
     * Add entries to the list of entries beginning with this one
     */
    private Entry addEntries(
            String shortName,
            String longName,
            int attributes,
            long millis)
            throws IOException
    {
        // calculate checksum
        int period = shortName.indexOf('.');
        String basis = (period == -1)
                ? shortName 
                : shortName.substring(0, period);
        String ext = (period == -1) ? "" : shortName.substring(period + 1);
        String raw = (period != -1)
                ? pad(basis, 8) + pad(ext, 3)
                : pad(basis, 11);
        int cs = calculateChecksum(raw);

        // find out how many long entries required
        int length = longName.length();
        int numLongEntries = (length + 12) / 13;
        int max = numLongEntries * 13;

        // write the long entries
        int location = offset + 32;
        int order = numLongEntries;
        while (order > 0) {
            int first = (order == numLongEntries)
                    ? (order | LAST_LONG_ENTRY)
                    : order;
            data.set8(first, location); // LDIR_Ord
            data.set8(0x0f, location + 11); // LDIR_Attr
            data.set8(0x00, location + 12); // LDIR_Type
            data.set8(cs, location + 13); // LDIR_Chksum
            data.set16(0x0000, location + 26); // LDIR_FstClusLO

            // write characters
            for (int i = 0; i < 13; i++) {
                int index = (order * 13) - 1 - i;
                int c = 0xffff;
                if (index == length) {
                    c = 0x0000;
                } else if (index < length) {
                    c = (int) longName.charAt(index);
                }
                data.set16(c, location + longCharOffsets[i]); // set char
            }
            order--;
            location += 32;
        }
        
        // find the cluster number
        int number = 0xff8; // end of cluster chain for files
        if (attributes != 0) {
            Cluster clus = model.getFreeCluster();
            clus.setNextCluster(0xff8); // now used by directory
            for (int i = 0, n = clus.getSize(); i < n; i++) {
                clus.set8(0, i); // fill with null bytes
            }
            number = clus.getNumber();
        }

        // write the short entry
        writeShortEntry(data, location, attributes, number, raw, millis);
        
        // read the created entries and return the short entry
        next = read(data, offset + 32, model);
        Entry result = next;
        while (result.next != null) { result = result.next; }
        return result;
    }
    
    /**
     * Write the short entry with the given parameters
     */
    private void writeShortEntry(
            Data data,
            int location,
            int attributes,
            int number,
            String raw,
            long millis)
            throws IOException
    {
        data.set(raw, location);                            // DIR_Name
        data.set8(attributes, location + 11);               // DIR_Attr
        data.set8(0, location + 12);                        // DIR_NTRes
        data.set8(0, location + 13);                        // DIR_CrtTimeTenth
        data.set16(0, location + 14);                       // DIR_CrtTime
        data.set16(0, location + 16);                       // DIR_CrtDate
        data.set16(0, location + 18);                       // DIR_LstAccDate
        data.set16(number >> 16, location + 20);            // DIR_FstClusHI
        data.set16(Util.timeStamp(millis), location + 22);  // DIR_WrtTime
        data.set16(Util.dateStamp(millis), location + 24);  // DIR_WrtDate
        data.set16(number, location + 26);                  // DIR_FstClusLO
        data.set32(0, location + 28);                       // DIR_FileSize
    }

    /**
     * Pad the given string with spaces to the desired length
     */
    private String pad(String s, int length) {
        String result = s;
        while (result.length() < length) { result += " "; }
        return result;
    }

    /**
     * Add a volume label entry to the list of entries beginning with this one
     */
    void addVolumeLabelEntry(String raw) throws IOException {
        if (next != null) {
            next.addVolumeLabelEntry(raw);
        } else {
            createVolumeLabelEntry(raw, data, offset + 32);
            next = read(data, offset + 32, model);
        }
    }

    /**
     * Create a volume label entry at the specified offset of the given data
     */
    static void createVolumeLabelEntry(String raw, Data data, int offset)
            throws IOException
    {
        checkShortNameChars(raw);

        // create timestamps
        long now = System.currentTimeMillis();
        int time = Util.timeStamp(now);
        int date = Util.dateStamp(now);

        // add entry
        int init = raw.charAt(0) & 0xff;
        if (init == 0xe5) { init = 0x05; }
        data.set8(init, offset); // DIR_NAME first character
        data.set(raw.substring(1), offset + 1); // DIR_Name characters 1-11
        data.set8(ATTR_VOLUME_ID, offset + 11); // DIR_Attr
        data.set8(0, offset + 12); // DIR_NTRes
        data.set8(0, offset + 13); // DIR_CrtTimeTenth
        data.set16(time, offset + 14); // DIR_CrtTime
        data.set16(date, offset + 16); // DIR_CrtDate
        data.set16(date, offset + 18); // DIR_LstAccDate
        data.set16(0, offset + 20); // DIR_FstClusHI (always zero for FAT12)
        data.set16(time, offset + 22); // DIR_WrtTime
        data.set16(date, offset + 24); // DIR_WrtDate
        data.set16(0, offset + 26); // DIR_FstClusLO (must be zero for label)
        data.set32(0, offset + 29); // DIR_FileSize (must be zero for label)
    }

    /**
     * Compact this directory by removing the free entries and
     * sliding the rest down.
     */
    void compact(int to) throws IOException {
        if (isFree) {
            // no need to copy this entry
            if (next != null) { next.compact(to); }
        } else {
            // keep this entry
            if (to < offset) {
                for (int i = 0; i < 32; i++) {
                    // copy this entry
                    data.set8(data.get8(offset + i), to + i);
                }
            }
            // compact next entry
            if (next != null) { next.compact(to + 32); } 
        }
    }

    /**
     * Read the portion of the long file name contained in this entry
     */
    private void readLongNamePortion() throws IOException {
        StringBuffer buf = new StringBuffer();
        for (int i = 0, n = longCharOffsets.length; i < n; i++) {
            int k = data.get16(offset + longCharOffsets[i]);
            if (k != 0x0000 && k != 0xFFFF) { buf.append((char) k); }
        }
        longNamePortion = buf.toString();
    }

    /**
     * Read the long file name contained in this entry and the ones following
     */
    private void readLongName() {
        // concatenate the characters from the long entries
        // to form the long filename
        int count = initial ^ LAST_LONG_ENTRY; // number of long file entries
        StringBuffer buf = new StringBuffer(13 * count); // max 13 chars
        buf.append(longNamePortion);
        Entry entry = next;
        for (int i = count - 1; i > 0; i--) {
            // if any entry is found to be invalid, abort the process
            if (entry == null
                    || !entry.isLong
                    || entry.initial != i
                    || entry.checksum != checksum)
            {
                return;
            }
            buf.append(entry.longNamePortion);
            entry = entry.next;
        }

        // if the short entry is invalid, abort the process
        if (entry == null || entry.isLong || entry.checksum != checksum) {
            return;
        }
        entry.setLongName(buf.reverse().toString());
    }

    /**
     * Set the long file name
     */
    private void setLongName(String longName) {
        if (longName == null) { throw new NullPointerException(); }
        this.longName = longName;
        name = longName;
    }

    /**
     * Read the short name
     */
    private void readShortName() throws IOException {
        // read the raw version (11 chars)
        StringBuffer buf = new StringBuffer(11);
        char c = (char) initial;
        if (c == '\u0005') { c = '\u00e5'; }
        buf.append(c); // first character
        for (int i = 1; i < 11; i++) {
            buf.append((char) data.get8(offset + i)); // rest of characters
        }
        rawShortName = buf.toString();

        // calculate short file name
        String prefix = rawShortName.substring(0, 8).trim();
        String ext = rawShortName.substring(8).trim();
        shortName = (ext.equals("")) ? prefix : prefix + "." + ext;
        name = shortName.toLowerCase();
    }

    /**
     * Return the short file name in its raw form (11 chars without '.')
     */
    String getRawShortName() { return rawShortName; }

    /**
     * Return the name of this entry.
     */
    String getName() { return name; }

    /**
     * Return the short name of this entry
     */
    String getShortName() { return shortName; }

    /**
     * Return the long name of this entry
     */
    String getLongName() { return longName; }

   /**
     * Calculate the short name checksum using the given string.
     */
    private int calculateChecksum(String s) throws IOException {
        if (s.length() != 11) { throw new IllegalArgumentException(); }
        int cs = 0;
        for (int i = 0; i < 11; i++) {
            int signAfterRightShift = ((cs & 1) != 0) ? 0x80 : 0x00;
            cs = (signAfterRightShift + (cs >>> 1) + s.charAt(i)) & 0xff;
        }
        return cs;
    }

    /**
     * Return true if this entry represents a directory
     */
    boolean isDirectory() { return isDirectory; }

    /**
     * Return true if this entry is free
     */
    boolean isFree() { return isFree; }

    /**
     * Return true if this entry represents a volume label
     */
    boolean isVolumeLabel() { return isVolumeLabel; }

    /**
     * Return the next entry in the chain
     */
    Entry next() { return next; }

    /**
     * Recurse through the entries in this list and return the first
     * file or directory entry matching the given name. Throw an io exception
     * if no match is found.
     */
    Entry resolve(String name) throws IOException {
        if (isDirectory || isFile) {
            if (name.equalsIgnoreCase(shortName)) { return this; }
            if ((longName != null) && (name.equalsIgnoreCase(longName))) {
                return this;
            }
        }
        if (next == null) { throw new FileNotFoundException(); }
        return next.resolve(name);
    }

    /**
     * Return the directory represented by this entry
     */
    Directory getDirectory() throws IOException {
        if (!isDirectory) { throw new IllegalStateException(); }
        if (directory == null) {
            directory = new ChainDirectory(getChain(), model, this);
        }
        return directory;
    }

    /**
     * Return the file represented by this entry
     */
    File getFile() throws IOException {
        if (!isFile) { throw new IllegalStateException(); }
        if (file == null) { file = new File(getChain(), this); }
        return file;
    }

    /**
     * Return the chain represented by this entry
     */
    Chain getChain() throws IOException {
        if (!isFile && !isDirectory) { throw new IllegalStateException(); }
        if (chain == null) { chain = new Chain(model, this, cluster); }
        return chain;
    }

    /**
     * Return the file size
     */
    int getSize() { return size; }

    /**
     * Set the file size
     */
    void setSize(int size) throws IOException {
        data.set32(size, offset + 28);
        this.size = size;
    }

    /**
     * Set the last modified timestamp for this entry
     */
    void setLastModified(long millis) throws IOException {
        data.set16(Util.timeStamp(millis), offset + 22);
        data.set16(Util.dateStamp(millis), offset + 24);
    }

    /**
     * Return the last modified timestamp for this entry
     */
    long getLastModified() throws IOException {
        int timeStamp = data.get16(offset + 22);
        int dateStamp = data.get16(offset + 24);
        return Util.convertToMillis(timeStamp, dateStamp);
    }

    /**
     * Delete this entry by setting it to free
     */
    void delete() throws IOException {
        initial = 0xe5;
        data.set8(initial, offset);
        isFree = true;
        isLong = false;
        isFile = false;
        isDirectory = false;
        isVolumeLabel = false;
    }

    /**
     * Set the raw short name
     */
    void setRawShortName(String raw) throws IOException {
        if (raw.length() != 11) { throw new IllegalArgumentException(); }
        checkShortNameChars(raw);
        initial = raw.charAt(0) & 0xff;
        if (initial == 0xe5) { initial = 0x05; }
        data.set8(initial, offset);
        for (int i = 1; i < 11; i++) {
            data.set8(raw.charAt(i) & 0xff, offset + i);
        }
    }

    /**
     * Check that the characters in the given string are valid
     * short name characters. Throw an io exception if any
     * invalid characters are found.
     */
    private static void checkShortNameChars(String name) throws IOException {
        String special = "$%'-_@~`!(){}^#& ";
        for (int i = 0, n = name.length(); i < n; i++) {
            char c = name.charAt(i);
            if ((c < 'A' || c > 'Z') && (special.indexOf(c) == -1)) {
                throw new IOException("Invalid character: " + c);
            }
        }
    }

    /**
     * Set the cluster number for this entry
     */
    void setCluster(int cluster) throws IOException {
        data.set16(cluster, offset + 26);
        this.cluster = cluster;
    }

}










