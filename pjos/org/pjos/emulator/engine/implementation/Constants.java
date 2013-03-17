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
package org.pjos.emulator.engine.implementation;

/**
 * Extends runtime constants to add emulator specific constant definitions.
 */
public interface Constants extends
        org.pjos.common.runtime.Constants,
        org.pjos.emulator.driver.Constants
{
    
    /** Magic method id for Core.debug(String) */
    int MAGIC_RUNTIME_CORE_DEBUG = 1;
    
    /** Magic method id for Core.executeStatic(Method) */
    int MAGIC_RUNTIME_CORE_EXECUTE_STATIC = 2;
    
    /** Magic method id for Core.get() */
    int MAGIC_RUNTIME_CORE_GET = 3;
    
    /** Magic method id for Core.getType(Class) */
    int MAGIC_RUNTIME_CORE_GET_TYPE = 4;
    
    /** Magic method id for Frame.currentFrame() */
    int MAGIC_RUNTIME_FRAME_CURRENT = 5;
    
    /** Magic method id for Idle.sleep() */
    int MAGIC_RUNTIME_IDLE_SLEEP = 6;
    
    /** Magic method id for Statics.create(Type) */
    int MAGIC_RUNTIME_STATICS_CREATE = 7;
    
    /** Magic method id for Thread.currentThread() */
    int MAGIC_RUNTIME_THREAD_CURRENT = 8;
    
    /** Magic method id for Thread.sleep(long, int) */
    int MAGIC_RUNTIME_THREAD_SLEEP = 9;
    
    /** Magic method id for Thread.start() */
    int MAGIC_RUNTIME_THREAD_START_HOOK = 10;
    
    /** Magic method id for Thread.suspend() */
    int MAGIC_RUNTIME_THREAD_SUSPEND = 11;
    
    /** Magic method id for Thread.resume() */
    int MAGIC_RUNTIME_THREAD_RESUME = 12;


    /** Magic method id for Object.getClass() */
    int MAGIC_JAVA_OBJECT_GET_CLASS = 13;

    /** Magic method id for Object.notify() */
    int MAGIC_JAVA_OBJECT_NOTIFY = 14;

    /** Magic method id for Object.notifyAll() */
    int MAGIC_JAVA_OBJECT_NOTIFY_ALL = 15;

    /** Magic method id for Object.wait(long, int) */
    int MAGIC_JAVA_OBJECT_WAIT = 16;

    /** Magic method id for System.currentTimeMillis() */
    int MAGIC_JAVA_SYSTEM_TIME = 17;

    /** Magic method id for System.identityHashCode(Object) */
    int MAGIC_JAVA_SYSTEM_HASHCODE = 18;

    /** Magic method id for System.setErr(PrintStream) */
    int MAGIC_JAVA_SYSTEM_SET_ERR = 19;

    /** Magic method id for System.setOut(PrintStream) */
    int MAGIC_JAVA_SYSTEM_SET_OUT = 20;

    /** Magic method id for System.setIn(InputStream) */
    int MAGIC_JAVA_SYSTEM_SET_IN = 21;


    /** Magic method id for Emulator.writeToConsole(I)V */
    int MAGIC_EMULATOR_WRITE_CONSOLE = 22;

    /** Magic method id for Emulator.readFromFloppy(I)I */
    int MAGIC_EMULATOR_READ_FLOPPY = 23;
    
    /** Magic method id for Emulator.writeToFloppy(II)V */
    int MAGIC_EMULATOR_WRITE_FLOPPY = 24;
    
    /** Magic method id for Emulator.nextInterrupt()I */
    int MAGIC_EMULATOR_NEXT_INTERRUPT = 25;
    
    /** Magic method id for Emulator.readFromKeyboard()I */
    int MAGIC_EMULATOR_READ_FROM_KEYBOARD = 26;
    
}









