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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.lang.reflect.Modifier;

import java.util.LinkedList;

import org.pjos.common.runtime.Entry;

/**
 * Contains code to implement the instructions which invoke
 * methods.
 */
class Invoke implements Constants {

    /** Output stream to console log */
    private static OutputStream consoleOut;

    /**
     * invokeinterface instruction
     */
    static void invokeinterface() {
        // read the constant pool entry
        int index = Instruction.twoByteCodes(1);
        int count = Instruction.byteCode(3);
        if (count <= 0) {
            throw new IllegalStateException(
                    "Count should not be zero: " + count);
        }
        int zero = Instruction.byteCode(4);
        if (zero != 0) {
            throw new IllegalStateException(
                    "Value should be zero: 0x" + Integer.toHexString(zero));
        }

        // look up the method in the constant pool
        int entry = Resolve.resolve(index, Entry.METHOD);
        if (entry == NULL) { return; } // roll back if not resolved
        int argcount = Mem.load(entry + 4 * METHOD_ARG_COUNT);

        // Resolve the method by searching through the
        // object's available methods. Check all super
        // classes until found.
        int descriptor = Mem.load(entry + 4 * ENTRY_DESCRIPTOR);
        int name = Mem.load(entry + 4 * ENTRY_NAME);
        int object = Stack.peekPointer(argcount - 1);
        if (object == NULL) {
            Exceptions.throwException(CORE_THROW_NULL_POINTER);
            return;
        }
        int type = Mem.load(object + 4 * OBJECT_TYPE);
        int method = resolveMethod(type, name, descriptor);
        
        // execute
        executeMethod(object, method, 5);
    }
    
    /**
     * invokestatic instruction
     */
    static void invokestatic() {
        // read the constant pool entry
        int index = Instruction.twoByteCodes(1);
        int entry = Resolve.resolve(index, Entry.METHOD);
        if (entry == NULL) { return; } // roll back, gc done

        // execute
        int target = Mem.load(entry + 4 * ENTRY_OWNER);
        executeMethod(target, entry, 3);
    }
    
    /**
     * invokespecial instruction
     */
    static void invokespecial() {
        // read the constant pool entry
        int index = Instruction.twoByteCodes(1);
        int entry = Resolve.resolve(index, Entry.METHOD);
        if (entry == NULL) { return; } // roll back, gc done

        // execute
        int argcount = Mem.load(entry + 4 * METHOD_ARG_COUNT);
        int object = Stack.peekPointer(argcount - 1);
        if (object == NULL) {
            Exceptions.throwException(CORE_THROW_NULL_POINTER);
            return;
        }
        executeMethod(object, entry, 3);
    }
    
    /**
     * invokevirtual instruction
     */
    static void invokevirtual() {
        // read the constant pool entry index
        int index = Instruction.twoByteCodes(1);
        int entry = Resolve.resolve(index, Entry.METHOD);
        if (entry == NULL) { return; } // roll back, gc done

        // Get a pointer to the object for which the method will be invoked
        int argcount = Mem.load(entry + 4 * METHOD_ARG_COUNT);
        int object = Stack.peekPointer(argcount - 1);
        if (object == NULL) {
            Exceptions.throwException(CORE_THROW_NULL_POINTER);
            return;
        }
        
        // Resolve the method
        int descriptor = Mem.load(entry + 4 * ENTRY_DESCRIPTOR);
        int name = Mem.load(entry + 4 * ENTRY_NAME);
        int type = Mem.load(object + 4 * OBJECT_TYPE);
        int method = resolveMethod(type, name, descriptor);

        // execute
        executeMethod(object, method, 3);
    }

