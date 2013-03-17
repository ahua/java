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
; ldc instruction
;---------------------------------------------------------
proc_begin load_ldc, 0
	instruction_byte_code 1						; eax = index
	proc load_constant, eax
	instruction_offset 2
proc_end

;---------------------------------------------------------
; ldc_w instruction
;---------------------------------------------------------
proc_begin load_ldc_w, 0
	instruction_two_byte_codes 1				; eax = index
	proc load_constant, eax
	instruction_offset 3
proc_end


;---------------------------------------------------------
; Load the 32-bit constant with the given index
;
; args: index
;---------------------------------------------------------
proc_begin load_constant, 0
	mov eax, arg_1						; eax = index
	mov ebx, [reg_pool]					; ebx = address of first entry in pool
	mov ebx, [ebx + 4*eax]				; ebx = constant entry
	mov eax, [ebx + 4*entry_id]			; eax = constant id
	mov ecx, [ebx + 4*constant_first]	; ecx = constant value
	cmp eax, id_string_constant
	je .string
	push_data ecx						; push data value on stack
	jmp .return
.string:
	push_pointer ecx					; push pointer value on stack
proc_end



;---------------------------------------------------------
; ldc2_w instruction
;---------------------------------------------------------
proc_begin load_ldc2_w, 0
	; find the constant pool entry
	instruction_two_byte_codes 1				; eax = index of local
	shl eax, 2									; eax = index in bytes
	add eax, [reg_pool]							; eax = address of pool entry
	mov eax, [eax]								; eax = pool entry
	
	; push the constant value on the stack
	mov ebx, [eax + 4*constant_first]			; ebx = high 32 bits
	push_data ebx								; push high 32 bits
	mov ebx, [eax + 4*constant_second]			; ebx = low 32 bits
	push_data ebx								; push low 32 bits
	
	instruction_offset 3
proc_end


;---------------------------------------------------------
; iload instruction
;---------------------------------------------------------
proc_begin load_iload, 0
	instruction_byte_code 1						; eax = index of local
	mov ebx, [reg_locals]						; ebx = address of first local
	mov eax, [ebx + 8*eax]						; eax = value
	push_data eax
	instruction_offset 2
proc_end





;---------------------------------------------------------
; lload instruction
;---------------------------------------------------------
proc_begin load_lload, 0
	instruction_byte_code 1						; eax = index of local
	mov ebx, [reg_locals]						; ebx = address of first local
	mov edx, [ebx + 8*eax]						; edx = high bytes
	mov eax, [ebx + 8*eax + 8]					; eax = low bytes
	push_long edx, eax							; push value edx:eax
	instruction_offset 2
proc_end
	


error_message load_fload, 'load_fload'
error_message load_dload, 'load_dload'


;---------------------------------------------------------
; aload instruction
;---------------------------------------------------------
proc_begin load_aload, 0
	instruction_byte_code 1						; eax = index of local
	mov ebx, [reg_locals]						; ebx = address of first local
	mov eax, [ebx + 8*eax]						; eax = pointer value
	push_pointer eax
	instruction_offset 2
proc_end




;---------------------------------------------------------
; Create a procedure for a 32-bit xload_x instruction
;
; args: prefix, index, pointer/data
;---------------------------------------------------------
%macro load_xload_x32 3
proc_begin load_%1load_%2, 0
	mov ebx, [reg_locals]						; ebx = address of first local
	mov eax, [ebx + 8*%2]						; eax = value
	push_%3 eax
	instruction_offset 1
proc_end
%endmacro



;---------------------------------------------------------
; iload_x instructions
;---------------------------------------------------------
load_xload_x32 i, 0, data
load_xload_x32 i, 1, data
load_xload_x32 i, 2, data
load_xload_x32 i, 3, data




;---------------------------------------------------------
; Create a procedure for a 64-bit xload_x instruction
;
; arsg: prefix, index
;---------------------------------------------------------
%macro load_xload_x64 2
proc_begin load_%1load_%2, 0
	mov ebx, [reg_locals]						; ebx = address of first local
	mov edx, [ebx + 8*%2]						; edx = high bits
	mov eax, [ebx + 8*%2 + 8]					; eax = low bits
	push_long edx, eax
	instruction_offset 1
proc_end
%endmacro

;---------------------------------------------------------
; lload_x instructions
;---------------------------------------------------------
load_xload_x64 l, 0
load_xload_x64 l, 1
load_xload_x64 l, 2
load_xload_x64 l, 3



;---------------------------------------------------------
; fload_x instructions
;---------------------------------------------------------
load_xload_x32 f, 0, data
load_xload_x32 f, 1, data
load_xload_x32 f, 2, data
load_xload_x32 f, 3, data



;---------------------------------------------------------
; dload_x instructions
;---------------------------------------------------------
load_xload_x64 d, 0
load_xload_x64 d, 1
load_xload_x64 d, 2
load_xload_x64 d, 3




;---------------------------------------------------------
; aload_x instructions
;---------------------------------------------------------
load_xload_x32 a, 0, pointer
load_xload_x32 a, 1, pointer
load_xload_x32 a, 2, pointer
load_xload_x32 a, 3, pointer






