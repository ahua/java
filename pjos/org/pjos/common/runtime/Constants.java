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
 * Defines constants used by the runtime classes
 */
public interface Constants {

    /** Value of null pointer */
    int NULL = 0;

    /** Boolean value true representation */
    int TRUE = 1;

    /** Boolean value false representation */
    int FALSE = 0;


    /** Forwarding object header */
    int HEADER_FORWARD = 0xF0000000;

    /** Instance object header */
    int HEADER_INSTANCE = 0x0B000000;

    /** Data array object header */
    int HEADER_DATA_ARRAY = 0xDA000000;

    /** Object array object header */
    int HEADER_OBJECT_ARRAY = 0x0A000000;

    /** Static fields object header */
    int HEADER_STATIC_FIELDS = 0x5C000000;

    /** Stack frame object header */
    int HEADER_STACK_FRAME = 0x5F000000;


    /** Object offset of header */
    int OBJECT_HEADER = 0;

    /** Object offset of size */
    int OBJECT_SIZE = 1;

    /** Object offset of identity hash code */
    int OBJECT_HASHCODE = 2;

    /** Object offset of lock */
    int OBJECT_LOCK = 3;

    /** Object offset of type */
    int OBJECT_TYPE = 4;

    /** Object offset of instance fields */
    int OBJECT_FIELDS = 5;


    /** Type offset of entry id */
    int TYPE_ID = 5;

    /** Type offset of peer class */
    int TYPE_PEER = 6;

    /** Type offset of static fields */
    int TYPE_STATICS = 7;

    /** Type offset of name */
    int TYPE_NAME = 8;

    /** Type offset of code */
    int TYPE_CODE = 9;

    /** Type offset of flags */
    int TYPE_FLAGS = 10;

    /** Type offset of constant pool */
    int TYPE_POOL = 11;

    /** Type offset of super name */
    int TYPE_SUPER_NAME = 12;

    /** Type offset of super type */
    int TYPE_SUPER_TYPE = 13;

    /** Type offset of interface names */
    int TYPE_INTERFACE_NAMES = 14;

    /** Type offset of interface types */
    int TYPE_INTERFACE_TYPES = 15;

    /** Type offset of methods */
    int TYPE_METHODS = 16;

    /** Type offset of fields */
    int TYPE_FIELDS = 17;

    /** Type offset of instance field count */
    int TYPE_INSTANCE_FIELD_COUNT = 18;

    /** Type offset of static field count */
    int TYPE_STATIC_FIELD_COUNT = 19;

    /** Type offset of source */
    int TYPE_SOURCE = 20;

    /** Type offset of linked flag */
    int TYPE_LINKED = 21;

    /** Type offset of component name */
    int TYPE_COMPONENT_NAME = 22;

    /** Type offset of component type */
    int TYPE_COMPONENT_TYPE = 23;

    /** Type offset of array width */
    int TYPE_WIDTH = 24;

    /** Type offset of primitive flag */
    int TYPE_PRIMITIVE = 25;

    /** Type offset of array type */
    int TYPE_ARRAY_TYPE = 26;

    /** Type offset of instance map */
    int TYPE_INSTANCE_MAP = 27;

    /** Type offset of static map */
    int TYPE_STATIC_MAP = 28;


    /** String offset of character array */
    int STRING_CHARS = 5;

    /** String offset of first character */
    int STRING_FIRST = 6;

    /** String offset of last character */
    int STRING_LAST = 7;

    /** String offset of length field */
    int STRING_LENGTH = 8;

    /** String offset of hashcode field */
    int STRING_HASHCODE = 9;


    /** Frame offset of return frame */
    int FRAME_RETURN_FRAME = 5;

    /** Frame offset of method */
    int FRAME_METHOD = 6;

    /** Frame offset of return code pointer */
    int FRAME_RETURN_PC = 7;

    /** Frame offset of code pointer */
    int FRAME_PC = 8;

    /** Frame offset of stack pointer */
    int FRAME_SP = 9;

