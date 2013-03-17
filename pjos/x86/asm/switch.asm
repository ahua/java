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
; lookupswitch instruction
;
; locals: defaultValue
;---------------------------------------------------------
proc_begin switch_lookupswitch, 1
	; calculate offset as 4 - (pc % 4)
	mov eax, edi				; eax = address of current instruction
	sub eax, [reg_code]			; eax = pc
	and eax, 0x3				; eax = pc % 4
	mov ebx, 0x4				; ebx = 4
	sub ebx, eax				; ebx = offset

	; read the values
	instruction_sign_four_byte_codes ebx	; eax = defaultValue
	mov local_1, eax			; defaultValue = eax
	add ebx, 4					; offset += 4
	instruction_sign_four_byte_codes ebx	; eax = npairs
	mov ecx, eax				; ecx = npairs
	add ebx, 4					; offset += 4
	
	; find the table entry
	pop_edx						; edx = key
.next:
	instruction_sign_four_byte_codes ebx	; eax = match
	cmp edx, eax
	je .match
	add ebx, 8					; point ebx to next entry
	loop .next

	; no match so jump to default
.default:
	instruction_offset local_1				
	jmp .return

	; jump to the matching entry
.match:
	add ebx, 4
	instruction_sign_four_byte_codes ebx	; eax = jump offset	
	instruction_offset eax
proc_end






;---------------------------------------------------------
; tableswitch instruction
;
; locals: offset
;---------------------------------------------------------
proc_begin switch_tableswitch, 1
	; calculate offset as 4 - (pc % 4)
	mov eax, edi								; eax = address of current instruction
	sub eax, [reg_code]							; eax = pc
	and eax, 0x3								; eax = pc % 4;
	mov ebx, 0x4								; ebx = 4;
	sub ebx, eax								; ebx = offset
	mov local_1, ebx							; offset = ebx
	
	; read table values
	instruction_sign_four_byte_codes ebx		; eax = defaultValue
	mov edx, eax								; edx = defaultValue
	add ebx, 4									; ebx = offset + 4
	instruction_sign_four_byte_codes ebx		; eax = low
	mov ecx, eax								; ecx = low
	add ebx, 4									; ebx = offset + 8
	instruction_sign_four_byte_codes ebx		; eax = high
	
	; get the index
	pop_ebx										; ebx = index
	
	; check index is within bounds
	cmp ebx, ecx
	jl .def										; index >= low
	cmp ebx, eax
	jg .def										; index <= high
	
	; use the jump entry from the table to increment the pc
	sub ebx, ecx								; ebx = (index - low)
	shl ebx, 2									; ebx = jumpOffset = (index - low) * 4
	mov ecx, local_1							; ecx = offset
	add ecx, 12									; ecx = offset + 12 = jumpTable
	add ecx, ebx								; ecx = jumpTable + jumpOffset = offset of entry
	instruction_sign_four_byte_codes ecx		; eax = jumpEntry
	instruction_offset eax						; pc += jumpEntry
	jmp .return
	
	; increment pc by default value
.def:
	instruction_offset edx						; pc += defaultvalue
proc_end





