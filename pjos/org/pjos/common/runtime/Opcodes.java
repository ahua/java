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
package org.pjos.common.runtime;

/**
 * Provides string names for opcodes.
 */
public final class Opcodes implements Opcode {
    
    /**
     * @param code the op code as an unsigned byte value
     * @return the name of the specified byte code
     */
    public static String getName(int code) {
        return names[code & 0xff];
    }
    
    /** The byte code names */
    private static String[] names = new String[] {
        "NOP",
        "ACONST_NULL",
        "ICONST_M1",
        "ICONST_0",
        "ICONST_1",
        "ICONST_2",
        "ICONST_3",
        "ICONST_4",
        "ICONST_5",
        "LCONST_0",
        "LCONST_1",
        "FCONST_0",
        "FCONST_1",
        "FCONST_2",
        "DCONST_2",
        "DCONST_1",
        "BIPUSH",
        "SIPUSH",
        "LDC",
        "LDC_W",
        "LDC2_W",
        "ILOAD",
        "LLOAD",
        "FLOAD",
        "DLOAD",
        "ALOAD",
        "ILOAD_0",
        "ILOAD_1",
        "ILOAD_2",
        "ILOAD_3",
        "LLOAD_0",
        "LLOAD_1",
        "LLOAD_2",
        "LLOAD_3",
        "FLOAD_0",
        "FLOAD_1",
        "FLOAD_2",
        "FLOAD_3",
        "DLOAD_0",
        "DLOAD_1",
        "DLOAD_2",
        "DLOAD_3",
        "ALOAD_0",
        "ALOAD_1",
        "ALOAD_2",
        "ALOAD_3",
        "IALOAD",
        "LALOAD",
        "FALOAD",
        "DALOAD",
        "AALOAD",
        "BALOAD",
        "CALOAD",
        "SALOAD",
        "ISTORE",
        "LSTORE",
        "FSTORE",
        "DSTORE",
        "ASTORE",
        "ISTORE_0",
        "ISTORE_1",
        "ISTORE_2",
        "ISTORE_3",
        "LSTORE_0",
        "LSTORE_1",
        "LSTORE_2",
        "LSTORE_3",
        "FSTORE_0",
        "FSTORE_1",
        "FSTORE_2",
        "FSTORE_3",
        "DSTORE_0",
        "DSTORE_1",
        "DSTORE_2",
        "DSTORE_3",
        "ASTORE_0",
        "ASTORE_1",
        "ASTORE_2",
        "ASTORE_3",
        "IASTORE",
        "LASTORE",
        "FASTORE",
        "DASTORE",
        "AASTORE",
        "BASTORE",
        "CASTORE",
        "SASTORE",
        "POP",
        "POP2",
        "DUP",
        "DUP_X1",
        "DUP_X2",
        "DUP2",
        "DUP2_X1",
        "DUP2_X2",
        "SWAP",
        "IADD",
        "LADD",
        "FADD",
        "DADD",
        "ISUB",
        "LSUB",
        "FSUB",
        "DSUB",
        "IMUL",
        "LMUL",
        "FMUL",
        "DMUL",
        "IDIV",
        "LDIV",
        "FDIV",
        "DDIV",
        "IREM",
        "LREM",
        "FREM",
        "DREM",
        "INEG",
        "LNEG",
        "FNEG",
        "DNEG",
        "ISHL",
        "LSHL",
        "ISHR",
        "LSHR",
        "IUSHR",
        "LUSHR",
        "IAND",
        "LAND",
        "IOR",
        "LOR",
        "IXOR",
        "LXOR",
        "IINC",
        "I2L",
        "I2F",
        "I2D",
        "L2I",
        "L2F",
        "L2D",
        "F2I",
        "F2L",
        "F2D",
        "D2I",
        "D2L",
        "D2F",
        "I2B",
        "I2C",
        "I2S",
        "LCMP",
        "FCMPL",
        "FCMPG",
        "DCMPL",
        "DCMPG",
        "IFEQ",
        "IFNE",
        "IFLT",
        "IFGE",
        "IFGT",
        "IFLE",
        "IF_ICMPEQ",
        "IF_ICMPNE",
        "IF_ICMPLT",
        "IF_ICMPGE",
        "IF_ICMPGT",
        "IF_ICMPLE",
        "IF_ACMPEQ",
        "IF_ACMPNE",
        "GOTO",
        "JSR",
        "RET",
        "TABLESWITCH",
        "LOOKUPSWITCH",
        "IRETURN",
        "LRETURN",
        "FRETURN",
        "DRETURN",
        "ARETURN",
        "RETURN",
        "GETSTATIC",
        "PUTSTATIC",
        "GETFIELD",
        "PUTFIELD",
        "INVOKEVIRTUAL",
        "INVOKESPECIAL",
        "INVOKESTATIC",
        "INVOKEINTERFACE",
        "unused_0xba",
        "NEW",
        "NEWARRAY",
        "ANEWARRAY",
        "ARRAYLENGTH",
        "ATHROW",
        "CHECKCAST",
        "INSTANCEOF",
        "MONITORENTER",
        "MONITOREXIT",
        "WIDE",
        "MULTIANEWARRAY",
        "IFNULL",
        "IFNONNULL",
        "GOTO_W",
        "JSR_W",
        "BREAKPOINT",
        "unused_0xcb",
        "unused_0xcc",
        "unused_0xcd",
        "unused_0xce",
        "unused_0xcf",
        "unused_0xd0",
        "unused_0xd1",
        "unused_0xd2",
        "unused_0xd3",
        "unused_0xd4",
        "unused_0xd5",
        "unused_0xd6",
        "unused_0xd7",
        "unused_0xd8",
        "unused_0xd9",
        "unused_0xda",
        "unused_0xdb",
        "unused_0xdc",
        "unused_0xdd",
        "unused_0xde",
        "unused_0xdf",
        "unused_0xe0",
        "unused_0xe1",
        "unused_0xe2",
        "unused_0xe3",
        "unused_0xe4",
        "unused_0xe5",
        "unused_0xe6",
        "unused_0xe7",
        "unused_0xe8",
        "unused_0xe9",
        "unused_0xea",
        "unused_0xeb",
        "unused_0xec",
        "unused_0xed",
        "unused_0xee",
        "unused_0xef",
        "unused_0xf0",
        "unused_0xf1",
        "unused_0xf2",
        "unused_0xf3",
        "unused_0xf4",
        "unused_0xf5",
        "unused_0xf6",
        "unused_0xf7",
        "unused_0xf8",
        "unused_0xf9",
        "unused_0xfa",
        "unused_0xfb",
        "unused_0xfc",
        "unused_0xfd",
        "IMPDEP1",
        "IMPDEP2"
    };

}










