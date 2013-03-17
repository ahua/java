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
package org.pjos.common.runtime;

/**
 * Method stack frames are instances of this class.
 */
public final class Frame {
    
    /**
     * Should only be instantiated by system!
     */
    private Frame() {
        // this method should never be called
    }

    /** The return frame */
    Frame returnFrame;

    /** The method using this frame */
    Method method;
    
    /** The return pc value */
    int returnPc;

    /** The program counter value */
    int pc;

    /** The stack pointer value */
    int sp;

    // local variables... (determined by system)

    // stack space... (determined by system)
    
    /**
     * @return the frame of the current method
     */
    public static native Frame currentFrame();
    
    /**
     * @return the return frame
     */
    public Frame getReturnFrame() {
        return returnFrame;
    }
    
    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }
    
    /**
     * @return the return pc
     */
    public int getReturnPc() {
        return returnPc;
    }
    
    /**
     * @return the pc
     */
    public int getPc() {
        return pc;
    }
    
    /**
     * @return the sp
     */
    public int getSp() {
        return sp;
    }
    
    /**
     * Count the frames in the stack consisting of this frame
     * and the return frames underneath it.
     * @return the number of frames
     */
    public int getStackSize() {
        int result = 1;
        Frame rf = returnFrame;
        while (rf != null) {
            result++;
            rf = rf.returnFrame;
        }
        return result;
    }
    
}










