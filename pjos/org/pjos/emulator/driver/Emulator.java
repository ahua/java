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
package org.pjos.emulator.driver;

import java.io.IOException;
import java.io.PrintStream;
import org.pjos.common.device.DeviceManager;
import org.pjos.common.file.DriveManager;
import org.pjos.common.fs.fat12.Floppy1440;
import org.pjos.common.runtime.Architecture;
import org.pjos.common.shell.Shell;

/**
 * Architecture implementation for the java emulator.
 */
public class Emulator extends Architecture implements Constants {

    /**
     * Create an instance
     */
    private Emulator() {
        // nothing to do here
    }

    /**
     * This method will be called by the initialisation thread to do
     * platform-specific initialisation.
     */
    public static void init() {
        // set the singleton architecture instance
        Architecture.singleton = new Emulator();

        // set up streams
        PrintStream out = new PrintStream(new ConsoleOutputStream());
        System.setOut(out);
        System.setErr(out);
        System.out.println("Output Streams initialised");

        // initialise emulator floppy drive
        try {
            FloppyDevice fd = new FloppyDevice();
            DeviceManager.add("floppy", fd);
            DriveManager.add("floppy", new Floppy1440(fd));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // test the class loading mechanism
        System.out.println("Testing class loading mechanism");
        Test test = new Test();
        System.out.println("test: " + test);

        // start keyboard handler, set input stream
        KeyboardHandler kh = new KeyboardHandler();
        Thread thread = new Thread(kh, "Keyboard Handler");
        thread.start();
        System.setIn(kh.getInputStream());

        // start the shell with local echo
        Shell shell = new Shell(true);
        thread = new Thread(shell, "Shell");
        thread.start();

        /*
        // run some tests
        try {
            TestRunner tr = new TestRunner();
            java.lang.Thread thread = new java.lang.Thread(tr, "TestRunner");
            thread.start();
        } catch (Throwable e) { e.printStackTrace(); }
        */
    }

    /**
     * Read the unsigned byte value from the floppy drive at the
     * specified position. The specified position must be within
     * the range [0, 1474560).
     * @param pos the absolute position on the floppy disk
     * @return the unsigned byte value read
     */
    public static native int readFromFloppy(int pos);

    /**
     * Write the given unsigned byte value to the floppy drive
     * at the specified position. The specified position must be within
     * the range [0, 1474560).
     * @param value the unsigned byte value
     * @param pos the absolute position on the floppy disk
     */
    public static native void writeToFloppy(int value, int pos);

    /**
     * Write the given char value to the system console
     * @param c the char
     */
    public static native void writeToConsole(int c);

    /**
     * Read the next value from the keyboard
     * @return the next value or -1 for no value
     */
    public static native int readFromKeyboard();

    /**
     * Read the next interrupt value, return -1 for no value
     */
    private static native int nextInterrupt();

    /**
     * @return a key object for the next interrupt
     */
    public Object getNextInterrupt() {
        int id = nextInterrupt();
        return (id == KEYBOARD_INTERRUPT) ? KEYBOARD_INTERRUPT_KEY : null;
    }

}

