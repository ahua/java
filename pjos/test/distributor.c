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
 * This method is called when trying to execute
 * a bytecode which has not been implemented
 */
static void error() {
    printf("Opcode not recognised: 0x%X\n", code[pc] & 0xff);
    exit(1);
}

/*
 * This array holds a pointer to a function for each
 * valid java byte code.
 */
void* distributor[256] = {
    error,         // NOP
    op_aconst_null,
    op_iconst_m1,
    op_iconst_0,
    op_iconst_1,
    op_iconst_2,
    op_iconst_3,
    op_iconst_4,
    op_iconst_5,
    op_lconst_0,
    op_lconst_1,
    op_fconst_0,
    op_fconst_1,
    op_fconst_2,
    error,         // DCONST_2
    error,         // DCONST_1
    op_bipush,
    op_sipush,
    op_ldc,
    op_ldc_w,
    op_ldc2_w,
    op_iload,
    op_lload,
    op_fload,
    op_dload,
    op_aload,
    op_iload_0,
    op_iload_1,
    op_iload_2,
    op_iload_3,
    op_lload_0,
    op_lload_1,
    op_lload_2,
    op_lload_3,
    op_fload_0,
    op_fload_1,
    op_fload_2,
    op_fload_3,
    error,         // DLOAD_0
    error,         // DLOAD_1
    error,         // DLOAD_2
    error,         // DLOAD_3
    op_aload_0,
    op_aload_1,
    op_aload_2,
    op_aload_3,
    op_iaload,
    op_laload,
    op_faload,
    op_daload,
    op_aaload,
    op_baload,
    op_caload,
    op_saload,
    op_istore,
    op_lstore,
    op_fstore,
    op_dstore,
    op_astore,
    op_istore_0,
    op_istore_1,
    op_istore_2,
    op_istore_3,
    op_lstore_0,
    op_lstore_1,
    op_lstore_2,
    op_lstore_3,
    error,         // FSTORE_0
    error,         // FSTORE_1
    error,         // FSTORE_2
    error,         // FSTORE_3
    error,         // DSTORE_0
    error,         // DSTORE_1
    error,         // DSTORE_2
    error,         // DSTORE_3
    op_astore_0,
    op_astore_1,
    op_astore_2,
    op_astore_3,
    op_iastore,
    op_lastore,
    op_fastore,
    op_dastore,
    op_aastore,
    op_bastore,
    op_castore,
    op_sastore,
    op_pop,
    op_pop2,
    op_dup,
    op_dup_x1,
    op_dup_x2,
    op_dup2,
    op_dup2_x1,
    op_dup2_x2,
    op_swap,
    op_iadd,
    op_ladd,
    op_fadd,
    op_dadd,
    op_isub,
    op_lsub,
    op_fsub,
    op_dsub,
    op_imul,
    op_lmul,
    op_fmul,
    op_dmul,
    op_idiv,
    op_ldiv,
    op_fdiv,
    op_ddiv,
    op_irem,
    op_lrem,
    op_frem,
    op_drem,
    op_ineg,
    op_lneg,
    op_fneg,
    op_dneg,
    op_ishl,
    op_lshl,
    op_ishr,
    op_lshr,
    op_iushr,
    op_lushr,
    op_iand,
    op_land,
    op_ior,
    op_lor,
    op_ixor,
    op_lxor,
    op_iinc,
    op_i2l,
    op_i2f,
    op_i2d,
    op_l2i,
    op_l2f,
    op_l2d,
    op_f2i,
    op_f2l,
    op_f2d,
    op_d2i,
    op_d2l,
    op_d2f,
    op_i2b,
    op_i2c,
    op_i2s,
    op_lcmp,
    op_fcmpl,
    op_fcmpg,
    error,         // DCMPL
    error,         // DCMPG
    op_ifeq,
    op_ifne,
    op_iflt,
    op_ifge,
    op_ifgt,
    op_ifle,
    op_if_icmpeq,
    op_if_icmpne,
    op_if_icmplt,
    op_if_icmpge,
    op_if_icmpgt,
    op_if_icmple,
    op_if_acmpeq,
    op_if_acmpne,
    op_goto,
    op_jsr,
    op_ret,
    op_tableswitch,
    op_lookupswitch,
    op_ireturn,
    op_lreturn,
    op_freturn,
    op_dreturn,
    op_areturn,
    op_return,
    op_getstatic,
    op_putstatic,
    op_getfield,
    op_putfield,
    op_invokevirtual,
    op_invokespecial,
    op_invokestatic,
    op_invokeinterface,
    error,         // unused: 186
    op_new,
    op_newarray,
    op_anewarray,
    op_arraylength,
    op_athrow,
    op_checkcast,
    op_instanceof,
    op_monitorenter,
    op_monitorexit,
    error,         // WIDE
    op_multianewarray,
    op_ifnull,
    op_ifnonnull,
    error,         // GOTO_W
    error,         // JSR_W
    error,         // BREAKPOINT
    error,         // unused: 203
    error,         // unused: 204
    error,         // unused: 205
    error,         // unused: 206
    error,         // unused: 207
    error,         // unused: 208
    error,         // unused: 209
    error,         // unused: 210
    error,         // unused: 211
    error,         // unused: 212
    error,         // unused: 213
    error,         // unused: 214
    error,         // unused: 215
    error,         // unused: 216
    error,         // unused: 217
    error,         // unused: 218
    error,         // unused: 219
    error,         // unused: 220
    error,         // unused: 221
    error,         // unused: 222
    error,         // unused: 223
    error,         // unused: 224
    error,         // unused: 225
    error,         // unused: 226
    error,         // unused: 227
    error,         // unused: 228
    error,         // unused: 229
    error,         // unused: 230
    error,         // unused: 231
    error,         // unused: 232
    error,         // unused: 233
    error,         // unused: 234
    error,         // unused: 235
    error,         // unused: 236
    error,         // unused: 237
    error,         // unused: 238
    error,         // unused: 239
    error,         // unused: 240
    error,         // unused: 241
    error,         // unused: 242
    error,         // unused: 243
    error,         // unused: 244
    error,         // unused: 245
    error,         // unused: 246
    error,         // unused: 247
    error,         // unused: 248
    error,         // unused: 249
    error,         // unused: 250
    error,         // unused: 251
    error,         // unused: 252
    error,         // unused: 253
    error,         // IMPDEP1
    error          // IMPDEP2
};
