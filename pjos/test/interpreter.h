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

// null pointer definition (in case stdio not used)
#ifndef NULL
#define NULL 0
#endif

// Access constants from java spec
#define ACC_PUBLIC                              0x1
#define ACC_PRIVATE                             0x2
#define ACC_PROTECTED                           0x4
#define ACC_STATIC                              0x8
#define ACC_FINAL                               0x10
#define ACC_SUPER                               0x20
#define ACC_SYNCHRONIZED                        0x20
#define ACC_VOLATILE                            0x40
#define ACC_TRANSIENT                           0x80
#define ACC_NATIVE                              0x100
#define ACC_INTERFACE                           0x200
#define ACC_ABSTRACT                            0x400
#define ACC_STRICT                              0x800

// array layout
#define ARRAY_LENGTH                             5
#define ARRAY_DATA                               6

// constant layout for 32/64 bit values
#define CONSTANT_FIRST                           6
#define CONSTANT_SECOND                          7

// core object layout
#define CORE_NEXT								 5
#define CORE_PRIORITY							 6
#define CORE_RUNNING							 7
#define CORE_SLEEPING							 8
#define CORE_IDLE								 9
#define CORE_NOTIFIER							 10
#define CORE_THREAD_RUN_METHOD					 11
#define CORE_RESOLVE_METHOD						 12
#define CORE_THROW_NULL_POINTER					 13
#define CORE_THROW_CLASS_CAST					 14
#define CORE_THROW_ARRAY_INDEX					 15
#define CORE_THROW_ARITHMETIC					 16
#define CORE_ARCHITECTURE_TYPE					 17
#define CORE_STATICS_TYPE						 18
#define CORE_LOCK_TYPE							 19
#define CORE_ARRAYS								 20 - 4

// entry object layout
#define ENTRY_ID								 5
#define ENTRY_NAME								 6
#define ENTRY_DESCRIPTOR						 7
#define ENTRY_CLASSNAME							 8
#define ENTRY_FLAGS								 9
#define ENTRY_OWNER								 10

// field object layout
#define FIELD_INDEX								 11
#define FIELD_REFERENCE_FLAG					 12
#define FIELD_SIZE								 13
#define FIELD_CONSTANT_VALUE					 14

// frame object layout
#define FRAME_RETURN_FRAME						 5
#define FRAME_METHOD							 6
#define FRAME_RETURN_PC							 7
#define FRAME_PC								 8
#define FRAME_SP								 9
#define FRAME_LOCALS							 10

// object headers
#define HEADER_FORWARD							 0xF0000000
#define HEADER_INSTANCE							 0x0B000000
#define HEADER_DATA_ARRAY						 0xDA000000
#define HEADER_OBJECT_ARRAY						 0x0A000000
#define HEADER_STATIC_FIELDS					 0x5C000000
#define HEADER_STACK_FRAME						 0x5F000000
#define HEADER_END                               0xED000000

// runtime constant pool entry ids
#define ID_TYPE									 1
#define ID_UNRESOLVED_TYPE						 2
#define ID_FIELD								 3
#define ID_UNRESOLVED_FIELD						 4
#define ID_METHOD								 5
#define ID_UNRESOLVED_METHOD					 6
#define ID_INTEGER_CONSTANT						 7
#define ID_LONG_CONSTANT						 8
#define ID_STRING_CONSTANT						 9

// lock layout
#define LOCK_OWNER								 5
#define LOCK_COUNT								 6
#define LOCK_LOCK_HEAD							 7
#define LOCK_LOCK_TAIL							 8
#define LOCK_WAIT_HEAD							 9
#define LOCK_WAIT_TAIL							 10

