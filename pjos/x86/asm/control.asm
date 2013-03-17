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
; goto instruction
;---------------------------------------------------------
proc_begin control_goto, 0
	instruction_sign_two_byte_codes 1			; eax = offset
	instruction_offset eax
proc_end




;---------------------------------------------------------
; jsr instruction
;---------------------------------------------------------
proc_begin control_jsr, 0
	mov eax, edi								; eax = address of current instruction
	sub eax, [reg_code]							; eax = pc (offset of current instruction)
	add eax, 3									; eax = pc + 3
	push_data eax								; push return pc
	instruction_sign_two_byte_codes 1			; eax = offset
	instruction_offset eax
proc_end




;---------------------------------------------------------
; ret instruction
;---------------------------------------------------------
proc_begin control_ret, 0
	instruction_byte_code 1						; eax = local index
	mov ebx, [reg_locals]						; ebx = address of first local
	mov eax, [ebx + 4*eax]						; eax = return pc
	add eax, [reg_code]							; eax = return instruction pointer
	mov edi, eax								; set instruction pointer
proc_end
	




;---------------------------------------------------------
; lcmp instruction
;---------------------------------------------------------
proc_begin control_lcmp, 0
	pop_long edx, eax							; edx:eax = value2
	pop_long ecx, ebx							; ecx:ebx = value1

	; compare the values
	cmp ecx, edx								; compare high bits
	jg .greater
	jl .less
	cmp ebx, eax								; compare low bits
	jg .greater
	jl .less
	
	; return value possibilities
.equal:
	push_data 0
	jmp .offset
.greater:
	push_data 1
	jmp .offset
.less:
	push_data -1
	
.offset:
	instruction_offset 1
proc_end





;---------------------------------------------------------
; fcmpl instruction
;---------------------------------------------------------
proc_begin control_fcmpl, 0
	; push values on fp stack
	fld dword [esi]					; push value2
	add esi, 8						; pop value2 from stack
	fld dword [esi]					; push value1
	add esi, 8						; pop value1 from stack
	
	; set eax to desired result
	fucompp							; compare value1 and value2
	fstsw ax						; mov flags to ax
	sahf							; set flags from ah
	jg .gt
	je .eq
	mov eax, -1						; if v1 > v2 return -1
	jmp .set
.gt:
	mov eax, 1						; if v1 < v2 return 1
	jmp .set
.eq:
	mov eax, 0						; if v1 == v2 return 0
	
	; push result on stack and offset pc
.set:
	push_data eax
	instruction_offset 1
proc_end



;---------------------------------------------------------
; fcmpg instruction
;---------------------------------------------------------
proc_begin control_fcmpg, 0
	; push values on fp stack
	fld dword [esi]					; push value2
	add esi, 8						; pop value2 from stack
	fld dword [esi]					; push value1
	add esi, 8						; pop value1 from stack
	
	; set eax to desired result
	fucompp							; compare value1 and value2
	fstsw ax						; mov flags to ax
	sahf							; set flags from ah
	jl .lt
	je .eq
	mov eax, 1						; if v1 > v2 return 1
	jmp .set
.lt:
	mov eax, -1						; if v1 < v2 return -1
	jmp .set
.eq:
	mov eax, 0						; if v1 == v2 return 0
	
	; push result on stack and offset pc
.set:
	push_data eax
	instruction_offset 1
proc_end












;---------------------------------------------------------
; dcmpl instruction
;---------------------------------------------------------
proc_begin control_dcmpl, 0
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'dcmpl not implemented!!!', 0xA
.end:
proc_end

;---------------------------------------------------------
; dcmpg instruction
;---------------------------------------------------------
proc_begin control_dcmpg, 0
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'dcmpg not implemented!!!', 0xA
.end:
proc_end





;---------------------------------------------------------
; ifeq instruction
;---------------------------------------------------------
proc_begin control_ifeq, 0
	pop_eax										; eax = value
	cmp eax, 0
	je .is_eq
	mov eax, 3									; eax = 3
	jmp .offset
.is_eq:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end

;---------------------------------------------------------
; ifne instruction
;---------------------------------------------------------
proc_begin control_ifne, 0
	pop_eax										; eax = value
	cmp eax, 0
	jne .is_ne
	mov eax, 3									; eax = 3
	jmp .offset
