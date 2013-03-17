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
 * Execute the IADD instruction
 */
void op_iadd() {
    Int value2 = popInt();
    Int value1 = popInt();
    pushInt(value1 + value2);
    pc++;
}

/*
 * Execute the LADD instruction
 */
void op_ladd() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.l = value1.l + value2.l;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the FADD instruction
 */
void op_fadd() {
    Float value2 = popFloat();
    Float value1 = popFloat();
    pushFloat(value1 + value2);
    pc++;
}

/*
 * Execute the DADD instruction
 */
void op_dadd() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.d = value1.d + value2.d;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the ISUB instruction
 */
void op_isub() {
    Int value2 = popInt();
    Int value1 = popInt();
    pushInt(value1 - value2);
    pc++;
}

/*
 * Execute the LSUB instruction
 */
void op_lsub() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.l = value1.l - value2.l;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the FSUB instruction
 */
void op_fsub() {
    Float value2 = popFloat();
    Float value1 = popFloat();
    pushFloat(value1 - value2);
    pc++;
}

/*
 * Execute the DSUB instruction
 */
void op_dsub() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.d = value1.d - value2.d;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the IMUL instruction
 */
void op_imul() {
    Int value2 = popInt();
    Int value1 = popInt();
    pushInt(value1 * value2);
    pc++;
}

/*
 * Execute the LMUL instruction
 */
void op_lmul() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.l = value1.l * value2.l;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the FMUL instruction
 */
void op_fmul() {
    Float value2 = popFloat();
    Float value1 = popFloat();
    pushFloat(value1 * value2);
    pc++;
}

/*
 * Execute the DMUL instruction
 */
void op_dmul() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.d = value1.d * value2.d;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the IDIV instruction
 */
void op_idiv() {
    Int value2 = popInt();
    Int value1 = popInt();
    if (value2 == 0) {
        throwException(CORE_THROW_ARITHMETIC);
        return;
    }
    pushInt(value1 / value2);
    pc++;
}

/*
 * Execute the LDIV instruction
 */
void op_ldiv() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    if (value2.l == 0) {
        throwException(CORE_THROW_ARITHMETIC);
        return;
    }
    value1.l = value1.l / value2.l;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the FDIV instruction
 */
void op_fdiv() {
    Float value2 = popFloat();
    Float value1 = popFloat();
    pushFloat(value1 / value2);
    pc++;
}

/*
 * Execute the DDIV instruction
 */
void op_ddiv() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.d = value1.d / value2.d;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the IREM instruction
 */
void op_irem() {
    Int value2 = popInt();
    Int value1 = popInt();
    if (value2 == 0) {
        throwException(CORE_THROW_ARITHMETIC);
        return;
    }
    pushInt(value1 % value2);
    pc++;
}

/*
 * Execute the LREM instruction
 */
void op_lrem() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    if (value2.l == 0) {
        throwException(CORE_THROW_ARITHMETIC);
        return;
    }
    value1.l = value1.l % value2.l;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the FREM instruction
 */
void op_frem() {
    printf("frem not implemented!!!\n");
    exit(1);
    /*
    Float value2 = popFloat();
    Float value1 = popFloat();
    pushFloat(value1 % value2);
    pc++;
    */
}

/*
 * Execute the DREM instruction
 */
void op_drem() {
    printf("drem not implemented!!!\n");
    exit(1);
    /*
    Double value2 = popDouble();
    Double value1 = popDouble();
    pushDouble(value1 % value2);
    pc++;
    */
}

/*
 * Execute the INEG instruction
 */
void op_ineg() {
    Int value = popInt();
    pushInt(-value);
    pc++;
}

/*
 * Execute the LNEG instruction
 */
void op_lneg() {
    DoubleWord dw = popDoubleWord();
    dw.l = -dw.l;
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the FNEG instruction
 */
void op_fneg() {
    Float value = popFloat();
    pushFloat(-value);
    pc++;
}

/*
 * Execute the DNEG instruction
 */
void op_dneg() {
    DoubleWord dw = popDoubleWord();
    dw.d = -dw.d;
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the IINC instruction
 */
void op_iinc() {
    int index = u8(1);
    Int value = getLocalInt(index);
    setLocalInt(index, value + s8(2), frame);
    pc += 3;
}