// magic ids for runtime methods
#define MAGIC_RUNTIME_CORE_DEBUG				 1
#define MAGIC_RUNTIME_CORE_EXECUTE_STATIC		 2
#define MAGIC_RUNTIME_CORE_GET					 3
#define MAGIC_RUNTIME_CORE_GET_TYPE				 4
#define MAGIC_RUNTIME_FRAME_CURRENT				 5
#define MAGIC_RUNTIME_IDLE_SLEEP				 6
#define MAGIC_RUNTIME_STATICS_CREATE			 7
#define MAGIC_RUNTIME_THREAD_CURRENT			 8
#define MAGIC_RUNTIME_THREAD_SLEEP				 9
#define MAGIC_RUNTIME_THREAD_START_HOOK			 10
#define MAGIC_RUNTIME_THREAD_SUSPEND			 11
#define MAGIC_RUNTIME_THREAD_RESUME				 12

// magic ids for java methods
#define MAGIC_JAVA_OBJECT_GET_CLASS				 13
#define MAGIC_JAVA_OBJECT_NOTIFY				 14
#define MAGIC_JAVA_OBJECT_NOTIFY_ALL			 15
#define MAGIC_JAVA_OBJECT_WAIT					 16
#define MAGIC_JAVA_SYSTEM_TIME					 17
#define MAGIC_JAVA_SYSTEM_HASHCODE				 18
#define MAGIC_JAVA_SYSTEM_SET_ERR				 19
#define MAGIC_JAVA_SYSTEM_SET_OUT				 20
#define MAGIC_JAVA_SYSTEM_SET_IN				 21

// magic ids for x86-specific methods
#define MAGIC_TEST_WRITE_TO_CONSOLE				 22
#define MAGIC_TEST_READ_FROM_CONSOLE             23
#define MAGIC_TEST_WRITE_TO_FLOPPY               24
#define MAGIC_TEST_READ_FROM_FLOPPY              25

// method object layout
#define METHOD_POOL								 11
#define METHOD_MAX_STACK						 12
#define METHOD_MAX_LOCALS						 13
#define METHOD_ARG_COUNT						 14
#define METHOD_CODE								 15
#define METHOD_EXCEPTIONS						 16
#define METHOD_LINE_NUMBERS						 17
#define METHOD_MAGIC							 18

// object layout
#define OBJECT_ID   							 0
#define OBJECT_SIZE								 1
#define OBJECT_HASHCODE							 2
#define OBJECT_LOCK								 3
#define OBJECT_TYPE								 4
#define OBJECT_FIELDS							 5

// the number of instructions to execute for a thread
// before moving to the next
#define SLICE_MAX								 10

// statics layout
#define STATICS_MAP								 5
#define STATICS_FIELDS							 6

// string layout
#define STRING_CHARS							 5
#define STRING_FIRST							 6
#define STRING_LAST								 7
#define STRING_LENGTH							 8
#define STRING_HASHCODE							 9

// system class static fields
#define SYSTEM_IN								 0
#define SYSTEM_OUT								 1
#define SYSTEM_ERR								 2

// array types
#define T_BOOLEAN								 4
#define T_CHAR									 5
#define T_FLOAT									 6
#define T_DOUBLE								 7
#define T_BYTE									 8
#define T_SHORT									 9
#define T_INT									 10
#define T_LONG									 11

// thread object layout
#define THREAD_THREAD							 5
#define THREAD_FRAME							 6
#define THREAD_LOCK								 7
#define THREAD_LOCK_COUNT						 8
#define THREAD_NEXT_LOCK						 9
#define THREAD_PREV_LOCK						 10
#define THREAD_NEXT_RUNNING						 11
#define THREAD_PREV_RUNNING						 12
#define THREAD_NEXT_SLEEPING					 13
#define THREAD_PREV_SLEEPING					 14
#define THREAD_WAKEUP							 15
// thread_wakeup takes two entries		         16
#define THREAD_NAME								 17
#define THREAD_PRIORITY							 18
#define THREAD_STARTED							 19
#define THREAD_SUSPENDED						 20

