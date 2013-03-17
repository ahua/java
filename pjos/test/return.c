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
 * Point the current thread to the previous frame
 */
static void previousFrame() {
    // check synchronization
    Int flags = getInt(method, ENTRY_FLAGS);
    if (flags & ACC_SYNCHRONIZED) {
        Ref lock = getRef(frame, OBJECT_LOCK);
        if (lock == NULL) {
            printf("previousFrame(): no lock found for synchronized method\n");
            debugTrace();
            exit(1);
        }
        unlockLock(lock);
    }

    // point thread to previous frame
    Ref previous = getRef(frame, FRAME_RETURN_FRAME);
    setRef(thread, THREAD_FRAME, previous);
    if (previous == NULL) {
        unschedule(thread);
    } else {
        Int returnPc = getInt(frame, FRAME_RETURN_PC);
        setInt(previous, FRAME_PC, returnPc);
        loadRegisters();
    }
}

/*
 * Execute the RETURN instruction
 */
void op_return() {
    previousFrame();
}

/*
 * Execute the ARETURN instruction
 */
void op_areturn() {
    Ref r = popRef();
    previousFrame();
    pushRef(r);
}

/*
 * Execute the IRETURN instruction
 */
void op_ireturn() {
    Int value = popInt();
    previousFrame();
    pushInt(value);
}

/*
 * Execute the FRETURN instruction
 */
void op_freturn() {
    Float value = popFloat();
    previousFrame();
    pushFloat(value);
}

/*
 * Execute the LRETURN instruction
 */
void op_lreturn() {
    DoubleWord value = popDoubleWord();
    previousFrame();
    pushDoubleWord(value);
}

/*
 * Execute the DRETURN instruction
 */
void op_dreturn() {
    DoubleWord value = popDoubleWord();
    previousFrame();
    pushDoubleWord(value);
}

