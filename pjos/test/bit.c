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
 * Execute the ISHL instruction
 */
void op_ishl() {
    unsigned int value2 = popUInt();
    Int value1 = popInt();
    pushInt(value1 << value2);
    pc++;
}

/*
 * Execute the LSHL instruction
 */
void op_lshl() {
    unsigned int value2 = popUInt();
    DoubleWord value1 = popDoubleWord();
    value1.l = value1.l << value2;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the ISHR instruction
 */
void op_ishr() {
    unsigned int value2 = popUInt();
    Int value1 = popInt();
    pushInt(value1 >> value2);
    pc++;
}

/*
 * Execute the LSHR instruction
 */
void op_lshr() {
    unsigned int value2 = popUInt();
    DoubleWord value1 = popDoubleWord();
    value1.l = value1.l >> value2;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the IUSHR instruction
 */
void op_iushr() {
    unsigned int value2 = popUInt();
    unsigned int value1 = popUInt();
    pushUInt(value1 >> value2);
    pc++;
}

/*
 * Execute the LUSHR instruction
 */
void op_lushr() {
    unsigned int value2 = popUInt();
    DoubleWord value1 = popDoubleWord();
    value1.u = value1.u >> value2;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the IAND instruction
 */
void op_iand() {
    Int value2 = popInt();
    Int value1 = popInt();
    pushInt(value1 & value2);
    pc++;
}

/*
 * Execute the LAND instruction
 */
void op_land() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.l = value1.l & value2.l;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the IOR instruction
 */
void op_ior() {
    Int value2 = popInt();
    Int value1 = popInt();
    pushInt(value1 + value2);
    pc++;
}

/*
 * Execute the LOR instruction
 */
void op_lor() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.l = value1.l | value2.l;
    pushDoubleWord(value1);
    pc++;
}

/*
 * Execute the IXOR instruction
 */
void op_ixor() {
    Int value2 = popInt();
    Int value1 = popInt();
    pushInt(value1 ^ value2);
    pc++;
}

/*
 * Execute the LXOR instruction
 */
void op_lxor() {
    DoubleWord value2 = popDoubleWord();
    DoubleWord value1 = popDoubleWord();
    value1.l = value1.l ^ value2.l;
    pushDoubleWord(value1);
    pc++;
}