// type layout
#define TYPE_ID									 5
#define TYPE_PEER								 6
#define TYPE_STATICS							 7
#define TYPE_NAME								 8
#define TYPE_CODE								 9
#define TYPE_FLAGS								 10
#define TYPE_POOL								 11
#define TYPE_SUPER_NAME							 12
#define TYPE_SUPER_TYPE							 13
#define TYPE_INTERFACE_NAMES					 14
#define TYPE_INTERFACE_TYPES					 15
#define TYPE_METHODS							 16
#define TYPE_FIELDS								 17
#define TYPE_INSTANCE_FIELD_COUNT				 18
#define TYPE_STATIC_FIELD_COUNT					 19
#define TYPE_SOURCE								 20
#define TYPE_LINKED								 21
#define TYPE_COMPONENT_NAME						 22
#define TYPE_COMPONENT_TYPE						 23
#define TYPE_WIDTH								 24
#define TYPE_PRIMITIVE							 25
#define TYPE_ARRAY_TYPE							 26
#define TYPE_INSTANCE_MAP						 27
#define TYPE_STATIC_MAP							 28

// values
#define TRUE								     1
#define FALSE								     0

// Type definitions corresponding to Java VM-defined primitive types
typedef char Byte;
typedef short Short;
typedef unsigned short Char;
typedef int Int;
typedef float Float;
typedef char Boolean;

// each 32-bit word slot in memory represents a value or a reference
typedef union word Word;
typedef struct var Var;
typedef Word* Ref; // java reference (pointer to an object)
union word {
    Ref r;
    Int i;
    Float f;
    unsigned int u;
};
struct var {
    Word value;
    int flag;
};

// a 64-bit double or long consists of two 32-bit words
typedef struct composite Composite;
struct composite {
    unsigned int low;
    unsigned int high;
};
typedef union doubleword DoubleWord;
union doubleword {
    Composite c;
    unsigned long long int u;
    long long int l;
    double d;
};

// these values are set by the platform specific code
extern char* heap;          // start of heap
extern char* heapLimit;     // end of heap

// debug methods
void debugString(Ref string);
void debugLine(Ref string);
void debugTrace();
void checkReference(Ref obj);

// the virtual registers for the VM
extern Ref core;            // reference to core object
extern Ref thread;          // reference to current thread
extern Ref frame;           // reference to current frame
extern Ref method;          // reference to current method
extern Ref pool;            // reference to constant pool
extern Var* locals;         // reference to locals of current frame
extern Var* stack;          // reference to current top of stack
extern unsigned char* code; // points to first bytecode of current method
extern int pc;              // program counter

// the array containing implementation functions for each byte code
extern void* distributor[]; 

// platform specific
int readFromFloppy(int pos);
char readFromConsole();

// memory functions
Ref allocate(int numWords, int header);
Float getFloat(Ref object, int offset);
Int getInt(Ref object, int offset);
Ref getRef(Ref object, int offset);
void setFloat(Ref object, int offset, Float value);
void setInt(Ref object, int offset, Int value);
void setRef(Ref object, int offset, Ref ref);

// stack functions
int refOnStack();
Int peekInt();
Ref peekRef();
DoubleWord popDoubleWord();
Float popFloat();
Int popInt();
Ref popRef();
unsigned int popUInt();
void pushInt(Int value);
void pushFloat(Float value);
void pushRef(Ref ref);
void pushUInt(unsigned int value);
void pushDoubleWord(DoubleWord value);

// lock functions
void acquireLock(Ref thread, Ref lock, int increment);
void addToLockQueue(Ref thread, Ref lock);
Ref getLock(Ref object);
void unlockLock(Ref lock);

// local variables for stack frames
Float getLocalFloat(int index);
Int getLocalInt(int index);
Ref getLocalRef(int index);
DoubleWord getLocalDoubleWord(int index);
void setLocalFloat(int index, Float value, Ref frame);
void setLocalInt(int index, Int value, Ref frame);
void setLocalRef(int index, Ref ref, Ref frame);
void setLocalDoubleWord(int index, DoubleWord value, Ref frame);

// thread functions
void loadRegisters();
void saveRegisters();
void schedule(Ref thread);
void unschedule(Ref thread);
void scheduleNextThread();
void wakeSleepingThreads();
void startNewThread(int offset);

// VM actions
int executeMethod(Ref target, Ref method, int offset);
Ref getPoolEntry(int index);
Ref resolve(int index, int id); 
void throwException(int offset);

