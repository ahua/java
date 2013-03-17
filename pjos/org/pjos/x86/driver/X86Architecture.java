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

import java.io.IOException;
import java.io.PrintStream;

import org.pjos.common.device.DeviceManager;

import org.pjos.common.file.DriveManager;

import org.pjos.common.fs.fat12.Floppy1440;

import org.pjos.common.runtime.Architecture;

import org.pjos.common.shell.Shell;

/**
 * Architecture implementation for the x86 platform.
 */
public class X86Architecture extends Architecture {

    /** Address of dma mask register */
    private static final int DMA_MASK = 0x0a;

    /** Address of dma mode port */
    private static final int DMA_MODE = 0x0b;

    /** Address of dma byte pointer port */
    private static final int DMA_BPTR = 0x0c;

    /** Address of dma page port */
    private static final int DMA_PAGE = 0x81;
    
    /** Write specifier: device writes to dma memory */
    private static final int DMA_WRITE = 0x46;
    
    /** Read specifier: device reads from dma memory */
    private static final int DMA_READ = 0x4a;

    /** The interrupt keys, 32 error keys and 16 irq keys */
    private static final Object[] interruptKeys = new Object[48];

    /**
     * Create an instance
     */
    private X86Architecture() {
        // nothing to do here
    }

    /**
     * This method will be called by the initialisation thread to do
     * platform-specific initialisation.
     */
    public static void init() {
        // Set the singleton architecture instance
        Architecture.singleton = new X86Architecture();

        // set up output streams
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

        // start keyboard handler
        KeyboardHandler kh = new KeyboardHandler();
        Thread thread = new Thread(kh, "Keyboard Handler");
        thread.start();
        System.out.println("Keyboard handler started");

        // set up system input stream
        System.setIn(kh.getInputStream());

        // start shell with local echo
        Shell shell = new Shell(true);
        thread = new Thread(shell, "Shell");
        thread.start();
    }

    /**
     * Write the given char value to the system console
     * @param c the character to write
     */
    public static native void writeToConsole(int c);

    /**
     * Retrieve the id of the next interrupt, return -1 if
     * there are no interrupt codes in the buffer.
     */
    private static native int nextInterrupt();

    /**
     * Write the given byte value to the specified i/o port
     * @param value the value to be written
     * @param port the i/o port number
     */
    public static native void out(int value, int port);

    /**
     * Read a byte value from the specified i/o port
     * @param port the i/o port number
     * @return the value read
     */
    public static native int in(int port);

    /**
     * Return a key object for the next interrupt, null
     * if there are no interrupts waiting.
     * @return the desired interrupt key
     */
    public Object getNextInterrupt() {
        int next = nextInterrupt();
        return (next != -1) ? getInterruptKey(next) : null;
    }

    /**
     * Return the interrupt key for the specified index
     */
    private static Object getInterruptKey(int id) {
        synchronized (interruptKeys) {
            Object result = interruptKeys[id];
            if (result == null) {
                result = new Object();
                interruptKeys[id] = result;
            }
            return result;
        }
    }

    /**
     * Return the irq key for the specified index. Note that the
     * timer irq (value 0) is not available here. This is used
     * for multithreading. Device drivers should use standard timing
     * services provided by the jvm.
     * @param irq the irq number
     * @return the irq key object
     */
    public static Object getIrqKey(int irq) {
        if (irq < 1 || irq >= 16) {
            throw new IllegalArgumentException();
        }
        // irqs come after 32 reserved interrupts
        return getInterruptKey(irq + 32);
    }

    /**
     * Initialise the specified DMA channel to have 512 bytes
     * available for read or write at physical address 0x00
     * @param channel the DMA channel number
     * @param write set to true for write mode, false for read mode
     */
    public static void initDma(int channel, boolean write) {
        if (channel < 0 || channel > 3) {
            throw new IllegalArgumentException();
        }
        int base = channel * 2;
        int count = base + 1;
        int mode = (write) ? DMA_WRITE : DMA_READ;

        out((4 | channel), DMA_MASK);           // set mask for channel
        out(0x00, DMA_BPTR);                    // clear flip/flop
        out(0x00, base);
        out(0x00, base);                        // send address 0x00
        out(0xff, count);
        out(0x01, count);                       // count = 512 bytes
        out(mode, DMA_MODE);                    // set read/write mode
        out(0x00, DMA_PAGE);                    // send physical page
        out(channel, DMA_MASK);                 // clear mask for channel
    }
    
    /**
     * Read the dma memory into the given array
     * @param data the array to receive the data
     */
    public static void readDmaMemory(byte[] data) {
        if (data.length != 512) {
            throw new IllegalArgumentException();
        }
        readDma(data);
    }
    
    /**
     * Implementation of dma read
     */
    private static native void readDma(byte[] data);

}

