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
 * Execute the TABLESWITCH instruction
 */
void op_tableswitch() {
    int offset = 4 - (pc % 4);
    int defaultValue = s32(offset);
    int low = s32(offset + 4);
    int high = s32(offset + 8);
    int index = popInt();

    // get the offset from the table and jump
    if (index < low || index > high) {
        pc += defaultValue;
    } else {
        int jumpOffset = offset + 12 + ((index - low) * 4);
        pc += s32(jumpOffset);
    }
}

/*
 * Execute the LOOKUPSWITCH instruction
 */
void op_lookupswitch() {
    int offset = 4 - (pc % 4);
    int defaultValue = s32(offset);
    offset += 4;
    int npairs = s32(offset);
    offset += 4;
    int key = popInt();
    
    // find the entry in the table
    int i = 0;
    for (; i < npairs; i++) {
        int match = s32(offset);
        offset += 4;
        int jump = s32(offset);
        offset += 4;
        if (match == key) {
            pc += jump;
            return;
        }
    }

    // jump to default
    pc += defaultValue;
}