.is_ne:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end

;---------------------------------------------------------
; iflt instruction
;---------------------------------------------------------
proc_begin control_iflt, 0
	pop_eax										; eax = value
	cmp eax, 0
	jl .is_lt
	mov eax, 3									; eax = 3
	jmp .offset
.is_lt:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end

;---------------------------------------------------------
; ifge instruction
;---------------------------------------------------------
proc_begin control_ifge, 0
	pop_eax										; eax = value
	cmp eax, 0
	jge .is_ge
	mov eax, 3									; eax = 3
	jmp .offset
.is_ge:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end



;---------------------------------------------------------
; ifgt instruction
;---------------------------------------------------------
proc_begin control_ifgt, 0
	pop_eax										; eax = value
	cmp eax, 0
	jg .is_gt
	mov eax, 3									; eax = 3
	jmp .offset
.is_gt:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end

;---------------------------------------------------------
; ifle instruction
;---------------------------------------------------------
proc_begin control_ifle, 0
	pop_eax										; eax = value
	cmp eax, 0
	jle .is_le
	mov eax, 3									; eax = 3
	jmp .offset
.is_le:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end





;---------------------------------------------------------
; if_icmpeq instruction
;---------------------------------------------------------
proc_begin control_if_icmpeq, 0
	pop_eax										; eax = value 2
	mov ebx, eax								; ebx = value 2
	pop_eax										; eax = value 1
	cmp eax, ebx
	je .true
	mov eax, 3
	jmp .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end




;---------------------------------------------------------
; if_icmpne instruction
;---------------------------------------------------------
proc_begin control_if_icmpne, 0
	pop_ebx										; ebx = value 2
	pop_eax										; eax = value 1
	cmp eax, ebx
	jne .true
	mov eax, 3
	jmp .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end




;---------------------------------------------------------
; if_icmplt instruction
;---------------------------------------------------------
proc_begin control_if_icmplt, 0
	pop_ebx										; ebx = value 2
	pop_eax										; eax = value 1
	cmp eax, ebx
	jl .true
	mov eax, 3
	jmp .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end

;---------------------------------------------------------
; if_icmpge instruction
;---------------------------------------------------------
proc_begin control_if_icmpge, 0
	pop_ebx										; ebx = value 2
	pop_eax										; eax = value 1
	cmp eax, ebx
	jge .true
	mov eax, 3
	jmp .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end

;---------------------------------------------------------
; if_icmpgt instruction
;---------------------------------------------------------
proc_begin control_if_icmpgt, 0
	pop_ebx										; ebx = value 2
	pop_eax										; eax = value 1
	cmp eax, ebx
	jg .true
	mov eax, 3
	jmp .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end

;---------------------------------------------------------
; if_icmple instruction
;---------------------------------------------------------
proc_begin control_if_icmple, 0
	pop_ebx										; ebx = value 2
	pop_eax										; eax = value 1
	cmp eax, ebx
	jle .true
	mov eax, 3
	jmp .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end




;---------------------------------------------------------
; if_acmpeq instruction
;---------------------------------------------------------
proc_begin control_if_acmpeq, 0
	pop_ebx										; ebx = pointer 2
	pop_eax										; eax = pointer 1
	cmp eax, ebx
	je .true
	mov eax, 3
	jne .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end

;---------------------------------------------------------
; if_acmpne instruction
;---------------------------------------------------------
proc_begin control_if_acmpne, 0
	pop_ebx										; ebx = pointer 2
	pop_eax										; eax = pointer 1
	cmp eax, ebx
	jne .true
	mov eax, 3
	jmp .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end




;---------------------------------------------------------
; ifnull instruction
;---------------------------------------------------------
proc_begin control_ifnull, 0
	pop_eax										; eax = pointer
	cmp eax, value_null
	je .true
	mov eax, 3									; eax = 3
	jmp .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end

;---------------------------------------------------------
; ifnonnull instruction
;---------------------------------------------------------
proc_begin control_ifnonnull, 0
	pop_eax										; eax = pointer
	cmp eax, value_null
	jne .true
	mov eax, 3									; eax = 3
	jmp .offset
.true:
	instruction_sign_two_byte_codes 1			; eax = offset
.offset:
	instruction_offset eax
proc_end










