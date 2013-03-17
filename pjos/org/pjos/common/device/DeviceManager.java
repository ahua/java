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

import java.util.HashMap;
import java.util.Map;

/**
 * This class maps devices to names.
 */
public class DeviceManager {
    
    /** The singleton device manager */
    private static final DeviceManager singleton = new DeviceManager();
    
    /** The registered devices */
    private Map devices;
    
    /**
     * Create a device manager
     */
    private DeviceManager() {
        devices = new HashMap();
    }

    /**
     * Return the device with the given name
     */
    private synchronized Device getDevice(String name) {
        return (Device) devices.get(name);
    }
    
    /**
     * @param name the device name
     * @return the device with the given name
     */
    public static Device get(String name) {
        return singleton.getDevice(name);
    }
    
    /**
     * Add a device
     * @param name the device name
     * @param device the device
     */
    public static void add(String name, Device device) {
        singleton.addDevice(name, device);
    }
    
    /**
     * Add a device
     */
    private synchronized void addDevice(String name, Device device) {
        if (name == null || device == null || name.equals("")) {
            throw new IllegalArgumentException();
        }
        if (devices.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Device already exists: " + name);
        }
        devices.put(name, device);
    }
    
    /**
     * Remove a device
     * @param name the device name
     */
    public static void remove(String name) {
        singleton.removeDevice(name);
    }
    
    /**
     * Remove a device
     */
    private synchronized void removeDevice(String name) {
        devices.remove(name);
    }
    
}










