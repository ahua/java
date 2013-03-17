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
 * This object maintains a model of the floppy data.
 *
 * Not safe for use by multiple threads.
 */
class Model {

    /** The number of bytes on a 1.4MB floppy */
    private static final long SIZE = 1024L * 1440L;

    /** The underlying storage device */
    private Storage storage;

    /** The sectors */
    private Sector[] sectors;

    /** The clusters */
    private Cluster[] clusters;

    /** The number of bytes per sector */
    private int bytesPerSector;

    /** The number of sectors per cluster */
    private int sectorsPerCluster;

    /** The number of bytes per cluster */
    private int bytesPerCluster;

    /** The number of reserved sectors */
    private int numReservedSectors;

    /** The number of fat tables */
    private int numFatTables;

    /** The number of entries in the root directory */
    private int numRootEntries;

    /** The total number of sectors */
    private int totalSectors;

    /** The number of sectors in each fat table */
    private int numFatSectors;

    /** The number of sectors in the root directory */
    private int numRootSectors;

    /** The index of the first sector in the root directory */
    private int firstRootSector;

    /** The index of the first data sector */
    private int firstDataSector;

    /** The number of valid clusters */
    private int numClusters;

    /** The root directory block */
    private Block rootBlock;

    /** The reserved block */
    private Block reservedBlock;

    /** The fat blocks */
    private Block[] fatBlocks;

    /**
     * Create a model for the given storage device
     * @param storage the storage
     * @throws IOException if an error occurs
     */
    Model(Storage storage) throws IOException {
        if (storage.getSize() != SIZE) {
            throw new IOException("Device size invalid");
        }
        this.storage = storage;
        readSettings();
    }

    /**
     * Read the settings from the bootsector.
     */
    private void readSettings() throws IOException {
        // create a temporary sector used to read the boot sector
        Sector bs = new Sector(storage, 0, 512);
        bs.load();

        // read the values
        bytesPerSector = bs.get16(11);
        sectorsPerCluster = bs.get8(13);
        bytesPerCluster = bytesPerSector * sectorsPerCluster;
        numReservedSectors = bs.get16(14);
        numFatTables = bs.get8(16);
        numRootEntries = bs.get16(17);
        numFatSectors = bs.get16(22);
        int totalSectors16 = bs.get16(19);
        int totalSectors32 = bs.get32(32);
        totalSectors = (totalSectors16 != 0) ? totalSectors16 : totalSectors32;

        // check values
        if (bytesPerSector != 512
                && bytesPerSector != 1024
                && bytesPerSector != 2048
                && bytesPerSector != 4096)
        {
            throw new IOException("Invalid format");
        }
        if (sectorsPerCluster != 1
                && sectorsPerCluster != 2
                && sectorsPerCluster != 4
                && sectorsPerCluster != 8
                && sectorsPerCluster != 16
                && sectorsPerCluster != 32
                && sectorsPerCluster != 64
                && sectorsPerCluster != 128)
        {
            throw new IOException("Invalid format");
        }
        if (numReservedSectors <= 0) {
            throw new IOException("Invalid format");
        }
        if (numFatTables <= 0) { throw new IOException("Invalid format"); }
        long totalBytes = (long) (totalSectors * bytesPerSector);
        if (totalBytes != SIZE) { throw new IOException("Invalid format"); }

        // check the number of clusters, must be within valid range for fat12
        numRootSectors =
                ((numRootEntries * 32) + (bytesPerSector - 1)) / bytesPerSector;
        firstRootSector = numReservedSectors + (numFatTables * numFatSectors);
        firstDataSector = firstRootSector + numRootSectors;
        int numDataSectors = totalSectors - firstDataSector;
        numClusters = numDataSectors / sectorsPerCluster;
        if (numClusters <= 0 || numClusters >= 4085) {
            throw new IOException("Invalid format");
        }
    }

    /**
     * Retrieve the sector with the specified index
     */
    private Sector getSector(int index) {
        if (index < 0 || index >= totalSectors) {
            throw new IllegalArgumentException();
        }
        if (sectors == null) { sectors = new Sector[totalSectors]; }
        Sector result = sectors[index];
        if (result == null) {
            result = new Sector(
                    storage,
                    index * bytesPerSector,
                    bytesPerSector);
            sectors[index] = result;
        }
        return result;
    }

    /**
     * @return a data object representing the reserved area
     */
    Data getReservedData() {
        if (reservedBlock == null) {
            Sector[] s = new Sector[numReservedSectors];
            for (int i = 0; i < numReservedSectors; i++) {
                s[i] = getSector(i);
            }
            reservedBlock = new Block(this, s);
        }
        return reservedBlock;
    }

