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

import org.pjos.common.runtime.InterruptManager;

/**
 * Provides access to the physical data of a floppy disk
 *
 * 1.44M floppy has 160 tracks with 18 sectors in each track
 * 1474560 bytes in total
 * => 9216 bytes per track
 * => 512 bytes per sector
 *
 * Sectors are numbered 0 to 2879
 */
class Floppy implements Runnable {

    /** The digital output register address */
    private static final int DOR = 0x3f2;

    /** The main status register address */
    private static final int MSR = 0x3f4;

    /** The data register address */
    private static final int DR = 0x3f5;

    /** The number of milliseconds in a pause */
    private static final long PAUSE = 500;

    /** Set while the motor is running */
    private boolean running = false;

    /** Set if calibrated */
    private boolean calibrated = false;

    /**
     * Create a floppy object
     */
    Floppy() {
        // start the background thread to listen for interrupts
        Thread thread = new Thread(this, "Floppy Interrupt Listener");
        thread.start();
    }

    /**
     * Wait for a short time or until notified
     */
    private synchronized void pause() {
        try {
            wait(PAUSE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the track data as a byte array
     * @param index the sector index
     * @param data write sector data to here
     */
    synchronized void readSector(int index, byte[] data) {
        if (index < 0 || index >= 2880 || data.length != 512) {
            throw new IllegalArgumentException();
        }

        // calculate track and sector numbers
        int cylinder = index / 36; // 36 sectors per cylinder
        int block = index % 36; // blocks are numbered 0-35
        // sectors are numbered 1-18
        int sector = (block < 18) ? block + 1 : block - 17;
        int head = (block < 18) ? 0 : 1;
        //System.out.println("cylinder: " + cylinder);
        //System.out.println("sector: " + sector);
        //System.out.println("head: " + head);
        //System.out.println("cylinder: " + cylinder);

        // switch motor on if necessary
        int dor = X86Architecture.in(DOR);
        //System.out.println("dor: 0x" + Integer.toHexString(dor));
        if ((dor & 0x10) == 0) {
            X86Architecture.out(0x1c, DOR);
        }

        // wait until controller is ready to accept the command
        int msr = X86Architecture.in(MSR);
        //System.out.println("msr: 0x" + Integer.toHexString(msr));
        while ((msr & 0x80) == 0) {
            pause();
            msr = X86Architecture.in(MSR);
            //System.out.println("msr: 0x" + Integer.toHexString(msr));
        }

        // set up dma for write from device to dma memory using channel 2
        X86Architecture.initDma(2, true);

        // issue read sector command
        X86Architecture.out(0xe6, DR);
        X86Architecture.out((head << 2), DR);   // head << 2 | driveA(0x00)
        X86Architecture.out(cylinder, DR);
        X86Architecture.out(head, DR);
        X86Architecture.out(sector, DR);
        X86Architecture.out(2, DR);             // sector size (512 bytes)
        X86Architecture.out(0, DR);             // track length
        X86Architecture.out(0, DR);             // length of GAP3
        X86Architecture.out(0xff, DR);          // data length not used

        // wait until controller is ready for read
        msr = X86Architecture.in(MSR);
        while ((msr & 0xc0) == 0) {
            pause();
            msr = X86Architecture.in(MSR);
        }

        // read track results
        int value = X86Architecture.in(DR); // st0
        value = X86Architecture.in(DR); // st1
        value = X86Architecture.in(DR); // st2
        value = X86Architecture.in(DR); // cylinder
        value = X86Architecture.in(DR); // head
        value = X86Architecture.in(DR); // sector
        value = X86Architecture.in(DR); // sector size

        // read the sector data from dma memory
        X86Architecture.readDmaMemory(data);
    }

    /**
     * Notify all threads waiting
     */
    private synchronized void handleInterrupt() {
        notifyAll();
    }

    /**
     * Listen for interrupts
     */
    public void run() {
        Object key = X86Architecture.getIrqKey(6);
        while (true) {
            InterruptedException e = InterruptManager.waitFor(key);
            //System.out.println("Floppy interrupt received");
            if (e != null) { e.printStackTrace(); }
            handleInterrupt();
        }
    }

}

