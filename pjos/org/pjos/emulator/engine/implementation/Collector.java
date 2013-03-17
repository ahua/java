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
package org.pjos.emulator.engine.implementation;

/**
 * Contains code for garbage collection
 */
class Collector implements Constants {

    /** The from space */
    private static int fromSpace;
    
    /** The next pointer */
    private static int next;
    
    /** The scan pointer */
    private static int scan;
    
    /** The number of objects evacuated so far */
    private static int count;
    
    /**
     * Collect garbage. This copies all the live objects from one
     * semispace to the other.
     */
    static synchronized void gc() {
        // save register values
        Mem.store(Reg.instruction - Reg.code, Reg.frame + 4 * FRAME_PC);
        Mem.store(Reg.stack - Reg.frame, Reg.frame + 4 * FRAME_SP);
        
        // flip spaces so core reg now points to new space
        fromSpace = Reg.core;
        Reg.core = (Reg.core == Mem.OFFSET)
                ? Mem.OFFSET + Mem.LIMIT
                : Mem.OFFSET;

        // set next and scan pointers
        next =  Reg.core;
        scan = next;
        
        // scan live object tree starting with the core object
        // until all live objects have been evacuated
        // (ie. until scan pointer catches up to next pointer)
        evacuate(fromSpace);
        count = 0; // reset count
        while (scan < next) {
            // check the lock and type
            check(OBJECT_LOCK);
            check(OBJECT_TYPE);

            // Check all other references in the object
            int header = Mem.load(scan);
            switch (header) {
                case HEADER_INSTANCE:           checkInstanceFields();  break;
                case HEADER_DATA_ARRAY:                                 break;
                case HEADER_OBJECT_ARRAY:       checkObjectArray();     break;
                case HEADER_STATIC_FIELDS:      checkStaticFields();    break;
                case HEADER_STACK_FRAME:        checkStackFrame();      break;
            }
        
            // set scan to next object
            int numWords = Mem.load(scan + 4);
            scan += numWords * 4;
        }

        // write the address of the next free space to core object
        Mem.store(next, Reg.core + 4 * CORE_NEXT);

        // debug report
        System.out.println("Garbage collected: " + count
                + " objects evacuated, " + Allocate.used() + "% used");
        
        // reload registers for new memory space
        Reg.thread = Mem.load(Reg.core + 4 * CORE_RUNNING);
        Reg.load();
    }
    
    /**
     * Check the reference values in an object array
     */
    private static void checkObjectArray() {
        int numWords = Mem.load(scan + 4);
        for (int i = ARRAY_DATA; i < numWords; i++) {
            check(i);
        }
    }
    
    /**
     * Check the values in an instance object
     */
    private static void checkInstanceFields() {
        int type = Mem.load(scan + 4 * OBJECT_TYPE);
        int map = Mem.load(type + 4 * TYPE_INSTANCE_MAP);
        int nextFlagAddress = map + 4 * ARRAY_DATA;
        int numWords = Mem.load(scan + 4);
        for (int i = OBJECT_FIELDS; i < numWords; i++) {
            int flag = Mem.loadByte(nextFlagAddress);
            if (flag == TRUE) { check(i); }
            nextFlagAddress++;
        }
    }
    
    /**
     * Check the values in a static fields object
     */
    private static void checkStaticFields() {
        check(STATICS_MAP);
        
        // check field values
        int map = Mem.load(scan + 4 * STATICS_MAP);
        int nextFlagAddress = map + 4 * ARRAY_DATA;
        int numWords = Mem.load(scan + 4);
        for (int i = STATICS_FIELDS; i < numWords; i++) {
            int flag = Mem.loadByte(nextFlagAddress);
            if (flag == TRUE) { check(i); }
            nextFlagAddress++;
        }
    }
    
    /**
     * Check the fields of a stack frame object
     */
    private static void checkStackFrame() {
        check(FRAME_RETURN_FRAME);
        check(FRAME_METHOD);

        // check tagged values
        int numWords = Mem.load(scan + 4);
        for (int i = FRAME_LOCALS; i < numWords; i += 2) {
            int flag = Mem.load(scan + 4 * i + 4);
            if (flag == TRUE) { check(i); }
        }
    }

    /**
     * Check the pointer at the specified index within the current scan object.
     * Evacuate the target object if it has not already been moved evacuated,
     * otherwise just update the pointer value to the new location. Ignore
     * null pointer values.
     */
    private static void check(int index) {
        // ignore null values
        int target = Mem.load(scan + 4 * index);
        if (target == NULL) { return; }

        // find the new location of the target object, evacuating if necessary,
        // and update the value in the pointer
        int header = Mem.load(target);
        int newLocation = (header == HEADER_FORWARD)
                ? Mem.load(target + 4)
                : evacuate(target);
        Mem.store(newLocation, scan + 4 * index);
    }
    
    /**
     * Evacuate the object at the specified location in from-space to
     * the next available location in to-space.
     * @param location the object address before evacuation
     * @return the object address after evacuation
     */
    static int evacuate(int location) {
        if (location == NULL) { throw new IllegalArgumentException(); }

        // copy each word
        int result = next;
        int numWords = Mem.load(location + 4);
        int numBytes = numWords * 4;
        for (int i = 0; i < numBytes; i += 4) {
            int value = Mem.load(location + i);
            Mem.store(value, result + i);
        }
        
        // set forwarding header and pointer
        Mem.store(HEADER_FORWARD, location);
        Mem.store(result, location + 4);
        
        // set the next pointer to the next available space and increment count
        next += numBytes;
        count++;
        return result;
    }
    
    
}









