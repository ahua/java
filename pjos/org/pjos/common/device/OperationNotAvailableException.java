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
package org.pjos.common.device;

import java.io.IOException;

/**
 * This exception can be thrown if an operation is not supported
 */
public final class OperationNotAvailableException extends IOException {

    /**
     * Create a OperationNotAvailableException with message of null
     */
    public OperationNotAvailableException() {
        super();
    }
    
    /**
     * Create a OperationNotAvailableException with the specified message
     * @param message the error message
     */
    public OperationNotAvailableException(String message) {
        super(message);
    }
    
    /**
     * Create a OperationNotAvailableException with the specified message
     * and cause
     * @param message the error message
     * @param cause the underlying cause
     */
    public OperationNotAvailableException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
    
    /**
     * Create a OperationNotAvailableException with the specified cause
     * @param cause the underlying cause
     */
    public OperationNotAvailableException(Throwable cause) {
        initCause(cause);
    }

}
