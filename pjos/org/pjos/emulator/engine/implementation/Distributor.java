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

import org.pjos.common.runtime.Opcode;
import org.pjos.common.runtime.Opcodes;

/**
 * Takes a byte code to be executed and calls the code
 * which implements the instruction.
 */
class Distributor implements Opcode {

    /**
     * Execute the next bytecode instruction
     */
    static void execute() {
        int op = Mem.loadByte(Reg.instruction);
        switch (op) {
            case NOP:               error(op);                          break;
            case ACONST_NULL:       Const.aconst_null();                break;
            case ICONST_M1:         Const.iconst_x(-1);                 break;
            case ICONST_0:          Const.iconst_x(0);                  break;
            case ICONST_1:          Const.iconst_x(1);                  break;
            case ICONST_2:          Const.iconst_x(2);                  break;
            case ICONST_3:          Const.iconst_x(3);                  break;
            case ICONST_4:          Const.iconst_x(4);                  break;
            case ICONST_5:          Const.iconst_x(5);                  break;
            case LCONST_0:          Const.lconst_x(0);                  break;
            case LCONST_1:          Const.lconst_x(1);                  break;
            case FCONST_0:          Const.fconst_x(0f);                 break;
            case FCONST_1:          Const.fconst_x(1f);                 break;
            case FCONST_2:          Const.fconst_x(2f);                 break;
            case DCONST_2:          error(op);                          break;
            case DCONST_1:          error(op);                          break;
            case BIPUSH:            Stack.bipush();                     break;
            case SIPUSH:            Stack.sipush();                     break;
            case LDC:               Load.ldc();                         break;
            case LDC_W:             Load.ldc_w();                       break;
            case LDC2_W:            Load.ldc2_w();                      break;
            case ILOAD:             Load.iload();                       break;
            case LLOAD:             Load.lload();                       break;
            case FLOAD:             Load.fload();                       break;
            case DLOAD:             Load.dload();                       break;
            case ALOAD:             Load.aload();                       break;
            case ILOAD_0:           Load.iload_x(0);                    break;
            case ILOAD_1:           Load.iload_x(1);                    break;
            case ILOAD_2:           Load.iload_x(2);                    break;
            case ILOAD_3:           Load.iload_x(3);                    break;
            case LLOAD_0:           Load.lload_x(0);                    break;
            case LLOAD_1:           Load.lload_x(1);                    break;
            case LLOAD_2:           Load.lload_x(2);                    break;
            case LLOAD_3:           Load.lload_x(3);                    break;
            case FLOAD_0:           Load.fload_x(0);                    break;
            case FLOAD_1:           Load.fload_x(1);                    break;
            case FLOAD_2:           Load.fload_x(2);                    break;
            case FLOAD_3:           Load.fload_x(3);                    break;
            case DLOAD_0:           Load.dload_x(0);                    break;
            case DLOAD_1:           Load.dload_x(1);                    break;
            case DLOAD_2:           Load.dload_x(2);                    break;
            case DLOAD_3:           Load.dload_x(3);                    break;
            case ALOAD_0:           Load.aload_x(0);                    break;
            case ALOAD_1:           Load.aload_x(1);                    break;
            case ALOAD_2:           Load.aload_x(2);                    break;
            case ALOAD_3:           Load.aload_x(3);                    break;
            case IALOAD:            Array.iaload();                     break;
            case LALOAD:            Array.laload();                     break;
            case FALOAD:            Array.faload();                     break;
            case DALOAD:            Array.daload();                     break;
            case AALOAD:            Array.aaload();                     break;
            case BALOAD:            Array.baload();                     break;
            case CALOAD:            Array.caload();                     break;
            case SALOAD:            Array.saload();                     break;
            case ISTORE:            Store.istore();                     break;
            case LSTORE:            Store.lstore();                     break;
            case FSTORE:            Store.fstore();                     break;
            case DSTORE:            Store.dstore();                     break;
            case ASTORE:            Store.astore();                     break;
            case ISTORE_0:          Store.istore_x(0);                  break;
            case ISTORE_1:          Store.istore_x(1);                  break;
            case ISTORE_2:          Store.istore_x(2);                  break;
            case ISTORE_3:          Store.istore_x(3);                  break;
            case LSTORE_0:          Store.lstore_x(0);                  break;
            case LSTORE_1:          Store.lstore_x(1);                  break;
            case LSTORE_2:          Store.lstore_x(2);                  break;
            case LSTORE_3:          Store.lstore_x(3);                  break;
            case FSTORE_0:          Store.fstore_x(0);                  break;
            case FSTORE_1:          Store.fstore_x(1);                  break;
            case FSTORE_2:          Store.fstore_x(2);                  break;
            case FSTORE_3:          Store.fstore_x(3);                  break;
            case DSTORE_0:          Store.dstore_x(0);                  break;
            case DSTORE_1:          Store.dstore_x(1);                  break;
            case DSTORE_2:          Store.dstore_x(2);                  break;
            case DSTORE_3:          Store.dstore_x(3);                  break;
            case ASTORE_0:          Store.astore_x(0);                  break;
            case ASTORE_1:          Store.astore_x(1);                  break;
            case ASTORE_2:          Store.astore_x(2);                  break;
            case ASTORE_3:          Store.astore_x(3);                  break;
            case IASTORE:           Array.iastore();                    break;
            case LASTORE:           Array.lastore();                    break;
            case FASTORE:           Array.fastore();                    break;
            case DASTORE:           Array.dastore();                    break;
            case AASTORE:           Array.aastore();                    break;
            case BASTORE:           Array.bastore();                    break;
            case CASTORE:           Array.castore();                    break;
            case SASTORE:           Array.sastore();                    break;
            case POP:               Stack.pop();                        break;
            case POP2:              Stack.pop2();                       break;
            case DUP:               Stack.dup();                        break;
            case DUP_X1:            Stack.dup_x1();                     break;
            case DUP_X2:            Stack.dup_x2();                     break;
            case DUP2:              Stack.dup2();                       break;
            case DUP2_X1:           Stack.dup2_x1();                    break;
            case DUP2_X2:           Stack.dup2_x2();                    break;
            case SWAP:              Stack.swap();                       break;
            case IADD:              Math.iadd();                        break;
            case LADD:              Math.ladd();                        break;
            case FADD:              Math.fadd();                        break;
            case DADD:              Math.dadd();                        break;
            case ISUB:              Math.isub();                        break;
            case LSUB:              Math.lsub();                        break;
            case FSUB:              Math.fsub();                        break;
            case DSUB:              Math.dsub();                        break;
            case IMUL:              Math.imul();                        break;
            case LMUL:              Math.lmul();                        break;
            case FMUL:              Math.fmul();                        break;
            case DMUL:              Math.dmul();                        break;
            case IDIV:              Math.idiv();                        break;
            case LDIV:              Math.ldiv();                        break;
            case FDIV:              Math.fdiv();                        break;
            case DDIV:              Math.ddiv();                        break;
            case IREM:              Math.irem();                        break;
            case LREM:              Math.lrem();                        break;
            case FREM:              Math.frem();                        break;
            case DREM:              Math.drem();                        break;
            case INEG:              Math.ineg();                        break;
            case LNEG:              Math.lneg();                        break;
            case FNEG:              Math.fneg();                        break;
            case DNEG:              Math.dneg();                        break;
            case ISHL:              Bit.ishl();                         break;
            case LSHL:              Bit.lshl();                         break;
            case ISHR:              Bit.ishr();                         break;
            case LSHR:              Bit.lshr();                         break;
            case IUSHR:             Bit.iushr();                        break;
            case LUSHR:             Bit.lushr();                        break;
            case IAND:              Bit.iand();                         break;
            case LAND:              Bit.land();                         break;
            case IOR:               Bit.ior();                          break;
            case LOR:               Bit.lor();                          break;
            case IXOR:              Bit.ixor();                         break;
            case LXOR:              Bit.lxor();                         break;
            case IINC:              Locals.iinc();                      break;
            case I2L:               Convert.i2l();                      break;
            case I2F:               Convert.i2f();                      break;
            case I2D:               Convert.i2d();                      break;
            case L2I:               Convert.l2i();                      break;
            case L2F:               Convert.l2f();                      break;
            case L2D:               Convert.l2d();                      break;
            case F2I:               Convert.f2i();                      break;
            case F2L:               Convert.f2l();                      break;
            case F2D:               Convert.f2d();                      break;
            case D2I:               Convert.d2i();                      break;
            case D2L:               Convert.d2l();                      break;
            case D2F:               Convert.d2f();                      break;
            case I2B:               Convert.i2b();                      break;
            case I2C:               Convert.i2c();                      break;
            case I2S:               Convert.i2s();                      break;
            case LCMP:              Compare.lcmp();                     break;
            case FCMPL:             Compare.fcmpl();                    break;
            case FCMPG:             Compare.fcmpg();                    break;
            case DCMPL:             Compare.dcmpl();                    break;
            case DCMPG:             Compare.dcmpg();                    break;
            case IFEQ:              Control.ifeq();                     break;
            case IFNE:              Control.ifne();                     break;
            case IFLT:              Control.iflt();                     break;
            case IFGE:              Control.ifge();                     break;
            case IFGT:              Control.ifgt();                     break;
            case IFLE:              Control.ifle();                     break;
            case IF_ICMPEQ:         Control.if_icmpeq();                break;
            case IF_ICMPNE:         Control.if_icmpne();                break;
            case IF_ICMPLT:         Control.if_icmplt();                break;
            case IF_ICMPGE:         Control.if_icmpge();                break;
            case IF_ICMPGT:         Control.if_icmpgt();                break;
            case IF_ICMPLE:         Control.if_icmple();                break;
            case IF_ACMPEQ:         Control.if_acmpeq();                break;
            case IF_ACMPNE:         Control.if_acmpne();                break;
            case GOTO:              Control.op_goto();                  break;
            case JSR:               Control.jsr();                      break;
            case RET:               Control.ret();                      break;
            case TABLESWITCH:       Switch.tableswitch();               break;
            case LOOKUPSWITCH:      Switch.lookupswitch();              break;
            case IRETURN:           Return.ireturn();                   break;
            case LRETURN:           Return.lreturn();                   break;
            case FRETURN:           Return.freturn();                   break;
            case DRETURN:           Return.dreturn();                   break;
            case ARETURN:           Return.areturn();                   break;
            case RETURN:            Return.op_return();                 break;
            case GETSTATIC:         Field.getstatic();                  break;
            case PUTSTATIC:         Field.putstatic();                  break;
            case GETFIELD:          Field.getfield();                   break;
            case PUTFIELD:          Field.putfield();                   break;
            case INVOKEVIRTUAL:     Invoke.invokevirtual();             break;
            case INVOKESPECIAL:     Invoke.invokespecial();             break;
            case INVOKESTATIC:      Invoke.invokestatic();              break;
            case INVOKEINTERFACE:   Invoke.invokeinterface();           break;
            case NEW:               Instance.op_new();                  break;
            case NEWARRAY:          Instance.newarray();                break;
            case ANEWARRAY:         Instance.anewarray();               break;
            case ARRAYLENGTH:       Array.arraylength();                break;
            case ATHROW:            Exceptions.athrow();                break;
            case CHECKCAST:         Cast.checkcast();                   break;
            case INSTANCEOF:        Cast.op_instanceof();               break;
            case MONITORENTER:      Monitor.monitorenter();             break;
            case MONITOREXIT:       Monitor.monitorexit();              break;
            case WIDE:              error(op);                          break;
            case MULTIANEWARRAY:    Instance.multianewarray();          break;
            case IFNULL:            Control.ifnull();                   break;
            case IFNONNULL:         Control.ifnonnull();                break;
            case GOTO_W:            error(op);                          break;
            case JSR_W:             error(op);                          break;
            case BREAKPOINT:        error(op);                          break;
            case IMPDEP1:           error(op);                          break;
            case IMPDEP2:           error(op);                          break;
            
            // instruction not valid
            default: error(op);
        }
    }
    
    /**
     * Throw an exception for the given byte code
     */
    private static void error(int op) {
        throw new IllegalStateException(
                "Instruction " + Opcodes.getName(op) + " not implemented");
    }

}
