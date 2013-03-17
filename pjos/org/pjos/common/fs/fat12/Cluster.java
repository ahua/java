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

/**
 * Represents one link in a cluster chain.
 *
 * Not safe for use by multiple threads.
 */
class Cluster extends Data {
    
    /** The model */
    private Model model;
    
    /** The cluster number */
    private int number;
    
    /** The block containing the data for this cluster */
    private Block block;

    /** The size of the cluster chain beginning with this cluster */ 
    private int size;

    /**
     * Create a cluster
     */
    Cluster(Model model, int number, Block block) {
        this.model = model;
        this.number = number;
        this.block = block;
    }

    /**
     * Return the size (number of bytes) of this chain.
     */
    int getSize() { return block.getSize(); }

    /**
     * Set the size of this cluster
     */
    void setSize(int size) {
        throw new IllegalStateException(); // clusters are a fixed size
    }
    
    /**
     * Set the unsigned 8-bit value at the specified offset.
     */
    void set8(int value, int offset) throws IOException {
        block.set8(value, offset);
    }
    
    /**
     * Return the unsigned 8-bit value at the specified offset.
     */
    int get8(int offset) throws IOException {
        return block.get8(offset);
    }
    
    /**
     * Return the cluster number
     */
    int getNumber() { return number; }

    /**
     * Set the next cluster in the chain to the specified cluster number
     */
    void setNextCluster(int nextClusterNumber) throws IOException {
        model.setFatEntry(nextClusterNumber, number);
    }

    /**
     * Mark this cluster as free
     */
    void setFree() throws IOException {
        model.setFatEntry(number, 0x00);
    }
    
}










