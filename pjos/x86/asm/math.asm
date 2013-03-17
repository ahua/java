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





;---------------------------------------------------------
; iadd instruction
;---------------------------------------------------------
proc_begin math_iadd, 0
	pop_ebx							; ebx = value2
	pop_eax							; eax = value1
	add eax, ebx					; eax = value1 + value2
	push_data eax					; push sum
	instruction_offset 1
proc_end



;---------------------------------------------------------
; ladd instruction
;---------------------------------------------------------
proc_begin math_ladd, 0
	pop_long ecx, ebx				; ecx:ebx = value2
	pop_long edx, eax				; edx:eax = value1
	add eax, ebx
	adc edx, ecx					; edx:eax = value1 + value2
	push_long edx, eax				; push sum
	instruction_offset 1
proc_end





error_message math_fadd, 'math_fadd'
error_message math_dadd, 'math_dadd'



;---------------------------------------------------------
; isub instruction
;---------------------------------------------------------
proc_begin math_isub, 0
	pop_ebx							; ebx = value2
	pop_eax							; eax = value1
	sub eax, ebx					; eax = value1 - value2
	push_data eax					; push difference
	instruction_offset 1
proc_end



;---------------------------------------------------------
; lsub instruction
;---------------------------------------------------------
proc_begin math_lsub, 0
	pop_long ecx, ebx				; ecx:ebx = value2
	pop_long edx, eax				; edx:eax = value1
	sub eax, ebx
	sbb edx, ecx					; edx:eax = value1 - value2
	push_long edx, eax				; push difference
	instruction_offset 1
proc_end





error_message math_fsub, 'math_fsub'
error_message math_dsub, 'math_dsub'



;---------------------------------------------------------
; imul instruction
;---------------------------------------------------------
proc_begin math_imul, 0
	pop_ebx							; ebx = value2
	pop_eax							; eax = value1
	imul ebx						; edx:eax = value1 * value2
	push_data eax					; push product on stack (low 32 bits of actual result)
	instruction_offset 1
proc_end




error_message math_lmul, 'math_lmul'



;---------------------------------------------------------
; fmul instruction
;---------------------------------------------------------
proc_begin math_fmul, 0
	fld dword [esi]					; push value2 on fp stack
	pop_ebx							; pop value2 off java stack
	fmul dword [esi]				; st0 = value1 * value2
	fstp dword [esi]				; push product on java stack
	instruction_offset 1
proc_end



error_message math_dmul, 'math_dmul'


;---------------------------------------------------------
; idiv instruction
;---------------------------------------------------------
proc_begin math_idiv, 0
	pop_ebx							; ebx = value2
	pop_eax							; eax = value1
	cdq								; edx:eax = value1
	
	; check for special case: 0x80000000/0xFFFFFFFF
	cmp eax, 0x80000000
	jne .zero
	cmp ebx, 0xFFFFFFFF
	jne .zero
	mov eax, 0x80000000				; eax = quotient
	mov edx, 0x0					; edx = remainder
	jmp .finish

	; check for divide by zero
.zero:
	cmp ebx, 0
	jne .divide
    proc exception_throw, core_throw_arithmetic
    jmp .return

.divide:
	idiv ebx						; eax = quotient, edx = remainder
.finish:
	push_data eax					; push quotient on stack
	instruction_offset 1
proc_end



;---------------------------------------------------------
; ldiv instruction
;---------------------------------------------------------
proc_begin math_ldiv, 0
	pop_long ecx, ebx				; ecx:ebx = value2
	pop_long edx, eax				; edx:eax = value1
	
	; check for divide by zero
	cmp ecx, 0
	jne .divide
	cmp ebx, 0
	jne .divide
    proc exception_throw, core_throw_arithmetic
    jmp .return
	
.divide:
	proc math_signed_division_64, edx, eax, ecx, ebx
	; edx:eax = quotient, ecx:ebx = remainder
	
	push_long edx, eax				; push quotient on stack
	instruction_offset 1
proc_end






error_message math_fdiv, 'math_fdiv'
error_message math_ddiv, 'math_ddiv'



;---------------------------------------------------------
; irem instruction
;---------------------------------------------------------
proc_begin math_irem, 0
	pop_ebx							; ebx = value2
	pop_eax							; eax = value1
	cdq								; edx:eax = value1
	
	; check for special case: 0x80000000/0xFFFFFFFF
	cmp eax, 0x80000000
	jne .zero
	cmp ebx, 0xFFFFFFFF
	jne .zero
	mov eax, 0x80000000				; eax = quotient
	mov edx, 0x0					; edx = remainder
	jmp .finish

	; check for divide by zero
.zero:
	cmp ebx, 0
	jne .divide
    proc exception_throw, core_throw_arithmetic
    jmp .return

.divide:
	idiv ebx						; eax = quotient, edx = remainder
.finish:
	push_data edx					; push remainder on stack
	instruction_offset 1
proc_end