    /**
     * Execute the method at the given address. Return true if the
     * method is executed, false if it is rolled back for garbage
     * collection. The target parameter is the class for static methods
     * and the object for instance methods.
     * @param target the target object or class
     * @param method the method
     * @param the program counter offset
     */
    static boolean executeMethod(int target, int method, int pcOffset) {
        // get number of arguments
        int argcount = Mem.load(method + 4 * METHOD_ARG_COUNT);
        
        // execute native method
        int flags = Mem.load(method + 4 * ENTRY_FLAGS);
        if (Modifier.isNative(flags)) {
            executeNativeMethod(method, pcOffset, argcount);
            return true;
        }
        
        // create a new stack frame for the new method
        // if method is synchronized, acquire lock
        int lock = NULL;
        boolean isSynchronized = Modifier.isSynchronized(flags);
        if (isSynchronized) {
            // acquire lock
            lock = Monitor.getLock(target);
            if (lock == NULL) {
                return false; // rollback because gc interrupted
            }
            Monitor.acquire(Reg.thread, lock, 0);
            if (Reg.thread == NULL) {
                // rollback - thread was unscheduled to wait for lock
                return false;
            }
        }

        // calculate size required
        int maxStack = Mem.load(method + 4 * METHOD_MAX_STACK);
        int maxLocals = Mem.load(method + 4 * METHOD_MAX_LOCALS);
        int numWords = FRAME_LOCALS + 2 * maxStack + 2 * maxLocals;

        // allocate space for new frame
        int address = Allocate.allocate(numWords, HEADER_STACK_FRAME);
        if (address == NULL) { return false; } // roll back gc done

        // can increment lock count now that instruction can't be rolled back
        if (isSynchronized) { Monitor.increment(lock, 1); }

        // init new frame
        int frameType = Mem.load(Reg.frame + 4 * OBJECT_TYPE);
        int returnpc = Reg.instruction - Reg.code + pcOffset;
        Mem.store(address, address + 4 * OBJECT_HASHCODE);
        // will be used when returning if method is synchronized
        Mem.store(lock, address + 4 * OBJECT_LOCK);
        Mem.store(frameType, address + 4 * OBJECT_TYPE);
        Mem.store(Reg.frame, address + 4 * FRAME_RETURN_FRAME);
        Mem.store(method, address + 4 * FRAME_METHOD);
        Mem.store(returnpc, address + 4 * FRAME_RETURN_PC);
        Mem.store(0, address + 4 * FRAME_PC);
        Mem.store(4 * numWords, address + 4 * FRAME_SP);
        
        // check method has code
        if (Mem.load(method + 4 * METHOD_CODE) == NULL) {
            throw new IllegalStateException("Null code for method");
        }

         // Pop arguments off current stack and write to new frame
        for (int i = argcount - 1; i >= 0; i--) {
            if (Stack.pointerOnStack()) {
                Mem.store(Stack.popPointer(),
                        address + 4 * FRAME_LOCALS + 8 * i);
                Mem.store(TRUE, address + 4 * FRAME_LOCALS + 8 * i + 4);
            } else {
                Mem.store(Stack.popData(), address + 4 * FRAME_LOCALS + 8 * i);
                Mem.store(FALSE, address + 4 * FRAME_LOCALS + 8 * i + 4);
            }
        }

        // save current register values in current frame
        Reg.save();
        
        // set current thread to point to new frame
        Mem.store(address, Reg.thread + 4 * THREAD_FRAME);
        
        // set registers for new frame
        Reg.load();
        return true;
    }
    
