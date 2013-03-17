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
 * Execute the LSTORE instruction
 */
void op_lstore() {
    setLocalDoubleWord(u8(1), popDoubleWord(), frame);
    pc += 2;
}

/*
 * Execute the FSTORE instruction
 */
void op_fstore() {
    setLocalFloat(u8(1), popFloat(), frame);
    pc += 2;
}

/*
 * Execute the DSTORE instruction
 */
void op_dstore() {
    setLocalDoubleWord(u8(1), popDoubleWord(), frame);
    pc += 2;
}

/*
 * Execute the ISTORE instruction
 */
void op_istore() {
    setLocalInt(u8(1), popInt(), frame);
    pc += 2;
}

/*
 * Execute the ISTORE_0 instruction
 */
void op_istore_0() {
    setLocalInt(0, popInt(), frame);
    pc++;
}

/*
 * Execute the ISTORE_1 instruction
 */
void op_istore_1() {
    setLocalInt(1, popInt(), frame);
    pc++;
}

/*
 * Execute the ISTORE_2 instruction
 */
void op_istore_2() {
    setLocalInt(2, popInt(), frame);
    pc++;
}

/*
 * Execute the ISTORE_3 instruction
 */
void op_istore_3() {
    setLocalInt(3, popInt(), frame);
    pc++;
}

/*
 * Execute the LSTORE_0 instruction
 */
void op_lstore_0() {
    setLocalDoubleWord(0, popDoubleWord(), frame);
    pc++;
}

/*
 * Execute the LSTORE_1 instruction
 */
void op_lstore_1() {
    setLocalDoubleWord(1, popDoubleWord(), frame);
    pc++;
}

/*
 * Execute the LSTORE_2 instruction
 */
void op_lstore_2() {
    setLocalDoubleWord(2, popDoubleWord(), frame);
    pc++;
}

/*
 * Execute the LSTORE_3 instruction
 */
void op_lstore_3() {
    setLocalDoubleWord(3, popDoubleWord(), frame);
    pc++;
}

//	/**
//	 * fstore_x instruction
//	 */
//	static void fstore_x(int index) {
//		Locals.storeDataToLocal(Stack.popData(), index);
//		Reg.instruction += 1;
//	}
//	
//	/**
//	 * dstore_x instruction
//	 */
//	static void dstore_x(int index) {
//		Locals.storeLongToLocal(Stack.popLong(), index);
//		Reg.instruction += 1;
//	}
//
//


/*
 * Pop a value off the stack and store it to the local
 * variable with the specified index
 */
static void popToLocal(int index) {
    if (refOnStack()) { setLocalRef(index, popRef(), frame); }
    else { setLocalInt(index, popInt(), frame); }
}

/*
 * Execute the ASTORE instruction
 */
void op_astore() {
    popToLocal(u8(1));
    pc += 2;
}

/*
 * Execute the ASTORE_0 instruction
 */
void op_astore_0() {
    popToLocal(0);
    pc++;
}

/*
 * Execute the ASTORE_1 instruction
 */
void op_astore_1() {
    popToLocal(1);
    pc++;
}

/*
 * Execute the ASTORE_2 instruction
 */
void op_astore_2() {
    popToLocal(2);
    pc++;
}

/*
 * Execute the ASTORE_3 instruction
 */
void op_astore_3() {
    popToLocal(3);
    pc++;
}

