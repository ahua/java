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
 * Defines constants for the java byte codes.
 */
public interface Opcode {
    /** NOP (0) */
    int NOP = 0x00;
    
    /** ACONST_NULL (1) */
    int ACONST_NULL = 0x01;
    
    /** ICONST_M1 (2) */
    int ICONST_M1 = 0x02;
    
    /** ICONST_0 (3) */
    int ICONST_0 = 0x03;
    
    /** ICONST_1 (4) */
    int ICONST_1 = 0x04;
    
    /** ICONST_2 (5) */
    int ICONST_2 = 0x05;
    
    /** ICONST_3 (6) */
    int ICONST_3 = 0x06;
    
    /** ICONST_4 (7) */
    int ICONST_4 = 0x07;
    
    /** ICONST_5 (8) */
    int ICONST_5 = 0x08;
    
    /** LCONST_0 (9) */
    int LCONST_0 = 0x09;
    
    /** LCONST_1 (10) */
    int LCONST_1 = 0x0a;
    
    /** FCONST_0 (11) */
    int FCONST_0 = 0x0b;
    
    /** FCONST_1 (12) */
    int FCONST_1 = 0x0c;
    
    /** FCONST_2 (13) */
    int FCONST_2 = 0x0d;
    
    /** DCONST_2 (14) */
    int DCONST_2 = 0x0e;
    
    /** DCONST_1 (15) */
    int DCONST_1 = 0x0f;
    
    /** BIPUSH (16) */
    int BIPUSH = 0x10;
    
    /** SIPUSH (17) */
    int SIPUSH = 0x11;
    
    /** LDC (18) */
    int LDC = 0x12;
    
    /** LDC_W (19) */
    int LDC_W = 0x13;
    
    /** LDC2_W (20) */
    int LDC2_W = 0x14;
    
    /** ILOAD (21) */
    int ILOAD = 0x15;
    
    /** LLOAD (22) */
    int LLOAD = 0x16;
    
    /** FLOAD (23) */
    int FLOAD = 0x17;
    
    /** DLOAD (24) */
    int DLOAD = 0x18;
    
    /** ALOAD (25) */
    int ALOAD = 0x19;
    
    /** ILOAD_0 (26) */
    int ILOAD_0 = 0x1a;
    
    /** ILOAD_1 (27) */
    int ILOAD_1 = 0x1b;
    
    /** ILOAD_2 (28) */
    int ILOAD_2 = 0x1c;
    
    /** ILOAD_3 (29) */
    int ILOAD_3 = 0x1d;
    
    /** LLOAD_0 (30) */
    int LLOAD_0 = 0x1e;
    
    /** LLOAD_1 (31) */
    int LLOAD_1 = 0x1f;
    
    /** LLOAD_2 (32) */
    int LLOAD_2 = 0x20;
    
    /** LLOAD_3 (33) */
    int LLOAD_3 = 0x21;
    
    /** FLOAD_0 (34) */
    int FLOAD_0 = 0x22;
    
    /** FLOAD_1 (35) */
    int FLOAD_1 = 0x23;
    
    /** FLOAD_2 (36) */
    int FLOAD_2 = 0x24;
    
    /** FLOAD_3 (37) */
    int FLOAD_3 = 0x25;
    
    /** DLOAD_0 (38) */
    int DLOAD_0 = 0x26;
    
    /** DLOAD_1 (39) */
    int DLOAD_1 = 0x27;
    
    /** DLOAD_2 (40) */
    int DLOAD_2 = 0x28;
    
    /** DLOAD_3 (41) */
    int DLOAD_3 = 0x29;
    
    /** ALOAD_0 (42) */
    int ALOAD_0 = 0x2a;
    
    /** ALOAD_1 (43) */
    int ALOAD_1 = 0x2b;
    
    /** ALOAD_2 (44) */
    int ALOAD_2 = 0x2c;
    
    /** ALOAD_3 (45) */
    int ALOAD_3 = 0x2d;
    
    /** IALOAD (46) */
    int IALOAD = 0x2e;
    