    /**
     * Execute a native method. Currently only magic methods are implemented.
     */
    private static void executeNativeMethod(
            int method,
            int pcOffset,
            int argcount)
    {
        int magic = Mem.load(method + 4 * METHOD_MAGIC);
        if (magic == 0) {
            throw new UnsupportedOperationException(
                    "Native method not supported: " + Debug.method(method));
        }
        switch (magic) {

            /*************************************************************
             * Runtime methods                                           *
             *************************************************************/

            // print debug message
            case MAGIC_RUNTIME_CORE_DEBUG:
                {
                    int string = Stack.popPointer();
                    String message = Debug.string(string);
                    for (int i = 0, n = message.length(); i < n; i++) {
                        Implementation.get().toConsole(message.charAt(i));
                    }
                    Implementation.get().toConsole('\n');
                }
                Reg.instruction += pcOffset;
                break;

            // execute the given static method
            case MAGIC_RUNTIME_CORE_EXECUTE_STATIC:
                {
                    int methodArg = Stack.popPointer();
                    if (methodArg == NULL) {
                        Exceptions.throwException(CORE_THROW_NULL_POINTER);
                    }
                    int target = Mem.load(methodArg + 4 * ENTRY_OWNER);
                    if (!executeMethod(target, methodArg, pcOffset)) {
                        Stack.pushPointer(methodArg); // restore stack
                    }
                }
                break;
            
            // return the core object
            case MAGIC_RUNTIME_CORE_GET:
                Stack.pushPointer(Reg.core);
                Reg.instruction += pcOffset;
                break;
                
            // read the type from a class
            case MAGIC_RUNTIME_CORE_GET_TYPE:
                {
                    int classAddress = Stack.popPointer();
                    // type is first field
                    int type = Mem.load(classAddress + 4 * OBJECT_FIELDS);
                    Stack.pushPointer(type);
                    Reg.instruction += pcOffset;
                }
                break;
            
            // return the current frame
            case MAGIC_RUNTIME_FRAME_CURRENT:
                Stack.pushPointer(Reg.frame);
                Reg.instruction += pcOffset;
                break;
            
            // put system to sleep to avoid wasting processor time idling
            case MAGIC_RUNTIME_IDLE_SLEEP:
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // ignore any interruptions
                }
                Reg.instruction += pcOffset;
                break;
                
            // create a statics object
            case MAGIC_RUNTIME_STATICS_CREATE:
                {
                    int type = Stack.popPointer();
                    int numStaticFields = Mem.load(
                            type + 4 * TYPE_STATIC_FIELD_COUNT);
                    int numWords = STATICS_FIELDS + numStaticFields;
                    
                    // allocate space for statics object
                    int address = Allocate.allocate(
                            numWords, HEADER_STATIC_FIELDS);
                    if (address == NULL) {
                        Stack.pushPointer(type); // restore stack
                        return; // roll back because gc has been done
                    }
                    
                    // initialise statics object
                    Mem.store(address, address + 4 * OBJECT_HASHCODE);
                    int staticsType = Mem.load(
                            Reg.core + 4 * CORE_STATICS_TYPE);
                    Mem.store(staticsType, address + 4 * OBJECT_TYPE);
                    int map = Mem.load(type + 4 * TYPE_STATIC_MAP);
                    Mem.store(map, address + 4 * STATICS_MAP);
                    
                    // set statics field in type object
                    Mem.store(address, type + 4 * TYPE_STATICS);
                    Reg.instruction += pcOffset;
                }
                break;
            
            // return the currently executing thread
            case MAGIC_RUNTIME_THREAD_CURRENT:
                Stack.pushPointer(Reg.thread);
                Reg.instruction += pcOffset;
                break;
                
            // put the currently executing thread to sleep
            case MAGIC_RUNTIME_THREAD_SLEEP:
                {
                    int nanos = Stack.popData();
                    long millis = Stack.popLong();
                    Reg.instruction += pcOffset;
                    Sleep.addToSleepQueue(millis);
                    Threads.unschedule(Reg.thread);
                }
                break;
                
            // start a new thread
            case MAGIC_RUNTIME_THREAD_START_HOOK:
                Threads.startNewThread(pcOffset);
                break;
                
            // suspend a thread
            case MAGIC_RUNTIME_THREAD_SUSPEND:
                {
                    int thread = Stack.popPointer();
                    Reg.instruction += pcOffset;
                    Threads.suspend(thread);
                }
                break;
                
            // resume a thread
            case MAGIC_RUNTIME_THREAD_RESUME:
                {
                    int thread = Stack.popPointer();
                    Reg.instruction += pcOffset;
                    Threads.resume(thread);
                }
                break;


            /*************************************************************
             * Java API methods                                          *
             *************************************************************/

            // return an object's class
            case MAGIC_JAVA_OBJECT_GET_CLASS:
                {
                    int obj = Stack.popPointer();
                    int type = Mem.load(obj + 4 * OBJECT_TYPE);
                    int peer = Mem.load(type + 4 * TYPE_PEER);
                    Stack.pushPointer(peer);
                }
                Reg.instruction += pcOffset;
                break;
            
            // notify the first thread waiting on an object
            case MAGIC_JAVA_OBJECT_NOTIFY:
                {
                    int object = Stack.popPointer();
                    Reg.instruction += pcOffset;
                    Monitor.notify(object);
                }
                break;

            // notify all threads waiting on an object
            case MAGIC_JAVA_OBJECT_NOTIFY_ALL:
                {
                    int object = Stack.popPointer();
                    Reg.instruction += pcOffset;
                    Monitor.notifyAll(object);
                }
                break;

            // Wait on an object (JI)V
            case MAGIC_JAVA_OBJECT_WAIT:
                {
                    int nanos = Stack.popData();
                    long millis = Stack.popLong();
                    int object = Stack.popPointer();
                    Reg.instruction += pcOffset; // can't roll back now
                    Monitor.wait(object, millis); // no time for nanos :)
                }
                break;

            // return system time in milliseconds
            case MAGIC_JAVA_SYSTEM_TIME:
                {
                    long time = System.currentTimeMillis();
                    Stack.pushLong(time);
                    Reg.instruction += pcOffset;
                }
                break;

            // return the identity hash code for an object
            case MAGIC_JAVA_SYSTEM_HASHCODE:
                {
                    int object = Stack.popPointer();
                    int hash = Mem.load(object + 4 * OBJECT_HASHCODE);
                    Stack.pushData(hash);
                    Reg.instruction += pcOffset;
                }
                break;

            // set system error stream
            case MAGIC_JAVA_SYSTEM_SET_ERR:
                {
                    int arg = Stack.popPointer();
                    int type = Mem.load(method + 4 * ENTRY_OWNER);
                    int statics = Mem.load(type + 4 * TYPE_STATICS);
                    Mem.store(arg,
                            statics + 4 * STATICS_FIELDS + 4 * SYSTEM_ERR);
                }
                Reg.instruction += pcOffset;
                break;

            // set system output stream
            case MAGIC_JAVA_SYSTEM_SET_OUT:
                {
                    int arg = Stack.popPointer();
                    int type = Mem.load(method + 4 * ENTRY_OWNER);
                    int statics = Mem.load(type + 4 * TYPE_STATICS);
                    Mem.store(arg,
                            statics + 4 * STATICS_FIELDS + 4 * SYSTEM_OUT);
                }
                Reg.instruction += pcOffset;
                break;

            // set system input stream
            case MAGIC_JAVA_SYSTEM_SET_IN:
                {
                    int arg = Stack.popPointer();
                    int type = Mem.load(method + 4 * ENTRY_OWNER);
                    int statics = Mem.load(type + 4 * TYPE_STATICS);
                    Mem.store(arg,
                            statics + 4 * STATICS_FIELDS + 4 * SYSTEM_IN);
                }
                Reg.instruction += pcOffset;
                break;
                

            /*************************************************************
             * Magic methods specific to the emulator                    *
             *************************************************************/

            // write an ascii character to the console
            case MAGIC_EMULATOR_WRITE_CONSOLE:
                {
                    char c = (char) Stack.popData();
                    Implementation.get().toConsole(c);
                    Reg.instruction += pcOffset;
                }
                break;
            
            // read a byte from the floppy drive
            case MAGIC_EMULATOR_READ_FLOPPY:
                {
                    int pos = Stack.popData();
                    Stack.pushData(Floppy.read(pos));
                    Reg.instruction += pcOffset;
                }
                break;

            // write a byte to the floppy drive
            case MAGIC_EMULATOR_WRITE_FLOPPY:
                {
                    int pos = Stack.popData();
                    int value = Stack.popData();
                    Floppy.write(value, pos);
                    Reg.instruction += pcOffset;
                }
                break;
                
            // read the next interrupt value
            case MAGIC_EMULATOR_NEXT_INTERRUPT:
                {
                    LinkedList interrupts = Implementation.interrupts;
                    Integer next = (!interrupts.isEmpty())
                            ? (Integer) interrupts.removeFirst()
                            : null;
                    int value = (next != null) ? next.intValue() : -1;
                    Stack.pushData(value);
                    Reg.instruction += pcOffset;
                }
                break;
                
            // read the next keyboard value
            case MAGIC_EMULATOR_READ_FROM_KEYBOARD:
                {
                    LinkedList keys = Implementation.keys;
                    Integer next = (!keys.isEmpty())
                            ? (Integer) keys.removeFirst()
                            : null;
                    int value = (next != null) ? next.intValue() : -1;
                    Stack.pushData(value);
                    Reg.instruction += pcOffset;
                }
                break;
                
            // illegal id
            default:
                throw new IllegalStateException(
                        "Invalid magic method id: " + magic);
        }
    }
    
    /**
     * Resolve a method address by a recursive search through the
     * method table of a class and its superclasses.
     */
    static int resolveMethod(int methodType, int name, int descriptor) {
        if (methodType == NULL) { throw new IllegalArgumentException(); }
        int method = NULL;
        int type = methodType;
        while (type != NULL && method == NULL) {
            method = findMethod(type, name, descriptor);
            
            // if not found, check superclass
            if (method == NULL) {
                type = Mem.load(type + 4 * TYPE_SUPER_TYPE);
            }
        }
        
        // throw exception if not found
        if (method == NULL) {
            int classname = Mem.load(methodType + 4 * TYPE_NAME);
            throw new UnsupportedOperationException(
                "Method not found: " + Debug.string(classname)
                    + "." + Debug.string(name)
                    + "." + Debug.string(descriptor)
            );
        }
        return method;
    }
    
    /**
     * Find the method matching the given name and descriptor in the
     * given class. If no method is found return NULL (0x00).
     */
    static int findMethod(int type, int name, int descriptor) {
        int result = NULL;
        int array = Mem.load(type + 4 * TYPE_METHODS);
        if (array != NULL) {
            int length = Mem.load(array + 4 * ARRAY_LENGTH);
            for (int i = 0; i < length && result == NULL; i++) {
                int method = Mem.load(array + 4 * ARRAY_DATA + 4 * i);
                int d = Mem.load(method + 4 * ENTRY_DESCRIPTOR);
                int n = Mem.load(method + 4 * ENTRY_NAME);
                if (name == n && descriptor == d) {
                    return method;
                }
            }
        }
        return NULL;
    }
    
    /**
     * Return an output stream to the console log file
     * @return the output stream
     * @throws IOException if an error occurs
     */
    private static OutputStream getConsoleOut() throws IOException {
        if (consoleOut == null) {
            consoleOut = new FileOutputStream("console.log");
        }
        return consoleOut;
    }
    
}









