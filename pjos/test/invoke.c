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

#include "interpreter.h"

/*
 * Find the method matching the given name and descriptor in the
 * given class. If no method is found, return null.
 */
static Ref findMethod(Ref type, Ref name, Ref desc) {
    Ref array = getRef(type, TYPE_METHODS);
    if (array != NULL) {
        Int length = getInt(array, ARRAY_LENGTH);
        int i;
        for (i = 0; i < length; i++) {
            Ref m = getRef(array, ARRAY_DATA + i);
            Ref mName = getRef(m, ENTRY_NAME);
            Ref mDesc = getRef(m, ENTRY_DESCRIPTOR);
            if (name == mName && desc == mDesc) { return m; }
        }
    }
    return NULL;
}

/*
 * Resolve a method by recursively searching through the method table
 * of the given class and its superclasses.
 */
static Ref resolveMethod(Ref type, Ref name, Ref descriptor) {
    Ref m = NULL;         // method
    Ref t = type;         // type
    while (t != NULL && m == NULL) {
        m = findMethod(t, name, descriptor);
        t = getRef(t, TYPE_SUPER_TYPE);
    }

    // throw exception if not found
    if (m == NULL) {
        printf("Need to throw proper exception here, method not found!!!\n");
        exit(1);
    }
    return m;
}

/*
 * Execute the INVOKESTATIC instruction
 */
void op_invokestatic() {
    Ref entry = resolve(u16(1), ID_METHOD);
    if (entry == NULL) { return; } // rollback, gc done
    Ref owner = getRef(entry, ENTRY_OWNER);
    executeMethod(owner, entry, 3);
}

/*
 * Execute the INVOKEVIRTUAL instruction
 */
void op_invokevirtual() {
    Ref entry = resolve(u16(1), ID_METHOD);
    if (entry == NULL) { return; } // rollback, gc done

    // check the target object
    int argcount = getInt(entry, METHOD_ARG_COUNT);
    Ref target = stack[argcount - 1].value.r;
    if (target == NULL) { throwException(CORE_THROW_NULL_POINTER); }

    // resolve and execute the method
    else {
        Ref type = getRef(target, OBJECT_TYPE);
        Ref name = getRef(entry, ENTRY_NAME);
        Ref desc = getRef(entry, ENTRY_DESCRIPTOR);
        Ref resolved = resolveMethod(type, name, desc);
        executeMethod(target, resolved, 3);
    }
}

/*
 * Execute the INVOKESPECIAL instruction
 */
void op_invokespecial() {
    Ref method = resolve(u16(1), ID_METHOD);
    if (method == NULL) { return; } // rollback, gc done

    // check the target object
    Int argcount = getInt(method, METHOD_ARG_COUNT);
    Ref target = stack[argcount - 1].value.r;
    if (target == NULL) { throwException(CORE_THROW_NULL_POINTER); }

    // execute the method
    else { executeMethod(target, method, 3); }
}

/*
 * Execute the INVOKEINTERFACE instruction
 */
void op_invokeinterface() {
    Ref entry = resolve(u16(1), ID_METHOD);
    if (entry == NULL) { return; } // rollback, gc done

    // check for null pointer
    Int argcount = getInt(entry, METHOD_ARG_COUNT);
    Ref object = stack[argcount - 1].value.r;
    if (object == NULL) {
        throwException(CORE_THROW_NULL_POINTER);
        return;
    }

    // resolve and execute
    Ref desc = getRef(entry, ENTRY_DESCRIPTOR);
    Ref name = getRef(entry, ENTRY_NAME);
    Ref type = getRef(object, OBJECT_TYPE);
    Ref method = resolveMethod(type, name, desc);
    executeMethod(object, method, 5);
}

/*
 * Execute the specified method. Return true if the method is executed,
 * false if it is rolled back for garbage collection.
 *
 * target: object for instance methods, class for static
 * method: the method to be run
 * offset: amount to increment program counter
 */
