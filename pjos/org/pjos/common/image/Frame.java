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

import org.w3c.dom.Node;

import org.pjos.common.runtime.Method;
import org.pjos.common.runtime.Type;

/**
 * Contains the information for a method stack frame
 */
class Frame {
    
    /** The name */
    private String name;
    
    /** The method */
    private Method method;
    
    /** The frame size */
    private int frameSize;
    
    /** The instance name */
    private String instance;
    
    /**
     * Create a frame using the data in the given node
     * @param node the node
     * @param creator the creator
     */
    Frame(Node node, Creator creator) {
        // read attributes
        name = Creator.getAttribute("name", node);
        String classname = Creator.getAttribute("class", node);
        String methodName = Creator.getAttribute("method", node);
        instance = Creator.getAttribute("instance", node);
        
        // get the method object from its type
        Type type = creator.getType(classname);
        if (type == null) {
            throw new IllegalStateException("Class not found: " + classname);
        }
        // only void-type methods are supported!
        method = type.getMethod(methodName, "()V");
        frameSize = method.getMaxStack() + method.getMaxLocals();
    }
    
    /**
     * @return the name
     */
    String getName() {
        return name;
    }
    
    /**
     * @return the method
     */
    Method getMethod() {
        return method;
    }
    
    /**
     * @return the frame size
     */
    int getFrameSize() {
        return frameSize;
    }
    
    /**
     * @return the instance name
     */
    String getInstance() {
        return instance;
    }
    
}