    /** Frame offset of local variables */
    int FRAME_LOCALS = 10;


    /** Entry offset of id */
    int ENTRY_ID = 5;

    /** Entry offset of name (Named class) */
    int ENTRY_NAME = 6;

    /** Entry offset of descriptor (Described class) */
    int ENTRY_DESCRIPTOR = 7;

    /** Entry offset of class name (Abstract class) */
    int ENTRY_CLASSNAME = 8;

    /** Entry offset of flags (Member class) */
    int ENTRY_FLAGS = 9;

    /** Member offset of owner class (Member class) */
    int ENTRY_OWNER = 10;


    /** Field offset of index */
    int FIELD_INDEX = 11;

    /** Field offset of reference flag */
    int FIELD_REFERENCE_FLAG = 12;

    /** Field offset of size */
    int FIELD_SIZE = 13;

    /** Field offset of constant value */
    int FIELD_CONSTANT_VALUE = 14;


    /** Method offset of constant pool */
    int METHOD_POOL = 11;

    /** Method offset of max stack entries */
    int METHOD_MAX_STACK = 12;

    /** Method offset of max local variables */
    int METHOD_MAX_LOCALS = 13;

    /** Method offset of argument count */
    int METHOD_ARG_COUNT = 14;

    /** Method offset of the code pointer */
    int METHOD_CODE = 15;

    /** Method offset of the exceptions */
    int METHOD_EXCEPTIONS = 16;

    /** Method offset of the line number table */
    int METHOD_LINE_NUMBERS = 17;

    /** Method offset of magic number */
    int METHOD_MAGIC = 18;


    /** Lock offset of owner thread */
    int LOCK_OWNER = 5;

    /** Lock offset of count */
    int LOCK_COUNT = 6;

    /** Lock offset of thread at head of lock list */
    int LOCK_LOCK_HEAD = 7;

    /** Lock offset of thread at tail of lock list */
    int LOCK_LOCK_TAIL = 8;

    /** Lock offset of thread at head of wait list */
    int LOCK_WAIT_HEAD = 9;

    /** Lock offset thread at tail of wait list */
    int LOCK_WAIT_TAIL = 10;


    /** Core offset of next object location */
    int CORE_NEXT = 5;

    /** Core offset of priority thread */
    int CORE_PRIORITY = 6;

    /** Core offset of running thead list */
    int CORE_RUNNING = 7;

    /** Core offset of sleeping thread list */
    int CORE_SLEEPING = 8;

    /** Core offset of idle thread */
    int CORE_IDLE = 9;

    /** Core offset of interrupt notifier */
    int CORE_NOTIFIER = 10;

    /** Core offset of thread run method */
    int CORE_THREAD_RUN_METHOD = 11;

    /** Core offset of resolver method */
    int CORE_RESOLVE_METHOD = 12;

    /** Core offset of null pointer method */
    int CORE_THROW_NULL_POINTER = 13;

    /** Core offset of class cast method */
    int CORE_THROW_CLASS_CAST = 14;

    /** Core offset of array index method */
    int CORE_THROW_ARRAY_INDEX = 15;

    /** Core offset of arithmetic method */
    int CORE_THROW_ARITHMETIC = 16;

    /** Core offset of architecture type */
    int CORE_ARCHITECTURE_TYPE = 17;

    /** Core offset of statics type */
    int CORE_STATICS_TYPE = 18;

    /** Core offset of lock type */
    int CORE_LOCK_TYPE = 19;

    /** Core offset of array types */
    int CORE_ARRAYS = 20 - 4; // Array offsets start at 4


    /** Thread offset of java thread */
    int THREAD_THREAD = 5;

    /** Thread offset of frame pointer */
    int THREAD_FRAME = 6;

    /** Thread offset of lock */
    int THREAD_LOCK = 7;

    /** Thread offset of lock count */
    int THREAD_LOCK_COUNT = 8;

    /** Thread offset of next lock thread */
    int THREAD_NEXT_LOCK = 9;