int executeMethod(Ref target, Ref method, int offset) {
/*
printf("[");
if (method[ENTRY_FLAGS].i & ACC_STATIC) { debugString(target[TYPE_NAME].s); }
else { debugString(method[ENTRY_OWNER].s[TYPE_NAME].s); }
printf(".");
debugString(method[ENTRY_NAME].s);
debugString(method[ENTRY_DESCRIPTOR].s);
printf("]\n");
*/
    // check for native method
    Int argcount = getInt(method, METHOD_ARG_COUNT);
    Int flags = getInt(method, ENTRY_FLAGS);
    if (flags & ACC_NATIVE) {
        return executeNativeMethod(target, method, offset);
    }

    // if synchronized, acquire the lock
    Ref lock = NULL;
    if (flags & ACC_SYNCHRONIZED) {
        lock = getLock(target);
        if (lock == NULL) { return FALSE; } // rollback, gc done
        acquireLock(thread, lock, 0);
        if (thread == NULL) { return FALSE; } // rollback, wait for lock 
    }

    // allocate space for new frame
    Int maxLocals = getInt(method, METHOD_MAX_LOCALS);
    Int maxStack = getInt(method, METHOD_MAX_STACK);
    int numWords = FRAME_LOCALS + 2*maxLocals + 2*maxStack;
    Ref newFrame = allocate(numWords, HEADER_STACK_FRAME);
    if (newFrame == NULL) { return FALSE; } // rollback, gc done

    // increment lock count now that instruction can't be rolled back
    if (flags & ACC_SYNCHRONIZED) {
        Int count = getInt(lock, LOCK_COUNT);
        setInt(lock, LOCK_COUNT, count + 1);
    }

    // initialise new frame
    int hash = (char*) newFrame - (char*) core;
    Ref frameType = getRef(frame, OBJECT_TYPE);
    setInt(newFrame, OBJECT_HASHCODE, hash);
    setRef(newFrame, OBJECT_LOCK, lock);
    setRef(newFrame, OBJECT_TYPE, frameType);
    setRef(newFrame, FRAME_RETURN_FRAME, frame);
    setRef(newFrame, FRAME_METHOD, method);
    setInt(newFrame, FRAME_RETURN_PC, pc + offset);
    setInt(newFrame, FRAME_PC, 0);
    setInt(newFrame, FRAME_SP, numWords * 4);

    // pop arguments off current stack and write to new frame
    int index = argcount;
    while (--index >= 0) {
        if (refOnStack()) { setLocalRef(index, popRef(), newFrame); }
        else { setLocalInt(index, popInt(), newFrame); }
    }

    // point current thread to new frame
    saveRegisters();
    setRef(thread, THREAD_FRAME, newFrame);
    loadRegisters();
    return TRUE;
}

/*
 * Execute the specified native method. Return true if the method
 * is executed, false if it is rolled back for garbage collection.
 */
