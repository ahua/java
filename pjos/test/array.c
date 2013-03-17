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
 * If the array pointer is null, throw a null pointer
 * exception. If the index is invalid throw an array out
 * of bounds exception. Return true only if no exception
 * was thrown.
 */
static int check(Ref array, int index) {
    if (array == NULL) {
        throwException(CORE_THROW_NULL_POINTER);
        return FALSE;
    }
    if (index < 0 || index > getInt(array, ARRAY_LENGTH)) {
        throwException(CORE_THROW_ARRAY_INDEX);
        return FALSE;
    }
    return TRUE;
}

/*
 * Execute the ARRAYLENGTH instruction
 */
void op_arraylength() {
    //printf("arraylength\n");
    Ref array = popRef();
    if (array == NULL) {
        throwException(CORE_THROW_NULL_POINTER);
        return;
    }
    //printf("length: %d\n", array[ARRAY_LENGTH].i);
    pushInt(getInt(array, ARRAY_LENGTH));
    pc++;
}

/*
 * Execute the IALOAD instruction
 */
void op_iaload() {
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    Int value = getInt(array, ARRAY_DATA + index);
    pushInt(value);
    pc++;
}

void op_laload() { printf("laload not implemented!!!\n"); exit(1); }
//	/**
//	 * laload instruction
//	 */
//	static void laload() {
//		int index = Stack.popData();
//		int array = Stack.popPointer();
//		if (check(array, index)) {
//			long high = Mem.load(array + 4*ARRAY_DATA + 4*index);
//			long low = Mem.load(array + 4*ARRAY_DATA + 4*index + 4)
//					& 0x00000000FFFFFFFFL;
//			Stack.pushLong((high << 32) | low);
//			Reg.instruction += 1;
//		}
//	}

/*
 * Execute the FALOAD instruction
 */
void op_faload() {
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    Float value = getFloat(array, ARRAY_DATA + index);
    pushFloat(value);
    pc++;
}

void op_daload() { printf("daload not implemented!!!\n"); exit(1); }
//	/**
//	 * daload instruction
//	 */
//	static void daload() {
//		int index = Stack.popData();
//		int array = Stack.popPointer();
//		if (check(array, index)) {
//			long high = Mem.load(array + 4*ARRAY_DATA + 4*index);
//			long low = Mem.load(array + 4*ARRAY_DATA + 4*index + 4)
//					& 0x00000000FFFFFFFFL;
//			Stack.pushLong((high << 32) | low);
//			Reg.instruction += 1;
//		}
//	}

/*
 * Execute the AALOAD instruction
 */
void op_aaload() {
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    pushRef(getRef(array, ARRAY_DATA + index));
    pc++;
}

/*
 * Execute the BALOAD instruction
 */
void op_baload() {
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    char* c = (char*) (array + ARRAY_DATA);
    pushInt(c[index]);
    pc++;
}

/*
 * Execute the CALOAD instruction
 */
void op_caload() {
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    unsigned short* s = (unsigned short*) (array + ARRAY_DATA);
    pushInt(s[index]);
    pc++;
}

/*
 * Execute the SALOAD instruction
 */
void op_saload() {
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    short* s = (short*) (array + ARRAY_DATA);
    pushInt(s[index]);
    pc++;
}

/*
 * Execute the IASTORE instruction
 */
void op_iastore() {
    int value = popInt();
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    array[ARRAY_DATA + index].i = value;
    pc++;
}

void op_lastore() { printf("array store instructions not implemented!!!\n"); exit(1); }
//	/**
//	 * lastore instruction
//	 */
//	static void lastore() {
//		long value = Stack.popLong();
//		int index = Stack.popData();
//		int array = Stack.popRef();
//		if (check(array, index)) {
//			int high = (int) (value >>> 32);
//			int low = (int) value;
//			Mem.store(high, array + 4*ARRAY_DATA + 8*index);
//			Mem.store(low, array + 4*ARRAY_DATA + 8*index + 4);
//			Reg.instruction += 1;
//		}
//	}

/*
 * Execute the FASTORE instruction
 */
void op_fastore() {
    float value = popFloat();
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    array[ARRAY_DATA + index].f = value;
    pc++;
}

void op_dastore() { printf("array store instructions not implemented!!!\n"); exit(1); }
//	/**
//	 * dastore instruction
//	 */
//	static void dastore() {
//		long value = Stack.popLong();
//		int index = Stack.popData();
//		int array = Stack.popPointer();
//		if (check(array, index)) {
//			int high = (int) (value >>> 32);
//			int low = (int) value;
//			Mem.store(high, array + 4*ARRAY_DATA + 8*index);
//			Mem.store(low, array + 4*ARRAY_DATA + 8*index + 4);
//			Reg.instruction += 1;
//		}
//	}

/*
 * Execute the AASTORE instruction
 */
void op_aastore() {
    Ref ref = popRef();
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    setRef(array, ARRAY_DATA + index, ref);
    pc++;
}

/*
 * Execute the BASTORE instruction
 */
void op_bastore() {
    int value = popInt();
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    char* c = (char*) (array + ARRAY_DATA);
    c[index] = (char) value;
    pc++;
}

/*
 * Execute the CASTORE instruction
 */
void op_castore() {
    int value = popInt();
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    unsigned short* s = (unsigned short*) (array + ARRAY_DATA);
    s[index] = (unsigned short) value;
    pc++;
}

/*
 * Execute the SASTORE instruction
 */
void op_sastore() {
    int value = popInt();
    int index = popInt();
    Ref array = popRef();
    if (!check(array, index)) { return; }
    short* s = (short*) (array + ARRAY_DATA);
    s[index] = (short) value;
    pc++;
}



