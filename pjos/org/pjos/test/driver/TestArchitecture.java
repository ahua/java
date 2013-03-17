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

import java.io.PrintStream;
import org.pjos.common.device.DeviceManager;
import org.pjos.common.file.DriveManager;
import org.pjos.common.fs.fat12.Floppy1440;
import org.pjos.common.runtime.Architecture;
import org.pjos.common.shell.Shell;

/**
 * Architecture implementation for the x86 platform.
 */
public final class TestArchitecture extends Architecture {

    /**
     * Create an instance
     */
    private TestArchitecture() {
        // nothing to do here
    }

    /**
     * This method will be called by the initialisation thread to do
     * platform-specific initialisation.
     * @throws Exception if something goes wrong
     */
    public static void init() throws Exception {
        // Set the singleton architecture instance
        Architecture.singleton = new TestArchitecture();

        // set up output streams
        PrintStream out = new PrintStream(new ConsoleOutputStream());
        System.setOut(out);
        System.setErr(out);
        System.out.println("Output Streams initialised");

        // set up system input stream
        System.setIn(new ConsoleInputStream());
        System.out.println("Input Stream initialised");

        // initialise floppy drive
        FloppyDevice fd = new FloppyDevice();
        DeviceManager.add("floppy", fd);
        DriveManager.add("floppy", new Floppy1440(fd));

        // test the class loading mechanism
        System.out.println("Testing class loading mechanism");
        Test test = new Test();
        System.out.println("test: " + test);

        // start shell without local echo
        Shell shell = new Shell(false);
        Thread thread = new Thread(shell, "Shell");
        thread.start();
    }

    /**
     * Write the given char value to the system console
     * @param c the character
     */
    public static native void writeToConsole(int c);

    /**
     * @return a char value read from the system console
     */
    public static native int readFromConsole();

    /**
     * Write an unsigned byte value to the floppy drive
     * @param pos the position to write to
     * @param value the unsigned byte value
     */
    public static native void writeToFloppy(int pos, int value);

    /**
     * Read an unsigned byte value from the floppy drive
     * @param pos the position to read from
     * @return the unsigned byte value read
     */
    public static native int readFromFloppy(int pos);

    /**
     * @return a key object for the next interrupt, null
     *         if there are no interrupts waiting.
     */
    public Object getNextInterrupt() {
        throw new UnsupportedOperationException();
    }

}