int executeNativeMethod(Ref target, Ref method, int offset) {
    int magic = getInt(method, METHOD_MAGIC);
    if (magic == 0) {
        printf("Native method not supported\n");
        debugTrace();
        exit(1);
    }
    switch (magic) {
        // print a debug message
        case MAGIC_RUNTIME_CORE_DEBUG:
            debugLine(popRef());
            pc += offset;
            break;

        // execute the given static method
        case MAGIC_RUNTIME_CORE_EXECUTE_STATIC:
            {
                Ref staticMethod = popRef();
                if (staticMethod  == NULL) {
                    throwException(CORE_THROW_NULL_POINTER);
                    return;
                }
                Ref owner = getRef(staticMethod, ENTRY_OWNER);
                if (!executeMethod(owner, staticMethod, offset)) {
                    // restore stack if rolled back for gc
                    pushRef(staticMethod); 
                }
            }
            break;

        // return the current frame
        case MAGIC_RUNTIME_FRAME_CURRENT:
            pushRef(frame);
            pc += offset;
            break;

        // return the core object
        case MAGIC_RUNTIME_CORE_GET:
            pushRef(core);
            pc += offset;
            break;

        // create a statics object
        case MAGIC_RUNTIME_STATICS_CREATE:
            {
                // allocate space for statics object
                Ref type = popRef();
                Int numStaticFields = getInt(type, TYPE_STATIC_FIELD_COUNT);
                Int numWords = STATICS_FIELDS + numStaticFields;
                Ref statics = allocate(numWords, HEADER_STATIC_FIELDS);
                if (statics == NULL) {
                    pushRef(type); // restore stack
                    return; // rollback, gc done
                }

                // initialise statics object and set in type object
                int hash = (char*) statics - (char*) core;
                setInt(statics, OBJECT_HASHCODE, hash);
                Ref staticsType = getRef(core, CORE_STATICS_TYPE);
                setRef(statics, OBJECT_TYPE, staticsType);
                Ref map = getRef(type, TYPE_STATIC_MAP);
                setRef(statics, STATICS_MAP, map);
                setRef(type, TYPE_STATICS, statics);
            }
            pc += offset;
            break;

        // read the type from a class
        case MAGIC_RUNTIME_CORE_GET_TYPE:
            {
                Ref class = popRef();
                // type is first field in class...
                Ref type = getRef(class, OBJECT_FIELDS);
                pushRef(type);
            }
            pc += offset;
            break;

        // put system to sleep to avoid wasting processor time while idle
        case MAGIC_RUNTIME_IDLE_SLEEP:
            idleSleep();
            pc += offset;
            break;

        // return the currently executing thread
        case MAGIC_RUNTIME_THREAD_CURRENT:
            pushRef(thread);
            pc += offset;
            break;

        // start a new thread
        case MAGIC_RUNTIME_THREAD_START_HOOK:
            startNewThread(offset);
            break;

        // return an object's class
        case MAGIC_JAVA_OBJECT_GET_CLASS:
            {
                Ref obj = popRef();
                Ref type = getRef(obj, OBJECT_TYPE);
                Ref peer = getRef(type, TYPE_PEER);
                pushRef(peer);
            }
            pc += offset;
            break;

        // set system output stream
        case MAGIC_JAVA_SYSTEM_SET_OUT:
            {
                Ref arg = popRef();
                Ref type = getRef(method, ENTRY_OWNER);
                Ref statics = getRef(type, TYPE_STATICS);
                setRef(statics, STATICS_FIELDS + SYSTEM_OUT, arg);
            }
            pc += offset;
            break;

        // set system error stream
        case MAGIC_JAVA_SYSTEM_SET_ERR:
            {
                Ref arg = popRef();
                Ref type = getRef(method, ENTRY_OWNER);
                Ref statics = getRef(type, TYPE_STATICS);
                setRef(statics, STATICS_FIELDS + SYSTEM_ERR, arg);
            }
            pc += offset;
            break;

        // set system input stream
        case MAGIC_JAVA_SYSTEM_SET_IN:
            {
                Ref arg = popRef();
                Ref type = getRef(method, ENTRY_OWNER);
                Ref statics = getRef(type, TYPE_STATICS);
                setRef(statics, STATICS_FIELDS + SYSTEM_IN, arg);
            }
            pc += offset;
            break;

        // read an ascii character from the console
        case MAGIC_TEST_READ_FROM_CONSOLE:
            {
                char c = readFromConsole();
                pushInt(c);
            }
            pc += offset;
            break;
            
        // write an ascii character to the console
        case MAGIC_TEST_WRITE_TO_CONSOLE:
            {
                char c = popInt() & 0xFF;
                printf("%c", c);
            }
            pc += offset;
            break;

        // read a byte from the floppy drive
        case MAGIC_TEST_READ_FROM_FLOPPY:
            {
                Int pos = popInt();
                pushInt(readFromFloppy(pos));
            }
            pc += offset;
            break;
			
        // magic id not recognised
        default:
            printf("Invalid magic method id: %d\n", magic);
            exit(1);
            break;
    }
}