// these functions implement the JVM bytecode instructions
void op_aconst_null();
void op_iconst_m1();
void op_iconst_0();
void op_iconst_1();
void op_iconst_2();
void op_iconst_3();
void op_iconst_4();
void op_iconst_5();
void op_lconst_0();
void op_lconst_1();
void op_fconst_0();
void op_fconst_1();
void op_fconst_2();
void op_bipush();
void op_sipush();
void op_dconst_2();
void op_ldc();
void op_ldc_w();
void op_ldc2_w();
void op_iload();
void op_lload();
void op_fload();
void op_dload();
void op_aload();
void op_iload_0();
void op_iload_1();
void op_iload_2();
void op_iload_3();
void op_lload_0();
void op_lload_1();
void op_lload_2();
void op_lload_3();
void op_fload_0();
void op_fload_1();
void op_fload_2();
void op_fload_3();
void op_aload_0();
void op_aload_1();
void op_aload_2();
void op_aload_3();
void op_iaload();
void op_laload();
void op_faload();
void op_daload();
void op_aaload();
void op_baload();
void op_caload();
void op_saload();
void op_istore();
void op_lstore();
void op_fstore();
void op_dstore();
void op_astore();
void op_istore_0();
void op_istore_1();
void op_istore_2();
void op_istore_3();
void op_lstore_0();
void op_lstore_1();
void op_lstore_2();
void op_lstore_3();
void op_astore_0();
void op_astore_1();
void op_astore_2();
void op_astore_3();
void op_iastore();
void op_lastore();
void op_fastore();
void op_dastore();
void op_aastore();
void op_bastore();
void op_castore();
void op_sastore();
void op_pop();
void op_pop2();
void op_dup();
void op_dup_x1();
void op_dup_x2();
void op_dup2();
void op_dup2_x1();
void op_dup2_x2();
void op_swap();
void op_iadd();
void op_ladd();
void op_fadd();
void op_dadd();
void op_isub();
void op_lsub();
void op_fsub();
void op_dsub();
void op_imul();
void op_lmul();
void op_fmul();
void op_dmul();
void op_idiv();
void op_ldiv();
void op_fdiv();
void op_ddiv();
void op_irem();
void op_lrem();
void op_frem();
void op_drem();
void op_ineg();
void op_lneg();
void op_fneg();
void op_dneg();
void op_ishl();
void op_lshl();
void op_ishr();
void op_lshr();
void op_iushr();
void op_lushr();
void op_iand();
void op_land();
void op_ior();
void op_lor();
void op_ixor();
void op_lxor();
void op_iinc();
void op_i2l();
void op_i2f();
void op_i2d();
void op_l2i();
void op_l2f();
void op_l2d();
void op_f2i();
void op_f2l();
void op_f2d();
void op_d2i();
void op_d2l();
void op_d2f();
void op_i2b();
void op_i2c();
void op_i2s();
void op_lcmp();
void op_fcmpl();
void op_fcmpg();
void op_ifeq();
void op_ifne();
void op_iflt();
void op_ifge();
void op_ifgt();
void op_ifle();
void op_if_icmpeq();
void op_if_icmpne();
void op_if_icmplt();
void op_if_icmpge();
void op_if_icmpgt();
void op_if_icmple();
void op_if_acmpeq();
void op_if_acmpne();
void op_goto();
void op_jsr();
void op_ret();
void op_tableswitch();
void op_lookupswitch();
void op_ireturn();
void op_lreturn();
void op_freturn();
void op_dreturn();
void op_areturn();
void op_return();
void op_getstatic();
void op_putstatic();
void op_getfield();
void op_putfield();
void op_invokevirtual();
void op_invokespecial();
void op_invokestatic();
void op_invokeinterface();
void op_new();
void op_newarray();
void op_anewarray();
void op_arraylength();
void op_athrow();
void op_checkcast();
void op_instanceof();
void op_monitorenter();
void op_monitorexit();
void op_multianewarray();
void op_ifnull();
void op_ifnonnull();





