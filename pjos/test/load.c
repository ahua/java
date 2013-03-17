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
 * Load the 32-bit constant pool entry with
 * the given index.
 */
static void loadConstant(int index) {
    Ref entry = getPoolEntry(index);
    Int id = getInt(entry, ENTRY_ID);
    if (id == ID_STRING_CONSTANT) {
        pushRef(getRef(entry, CONSTANT_FIRST));
    } else {
        pushInt(getInt(entry, CONSTANT_FIRST));
    }
}

/*
 * Execute the ACONST_NULL instruction
 */
void op_aconst_null() {
    pushRef(NULL);
    pc++;
}

/*
 * Execute the ICONST_M1 instruction
 */
void op_iconst_m1() {
    pushInt(-1);
    pc++;
}

/*
 * Execute the ICONST_0 instruction
 */
void op_iconst_0() {
    pushInt(0);
    pc++;
}

/*
 * Execute the ICONST_1 instruction
 */
void op_iconst_1() {
    pushInt(1);
    pc++;
}

/*
 * Execute the ICONST_2 instruction
 */
void op_iconst_2() {
    pushInt(2);
    pc++;
}

/*
 * Execute the ICONST_3 instruction
 */
void op_iconst_3() {
    pushInt(3);
    pc++;
}

/*
 * Execute the ICONST_4 instruction
 */
void op_iconst_4() {
    pushInt(4);
    pc++;
}

/*
 * Execute the ICONST_5 instruction
 */
void op_iconst_5() {
    pushInt(5);
    pc++;
}

/*
 * Execute the LCONST_0 instruction
 */
void op_lconst_0() {
    DoubleWord dw;
    dw.l = 0;
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the LCONST_1 instruction
 */
void op_lconst_1() {
    DoubleWord dw;
    dw.l = 1;
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the FCONST_0 instruction
 */
void op_fconst_0() {
    pushFloat(0.0f);
    pc++;
}

/*
 * Execute the FCONST_1 instruction
 */
void op_fconst_1() {
    pushFloat(1.0f);
    pc++;
}

/*
 * Execute the FCONST_2 instruction
 */
void op_fconst_2() {
    pushFloat(2.0f);
    pc++;
}

/*
 * Execute the LDC instruction
 */
void op_ldc() {
    loadConstant(u8(1));
    pc += 2;
}

/*
 * Execute the LDC_W instruction
 */
void op_ldc_w() {
    loadConstant(u16(1));
    pc += 3;
}

/*
 * Execute the LDC2_W instruction
 */
void op_ldc2_w() {
    Ref entry = getPoolEntry(u16(1));
    pushInt(getInt(entry, CONSTANT_FIRST));  // high
    pushInt(getInt(entry, CONSTANT_SECOND)); // low
    pc += 3;
}

/*
 * Execute the ALOAD instruction
 */
void op_aload() {
    pushRef(getLocalRef(u8(1)));
    pc += 2;
}

/*
 * Execute the ILOAD instruction
 */
void op_iload() {
    pushInt(getLocalInt(u8(1)));
    pc += 2;
}

/*
 * Execute the FLOAD instruction
 */
void op_fload() {
    pushFloat(getLocalFloat(u8(1)));
    pc += 2;
}

/*
 * Execute the LLOAD instruction
 */
void op_lload() {
    pushDoubleWord(getLocalDoubleWord(u8(1)));
    pc += 2;
}

/*
 * Execute the DLOAD instruction
 */
void op_dload() {
    pushDoubleWord(getLocalDoubleWord(u8(1)));
    pc += 2;
}

/*
 * Execute the ALOAD_0 instruction
 */
void op_aload_0() {
    pushRef(getLocalRef(0));
    pc++;
}

/*
 * Execute the ALOAD_1 instruction
 */
void op_aload_1() {
    pushRef(getLocalRef(1));
    pc++;
}

/*
 * Execute the ALOAD_2 instruction
 */
void op_aload_2() {
    pushRef(getLocalRef(2));
    pc++;
}

/*
 * Execute the ALOAD_3 instruction
 */
void op_aload_3() {
    pushRef(getLocalRef(3));
    pc++;
}

/*
 * Execute the ILOAD_0 instruction
 */
void op_iload_0() {
    pushInt(getLocalInt(0));
    pc++;
}

/*
 * Execute the ILOAD_1 instruction
 */
void op_iload_1() {
    pushInt(getLocalInt(1));
    pc++;
}

/*
 * Execute the ILOAD_2 instruction
 */
void op_iload_2() {
    pushInt(getLocalInt(2));
    pc++;
}

/*
 * Execute the ILOAD_3 instruction
 */
void op_iload_3() {
    pushInt(getLocalInt(3));
    pc++;
}

/*
 * Execute the FLOAD_0 instruction
 */
void op_fload_0() {
    pushFloat(getLocalFloat(0));
    pc++;
}

/*
 * Execute the FLOAD_1 instruction
 */
void op_fload_1() {
    pushFloat(getLocalFloat(1));
    pc++;
}

/*
 * Execute the FLOAD_2 instruction
 */
void op_fload_2() {
    pushFloat(getLocalFloat(2));
    pc++;
}

/*
 * Execute the FLOAD_3 instruction
 */
void op_fload_3() {
    pushFloat(getLocalFloat(3));
    pc++;
}

/*
 * Execute the LLOAD_0 instruction
 */
void op_lload_0() {
    pushDoubleWord(getLocalDoubleWord(0));
    pc++;
}

/*
 * Execute the LLOAD_1 instruction
 */
void op_lload_1() {
    pushDoubleWord(getLocalDoubleWord(1));
    pc++;
}

/*
 * Execute the LLOAD_2 instruction
 */
void op_lload_2() {
    pushDoubleWord(getLocalDoubleWord(2));
    pc++;
}

/*
 * Execute the LLOAD_3 instruction
 */
void op_lload_3() {
    pushDoubleWord(getLocalDoubleWord(3));
    pc++;
}

