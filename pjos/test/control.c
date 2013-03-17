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
 * Execute the GOTO instruction
 */
void op_goto() {
    pc += s16(1);
}

/*
 * Execute the JSR instruction
 */
void op_jsr() {
    pushInt(pc + 3);
    pc += s16(1);
}

/*
 * Execute the RET instruction
 */
void op_ret() {
    pc = getLocalInt(u8(1));
}

/*
 * Execute the IFEQ instruction
 */
void op_ifeq() {
    if (popInt() == 0) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IFNE instruction
 */
void op_ifne() {
    if (popInt() != 0) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IFLT instruction
 */
void op_iflt() {
    if (popInt() < 0) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IFGE instruction
 */
void op_ifge() {
    if (popInt() >= 0) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IFGT instruction
 */
void op_ifgt() {
    if (popInt() > 0) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IFLE instruction
 */
void op_ifle() {
    if (popInt() <= 0) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IF_ICMPEQ instruction
 */
void op_if_icmpeq() {
    int value2 = popInt();
    int value1 = popInt();
    if (value1 == value2) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IF_ICMPNE instruction
 */
void op_if_icmpne() {
    int value2 = popInt();
    int value1 = popInt();
    if (value1 != value2) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IF_ICMPLT instruction
 */
void op_if_icmplt() {
    int value2 = popInt();
    int value1 = popInt();
    if (value1 < value2) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IF_ICMPGE instruction
 */
void op_if_icmpge() {
    int value2 = popInt();
    int value1 = popInt();
    if (value1 >= value2) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IF_ICMPGT instruction
 */
void op_if_icmpgt() {
    int value2 = popInt();
    int value1 = popInt();
    if (value1 > value2) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IF_ICMPLE instruction
 */
void op_if_icmple() {
    int value2 = popInt();
    int value1 = popInt();
    if (value1 <= value2) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IF_ACMPEQ instruction
 */
void op_if_acmpeq() {
    Ref value2 = popRef();
    Ref value1 = popRef();
    if (value1 == value2) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IF_ACMPNE instruction
 */
void op_if_acmpne() {
    Ref value2 = popRef();
    Ref value1 = popRef();
    if (value1 != value2) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IFNULL instruction
 */
void op_ifnull() {
    if (popRef() == NULL) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the IFNONNULL instruction
 */
void op_ifnonnull() {
    if (popRef() != NULL) { pc += s16(1); }
    else { pc += 3; }
}

/*
 * Execute the LCMP instruction
 */
void op_lcmp() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    int result = 0;
    if (value1.l > value2.l) { result = 1; }
    else if (value1.l < value2.l) { result = -1; }
    pushInt(result);
    pc++;
}

/*
 * Execute the FCMPL instruction
 */
void op_fcmpl() {
    float f2 = popFloat();
    float f1 = popFloat();
    int k = -1;
    if (f1 > f2) { k = 1; }
    if (f1 == f2) { k = 0; }
    pushInt(k);
    pc++;
}

/*
 * Execute the FCMPG instruction
 */
void op_fcmpg() {
    float f2 = popFloat();
    float f1 = popFloat();
    int k = 1;
    if (f1 < f2) { k = -1; }
    if (f1 == f2) { k = 0; }
    pushInt(k);
    pc++;
}