    /** LALOAD (47) */
    int LALOAD = 0x2f;
    
    /** FALOAD (48) */
    int FALOAD = 0x30;
    
    /** DALOAD (49) */
    int DALOAD = 0x31;
    
    /** AALOAD (50) */
    int AALOAD = 0x32;
    
    /** BALOAD (51) */
    int BALOAD = 0x33;
    
    /** CALOAD (52) */
    int CALOAD = 0x34;
    
    /** SALOAD (53) */
    int SALOAD = 0x35;
    
    /** ISTORE (54) */
    int ISTORE = 0x36;
    
    /** LSTORE (55) */
    int LSTORE = 0x37;
    
    /** FSTORE (56) */
    int FSTORE = 0x38;
    
    /** DSTORE (57) */
    int DSTORE = 0x39;
    
    /** ASTORE (58) */
    int ASTORE = 0x3a;
    
    /** ISTORE_0 (59) */
    int ISTORE_0 = 0x3b;
    
    /** ISTORE_1 (60) */
    int ISTORE_1 = 0x3c;
    
    /** ISTORE_2 (61) */
    int ISTORE_2 = 0x3d;
    
    /** ISTORE_3 (62) */
    int ISTORE_3 = 0x3e;
    
    /** LSTORE_0 (63) */
    int LSTORE_0 = 0x3f;
    
    /** LSTORE_1 (64) */
    int LSTORE_1 = 0x40;
    
    /** LSTORE_2 (65) */
    int LSTORE_2 = 0x41;
    
    /** LSTORE_3 (66) */
    int LSTORE_3 = 0x42;
    
    /** FSTORE_0 (67) */
    int FSTORE_0 = 0x43;
    
    /** FSTORE_1 (68) */
    int FSTORE_1 = 0x44;
    
    /** FSTORE_2 (69) */
    int FSTORE_2 = 0x45;
    
    /** FSTORE_3 (70) */
    int FSTORE_3 = 0x46;
    
    /** DSTORE_0 (71) */
    int DSTORE_0 = 0x47;
    
    /** DSTORE_1 (72) */
    int DSTORE_1 = 0x48;
    
    /** DSTORE_2 (73) */
    int DSTORE_2 = 0x49;
    
    /** DSTORE_3 (74) */
    int DSTORE_3 = 0x4a;
    
    /** ASTORE_0 (75) */
    int ASTORE_0 = 0x4b;
    
    /** ASTORE_1 (76) */
    int ASTORE_1 = 0x4c;
    
    /** ASTORE_2 (77) */
    int ASTORE_2 = 0x4d;
    
    /** ASTORE_3 (78) */
    int ASTORE_3 = 0x4e;
    
    /** IASTORE (79) */
    int IASTORE = 0x4f;
    
    /** LASTORE (80) */
    int LASTORE = 0x50;
    
    /** FASTORE (81) */
    int FASTORE = 0x51;
    
    /** DASTORE (82) */
    int DASTORE = 0x52;
    
    /** AASTORE (83) */
    int AASTORE = 0x53;
    
    /** BASTORE (84) */
    int BASTORE = 0x54;
    
    /** CASTORE (85) */
    int CASTORE = 0x55;
    
    /** SASTORE (86) */
    int SASTORE = 0x56;
    
    /** POP (87) */
    int POP = 0x57;
    
    /** POP2 (88) */
    int POP2 = 0x58;
    
    /** DUP (89) */
    int DUP = 0x59;
    
    /** DUP_X1 (90) */
    int DUP_X1 = 0x5a;
    
    /** DUP_X2 (91) */
    int DUP_X2 = 0x5b;
    
    /** DUP2 (92) */
    int DUP2 = 0x5c;
    
    /** DUP2_X1 (93) */
    int DUP2_X1 = 0x5d;
    
    /** DUP2_X2 (94) */
    int DUP2_X2 = 0x5e;
    
    /** SWAP (95) */
    int SWAP = 0x5f;
    
    /** IADD (96) */
    int IADD = 0x60;
    
    /** LADD (97) */
    int LADD = 0x61;
    
