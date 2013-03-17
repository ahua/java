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
package java.lang;

import java.io.Serializable;

import org.pjos.common.runtime.Frame;
import org.pjos.common.runtime.Method;
import org.pjos.common.runtime.Type;

/**
 * Implementation of java.lang.StackTraceElement based on Sun specification.
 */
public final class StackTraceElement implements Serializable {
    
    /** The file name */
    private String fileName = null;
    
    /** The class name */
    private String className;
    
    /** The method name */
    private String methodName;
    
    /** The native method flag */
    private boolean isNativeMethod;
    
    /** The line number */
    private int lineNumber = -1;
    
    /** The description */
    private String description = null;
    
    /** The hash code */
    private int hashCode = -1;
    
    /**
     * Create a stack trace element. All internal string
     * fields must be set to interned strings or null
     * so that the equals method works correctly.
     * @param frame the frame
     */
    StackTraceElement(Frame frame) {
        Method method = frame.getMethod();
        Type type = method.getOwner();
        
        // set fields
        fileName = type.getSource();
        className = type.getPeer().getName();
        methodName = method.getName();
        isNativeMethod = method.isNative();
        lineNumber = getLineNumber(frame, method);
    }
    
    /**
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * @return the class name
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }
    
    /**
     * @return true if the method is native
     */
    public boolean isNativeMethod() {
        return isNativeMethod;
    }
    
    /**
     * @return a string description of this element.
     */
    public String toString() {
        if (description == null) {
            String line = (lineNumber != -1) ? ":" + lineNumber : "";
            String source = (fileName != null) ? fileName + line : null;
            if (isNativeMethod) {
                source = "Native Method";
            } else if (source == null) {
                source = "Unknown Source";
            }
            description = className + "." + methodName + "(" + source + ")";
        }
        return description;
    }
    
    /**
     * Test for equality
     * @param o the object to test
     * @return true if equal
     */
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof StackTraceElement)) { return false; }
        StackTraceElement ste = (StackTraceElement) o;
        return (fileName == ste.fileName)
            && (lineNumber == ste.lineNumber)
            && (className == ste.className)
            && (methodName == ste.methodName);
    }
    
    /**
     * @return a hash code for this stack trace element
     */
    public int hashCode() {
        if (hashCode == -1) {
            hashCode = 17;
            hashCode = (37 * hashCode) + fileName.hashCode();
            hashCode = (37 * hashCode) + lineNumber;
            hashCode = (37 * hashCode) + className.hashCode();
            hashCode = (37 * hashCode) + methodName.hashCode();
        }
        return hashCode;
    }
    
    /**
     * Extract the line number of the current instruction, return
     * -1 if there is no valid line number found.
     */
    private int getLineNumber(Frame frame, Method method) {
        int pc = frame.getPc();
        short[] lineNumbers = method.getLineNumbers();
        if (lineNumbers == null) { return -1; }
        short result = -1;
        for (int i = 0, n = lineNumbers.length; i < n; ) {
            short start = lineNumbers[i++];
            short line = lineNumbers[i++];
            if (pc >= start) { result = line; }
            if (pc < start) { return result; }
        }
        return result;
    }
    
}
