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
 * This is the core of the runtime system and the
 * root of the tree of live references in the system.
 *
 * All the fields of this class are initialised by
 * the system when an execution image is created.
 */
public final class Core implements Opcode {

    /** The address of the next object */
    int next;

    /** The highest priority thread */
    Thread priority;

    /** Double linked list of running threads */
    Thread running;

    /** List of sleeping threads */
    Thread sleeping;

    /** The idle thread */
    Thread idle;

    /** The interrupt notifier */
    Thread notifier;

    /** Run method of system thread */
    Method threadRunMethod;

    /** Method to resolve types, fields and methods */
    Method resolveMethod;

    /** Method to throw null pointer exceptions */
    Method throwNullPointer;

    /** Method to throw class cast exceptions */
    Method throwClassCast;

    /** Method to throw array index out of bounds exceptions */
    Method throwArrayIndexOutOfBounds;

    /** Method to throw arithmetic exceptions */
    Method throwArithmetic;

    /** Platform architecture implementation type */
    Type architectureType;

    /** Type of static fields objects */
    Type staticsType;

    /** Type of lock objects */
    Type lockType;

    // The following array types are ordered
    // and offset using the array types from the VM spec

    /** boolean array type */
    Type booleanArrayType;

    /** char array type */
    Type charArrayType;

    /** float array type */
    Type floatArrayType;

    /** double array type */
    Type doubleArrayType;

    /** byte array type */
    Type byteArrayType;

    /** short array type */
    Type shortArrayType;

    /** int array type */
    Type intArrayType;

    /** long array type */
    Type longArrayType;

    /**
     * Execute the given static method (Used only for static methods
     * with no args, eg. <clinit>)
     * @param method the method to execute
     */
    public static native void executeStatic(Method method);

    /**
     * Print a debug message (useful before System.out initialised)
     * @param message the message
     */
    static native void debug(String message);

    /**
     * @param c the class
     * @return the type object associated with the given class
     */
    static native Type getType(Class c);

    /**
     * This method is called by the vm if it encounters a reference to
     * an unresolved type, method or field. The instruction is aborted
     * and this method is run to resolve the reference. This method
     * should throw any suitable runtime exceptions defined for the
     * underlying instructions if resolution is not successful.
     * @throws ClassNotFoundException if the class is not found
     */
    static void resolve() throws ClassNotFoundException {
        // get frame of calling method
        Frame frame = Frame.currentFrame().getReturnFrame();
        Method method = frame.getMethod();
        byte[] code = method.getCode();
        int pc = frame.getPc();
        int op = (int) (code[pc] & 0xff);
        switch (op) {
            // These instructions all have a 16bit unsigned int constant pool
            // index directly after the opcode.
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case INVOKEINTERFACE:
            case GETSTATIC:
            case PUTSTATIC:
            case GETFIELD:
            case PUTFIELD:
            case NEW:
            case ANEWARRAY:
                Class caller = method.getOwner().getPeer();
                int index = readIndex(frame, 1);
                Entry[] pool = method.getPool();
                Entry entry = pool[index];
                if (op == ANEWARRAY) {
                    resolveArray((Type) entry);
                } else {
                    String classname = entry.getClassnameToResolve();
                    Core.debug("resolving class: " + classname);
                    Type type = resolveType(classname, caller);
                    Entry resolved = entry.resolve(type);
                    pool[index] = resolved;
                }
                break;
                
            default:
                throw new InternalError("Can't resolve for opcode: "
                                        + Opcodes.getName(op));
        }
    }

    /**
     * Read the 16bit unsigned integer at the given offset after
     * the current instruction in the given frame
     */
    private static int readIndex(Frame frame, int offset) {
        byte[] code = frame.getMethod().getCode();
        int pc = frame.getPc();
        int high = code[pc + offset] & 0xff;
        int low = code[pc + offset + 1] & 0xff;
        return (high << 8) | low;
    }

    /**
     * Resolve the type of the given name for the given caller class
     */
    private static Type resolveType(String name, Class caller)
            throws ClassNotFoundException
    {
        ClassLoader loader = caller.getClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        Class c = loader.loadClass(name.replace('/', '.'));
        return getType(c);
    }
    
    /**
     * @throws NullPointerException
     */
    static void throwNullPointerException() {
        throw new NullPointerException();
    }

    /**
     * @throws ClassCastException
     */
    static void throwClassCastException() {
        throw new ClassCastException();
    }

    /**
     * @throws ArrayIndexOutOfBoundsException
     */
    static void throwArrayIndexOutOfBounds() {
        throw new ArrayIndexOutOfBoundsException();
    }

    /**
     * @throws ArithmeticException
     */
    static void throwArithmetic() {
        throw new ArithmeticException();
    }

    /**
     * @return the core instance
     */
    static native Core get();

    /**
     * @return the boolean class
     */
    public static Class getBooleanClass() {
        return get().booleanArrayType.getPeer().getComponentType();
    }

    /**
     * @return the char class
     */
    public static Class getCharClass() {
        return get().charArrayType.getPeer().getComponentType();
    }

    /**
     * @return the float class
     */
    public static Class getFloatClass() {
        return get().floatArrayType.getPeer().getComponentType();
    }

    /**
     * @return the double class
     */
    public static Class getDoubleClass() {
        return get().doubleArrayType.getPeer().getComponentType();
    }

    /**
     * @return the byte class
     */
    public static Class getByteClass() {
        return get().byteArrayType.getPeer().getComponentType();
    }

    /**
     * @return the short class
     */
    public static Class getShortClass() {
        return get().shortArrayType.getPeer().getComponentType();
    }

    /**
     * @return the int class
     */
    public static Class getIntClass() {
        return get().intArrayType.getPeer().getComponentType();
    }

    /**
     * @return the long class
     */
    public static Class getLongClass() {
        return get().longArrayType.getPeer().getComponentType();
    }

    /**
     * Generate an array type for the given type
     * and set the field so the code can access it.
     */
    private static void resolveArray(Type type) throws ClassNotFoundException {
        Class peer = type.getPeer();
        String classname = peer.getName();
        String arrayname = (peer.isArray() || peer.isPrimitive())
                ? "[" + classname
                : "[L" + classname + ";";
        ClassLoader loader = peer.getClassLoader();
        if (loader == null) { loader = ClassLoader.getSystemClassLoader(); }
        Class arrayclass = loader.loadClass(arrayname);
        Type result = getType(arrayclass);
        type.setArrayType(result);
    }

}