;---------------------------------------------------------
; lrem instruction
;---------------------------------------------------------
proc_begin math_lrem, 0
	pop_long ecx, ebx				; ecx:ebx = value2
	pop_long edx, eax				; edx:eax = value1
	
	; check for divide by zero
	cmp ecx, 0
	jne .divide
	cmp ebx, 0
	jne .divide
    proc exception_throw, core_throw_arithmetic
    jmp .return
	
.divide:
	proc math_signed_division_64, edx, eax, ecx, ebx
	; edx:eax = quotient, ecx:ebx = remainder
	
	push_long ecx, ebx				; push remainder on stack
	instruction_offset 1
proc_end

	





error_message math_frem, 'math_frem'
error_message math_drem, 'math_drem'



;---------------------------------------------------------
; ineg instruction
;---------------------------------------------------------
proc_begin math_ineg, 0
	pop_eax							; eax = value
	neg eax							; eax = -value
	push_data eax
	instruction_offset 1
proc_end




;---------------------------------------------------------
; lneg instruction
;---------------------------------------------------------
proc_begin math_lneg, 0
	pop_long edx, eax				; edx:eax = value
	not edx
	not eax
	add eax, 1
	adc edx, 0						; edx:eax = -value
	push_long edx, eax
	instruction_offset 1
proc_end








error_message math_fneg, 'math_fneg'
error_message math_dneg, 'math_dneg'





;---------------------------------------------------------
; 64 bit signed integer division, calculate the
; quotient and remainder. Does not check for
; divide by zero.
;
; return quotient in edx:eax, remainder in ecx:ebx
;
; args: Nh, Nl, Dh, Dl	(Numerator/Denominator high/low)
;
; locals:	1. Qh
;			2. Ql
;			3. Rh
;			4. Rl
;---------------------------------------------------------
proc_begin math_unsigned_division_64, 4
	mov ecx, arg_1
	mov ebx, arg_2									; ecx:ebx = N
	
	; handle special case N == D
	cmp ecx, arg_3
	jne .min
	cmp ebx, arg_4
	jne .min
	mov edx, 0
	mov eax, 1										; edx:eax = 1
	mov ecx, 0
	mov ebx, 0										; ecx:ebx = 0
	jmp .return										; return Q=1, R=0
	
	; handle special case N == 0x8000000000000000 (Long.MIN_VALUE)
.min:
	cmp ecx, 0
	jl .shift
	
	; handle special case N < D
	cmp ecx, arg_3
	jl .less
	jg .shift
	cmp ebx, arg_4
	jge .shift
.less:
	mov edx, 0
	mov eax, 0										; edx:eax = 0
	jmp .return										; return Q=0, R=N

	; shift N left until first significant bit is reached
.shift:
	mov edx, ecx
	mov eax, ebx									; edx:eax = N
	mov ecx, 64										; count = 64
.next:
	cmp edx, 0
	jl .divide
	shld edx, eax, 1
	shl eax, 1										; N <<= 1
	loop .next										; count--
.divide:
	mov arg_1, edx
	mov arg_2, eax									; save N
	
	; shift and subtract for each remaining bit
	mov dword local_1, 0
	mov dword local_2, 0							; Q = 0
	mov dword local_3, 0
	mov dword local_4, 0							; R = 0
.next1:
	; shift numerator left into remainder
	mov eax, local_4								; eax = Rl
	mov ebx, arg_1									; ebx = Nh
	mov edx, arg_2									; edx = Nl
	shld local_3, eax, 1
	shld local_4, ebx, 1
	shld arg_1, edx, 1
	shl dword arg_2, 1								; R:N <<= 1
	
	; subtract denominator from numerator
	mov edx, local_3
	mov eax, local_4								; edx:eax = R
	sub eax, arg_4
	sbb edx, arg_3									; edx:eax = X = R - D
	
	; shift quotient, carrying complement of leftmost bit from X
	mov ebx, local_2
	shld local_1, ebx, 1
	shl dword local_2, 1							; Q <<= 1
	cmp edx, 0
	jl .counter
	or dword local_2, 1								; Q |= 1 (carry bit)
	mov local_3, edx
	mov local_4, eax								; R = X
	
	; next iteration of loop
.counter:
	loop .next1										; count--

	; set registers for return
	mov edx, local_1
	mov eax, local_2								; edx:eax = Q
	mov ecx, local_3
	mov ebx, local_4								; ecx:ebx = R
proc_end


;---------------------------------------------------------
; 64 bit signed integer division, calculate the
; quotient and remainder. Does not check for
; divide by zero.
;
; return quotient in edx:eax, remainder in ecx:ebx
;
; args: Nh, Nl, Dh, Dl	(Numerator/Denominator high/low)
;---------------------------------------------------------
proc_begin math_signed_division_64, 0
	; read args
	mov edx, arg_1
	mov eax, arg_2									; edx:eax = N
	mov ecx, arg_3
	mov ebx, arg_4									; ecx:ebx = D
	
	; calculate absolute values if necessary
	cmp edx, 0
	jge .checkD
	not edx
	not eax
	add eax, 1
	adc edx, 0										; edx:eax = |N|
