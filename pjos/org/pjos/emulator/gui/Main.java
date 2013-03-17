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
package org.pjos.emulator.gui;

import org.pjos.emulator.engine.Engine;

import org.pjos.emulator.engine.implementation.Implementation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * This class has code to start the emulator gui from the
 * command line.
 */
public final class Main {
    
    /**
     * Start the emulator gui from the command line. The emulator expects two
     * files to be present in the current directory: "memory.bin" and
     * "floppy.bin".
     * @param args command line args (none)
     * @throws Exception if an error occurs
     */
    public static void main(String[] args) throws Exception {
        // Redirect streams to log file
        PrintStream log = new PrintStream(new FileOutputStream("emulator.log"));
        System.setOut(log);
        System.setErr(log);
        
        try {
            // Load the memory and floppy images
            File memory = new File("memory.bin");
            File floppy = new File("floppy.bin");
            byte[] memoryData = Util.getData(memory);
            byte[] floppyData = Util.getData(floppy);

            // Create the emulator implementation
            Engine engine = Implementation.get();
            engine.reset(memoryData, floppyData);

            // Replace the two lines above with this line to
            // use the reset implementation. This will reload
            // all classes in the package
            // org.pjos.emulator.engine.implementation
            // when reset, so that code changes can be tested without restarting
            // the emulator. Won't work if the emulator is run from the jar
            // file though.
            //Engine engine = new ResetImplementation(
            //      ".", memoryData, floppyData);

            // Start the gui
            Debugger debugger = new Debugger(engine, memory, floppy);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
}









