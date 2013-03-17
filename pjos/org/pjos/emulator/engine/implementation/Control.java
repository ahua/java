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
package org.pjos.emulator.engine.implementation;

/**
 * Contains code to implement the control flow instructions
 */
class Control implements Constants {
        
    /**
     * goto instruction
     */
    static void op_goto() {
        int offset = Instruction.signTwoByteCodes(1);
        Reg.instruction += offset;
    }
    
    /**
     * jsr instruction
     */
    static void jsr() {
        int pc = Reg.instruction - Reg.code;
        Stack.pushData(pc + 3);
        int offset = Instruction.signTwoByteCodes(1);
        Reg.instruction += offset;
    }
    
    /**
     * ret instruction
     */
    static void ret() {
        int index = Instruction.byteCode(1);
        int pc = Locals.getDataFromLocal(index);
        Reg.instruction = Reg.code + pc;
    }
    
    /**
     * ifeq instruction
     */
    static void ifeq() {
        int offset = Instruction.signTwoByteCodes(1);
        int value = Stack.popData();
        if (value == 0) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }
    
    /**
     * ifne instruction
     */
    static void ifne() {
        int offset = Instruction.signTwoByteCodes(1);
        int value = Stack.popData();
        if (value != 0) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }
    
    /**
     * iflt instruction
     */
    static void iflt() {
        int offset = Instruction.signTwoByteCodes(1);
        int value = Stack.popData();
        if (value < 0) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }
    
    /**
     * ifge instruction
     */
    static void ifge() {
        int offset = Instruction.signTwoByteCodes(1);
        int value = Stack.popData();
        if (value >= 0) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }
    
    /**
     * ifgt instruction
     */
    static void ifgt() {
        int offset = Instruction.signTwoByteCodes(1);
        int value = Stack.popData();
        if (value > 0) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }
    
    /**
     * ifle instruction
     */
    static void ifle() {
        int offset = Instruction.signTwoByteCodes(1);
        int value = Stack.popData();
        if (value <= 0) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }
    
    /**
     * if_icmpeq instruction
     */
    static void if_icmpeq() {
        int offset = Instruction.signTwoByteCodes(1);
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        if (value1 == value2) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }

    /**
     * if_icmpne instruction
     */
    static void if_icmpne() {
        int offset = Instruction.signTwoByteCodes(1);
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        if (value1 != value2) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }

    /**
     * if_icmplt instruction
     */
    static void if_icmplt() {
        int offset = Instruction.signTwoByteCodes(1);
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        if (value1 < value2) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }

    /**
     * if_icmpge instruction
     */
    static void if_icmpge() {
        int offset = Instruction.signTwoByteCodes(1);
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        if (value1 >= value2) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }

    /**
     * if_icmpgt instruction
     */
    static void if_icmpgt() {
        int offset = Instruction.signTwoByteCodes(1);
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        if (value1 > value2) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }

    /**
     * if_icmple instruction
     */
    static void if_icmple() {
        int offset = Instruction.signTwoByteCodes(1);
        int value2 = Stack.popData();
        int value1 = Stack.popData();
        if (value1 <= value2) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }

    /**
     * if_acmpeq instruction
     */
    static void if_acmpeq() {
        int offset = Instruction.signTwoByteCodes(1);
        int pointer2 = Stack.popPointer();
        int pointer1 = Stack.popPointer();
        if (pointer1 == pointer2) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }

    /**
     * if_acmpne instruction
     */
    static void if_acmpne() {
        int offset = Instruction.signTwoByteCodes(1);
        int pointer2 = Stack.popPointer();
        int pointer1 = Stack.popPointer();
        if (pointer1 != pointer2) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }

    /**
     * ifnull instruction
     */
    static void ifnull() {
        int offset = Instruction.signTwoByteCodes(1);
        int pointer = Stack.popPointer();
        if (pointer == NULL) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }
    
    /**
     * ifnonnull instruction
     */
    static void ifnonnull() {
        int offset = Instruction.signTwoByteCodes(1);
        int pointer = Stack.popPointer();
        if (pointer != NULL) {
            Reg.instruction += offset;
        } else {
            Reg.instruction += 3;
        }
    }
    
}









