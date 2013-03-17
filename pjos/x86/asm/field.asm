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
; getstatic instruction
;---------------------------------------------------------
proc_begin field_getstatic, 0
	; get the field entry from the constant pool
	instruction_two_byte_codes 1							; eax = pool index
	proc resolve_entry, eax, id_field						; eax = field entry
	cmp eax, value_null
	je .return												; abort if not resolved
	
	; read common values
	mov ecx, [eax + 4*entry_owner]							; ecx = field type
	mov ecx, [ecx + 4*type_statics]							; ecx = statics
	add ecx, 4*statics_fields								; ecx = address of first field in statics
	mov edx, [eax + 4*field_index]							; edx = field index
	shl edx, 2												; edx = field offset in bytes
	add ecx, edx											; ecx = address of value1
	mov edx, [ecx]											; edx = value1
	
	; determine field type
	cmp dword [eax + 4*field_size], 2						; compare field size with 2
	je .double
	cmp dword [eax + 4*field_reference_flag], value_true	; compare reference flag with true
	je .reference
	
	; 32 bit data field
	push_data edx
	jmp .offset
	
	; 32 bit reference
.reference:
	push_pointer edx
	jmp .offset
	
	; 64 bit field
.double:
	push_data edx							; push value1
	add ecx, 4								; ecx = address of value2
	mov edx, [ecx]							; edx = value2
	push_data edx							; push value2

	; increment pc and return
.offset:
	instruction_offset 3
proc_end




;---------------------------------------------------------
; putstatic instruction
;---------------------------------------------------------
proc_begin field_putstatic, 0
	; get the field entry from the constant pool
	instruction_two_byte_codes 1							; eax = pool index
	proc resolve_entry, eax, id_field						; eax = field entry
	cmp eax, value_null
	je .return												; abort if not resolved
	
	; find out where the value should be stored
	mov ebx, [eax + 4*entry_owner]							; ebx = field type
	mov ebx, [ebx + 4*type_statics]							; ebx = statics
	add ebx, 4*statics_fields								; ebx = address of first static field
	mov ecx, [eax + 4*field_index]							; ecx = field index
	shl ecx, 2												; ecx = field offset in bytes
	add ebx, ecx											; ebx = address of value1
	cmp dword [eax + 4*field_size], 2						; compare field size with 2
	je .double
	
	; store 32 bit value
	pop_eax													; eax = value
	mov [ebx], eax											; store value
	jmp .offset

	; store 64 bit value
.double:
	pop_edx													; edx = value 2
	pop_eax													; eax = value 1
	mov [ebx], eax											; store value 1
	mov [ebx + 4], edx										; store value 2

	; increment pc and return
.offset:
	instruction_offset 3
proc_end




;---------------------------------------------------------
; getfield instruction
;---------------------------------------------------------
proc_begin field_getfield, 0
	; get the field entry from the constant pool
	instruction_two_byte_codes 1							; eax = pool index
	proc resolve_entry, eax, id_field						; eax = field entry
	cmp eax, value_null
	je near .return												; abort if not resolved
	
	; get the object from the stack			
	pop_ebx													; ebx = object
	cmp ebx, value_null
	jne .values
	proc exception_throw, core_throw_null_pointer			; object was null so throw exception
	jmp .return
	
	; determine data location
.values:
	mov ecx, [eax + 4*field_index]							; ecx = index
	add ecx, object_fields									; ecx = offset
	shl ecx, 2												; ecx = offset in bytes
	add ecx, ebx											; ecx = address of value1
	mov edx, [ecx]											; edx = value1

	; determine field type
	cmp dword [eax + 4*field_size], 2						; compare field size with 2
	je .double
	cmp dword [eax + 4*field_reference_flag], value_true	; compare reference flag with true
	je .reference
	
	; 32-bit data field
	push_data edx											; push value1
	jmp .offset
	
	; 32-bit pointer field
.reference:
	push_pointer edx										; push value1
	jmp .offset
	
	; 64-bit field
.double:
	push_data edx											; push value1
	mov edx, [ecx + 4]										; edx = value2
	push_data edx											; push value2
	
	; increment pc and return
.offset:
	instruction_offset 3
proc_end








;---------------------------------------------------------
; putfield instruction
;---------------------------------------------------------
proc_begin field_putfield, 0
	; get the field entry
	instruction_two_byte_codes 1					; eax = pool index
	proc resolve_entry, eax, id_field				; eax = field entry
	cmp eax, value_null
	je .return										; abort if not resolved
	
	; read field values
	mov ebx, [eax + field_index*4]					; ebx = field index
	add ebx, object_fields							; ebx = field offset
	shl ebx, 2										; ebx = field offset in bytes
	
	; determine field type
	cmp dword [eax + field_size*4], 1				; compare field size with 1
	jne .double
	
	; set single word field
	pop_edx											; edx = value
	pop_eax											; eax = object
	add eax, ebx									; eax = location of value
	mov [eax], edx									; store the value
	jmp .offset

	; set double word field
.double:
	pop_edx											; edx = value2
	pop_ecx											; ecx = value1
	pop_eax											; eax = object
	add eax, ebx									; eax = location of value1
	mov [eax], ecx									; store value1
	mov [eax + 4], edx								; store value2

	; increment pc and return
.offset:
	instruction_offset 3
proc_end




