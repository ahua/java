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
 * Check that the current value of the stack pointer is
 * valid, and also that the value (stack + count) is valid.
 */
static void check(int count) {
    int frameSize = frame[OBJECT_SIZE].i;
    int max = getInt(method, METHOD_MAX_STACK);
    Var* base = (Var*) (frame + frameSize);
    int sp = base - stack;
    if (sp < 0 || sp > max) {
        printf("stack range: [%d, %d]\n", 0, max);
        printf("Illegal value for sp: %d\n", sp);
        debugTrace();
        exit(1);
    }
    int newSp = sp - count;
    if (newSp < 0 || newSp > max) {
        printf("count: %d\n", count);
        printf("stack range: [%d, %d]\n", 0, max);
        printf("Illegal value for new stack pointer: %d\n", newSp);
        debugTrace();
        exit(1);
    }
}

/*
 * Check that the value on top of the stack is the correct type
 * (pointer or value).
 */
static void checkTop(int flag) {
    if (flag != stack->flag) {
        printf("Top of stack invalid. Expected flag: %d\n", flag);
        debugTrace();
        exit(1);
    }
}

/*
 * Check that the specified frame local value is valid
 */
static void checkLocalIndex(Ref frame, int index) {
    Ref method = getRef(frame, FRAME_METHOD);
    Int max = getInt(method, METHOD_MAX_LOCALS);
    if (index < 0 || index >= max) {
        printf("Invalid local index: %d\n", index);
        printf("Range allowed is [0, %d)\n", max);
        debugTrace();
        exit(1);
    }
}

/*
 * Check that the specified local is of the given type
 */
static void checkLocalType(int index, int flag) {
    if (locals[index].flag != flag) {
        printf("Invalid local type\n");
        debugTrace();
        exit(1);
    }
}

/*
 * Return the local at the specified index as an int
 */
Int getLocalInt(int index) {
    checkLocalIndex(frame, index);
    checkLocalType(index, FALSE);
    return locals[index].value.i;
}

/*
 * Return the local at the specified index as a float
 */
Float getLocalFloat(int index) {
    checkLocalIndex(frame, index);
    checkLocalType(index, FALSE);
    return locals[index].value.f;
}

/*
 * Return the local at the specified index as a reference
 */
Ref getLocalRef(int index) {
    checkLocalIndex(frame, index);
    checkLocalType(index, TRUE);
    return locals[index].value.r;
}

/*
 * Set the integer frame local at the specified index
 */
void setLocalInt(int index, Int value, Ref frame) {
    checkLocalIndex(frame, index);
    Var* locals = (Var*) (frame + FRAME_LOCALS);
    Var* local = locals + index;
    local->value.i = value;
    local->flag = FALSE;
}

/*
 * Get the double word local at the specified index.
 * Note: components are stored in the opposite order
 *       as stack entries because of the way method
 *       parameters are passed.
 */
DoubleWord getLocalDoubleWord(int index) {
    DoubleWord result;
    result.c.high = getLocalInt(index);
    result.c.low = getLocalInt(index + 1);
    return result;
}

/*
 * Set the double word local at the specified index.
 * Note: components are stored in the opposite order
 *       as stack entries because of the way method
 *       parameters are passed.
 */
void setLocalDoubleWord(int index, DoubleWord value, Ref frame) {
    setLocalInt(index, value.c.high, frame);
    setLocalInt(index + 1, value.c.low, frame);
}

/*
 * Set the float frame local at the specified index
 */
void setLocalFloat(int index, Float value, Ref frame) {
    checkLocalIndex(frame, index);
    Var* locals = (Var*) (frame + FRAME_LOCALS);
    Var* local = locals + index;
    local->value.f = value;
    local->flag = FALSE;
}

/*
 * Set the reference frame local at the specified index
 */
void setLocalRef(int index, Ref ref, Ref frame) {
    checkLocalIndex(frame, index);
    Var* locals = (Var*) (frame + FRAME_LOCALS);
    Var* local = locals + index;
    local->value.r = ref;
    local->flag = TRUE;
}

/*
 * Return true if there is currently a reference value
 * on top of the stack, false otherwise.
 */
int refOnStack() {
    check(1);
    return stack->flag;
}

/*
 * Pop a float value off the stack
 */
Float popFloat() {
    check(1);
    checkTop(FALSE);
    return (stack++)->value.f;
}

/*
 * Peek at the int value on the stack
 */
Int peekInt() {
    check(1);
    checkTop(FALSE);
    return stack->value.i;
}

