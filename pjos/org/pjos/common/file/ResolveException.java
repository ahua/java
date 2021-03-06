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
package org.pjos.common.file;

/**
 * This exception can be thrown when a file path cannot be resolved.
 */
public final class ResolveException extends Exception {

    /**
     * Create a ResolveException with message of null
     */
    public ResolveException() {
        super();
    }
    
    /**
     * Create a ResolveException with the specified message
     * @param message the error message
     */
    public ResolveException(String message) {
        super(message);
    }
    
    /**
     * Create a ResolveException with the specified message and cause
     * @param message the error message
     * @param cause the underlying cause
     */
    public ResolveException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Create a ResolveException with the specified cause
     * @param cause the underlying cause
     */
    public ResolveException(Throwable cause) {
        super(cause);
    }

}