    /** Thread offset of previous lock thread */
    int THREAD_PREV_LOCK = 10;

    /** Thread offset of next running thread */
    int THREAD_NEXT_RUNNING = 11;

    /** Thread offset of previous running thread */
    int THREAD_PREV_RUNNING = 12;

    /** Thread offset of next sleeping thread */
    int THREAD_NEXT_SLEEPING = 13;

    /** Thread offset of previous sleeping thread */
    int THREAD_PREV_SLEEPING = 14;

    /** Thread offset of wakeup time (long takes 2 entries) */
    int THREAD_WAKEUP = 15;

    /** Thread offset of name */
    int THREAD_NAME = 17;

    /** Thread offset of priority */
    int THREAD_PRIORITY = 18;

    /** Thread offset of started flag */
    int THREAD_STARTED = 19;

    /** Thread offset of suspended flag */
    int THREAD_SUSPENDED = 20;


    /** Statics offset of reference map */
    int STATICS_MAP = 5;

    /** Statics offset of fields */
    int STATICS_FIELDS = 6;


    /** Array offset of length field */
    int ARRAY_LENGTH = 5;

    /** Array offset of data */
    int ARRAY_DATA = 6;


    /** Constant offset of first value */
    int CONSTANT_FIRST = 6;

    /** Constant offset of second value */
    int CONSTANT_SECOND = 7;


    /** System class offset of in */
    int SYSTEM_IN = 0;

    /** System class offset of out */
    int SYSTEM_OUT = 1;

    /** System class offset of err */
    int SYSTEM_ERR = 2;


    /** Boolean Array Type */
    int T_BOOLEAN = 4;

    /** Char Array Type */
    int T_CHAR = 5;

    /** Float Array Type */
    int T_FLOAT = 6;

    /** Double Array Type */
    int T_DOUBLE = 7;

    /** Byte Array Type */
    int T_BYTE = 8;

    /** Short Array Type */
    int T_SHORT = 9;

    /** Int Array Type */
    int T_INT = 10;

    /** Long Array Type */
    int T_LONG = 11;


    /** Constant Pool class tag */
    int CP_CLASS = 7;

    /** Constant Pool field reference tag */
    int CP_FIELDREF = 9;

    /** Constant Pool method reference tag */
    int CP_METHODREF = 10;

    /** Constant Pool interface method reference tag */
    int CP_INTERFACEMETHODREF = 11;

    /** Constant Pool string tag */
    int CP_STRING = 8;

    /** Constant Pool integer tag */
    int CP_INTEGER = 3;

    /** Constant Pool float tag */
    int CP_FLOAT = 4;

    /** Constant Pool long tag */
    int CP_LONG = 5;

    /** Constant Pool double tag */
    int CP_DOUBLE = 6;

    /** Constant Pool name and type tag */
    int CP_NAMEANDTYPE = 12;

    /** Constant Pool utf8 string tag */
    int CP_UTF8 = 1;


    /** Public Access Flag */
    int ACC_PUBLIC = 0x0001;

    /** Private Access Flag */
    int ACC_PRIVATE = 0x0002;

    /** Protected Access Flag */
    int ACC_PROTECTED = 0x0004;

    /** Static Access Flag */
    int ACC_STATIC = 0x0008;

    /** Final Access Flag */
    int ACC_FINAL = 0x0010;

    /** Super Access Flag */
    int ACC_SUPER = 0x0020;

    /** Synchronized Access Flag */
    int ACC_SYNCHRONIZED = 0x0020;

    /** Volatile Access Flag */
    int ACC_VOLATILE = 0x0040;

    /** Transient Access Flag */
    int ACC_TRANSIENT = 0x0080;

    /** Native Access Flag */
    int ACC_NATIVE = 0x0100;

    /** Interface Access Flag */
    int ACC_INTERFACE = 0x0200;

    /** Abstract Access Flag */
    int ACC_ABSTRACT = 0x0400;

    /** Strict Access Flag */
    int ACC_STRICT = 0x0800;

}