.checkD:
	cmp ecx, 0
	jge .udiv
	not ecx
	not ebx
	add ebx, 1
	adc ecx, 0										; ecx:ebx = |D|

	; do the unsigned division
.udiv:
	proc math_unsigned_division_64, edx, eax, ecx, ebx
	; edx:eax = |quotient|, ecx:ebx = |remainder|

	; give the remainder the sign from the numerator
	cmp dword arg_1, 0
	jge .npos
	neg_long ecx, ebx								; ecx:ebx = remainder

	; make the quotient negative if the numerator and
	; denominator have different signs
	cmp dword arg_3, 0
	jge .negq
	jmp .return
.npos:
	cmp dword arg_3, 0
	jl .negq
	jmp .return
.negq:
	neg_long edx, eax
proc_end





;---------------------------------------------------------
; The long division implementation is based on the
; algorithm demonstrated in the following java code.
;---------------------------------------------------------
;
;import java.util.Iterator;
;import java.util.TreeSet;
;import java.util.Random;
;
;/**
; * Testing an algorithm for signed 32-bit integer division
; */
;public class IntDivision {
;
;	/** Maximum value of unsigned integer value */
;	static final long UMAX = (1L << 32) - 1;
;
;	/**
;	 * Do unsigned integer division and return [quotient, remainder].
;	 * Return null if an attempt is made to divide by zero.
;	 */
;	static long[] udiv(long dividend, long divisor) {
;		long N = dividend;	// numerator
;		long D = divisor;	// denominator
;		if (N < 0 || D < 0 || N > UMAX || D > UMAX) throw new IllegalArgumentException();
;
;		// handle special cases
;		if (D == 0) return null; // divide by zero
;		if (N == D) return new long[] { 1, 0 };
;		if (N < D) return new long[] { 0, N };
;
;		// shift numerator left until first significant bit is reached
;		int count = 32; // number of shifts remaining (1 for each bit)
;		while ((N & 0x80000000L) == 0) {
;			N <<= 1;
;			count--;
;		}
;
;		// shift and subtract for remaining bits
;		long Q = 0; // quotient
;		long R = 0; // remainder
;		while (count-- > 0) {
;			long c = (N & 0x80000000L) >> 31;		// set carry flag
;			N <<= 1;								// shift left (no carry)
;			R = (R << 1) | c;						// shift left with carry
;			long X = R - D;
;			c = (R >= D) ? 1 : 0;					// set carry flag
;			Q = (Q << 1) | c;						// shift left with carry
;			if (c == 1) R = X;
;		}
;		return new long[] { Q, R };
;	}
;
;	/**
;	 * Do signed division and return [quotient, remainder].
;	 */
;	static int[] sdiv(int dividend, int divisor) {
;		long N = (long) dividend;	// numerator
;		long D = (long) divisor;	// denominator
;		if (D == 0) return null;	// divide by zero
;		long[] uqr = udiv(Math.abs(N), Math.abs(D));
;		int Q = (int) uqr[0];		// quotient
;		int R = (int) uqr[1];		// remainder
;
;		// give the remainder the sign from the dividend
;		if (N < 0) R = -R;
;
;		// make the quotient negative if the dividend and
;		// the divisor have different signs
;		if ((N < 0 && D > 0) || (N > 0 && D < 0)) Q = -Q;
;
;		return new int[] { Q, R };
;	}
;
;	/**
;	 * Run from command line
;	 */
;	public static void main(String[] args) throws Exception {
;		if (args.length != 1) {
;			System.err.println("usage: java IntDivision <count>");
;			System.exit(1);
;		}
;		int count = Integer.parseInt(args[0]);
;		TreeSet values = new TreeSet();
;		Random random = new Random(System.currentTimeMillis());
;
;		// test signed division
;		values.add(new Integer(0));
;		values.add(new Integer(Integer.MIN_VALUE));
;		values.add(new Integer(Integer.MAX_VALUE));
;		for (int i = 0; i < count; i++) {
;			values.add(new Integer(random.nextInt()));
;		}
;		for (Iterator it = values.iterator(); it.hasNext(); ) {
;			int i = ((Integer) it.next()).intValue();
;			for (Iterator jt = values.iterator(); jt.hasNext(); ) {
;				int j = ((Integer) jt.next()).intValue();
;				int[] result = sdiv(i, j);
;				if (result == null) {
;					if (j == 0) {
;						//System.out.println("sdiv succeeded for: " + i + " / " + j);
;					} else {
;						System.out.println("sdiv failed for: " + i + " / " + j);
;					}
;				} else {
;					if (result[0] == (i/j) && result[1] == (i%j)) {
;						//System.out.println("sdiv succeeded for: " + i + " / " + j);
;					} else {
;						System.out.println("sdiv failed for: " + i + " / " + j);
;					}
;				}
;			}
;		}
;
;	}
;
;}
