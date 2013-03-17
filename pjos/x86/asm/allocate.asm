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
; Allocate space on the heap for a new object requiring
; the specified number of words. If there is no
; space remaining, run the garbage collector and return
; null (zero) in eax, otherwise return the address of
; the newly created object in eax. Only the header
; and size fields of the object is initialised to
; correct values, the rest is zeroed.
;
; args: numWords, header
;---------------------------------------------------------
proc_begin allocate_new, 0
	; find out how many bytes are required
	mov eax, arg_1								; eax = numWords
	shl eax, 2									; eax = numBytes
	
	; check if there is enough space
	mov ebx, [reg_core]							; ebx = core
	mov edx, [ebx + 4*core_next]				; edx = address of new allocation
	add eax, edx								; eax = next available address
	add ebx, space_size							; ebx = max available in this space
	cmp eax, ebx
	jl .update
	
	; there is not enough space so run the garbage collector
	proc collector_gc							; run collector
	mov eax, value_null
	jmp .return									; return null
	
	; update the next available address in the core
.update:
	mov ebx, [reg_core]							; ebx = core
	mov [ebx + 4*core_next], eax				; core.next = next available address
	
	; initialise new allocation
	mov eax, arg_2								; eax = header
	mov [edx], eax								; set object header
	mov ecx, arg_1								; ecx = numWords
	mov [edx + 4], ecx							; set object size

	; zero the rest of the object
	sub ecx, 2									; ecx = numWords - 2
	mov eax, edx								; eax = address of object
	add eax, 8									; skip header
.next:
	mov dword [eax], 0
	add eax, 4
	loop .next

	; return the address of the new object
	mov eax, edx
proc_end
