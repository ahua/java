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
; newarray instruction
;
; locals: atype
;---------------------------------------------------------
proc_begin instance_newarray, 1
	; figure out the required width
	instruction_byte_code 1									; eax = atype (specifies type of array)
	mov local_1, eax										; atype = eax
	proc instance_get_width, eax							; eax = width
	
	; find required length, pad to next 4-byte boundary
	peek_ebx												; ebx = length
	mul ebx													; edx:eax = num bytes required
	mov ecx, 3												; ecx = mask for rightmost 2 bits
	and ecx, eax											; ecx = rightmost 2 bits of eax
	shr eax, 2												; eax = num data words required (excluding padding)
	cmp ecx, 0
	je .no_pad
	inc eax													; eax += 1 (add padding)
.no_pad:
	
	; allocate space
	add eax, array_data										; eax = num words required for array
	proc allocate_new, eax, header_data_array				; eax = new address
	cmp eax, value_null
	je .return												; roll back because gc has been done
	
	; retrieve the appropriate type object from the core
	mov ebx, local_1										; ebx = atype
	mov ecx, [reg_core]										; ecx = core
	mov ebx, [ecx + 4*ebx + 4*core_arrays]					; ebx = type
	
	; initialise as array object
	mov [eax + 4*object_hashcode], eax						; set hash code
	mov [eax + 4*object_type], ebx							; set type
	pop_ecx													; pop the length into ecx
	mov [eax + 4*array_length], ecx							; set length
	
	; push address of new array, offset and return
	push_pointer eax
	instruction_offset 2
proc_end






;---------------------------------------------------------
; For the given array type specifier, return the
; appropriate width in eax.
;
; args: atype
;---------------------------------------------------------
proc_begin instance_get_width, 0
	mov eax, arg_1											; eax = atype
	sub eax, 4												; first entry in table is value 4
	shl eax, 2												; eax = byte offset within table
	add eax, .lookup										; eax = address of value within table
	mov eax, [eax]											; eax = width
	jmp .return
	
	; lookup table with array sizes
.lookup:		; atype			value
	dd 1 		; t_boolean		4
	dd 2 		; t_char		5
	dd 4 		; t_float		6
	dd 8 		; t_double		7
	dd 1 		; t_byte		8
	dd 2 		; t_short		9
	dd 4 		; t_int			10
	dd 8 		; t_long		11
proc_end

;---------------------------------------------------------
; anewarray instruction
;
; locals: arrayType, length
;---------------------------------------------------------
proc_begin instance_anewarray, 2
	; get the type entry from the constant pool
	instruction_two_byte_codes 1							; eax = pool index
	proc resolve_entry, eax, id_type						; eax = type entry
	cmp eax, value_null										; roll back
	je near .return
	
	; get the array type entry
	mov eax, [eax + 4*type_array_type]						; eax = array type
	cmp eax, value_null
	jne .size

	; resolve array type
	mov eax, [reg_core]										; eax = core
	mov ebx, [eax + 4*core_resolve_method]					; ebx = method
	mov eax, [eax + 4*object_type]							; eax = target
	proc invoke_execute, eax, ebx, 0
	jmp .return												; rollback
	
	; figure out the required size
.size:
	mov local_1, eax										; arrayType = eax
	peek_eax												; eax = length
	mov local_2, eax										; length = eax
	add eax, array_data										; eax = num words
	
	; allocate space for new instance
	proc allocate_new, eax, header_object_array				; eax = address
	cmp eax, value_null
	je .return												; rollback

	; initialise as array object
	mov [eax + 4*object_hashcode], eax						; set hash code
	mov ebx, local_1										; ebx = arrayType
	mov [eax + 4*object_type], ebx							; set type
	mov ebx, local_2										; ebx = length
	mov [eax + 4*array_length], ebx							; set length
	
	; all elements are set to null (zero) by allocation

	; pop length from stack
	pop_ebx
	
	; push address of new array and return
	push_pointer eax
	instruction_offset 3
proc_end






;---------------------------------------------------------
; new instruction
;
; locals: type
;---------------------------------------------------------
proc_begin instance_new, 1
	; get the type entry from the constant pool
	instruction_two_byte_codes 1							; eax = pool index
	proc resolve_entry, eax, id_type						; eax = type entry
	mov local_1, eax										; type = eax

	; roll back if not resolved
	cmp eax, value_null
	je .return
	
	; calculate the size of allocation required
	mov ebx, [eax + 4*type_instance_field_count]			; ebx = num instance fields
	add ebx, object_fields									; ebx = num words required

	; allocate space for new instance
	proc allocate_new, ebx, header_instance					; eax now contains address of new instance
	cmp eax, value_null
	je .return												; gc has been done so abort
	
	; push address on stack
	push_pointer eax
	
	; initialise headers
	mov [eax + 4*object_hashcode], eax						; set hash code
	mov ebx, local_1										; ebx = type
	mov [eax + 4*object_type], ebx							; set type
	
	; fields have already been set to null/zero by allocation
	
	instruction_offset 3
proc_end




