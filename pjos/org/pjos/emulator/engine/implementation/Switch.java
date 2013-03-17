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
 * Contains code to implement the two switch instructions
 */
class Switch {

    /**
     * tableswitch instruction
     */
    static void tableswitch() {
        // read the table limits
        int pc = Reg.instruction - Reg.code;
        int padding = 3 - (pc % 4);
        int offset = 1 + padding;
        int defaultValue = Instruction.signFourByteCodes(offset);
        int low = Instruction.signFourByteCodes(offset + 4);
        int high = Instruction.signFourByteCodes(offset + 8);

        // get the index
        int index = Stack.popData();

        // jump to the correct value
        if (index < low || index > high) {
            Reg.instruction += defaultValue;
        } else {
            int jumpTable = offset + 12;
            int jumpOffset = (index - low) * 4;
            int jumpEntry = Instruction.signFourByteCodes(
                    jumpTable + jumpOffset);
            Reg.instruction += jumpEntry;
        }
    }

    /**
     * lookupswitch instruction
     */
    static void lookupswitch() {
        // read the table limits
        int pc = Reg.instruction - Reg.code;
        int padding = 3 - (pc % 4);
        int offset = 1 + padding;
        int defaultValue = Instruction.signFourByteCodes(offset);
        offset += 4;
        int npairs = Instruction.signFourByteCodes(offset);
        offset += 4;

        // find the offset
        int key = Stack.popData();
        for (int i = 0; i < npairs; i++) {
            int match = Instruction.signFourByteCodes(offset);
            offset += 4;
            int jump = Instruction.signFourByteCodes(offset);
            offset += 4;
            if (match == key) {
                Reg.instruction += jump;
                return;
            }
        }

        // jump to default
        Reg.instruction += defaultValue;
    }

}