    /** FADD (98) */
    int FADD = 0x62;
    
    /** DADD (99) */
    int DADD = 0x63;
    
    /** ISUB (100) */
    int ISUB = 0x64;
    
    /** LSUB (101) */
    int LSUB = 0x65;
    
    /** FSUB (102) */
    int FSUB = 0x66;
    
    /** DSUB (103) */
    int DSUB = 0x67;
    
    /** IMUL (104) */
    int IMUL = 0x68;
    
    /** LMUL (105) */
    int LMUL = 0x69;
    
    /** FMUL (106) */
    int FMUL = 0x6a;
    
    /** DMUL (107) */
    int DMUL = 0x6b;
    
    /** IDIV (108) */
    int IDIV = 0x6c;
    
    /** LDIV (109) */
    int LDIV = 0x6d;
    
    /** FDIV (110) */
    int FDIV = 0x6e;
    
    /** DDIV (111) */
    int DDIV = 0x6f;
    
    /** IREM (112) */
    int IREM = 0x70;
    
    /** LREM (113) */
    int LREM = 0x71;
    
    /** FREM (114) */
    int FREM = 0x72;
    
    /** DREM (115) */
    int DREM = 0x73;
    
    /** INEG (116) */
    int INEG = 0x74;
    
    /** LNEG (117) */
    int LNEG = 0x75;
    
    /** FNEG (118) */
    int FNEG = 0x76;
    
    /** DNEG (119) */
    int DNEG = 0x77;
    
    /** ISHL (120) */
    int ISHL = 0x78;
    
    /** LSHL (121) */
    int LSHL = 0x79;
    
    /** ISHR (122) */
    int ISHR = 0x7a;
    
    /** LSHR (123) */
    int LSHR = 0x7b;
    
    /** IUSHR (124) */
    int IUSHR = 0x7c;
    
    /** LUSHR (125) */
    int LUSHR = 0x7d;
    
    /** IAND (126) */
    int IAND = 0x7e;
    
    /** LAND (127) */
    int LAND = 0x7f;
    
    /** IOR (128) */
    int IOR = 0x80;
    
    /** LOR (129) */
    int LOR = 0x81;
    
    /** IXOR (130) */
    int IXOR = 0x82;
    
    /** LXOR (131) */
    int LXOR = 0x83;
    
    /** IINC (132) */
    int IINC = 0x84;
    
    /** I2L (133) */
    int I2L = 0x85;
    
    /** I2F (134) */
    int I2F = 0x86;
    
    /** I2D (135) */
    int I2D = 0x87;
    
    /** L2I (136) */
    int L2I = 0x88;
    
    /** L2F (137) */
    int L2F = 0x89;
    
    /** L2D (138) */
    int L2D = 0x8a;
    
    /** F2I (139) */
    int F2I = 0x8b;
    
    /** F2L (140) */
    int F2L = 0x8c;
    
    /** F2D (141) */
    int F2D = 0x8d;
    
    /** D2I (142) */
    int D2I = 0x8e;
    
    /** D2L (143) */
    int D2L = 0x8f;
    
    /** D2F (144) */
    int D2F = 0x90;
    
    /** I2B (145) */
    int I2B = 0x91;
    
    /** I2C (146) */
    int I2C = 0x92;
    
    /** I2S (147) */
    int I2S = 0x93;
    
    /** LCMP (148) */
    int LCMP = 0x94;
    
    /** FCMPL (149) */
    int FCMPL = 0x95;
    
    /** FCMPG (150) */
    int FCMPG = 0x96;
    
    /** DCMPL (151) */
    int DCMPL = 0x97;
    
    /** DCMPG (152) */
    int DCMPG = 0x98;
    
    /** IFEQ (153) */
    int IFEQ = 0x99;
    
    /** IFNE (154) */
    int IFNE = 0x9a;
    
    /** IFLT (155) */
    int IFLT = 0x9b;
    
    /** IFGE (156) */
    int IFGE = 0x9c;
    
    /** IFGT (157) */
    int IFGT = 0x9d;
    
