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
package org.pjos.test.driver;

import java.io.InputStream;

/**
 * Used to read ascii characters from the system console.
 */
public final class ConsoleInputStream extends InputStream {
    
    /**
     * Create a console input stream
     */
    ConsoleInputStream() {
        // nothing to do here
    }
    
    /**
     * Read an unsigned byte value from the console
     * @return the unsigned byte value
     */
    public int read() {
        return TestArchitecture.readFromConsole();
    }

}

