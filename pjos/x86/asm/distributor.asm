;/*
;Copyright 2002 Simon Daniel
;email: simon@pjos.org
;
;This file is part of PJOS.
;
;This program is free software; you can redistribute it and/or modify
;it under the terms of the GNU General Public License as published by
;the Free Software Foundation; either version 2 of the License, or
;(at your option) any later version.
;
;This program is distributed in the hope that it will be useful,
;but WITHOUT ANY WARRANTY; without even the implied warranty of
;MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;GNU General Public License for more details.
;
;You should have received a copy of the GNU General Public License
;along with this program; if not, write to the Free Software
;Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
;*/

distributor:
.NOP:               dd kernel_error
.ACONST_NULL:       dd const_aconst_null
.ICONST_M1:         dd const_iconst_m1
.ICONST_0:          dd const_iconst_0
.ICONST_1:          dd const_iconst_1
.ICONST_2:          dd const_iconst_2
.ICONST_3:          dd const_iconst_3
.ICONST_4:          dd const_iconst_4
.ICONST_5:          dd const_iconst_5
.LCONST_0:          dd const_lconst_0
.LCONST_1:          dd const_lconst_1
.FCONST_0:          dd const_fconst_0
.FCONST_1:          dd const_fconst_1
.FCONST_2:          dd const_fconst_2
.DCONST_2:          dd const_dconst_2
.DCONST_1:          dd const_dconst_1
.BIPUSH:            dd stack_bipush
.SIPUSH:            dd stack_sipush
.LDC:               dd load_ldc
.LDC_W:             dd load_ldc_w
.LDC2_W:            dd load_ldc2_w
.ILOAD:             dd load_iload
.LLOAD:             dd load_lload
.FLOAD:             dd load_fload
.DLOAD:             dd load_dload
.ALOAD:             dd load_aload
.ILOAD_0:           dd load_iload_0
.ILOAD_1:           dd load_iload_1
.ILOAD_2:           dd load_iload_2
.ILOAD_3:           dd load_iload_3
.LLOAD_0:           dd load_lload_0
.LLOAD_1:           dd load_lload_1
.LLOAD_2:           dd load_lload_2
.LLOAD_3:           dd load_lload_3
.FLOAD_0:           dd load_fload_0
.FLOAD_1:           dd load_fload_1
.FLOAD_2:           dd load_fload_2
.FLOAD_3:           dd load_fload_3
.DLOAD_0:           dd load_dload_0
.DLOAD_1:           dd load_dload_1
.DLOAD_2:           dd load_dload_2
.DLOAD_3:           dd load_dload_3
.ALOAD_0:           dd load_aload_0
.ALOAD_1:           dd load_aload_1
.ALOAD_2:           dd load_aload_2
.ALOAD_3:           dd load_aload_3
.IALOAD:            dd array_iaload
.LALOAD:            dd array_laload
.FALOAD:            dd array_faload
.DALOAD:            dd array_eaload
.AALOAD:            dd array_aaload
.BALOAD:            dd array_baload
.CALOAD:            dd array_caload
.SALOAD:            dd array_saload
.ISTORE:            dd store_istore
.LSTORE:            dd store_lstore
.FSTORE:            dd store_fstore
.DSTORE:            dd store_dstore
.ASTORE:            dd store_astore
.ISTORE_0:          dd store_istore_0
.ISTORE_1:          dd store_istore_1
.ISTORE_2:          dd store_istore_2
.ISTORE_3:          dd store_istore_3
.LSTORE_0:          dd store_lstore_0
.LSTORE_1:          dd store_lstore_1
.LSTORE_2:          dd store_lstore_2
.LSTORE_3:          dd store_lstore_3
.FSTORE_0:          dd store_fstore_0
.FSTORE_1:          dd store_fstore_1
.FSTORE_2:          dd store_fstore_2
.FSTORE_3:          dd store_fstore_3
.DSTORE_0:          dd store_dstore_0
.DSTORE_1:          dd store_dstore_1
.DSTORE_2:          dd store_dstore_2
.DSTORE_3:          dd store_dstore_3
.ASTORE_0:          dd store_astore_0
.ASTORE_1:          dd store_astore_1
.ASTORE_2:          dd store_astore_2
.ASTORE_3:          dd store_astore_3
.IASTORE:           dd array_iastore
.LASTORE:           dd array_lastore
.FASTORE:           dd array_fastore
.DASTORE:           dd array_dastore
.AASTORE:           dd array_aastore
.BASTORE:           dd array_bastore
.CASTORE:           dd array_castore
.SASTORE:           dd array_sastore
.POP:               dd stack_pop
.POP2:              dd stack_pop2
.DUP:               dd stack_dup
.DUP_X1:            dd stack_dup_x1
.DUP_X2:            dd stack_dup_x2
.DUP2:              dd stack_dup2
.DUP2_X1:           dd stack_dup2_x1
.DUP2_X2:           dd stack_dup2_x2
.SWAP:              dd stack_swap
.IADD:              dd math_iadd
.LADD:              dd math_ladd
.FADD:              dd math_fadd
.DADD:              dd math_dadd
.ISUB:              dd math_isub
.LSUB:              dd math_lsub
.FSUB:              dd math_fsub
.DSUB:              dd math_dsub
.IMUL:              dd math_imul
.LMUL:              dd math_lmul
.FMUL:              dd math_fmul
.DMUL:              dd math_dmul
.IDIV:              dd math_idiv
.LDIV:              dd math_ldiv
.FDIV:              dd math_fdiv
.DDIV:              dd math_ddiv
.IREM:              dd math_irem
.LREM:              dd math_lrem
.FREM:              dd math_frem
.DREM:              dd math_drem
.INEG:              dd math_ineg
.LNEG:              dd math_lneg
.FNEG:              dd math_fneg
.DNEG:              dd math_dneg
.ISHL:              dd bit_ishl
.LSHL:              dd bit_lshl
.ISHR:              dd bit_ishr
.LSHR:              dd bit_lshr
.IUSHR:             dd bit_iushr
.LUSHR:             dd bit_lushr
.IAND:              dd bit_iand
.LAND:              dd bit_land
.IOR:               dd bit_ior
.LOR:               dd bit_lor
.IXOR:              dd bit_ixor
.LXOR:              dd bit_lxor
.IINC:              dd locals_iinc
.I2L:               dd convert_i2l
.I2F:               dd convert_i2f
.I2D:               dd convert_i2d
.L2I:               dd convert_l2i
.L2F:               dd convert_l2f
.L2D:               dd convert_l2d
.F2I:               dd convert_f2i
.F2L:               dd convert_f2l
.F2D:               dd convert_f2d
.D2I:               dd convert_d2i
.D2L:               dd convert_d2l
.D2F:               dd convert_d2f
.I2B:               dd convert_i2b
.I2C:               dd convert_i2c
.I2S:               dd convert_i2s
.LCMP:              dd control_lcmp
.FCMPL:             dd control_fcmpl
.FCMPG:             dd control_fcmpg
.DCMPL:             dd control_dcmpl
.DCMPG:             dd control_dcmpg
.IFEQ:              dd control_ifeq
.IFNE:              dd control_ifne
.IFLT:              dd control_iflt
.IFGE:              dd control_ifge
.IFGT:              dd control_ifgt
.IFLE:              dd control_ifle
.IF_ICMPEQ:         dd control_if_icmpeq
.IF_ICMPNE:         dd control_if_icmpne
.IF_ICMPLT:         dd control_if_icmplt
.IF_ICMPGE:         dd control_if_icmpge
.IF_ICMPGT:         dd control_if_icmpgt
.IF_ICMPLE:         dd control_if_icmple
.IF_ACMPEQ:         dd control_if_acmpeq
.IF_ACMPNE:         dd control_if_acmpne
.GOTO:              dd control_goto
.JSR:               dd control_jsr
.RET:               dd control_ret
.TABLESWITCH:       dd switch_tableswitch
.LOOKUPSWITCH:      dd switch_lookupswitch
.IRETURN:           dd return_ireturn
.LRETURN:           dd return_lreturn
.FRETURN:           dd return_freturn
.DRETURN:           dd return_dreturn
.ARETURN:           dd return_areturn
.RETURN:            dd return_return
.GETSTATIC:         dd field_getstatic
.PUTSTATIC:         dd field_putstatic
.GETFIELD:          dd field_getfield
.PUTFIELD:          dd field_putfield
.INVOKEVIRTUAL:     dd invoke_invokevirtual
.INVOKESPECIAL:     dd invoke_invokespecial
.INVOKESTATIC:      dd invoke_invokestatic
.INVOKEINTERFACE:   dd invoke_invokeinterface
.unused:            dd kernel_error
.NEW:               dd instance_new
.NEWARRAY:          dd instance_newarray
.ANEWARRAY:         dd instance_anewarray
.ARRAYLENGTH:       dd array_arraylength
.ATHROW:            dd exception_athrow
.CHECKCAST:         dd cast_checkcast
.INSTANCEOF:        dd cast_instanceof
.MONITORENTER:      dd monitor_monitorenter
.MONITOREXIT:       dd monitor_monitorexit
.WIDE:              dd kernel_error
.MULTIANEWARRAY:    dd kernel_error
.IFNULL:            dd control_ifnull
.IFNONNULL:         dd control_ifnonnull
.GOTO_W:            dd kernel_error
.JSR_W:             dd kernel_error
.BREAKPOINT:        dd kernel_error
times (254 - 203)   dd kernel_error                     ; 203 to 253 inclusive not used
.IMPDEP1:           dd kernel_error
.IMPDEP2:           dd kernel_error


