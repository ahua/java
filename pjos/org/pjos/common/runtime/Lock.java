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
 * Used to synchronise threads calling synchronised code on an object.
 */
class Lock {

    /** The current owner of the lock */
    Thread owner;
    
    /** Holds the number of times the owner has obtained this lock */
    int count;
    
    /** The head of the list of threads waiting to obtain this lock */
    Thread lockHead;
    
    /** The tail of the list of threads waiting to obtain this lock */
    Thread lockTail;
    
    /** The head of the list of threads waiting for notification */
    Thread waitHead;
    
    /** The tail of the list of threads waiting for notification */
    Thread waitTail;

}