    /** IFLE (158) */
    int IFLE = 0x9e;
    
    /** IF_ICMPEQ (159) */
    int IF_ICMPEQ = 0x9f;
    
    /** IF_ICMPNE (160) */
    int IF_ICMPNE = 0xa0;
    
    /** IF_ICMPLT (161) */
    int IF_ICMPLT = 0xa1;
    
    /** IF_ICMPGE (162) */
    int IF_ICMPGE = 0xa2;
    
    /** IF_ICMPGT (163) */
    int IF_ICMPGT = 0xa3;
    
    /** IF_ICMPLE (164) */
    int IF_ICMPLE = 0xa4;
    
    /** IF_ACMPEQ (165) */
    int IF_ACMPEQ = 0xa5;
    
    /** IF_ACMPNE (166) */
    int IF_ACMPNE = 0xa6;
    
    /** GOTO (167) */
    int GOTO = 0xa7;
    
    /** JSR (168) */
    int JSR = 0xa8;
    
    /** RET (169) */
    int RET = 0xa9;
    
    /** TABLESWITCH (170) */
    int TABLESWITCH = 0xaa;
    
    /** LOOKUPSWITCH (171) */
    int LOOKUPSWITCH = 0xab;
         
    /** IRETURN (172) */
    int IRETURN = 0xac;
    
    /** LRETURN (173) */
    int LRETURN = 0xad;
    
    /** FRETURN (174) */
    int FRETURN = 0xae;
    
    /** DRETURN (175) */
    int DRETURN = 0xaf;
    
    /** ARETURN (176) */
    int ARETURN = 0xb0;
    
    /** RETURN (177) */
    int RETURN = 0xb1;
    
    /** GETSTATIC (178) */
    int GETSTATIC = 0xb2;
    
    /** PUTSTATIC (179) */
    int PUTSTATIC = 0xb3;
    
    /** GETFIELD (180) */
    int GETFIELD = 0xb4;
    
    /** PUTFIELD (181) */
    int PUTFIELD = 0xb5;
    
    /** INVOKEVIRTUAL (182) */
    int INVOKEVIRTUAL = 0xb6;
         
    /** INVOKESPECIAL (183) */
    int INVOKESPECIAL = 0xb7;
         
    /** INVOKESTATIC (184) */
    int INVOKESTATIC = 0xb8;
         
    /** INVOKEINTERFACE (185) */
    int INVOKEINTERFACE = 0xb9;

    //int XXXUNUSEDXXX = 0xba;      //  186

    /** NEW (187) */
    int NEW = 0xbb;
    
    /** NEWARRAY (188) */
    int NEWARRAY = 0xbc;
    
    /** ANEWARRAY (189) */
    int ANEWARRAY = 0xbd;
    
    /** ARRAYLENGTH (190) */
    int ARRAYLENGTH = 0xbe;
    
    /** ATHROW (191) */
    int ATHROW = 0xbf;
    
    /** CHECKCAST (192) */
    int CHECKCAST = 0xc0;
    
    /** INSTANCEOF (193) */
    int INSTANCEOF = 0xc1;
    
    /** MONITORENTER (194) */
    int MONITORENTER = 0xc2;
         
    /** MONITOREXIT (195) */
    int MONITOREXIT = 0xc3;
    
    /** WIDE (196) */
    int WIDE = 0xc4;
    
    /** MULTIANEWARRAY (197) */
    int MULTIANEWARRAY = 0xc5;
         
    /** IFNULL (198) */
    int IFNULL = 0xc6;
    
    /** IFNONNULL (199) */
    int IFNONNULL = 0xc7;
    
    /** GOTO_W (200) */
    int GOTO_W = 0xc8;
    
    /** JSR_W (201) */
    int JSR_W = 0xc9;
    
    /** BREAKPOINT (202) */
    int BREAKPOINT = 0xca;
    
    // ... unused ...

    /** IMPDEP1 (254) */
    int IMPDEP1 = 0xfe;
    
    /** IMPDEP2 (255) */
    int IMPDEP2 = 0xff;
    
}










