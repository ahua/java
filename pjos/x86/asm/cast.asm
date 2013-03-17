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
; checkcast instruction
;---------------------------------------------------------
proc_begin cast_checkcast, 0
	; get the type entry from the constant pool
	instruction_two_byte_codes 1							; eax = pool index
	proc resolve_entry, eax, id_type						; eax = type entry
	cmp eax, value_null
	je .return												; abort if not resolved
	mov ebx, eax											; ebx = type entry

	; throw an exception if the cast cannot be allowed
	peek_eax												; eax = object on top of stack
	cmp eax, value_null
	je .offset												; if object is null just return
	mov eax, [eax + 4*object_type]							; eax = type of object
	proc cast_isSubClassOrImplementationOf, eax, ebx		; eax = true/false
	cmp eax, value_true
	je .offset												; if cast is ok, just offset and return
.exception:
	proc exception_throw, core_throw_class_cast
	jmp .return												; otherwise throw exception
.offset:
	instruction_offset 3
proc_end






;---------------------------------------------------------
; instanceof instruction
;---------------------------------------------------------
proc_begin cast_instanceof, 0
	; get the type entry from the constant pool
	instruction_two_byte_codes 1							; eax = pool index
	proc resolve_entry, eax, id_type						; eax = type entry
	cmp eax, value_null
	je .return												; abort if not resolved
	mov ebx, eax											; ebx = type entry
	
	; check if the object is an instance of the type
	pop_eax													; eax = object
	cmp eax, value_null
	je .isnull
	mov eax, [eax + 4*object_type]							; eax = type of object
	proc cast_isSubClassOrImplementationOf, eax, ebx		; eax = true/false
	jmp .result
.isnull:
	mov eax, value_false									; if object is null, eax = false
.result:
	push_data eax											; push result on stack
	instruction_offset 3
proc_end



;---------------------------------------------------------
; Return true in eax if the first type argument is either
; a subclass or an implementation of the second.
;
; args: typeA, typeB
;
; locals: A, B
;---------------------------------------------------------
proc_begin cast_isSubClassOrImplementationOf, 2
	; initialise locals
	mov eax, arg_1											; eax = typeA
	mov local_1, eax										; A = eax
	mov ebx, arg_2											; ebx = typeB
	mov local_2, ebx										; B = ebx
	
	; simple checks
.next:
	cmp eax, value_null
	je .false												; if A == null, return false
	cmp eax, ebx
	je .true												; if A == B, return true
	proc cast_isDeclaredImplementationOf, eax, ebx			; eax = true/false
	cmp eax, value_true
	je .return												; if A is declared implementation of B, return true

	; handle array types
	mov eax, local_1										; eax = A
	mov ebx, local_2										; ebx = B
	mov ecx, [eax + 4*type_component_type]					; ecx = compA
	cmp ecx, value_null
	je .super												; if compA == null, skip array checks (A is not array)
	mov eax, ecx											; eax = compA
	mov local_1, eax										; A = eax
	mov ebx, [ebx + 4*type_component_type]					; ebx = compB
	cmp ebx, value_null
	je .false												; if compB == null, return false (B is not array)
	mov local_2, ebx										; B = ebx
	jmp .next												; next iteration will compare component types
	
	; check super type of A
.super:
	mov eax, [eax + 4*type_super_type]						; eax = superA
	mov local_1, eax										; A = eax
	jmp .next												; next iteration will compare super type of A with B
	
	; return values
.false:														; return false
	mov eax, value_false
	jmp .return
.true:
	mov eax, value_true										; return true
proc_end


;---------------------------------------------------------
; Return true in eax if the first type argument is amoung
; the listed interfaces of the second.
;
; args: typeA, typeB
;---------------------------------------------------------
proc_begin cast_isDeclaredImplementationOf, 1
	mov eax, arg_1											; eax = typeA
	mov eax, [eax + 4*type_interface_types]					; eax = interface array
	mov edx, [eax + 4*array_length]							; edx = length of array
	add eax, 4*array_data									; eax = address of first element
	mov ecx, 0												; ecx = 0
	
	; check the next entry
.next:
	cmp ecx, edx
	je .false												; return false if loop finished and no match found
	mov ebx, [eax + 4*ecx]									; ebx = next interface type
	cmp ebx, arg_2
	je .true												; if ebx == typeB, return true
	inc ecx													; count++
	jmp .next
	
	; return values
.true:
	mov eax, value_true
	jmp .return
.false:
	mov eax, value_false
proc_end


