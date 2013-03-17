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

/**
 * Initialises frame nodes
 */
class FrameInitialiser extends AbstractInitialiser {
    
    /**
     * Create a frame initialiser
     */
    FrameInitialiser(Creator creator) {
        super(creator);
    }
    
    /**
     * Initialise a node
     */
    public boolean init(Node node, Object key) {
        if (!(key instanceof Frame)) { return false; }
        Frame f = (Frame) key;
        int frameSize = f.getFrameSize();
        
        node.setHeader(HEADER_STACK_FRAME);
        node.addPointer(getCreator().getType("org/pjos/common/runtime/Frame"));
        node.addPointer(null);           // no return frame
        node.addPointer(f.getMethod());
        node.addData(0);                 // return PC (will never be used)
        node.addData(0);                 // PC set to beginning of method
        node.addData(8 * frameSize + 4 * FRAME_LOCALS); // empty stack
        node.add(new InstanceSlot(getCreator(), f.getInstance()));
        node.addData(TRUE);              // reference flag for first local
        for (int i = 1; i < frameSize; i++) {
            node.addData(0);             // entry value
            node.addData(FALSE);         // entry reference flag
        }
        return true;
    }

}








