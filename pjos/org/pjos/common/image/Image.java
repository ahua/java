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
package org.pjos.common.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.ByteOrder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An image is used to generate an executable org.pjos.common.image
 */
class Image {
    
    /** The byte order */
    private ByteOrder order;
    
    /** The physical address */
    private int physical;
    
    /** The next available addres */
    private int next;
    
    /** The list of allocations */
    private List allocations = new LinkedList();

    /**
     * Create an image
     * @param physical the physical address
     * @param order the byte order
     */
    Image(int physical, ByteOrder order) {
        this.physical = physical;
        this.order = order;
        next = physical;
    }
    
    /**
     * @param slots the slots
     * @return an allocation for the given slots
     */
    synchronized Allocation allocate(Slot[] slots) {
        Allocation result = new Allocation(this, next, slots);
        next += result.numBytes();
        allocations.add(result);
        return result;
    }
    
    /**
     * Generate a byte array containing the binary image
     * @return the generated array
     * @throws IOException if an error occurs
     */
    synchronized byte[] generate() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Iterator it = allocations.iterator(); it.hasNext();) {
            Allocation a = (Allocation) it.next();
            a.writeTo(out);
        }
        return out.toByteArray();
    }
    
    /**
     * @return the next available address
     */
    int next() {
        return next;
    }
    
    /**
     * Write the given int to the given output stream
     * using the correct byte order
     * @param slot the slot
     * @param out the output stream
     * @throws IOException if an error occurs
     */
    void writeSlot(Slot slot, OutputStream out) throws IOException {
        int value = slot.getValue();
        SlotType st = slot.getType();
        if (st == SlotType.WORD) {
            writeInt(slot.getValue(), out);
        } else if (st == SlotType.SHORT) {
            writeShort(value >>> 16, out);
            writeShort(value, out);
        } else if (st == SlotType.BYTE) {
            out.write(value >>> 24);
            out.write(value >>> 16);
            out.write(value >>> 8);
            out.write(value);
        }
    }
    
    /**
     * Write the given int to the given output stream
     * using the correct byte order
     */
    private void writeInt(int value, OutputStream out) throws IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            out.write(value >>> 24);
            out.write(value >>> 16);
            out.write(value >>> 8);
            out.write(value);
        } else {
            out.write(value);
            out.write(value >>> 8);
            out.write(value >>> 16);
            out.write(value >>> 24);
        }
    }
    
    /**
     * Write the given short to the given output stream
     * using the correct byte order.
     */
    private void writeShort(int value, OutputStream out) throws IOException {
        if (order == ByteOrder.BIG_ENDIAN) {
            out.write(value >>> 8);
            out.write(value);
        } else {
            out.write(value);
            out.write(value >>> 8);
        }
    }

}





