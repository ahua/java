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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;

import org.pjos.common.runtime.Frame;

/**
 * Implementation of java.lang.Throwable based on Sun specification.
 */
public class Throwable implements Serializable {

    /** The message */
    private String message;

    /** The cause */
    private Throwable cause;

    /** The initialised flag */
    private boolean initialised;

    /** The stack trace data */
    private StackTraceElement[] stackTrace;

    /**
     * Create a throwable
     */
    public Throwable() {
        fillInStackTrace();
    }

    /**
     * Create a throwable
     * @param message the error message
     */
    public Throwable(String message) {
        this.message = message;
        fillInStackTrace();
    }

    /**
     * Create a throwable
     * @param message the error message
     * @param cause the exception that is the underlying cause
     */
    public Throwable(String message, Throwable cause) {
        this.message = message;
        initCause(cause);
        fillInStackTrace();
    }

    /**
     * Create a throwable
     * @param cause the exception that is the underlying cause
     */
    public Throwable(Throwable cause) {
        this((cause == null) ? null : cause.toString(), cause);
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return localised description
     */
    public String getLocalizedMessage() {
        return getMessage();
    }

    /**
     * @return the cause
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * Set the cause
     * @param cause the exception that is the underlying cause
     * @return a reference to this exception
     */
    public Throwable initCause(Throwable cause) {
        if (initialised) { throw new IllegalStateException(); }
        if (cause == this) { throw new IllegalArgumentException(); }
        this.cause = cause;
        initialised = true;
        return this;
    }

    /**
     * @return a string description
     */
    public String toString() {
        String result = getClass().getName();
        if (message != null) {
            result += ": " + getMessage();
        }
        return result;
    }

    /**
     * Print the stack trace to the standard error stream
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Print the stack trace to the given stream
     * @param s the print stream
     */
    public void printStackTrace(PrintStream s) {
        printStackTrace(new PrintWriter(s), null);
    }

    /**
     * Print the stack trace using the given writer
     * @param s the writer
     */
    public void printStackTrace(PrintWriter s) {
        printStackTrace(s, null);
    }

    /**
     * Print the stack trace using the given writer, and abbreviating
     * lines referring to elements from the given stack trace.
     */
    private void printStackTrace(PrintWriter s, StackTraceElement[] trace) {
        // find out how many elements match the bottom of the given trace
        StackTraceElement[] elements = stackTrace;
        int matching = (trace != null) ? getMatching(elements, trace) : 0;

        // print class name and message
        s.println(toString());

        // print elements in the stack
        for (int i = 0, n = elements.length - matching; i < n; i++) {
            s.print("        at ");
            s.println(elements[i].toString());
        }
        if (matching > 0) {
            s.println("        ... " + matching + " more");
        }

        // print cause traces recursively
        if (cause != null) {
            s.print("Caused by: ");
            cause.printStackTrace(s, elements);
        }
    }

    /**
     * Return the number of elements in stack A that match the
     * elements on the bottom of stack B
     */
    private int getMatching(StackTraceElement[] sta, StackTraceElement[] stb) {
        // skip to first matching element
        int a = sta.length - 1;
        int b = stb.length - 1;
        StackTraceElement element = sta[a];
        while (b >= 0 && !stb[b].equals(element)) {
            b--;
        }

        // count matching elements
        int result = 0;
        while (b >= 0 && stb[b].equals(sta[a])) {
            result++;
            a--;
            b--;
        }
        return result;
    }

    /**
     * Fill in the execution stack trace
     */
    public void fillInStackTrace() {
        // Skip frames until constructors reached
        Frame frame = Frame.currentFrame();
        Class thisClass = getClass();
        Class methodClass = getMethodClass(frame);
        while (frame != null && thisClass != methodClass) {
            frame = frame.getReturnFrame();
            methodClass = getMethodClass(frame);
        }

        // Skip constructors
        while (frame != null && thisClass == methodClass) {
            frame = frame.getReturnFrame();
            methodClass = getMethodClass(frame);
        }

        // Skip Core class method (used by system to throw exceptions)
        String methodTypeName = getMethodTypeName(frame);
        while (frame != null && methodTypeName == "vm/runtime/Core") {
            frame = frame.getReturnFrame();
            methodTypeName = getMethodTypeName(frame);
        }

        // now fill in the trace
        fillInStackTrace(frame);

        // for debug purposes: print the entire stack trace
        //fillInStackTrace(Frame.currentFrame().getReturnFrame());
    }

    /**
     * Return the class of the method for the given frame
     */
    private Class getMethodClass(Frame frame) {
        if (frame == null) { return null; }
        return frame.getMethod().getOwner().getPeer();
    }

    /**
     * Return the type name of the method for the given frame
     */
    private String getMethodTypeName(Frame frame) {
        if (frame == null) { return null; }
        return frame.getMethod().getOwner().getName();
    }

    /**
     * Fill in the execution stack trace from the given frame
     */
    private void fillInStackTrace(Frame frame) {
        int length = frame.getStackSize();
        StackTraceElement[] ste = new StackTraceElement[length];
        for (int i = 0; i < length; i++) {
            ste[i] = new StackTraceElement(frame);
            frame = frame.getReturnFrame();
        }
        stackTrace = ste;
    }

    /**
     * @return stack trace information
     */
    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    /**
     * Set the stack trace information
     * @param stackTrace the stack trace information
     */
    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }

}