/*
 * Pop an int value off the stack
 */
Int popInt() {
    check(1);
    checkTop(FALSE);
    return (stack++)->value.i;
}

/*
 * Pop an unsigned int value off the stack
 */
unsigned int popUInt() {
    check(1);
    checkTop(FALSE);
    return (stack++)->value.u;
}

/*
 * Peek at the reference on the stack
 */
Ref peekRef() {
    check(1);
    checkTop(TRUE);
    return stack->value.r;
}

/*
 * Pop a pointer value off the stack
 */
Ref popRef() {
    check(1);
    checkTop(TRUE);
    stack->flag = FALSE;
    return (stack++)->value.r;
}

/*
 * Pop a double word from the stack
 */
DoubleWord popDoubleWord() {
    DoubleWord result;
    result.c.low = popUInt();
    result.c.high = popUInt();
    return result;
}

/*
 * Push a double word onto the stack
 */
void pushDoubleWord(DoubleWord d) {
    pushUInt(d.c.high);
    pushUInt(d.c.low);
}

/*
 * Push a pointer value onto the stack
 */
void pushRef(Ref ref) {
    check(-1);
    checkReference(ref);
    (--stack)->value.r = ref;
    stack->flag = TRUE;
    checkTop(TRUE);
}

/*
 * Push an integer value onto the stack
 */
void pushInt(int value) {
    check(-1);
    (--stack)->value.i = value;
    checkTop(FALSE);
}

/*
 * Push an unsigned integer value onto the stack
 */
void pushUInt(unsigned int value) {
    check(-1);
    (--stack)->value.u = value;
    checkTop(FALSE);
}

/*
 * Push a float value onto the stack
 */
void pushFloat(float value) {
    check(-1);
    (--stack)->value.f = value;
    checkTop(FALSE);
}

/*
 * Execute the DUP instruction
 */
void op_dup() {
    check(1);
    check(-1);
    Var* top = stack--;
    *stack = *top;
    pc++;
}

/*
 * Execute the DUP_X1 instruction
 */
void op_dup_x1() {
    check(2);
    check(-1);
    Var first = *(stack++);
    Var second = *(stack++);
    *(--stack) = first;
    *(--stack) = second;
    *(--stack) = first;
    pc++;
}

/*
 * Execute the DUP_X2 instruction
 */
void op_dup_x2() {
    check(3);
    check(-1);
    Var first = *(stack++);
    Var second = *(stack++);
    Var third = *(stack++);
    *(--stack) = first;
    *(--stack) = third;
    *(--stack) = second;
    *(--stack) = first;
    pc++;
}

/*
 * Execute the DUP2 instruction
 */
void op_dup2() {
    check(2);
    check(-2);
    Var* top = stack;
    *(--stack) = *(top + 1);
    *(--stack) = *top;
    pc++;
}

/*
 * Execute the DUP2_X1 instruction
 */
void op_dup2_x1() {
    check(3);
    check(-2);
    Var first = *(stack++);
    Var second = *(stack++);
    Var third = *(stack++);
    *(--stack) = second;
    *(--stack) = first;
    *(--stack) = third;
    *(--stack) = second;
    *(--stack) = first;
    pc++;
}

/*
 * Execute the DUP2_X2 instruction
 */
void op_dup2_x2() {
    check(4);
    check(-2);
    Var first = *(stack++);
    Var second = *(stack++);
    Var third = *(stack++);
    Var fourth = *(stack++);
    *(--stack) = second;
    *(--stack) = first;
    *(--stack) = fourth;
    *(--stack) = third;
    *(--stack) = second;
    *(--stack) = first;
    pc++;
}

/*
 * Execute the SWAP instruction
 */
void op_swap() {
    check(2);
    Var first = *(stack++);
    Var second = *(stack++);
    *(--stack) = first;
    *(--stack) = second;
    pc++;
}

/*
 * Execute the BIPUSH instruction
 */
void op_bipush() {
    pushInt(s8(1));
    pc += 2;
}

/*
 * Execute the SIPUSH instruction
 */
void op_sipush() {
    pushInt(s16(1));
    pc += 3;
}

/*
 * Execute the POP instruction
 */
void op_pop() {
    check(1);
    (stack++)->flag = FALSE;
    pc++;
}

/*
 * Execute the POP2 instruction
 */
void op_pop2() {
    check(2);
    (stack++)->flag = FALSE;
    (stack++)->flag = FALSE;
    pc++;
}

