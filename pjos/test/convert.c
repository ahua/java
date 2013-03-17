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
 * Execute the I2L instruction
 */
void op_i2l() {
    DoubleWord dw;
    dw.l = (long long int) popInt();
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the I2F instruction
 */
void op_i2f() {
    pushFloat((Float) popInt());
    pc++;
}

/*
 * Execute the I2D instruction
 */
void op_i2d() {
    DoubleWord dw;
    dw.d = (double) popInt();
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the L2I instruction
 */
void op_l2i() {
    pushInt((Int) popDoubleWord().l);
    pc++;
}

/*
 * Execute the L2F instruction
 */
void op_l2f() {
    pushFloat((Float) popDoubleWord().d);
    pc++;
}

/*
 * Execute the L2D instruction
 */
void op_l2d() {
    DoubleWord dw = popDoubleWord();
    dw.d = (double) dw.l;
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the F2I instruction
 */
void op_f2i() {
    pushInt((Int) popFloat());
    pc++;
}

/*
 * Execute the F2L instruction
 */
void op_f2l() {
    DoubleWord dw;
    dw.l = (long long int) popFloat();
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the F2D instruction
 */
void op_f2d() {
    DoubleWord dw;
    dw.d = (double) popFloat();
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the D2I instruction
 */
void op_d2i() {
    pushInt((Int) popDoubleWord().d);
    pc++;
}

/*
 * Execute the D2L instruction
 */
void op_d2l() {
    DoubleWord dw = popDoubleWord();
    dw.l = (long long int) dw.d;
    pushDoubleWord(dw);
    pc++;
}

/*
 * Execute the D2F instruction
 */
void op_d2f() {
    pushFloat((Float) popDoubleWord().d);
    pc++;
}

/*
 * Execute the I2B instruction
 */
void op_i2b() {
    pushInt((char) popInt());
    pc++;
}

/*
 * Execute the I2C instruction
 */
void op_i2c() {
    pushInt((unsigned short) popInt());
    pc++;
}

/*
 * Execute the I2S instruction
 */
void op_i2s() {
    pushInt((short) popInt());
    pc++;
}

