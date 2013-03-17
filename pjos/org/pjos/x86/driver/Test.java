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
package org.pjos.x86.driver;

/**
 * This class is used to test if simple class loading is working
 */
class Test {

    /**
     * The test message
     */
    static String message = Test.class + " loaded successfully";

    /**
     * Create a test object
     */
    Test() {
        // nothing to do here
    }

    /**
     * The correct message will only be returned if the
     * class initialiser was run.
     * @return the test message
     */
    public String toString() {
        return message;
    }

}