    /**
     * @return a data object representing the root directory
     */
    Data getRootData() {
        if (rootBlock == null) {
            Sector[] s = new Sector[numRootSectors];
            for (int i = 0; i < numRootSectors; i++) {
                s[i] = getSector(firstRootSector + i);
            }
            rootBlock = new Block(this, s);
        }
        return rootBlock;
    }

    /**
     * Store all changed sectors to the underlying device
     * @throws IOException if an error occurs
     */
    void store() throws IOException {
        for (int i = 0; i < totalSectors; i++) {
            Sector sector = sectors[i];
            if (sector != null && sector.changed()) { sector.store(); }
        }
    }

    /**
     * Return the unsigned 12-bit value at the specified index
     * in the FAT table (only read the first FAT).
     */
    int getFatEntry(int index) throws IOException {
        if (index < 0 || index > numClusters + 1) {
            throw new IllegalArgumentException();
        }
        if (fatBlocks == null) { createFatBlocks(); }
        int offset = (index / 2) * 3; // offset of 3-byte pair containing entry
        int pair = fatBlocks[0].get24(offset); // get entry pair
        if (index % 2 == 0) {
            return pair & 0xfff; // return first entry
        } else {
            return pair >> 12; // return second entry
        }
    }

    /**
     * Set the unsigned 12-bit value at the specified index
     * in the FAT table (set in both FATs).
     */
    void setFatEntry(int value, int index) throws IOException {
        if (index < 0 || index > numClusters + 1) {
            throw new IllegalArgumentException();
        }
        if (fatBlocks == null) { createFatBlocks(); }
        int offset = (index / 2) * 3; // offset of 3-byte pair containing entry
        int pair = fatBlocks[0].get24(offset); // get entry pair
        if (index % 2 == 0) {
            pair = (pair & 0x00fff000) | value; // replace first entry
        } else {
            pair = (pair & 0x00000fff) | (value << 12); // replace second entry
        }
        for (int i = 0; i < numFatTables; i++) {
            fatBlocks[i].set24(pair, offset);
        }
    }

    /**
     * Create the fat blocks
     */
    private void createFatBlocks() {
        fatBlocks = new Block[numFatTables];
        for (int i = 0; i < numFatTables; i++) {
            Sector[] s = new Sector[numFatSectors];
            for (int j = 0; j < numFatSectors; j++) {
                int first = numReservedSectors + (i * numFatSectors);
                s[j] = getSector(first + j);
            }
            fatBlocks[i] = new Block(this, s);
        }
    }

    /**
     * @return the first free cluster found
     * @throws IOException if an error occurs
     */
    Cluster getFreeCluster() throws IOException {
        for (int i = 0; i < numClusters; i++) {
            int number = i + 2;
            int entry = getFatEntry(number);
            if (entry == 0x00) { return getCluster(number); }
        }
        throw new IOException("Disk full");
    }

    /**
     * @param number the cluster number
     * @return the cluster object for the given number
     * @throws IOException if an error occurs
     */
    Cluster getCluster(int number) throws IOException {
        if (number >= 0xff8) {
            return null; // end of chain
        }
        if (number < 2 || number > numClusters + 1) {
            throw new IllegalArgumentException();
        }
        int index = number - 2;
        if (clusters == null) { clusters = new Cluster[numClusters]; }
        Cluster result = clusters[index];
        if (result == null) {
            Sector[] s = new Sector[sectorsPerCluster];
            int firstSector =  firstDataSector + (index * sectorsPerCluster);
            for (int i = 0; i < sectorsPerCluster; i++) {
                s[i] = getSector(firstSector + i);
            }
            Block block = new Block(this, s);
            result = new Cluster(this, number, block);
            clusters[index] = result;
        }
        return result;
    }

    /**
     * @param number the cluster number
     * @return the next cluster in the chain for the
     *         given cluster number.
     * @throws IOException if an error occurs
     */
    Cluster getNextCluster(int number) throws IOException {
        if (number < 2 || number > numClusters + 1) {
            throw new IllegalArgumentException();
        }
        int entry = getFatEntry(number);
        if (entry == number) {
            throw new IOException("Invalid format");
        }
        if (entry == 0x00) {
            // free clusters don't have next defined!
            throw new IllegalStateException(); 
        } else if (entry >= 0xff8) {
            return null; // end of chain
        } else {
            return getCluster(entry);
        }
    }

    /**
     * @return the number of bytes per cluster
     */
    int getBytesPerCluster() {
        return bytesPerCluster;
    }

    /**
     * @return the number of bytes per sector
     */
    int getBytesPerSector() {
        return bytesPerSector;
    }

}










