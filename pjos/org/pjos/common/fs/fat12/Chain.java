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

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a cluster chain.
 *
 * Not safe for use by multiple threads.
 */
class Chain extends Data {
    
    /** The model */
    private Model model;
    
    /** The parent entry for this chain */
    private Entry parent;

    /** The clusters this chain consists of */
    private List clusters = new LinkedList();

    /** The size of this chain in bytes */
    private int size;

    /** The cluster number of the first cluster in the chain */
    private int number;

    /** The number of bytes in a cluster */
    private int bytesPerCluster;
    
    /**
     * Create a cluster chain starting at the specified cluster number
     */
    Chain(Model model, Entry parent, int number) throws IOException {
        this.model = model;
        this.parent = parent;
        this.number = number;
        Cluster next = model.getCluster(number);
        while (next != null) {
            clusters.add(next);
            next = model.getNextCluster(next.getNumber());
        }
        bytesPerCluster = model.getBytesPerCluster();
        size = clusters.size() * bytesPerCluster;
    }
    
    /**
     * Return the size (number of bytes) of this chain.
     */
    int getSize() { return size; }

    /**
     * Return the cluster at the specified index
     */
    private Cluster getCluster(int index) {
        return (Cluster) clusters.get(index);
    }

    /**
     * Return the last cluster in the chain
     */
    private Cluster getLastCluster() {
        int count = clusters.size();
        return (count > 0) ? getCluster(count - 1) : null;
    }

    /**
     * Set the size of this chain to at least the given size.
     */
    void setSize(int min) throws IOException {
        // calculate the number of clusters required (round up)
        int required = (min + bytesPerCluster - 1) / bytesPerCluster;

        // free clusters no longer required
        int count = clusters.size();
        while (count > required) {
            Cluster last = (Cluster) clusters.remove(count - 1);
            last.setFree();
            count--;
        }

        // add new clusters
        while (count < required) {
            Cluster prev = getLastCluster();
            Cluster next = model.getFreeCluster();
            next.setNextCluster(0xFFF); // cluster is no longer free
            int nextNumber = next.getNumber();
            if (prev == null) {
                number = nextNumber;
                parent.setCluster(number);
            } else {
                prev.setNextCluster(nextNumber);
            }
            clusters.add(next);
            count++;
        }

        // ensure last cluster is end of chain
        Cluster last = getLastCluster();
        if (last == null) {
            number = 0xFFF;
        } else {
            last.setNextCluster(0xFFF);
        }

        // recalculate size
        size = count * bytesPerCluster;
    }
    
    /**
     * Set the unsigned 8-bit value at the specified offset.
     */
    void set8(int value, int offset) throws IOException {
        if (offset >= size) { throw new IOException(); }
        int cindex = offset / bytesPerCluster;
        int coffset = offset % bytesPerCluster;
        getCluster(cindex).set8(value, coffset);
    }
    
    /**
     * Return the unsigned 8-bit value at the specified offset.
     */
    int get8(int offset) throws IOException {
        if (offset >= size) { throw new IOException(); }
        int cindex = offset / bytesPerCluster;
        int coffset = offset % bytesPerCluster;
        return getCluster(cindex).get8(coffset);
    }

    /**
     * Return the number of the first cluster in the chain
     */
    int getFirstClusterNumber() { return number; }
    
}










