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
; ishl instruction
;---------------------------------------------------------
proc_begin bit_ishl, 0
    pop_ecx									; ecx = value2
	pop_eax									; eax = value1
    shl eax, cl								; eax = value1 << value2
    push_data eax
    instruction_offset 1
proc_end



;---------------------------------------------------------
; lshl instruction
;---------------------------------------------------------
proc_begin bit_lshl, 0
	pop_ecx									; ecx = value2
	pop_long edx, eax						; edx:eax = value1
	shld edx, eax, cl
	shl eax, cl								; edx:eax = value1 << (value2 & 31)
	and ecx, 32
	jecxz .finish
	shld edx, eax, 32
	shl eax, 32								; edx:eax = value1 << (value2 & 32)
.finish
	push_long edx, eax
	instruction_offset 1
proc_end





;---------------------------------------------------------
; ishr instruction
;---------------------------------------------------------
proc_begin bit_ishr, 0
    pop_ecx									; ecx = value2
    pop_eax									; eax = value1
    sar eax, cl								; eax = value1 >> value2
    push_data eax
    instruction_offset 1
proc_end






;---------------------------------------------------------
; iushr instruction
;---------------------------------------------------------
proc_begin bit_iushr, 0
    pop_ecx									; ecx = value2
    pop_eax									; eax = value1
    shr eax, cl								; eax = value1 >>> value2
    push_data eax
    instruction_offset 1
proc_end





;---------------------------------------------------------
; lshr instruction
;---------------------------------------------------------
proc_begin bit_lshr, 0
	pop_ecx									; ecx = value2
	pop_long edx, eax						; edx:eax = value1
	shrd eax, edx, cl
	sar edx, cl								; edx:eax = value1 >> (value2 & 31)
	and ecx, 32
	jecxz .finish
	shrd eax, edx, 32
	sar edx, 32								; edx:eax = value1 >> (value2 & 32)
.finish
	push_long edx, eax
	instruction_offset 1
proc_end



;---------------------------------------------------------
; lushr instruction
;---------------------------------------------------------
proc_begin bit_lushr, 0
	pop_ecx									; ecx = value2
	pop_long edx, eax						; edx:eax = value1
	shrd eax, edx, cl
	shr edx, cl								; edx:eax = value1 >>> (value2 & 31)
	and ecx, 32
	jecxz .finish
	shrd eax, edx, 32
	shr edx, 32								; edx:eax = value1 >>> (value2 & 32)
.finish
	push_long edx, eax
	instruction_offset 1
proc_end





;---------------------------------------------------------
; iand instruction
;---------------------------------------------------------
proc_begin bit_iand, 0
    pop_ebx									; ebx = value2
    pop_eax									; eax = value1
    and eax, ebx							; eax = value1 & value2
    push_data eax
    instruction_offset 1
proc_end





;---------------------------------------------------------
; land instruction
;---------------------------------------------------------
proc_begin bit_land, 0
	pop_long ecx, ebx						; ecx:ebx = value2
	pop_long edx, eax						; edx:eax = value1
	and edx, ecx
	and eax, ebx							; edx:eax = value1 & value2
	push_long edx, eax
	instruction_offset 1
proc_end




;---------------------------------------------------------
; ior instruction
;---------------------------------------------------------
proc_begin bit_ior, 0
    pop_ebx									; ebx = value2
    pop_eax									; eax = value1
    or eax, ebx								; eax = value1 | value2
    push_data eax
    instruction_offset 1
proc_end




;---------------------------------------------------------
; lor instruction
;---------------------------------------------------------
proc_begin bit_lor, 0
	pop_long ecx, ebx						; ecx:ebx = value2
	pop_long edx, eax						; edx:eax = value1
	or edx, ecx
	or eax, ebx								; edx:eax = value1 | value2
	push_long edx, eax
	instruction_offset 1
proc_end



;---------------------------------------------------------
; ixor instruction
;---------------------------------------------------------
proc_begin bit_ixor, 0
    pop_ebx									; ebx = value2
    pop_eax									; eax = value1
    xor eax, ebx							; eax = value1 ^ value2
    push_data eax
    instruction_offset 1
proc_end

    
;---------------------------------------------------------
; lxor instruction
;---------------------------------------------------------
proc_begin bit_lxor, 0
	pop_long ecx, ebx						; ecx:ebx = value2
	pop_long edx, eax						; edx:eax = value1
	xor edx, ecx
	xor eax, ebx							; edx:eax = value1 ^ value2
	push_long edx, eax
	instruction_offset 1
proc_end