//			/*************************************************************
//			 * Runtime methods                                           *
//			 *************************************************************/
//
//
//			
//				
//			
//			// return the current frame
//			case MAGIC_RUNTIME_FRAME_CURRENT:
//				Stack.pushPointer(Reg.frame);
//				Reg.instruction += pcOffset;
//				break;
//			
//			// put the currently executing thread to sleep
//			case MAGIC_RUNTIME_THREAD_SLEEP:
//				{
//					int nanos = Stack.popData();
//					long millis = Stack.popLong();
//					Reg.instruction += pcOffset;
//					Sleep.addToSleepQueue(millis);
//					Threads.unschedule(Reg.thread);
//				}
//				break;
//				
//			// suspend a thread
//			case MAGIC_RUNTIME_THREAD_SUSPEND:
//				{
//					int thread = Stack.popPointer();
//					Reg.instruction += pcOffset;
//					Threads.suspend(thread);
//				}
//				break;
//				
//			// resume a thread
//			case MAGIC_RUNTIME_THREAD_RESUME:
//				{
//					int thread = Stack.popPointer();
//					Reg.instruction += pcOffset;
//					Threads.resume(thread);
//				}
//				break;
//
//
//			/*************************************************************
//			 * Java API methods                                          *
//			 *************************************************************/
//
//			// notify the first thread waiting on an object
//			case MAGIC_JAVA_OBJECT_NOTIFY:
//				{
//					int object = Stack.popPointer();
//					Reg.instruction += pcOffset;
//					Monitor.notify(object);
//				}
//				break;
//
//			// notify all threads waiting on an object
//			case MAGIC_JAVA_OBJECT_NOTIFY_ALL:
//				{
//					int object = Stack.popPointer();
//					Reg.instruction += pcOffset;
//					Monitor.notifyAll(object);
//				}
//				break;
//
//			// Wait on an object (JI)V
//			case MAGIC_JAVA_OBJECT_WAIT:
//				{
//					int nanos = Stack.popData();
//					long millis = Stack.popLong();
//					int object = Stack.popPointer();
//					Reg.instruction += pcOffset; // This instruction can't be rolled back
//					Monitor.wait(object, millis); // don't have time for nanos :)
//				}
//				break;
//
//			// return system time in milliseconds
//			case MAGIC_JAVA_SYSTEM_TIME:
//				{
//					long time = System.currentTimeMillis();
//					Stack.pushLong(time);
//					Reg.instruction += pcOffset;
//				}
//				break;
//
//			// return the identity hash code for an object
//			case MAGIC_JAVA_SYSTEM_HASHCODE:
//				{
//					int object = Stack.popPointer();
//					int hash = Mem.load(object + 4*OBJECT_HASHCODE);
//					Stack.pushData(hash);
//					Reg.instruction += pcOffset;
//				}
//				break;
//
//
//
//				
//
//			/*************************************************************
//			 * Magic methods specific to the emulator                    *
//			 *************************************************************/
//
//			
//
//			// write a byte to the floppy drive
//			case MAGIC_EMULATOR_WRITE_FLOPPY:
//				{
//					int pos = Stack.popData();
//					int value = Stack.popData();
//					Floppy.write(value, pos);
//					Reg.instruction += pcOffset;
//				}
//				break;
//				
//			// read the next interrupt value
//			case MAGIC_EMULATOR_NEXT_INTERRUPT:
//				{
//					LinkedList interrupts = Implementation.interrupts;
//					Integer next = (!interrupts.isEmpty()) ? (Integer) interrupts.removeFirst() : null;
//					int value = (next != null) ? next.intValue() : -1;
//					Stack.pushData(value);
//					Reg.instruction += pcOffset;
//				}
//				break;
//				
