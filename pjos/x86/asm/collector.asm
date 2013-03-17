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
; Some global values used by the gc procedures
;---------------------------------------------------------
collector_from:			dd 0x0					; address of from space
collector_to:			dd 0x0					; address of to space
collector_scan:			dd 0x0					; address of next object to be scanned
collector_next:			dd 0x0					; address where next object will be evacuated to
collector_count:		dd 0x0					; the number of objects evacuated so far




;---------------------------------------------------------
; Collect garbage. Copy all live objects from the current
; space to the other one.
;---------------------------------------------------------
proc_begin collector_gc, 0
        ; disable interrupts
        cli

	; save instruction and stack register values
	proc reg_save
	
	; set globals
	mov eax, [reg_core]							; eax = core
	mov [collector_from], eax					; from = core
	cmp eax, space_a
	je .a2b
	mov ebx, space_a							; ebx = A
	jmp .to
.a2b:
	mov ebx, space_b							; ebx = B
.to:
	mov [collector_to], ebx						; to = ebx
	mov [collector_scan], ebx					; scan = ebx
	mov [collector_next], ebx					; next = ebx
	mov dword [collector_count], 0				; count = 0
	
	; evacuate the core object
	proc collector_evacuate, eax
	
	; Scan live object tree until all live objects have
	; been evacuated. This will be the case once the scan
	; pointer has caught up to the next pointer.
.next:
	mov eax, [collector_scan]					; eax = scan
	mov ebx, [collector_next]					; ebx = next
	cmp eax, ebx								; is scan < next?
	jge near .all_evacuated

	; check the lock and type of the current scan object
	proc collector_check, object_lock
	proc collector_check, object_type
	
	; check the other references depending on the type of object
	mov eax, [collector_scan]					; eax = scan
	mov eax, [eax]								; eax = header
	cmp eax, header_instance
	je .instance
	cmp eax, header_object_array
	je .object_array
	cmp eax, header_static_fields
	je .static_fields
	cmp eax, header_stack_frame
	jne .checked
	
	; call method depending on type of object being scanned
	proc collector_check_frame
	jmp .checked
.instance:
	proc collector_check_instance
	jmp .checked
.object_array:
	proc collector_check_object_array
	jmp .checked
.static_fields:
	proc collector_check_static_fields
	jmp .checked
	
	; set scan to next object
.checked:
	mov eax, [collector_scan]					; eax = scan
	mov eax, [eax + 4]							; eax = size in words
	shl eax, 2									; eax = size in bytes
	add [collector_scan], eax					; point scan to next object
	jmp .next
	
	; write the address of the next free space
	; into the core object
.all_evacuated:
	mov ecx, [collector_to]						; ecx = to (also core object location)
	mov [ecx + 4*core_next], ebx				; core.next = next
	
	; reload register values
.reload:
	mov eax, [collector_to]						; eax = to
	mov [reg_core], eax							; core = eax
	mov ebx, [eax + 4*core_running]				; ebx = thread
	mov [reg_thread], ebx						; thread = ebx
	proc reg_load								; reload register values for new space
        
        ; enable interrupts
        sti
proc_end

;---------------------------------------------------------
; Evacuate the object at the specified location in
; from-space to the next available location in to-space.
; Return the new address in eax.
;
; args: from
;---------------------------------------------------------
proc_begin collector_evacuate, 0
	mov eax, arg_1								; eax = from
	mov ebx, [collector_next]					; ebx = to
	mov ecx, [eax + 4]							; ecx = numWords
	shl ecx, 2									; ecx = numBytes
	add [collector_next], ecx					; set next pointer to new location
	sub ecx, 4									; ecx = numBytes - 4
	
	; check if loop has finished
.check:
	cmp ecx, 0
	jl .copied
	mov edx, [eax + ecx]						; edx = value
	mov [ebx + ecx], edx						; store value to new object
	sub ecx, 4
	jmp .check

	; set forwarding header and pointer in old object
.copied:
	mov dword [eax], header_forward				; store forward header
	mov [eax + 4], ebx							; store forwarding pointer
	inc dword [collector_count]					; increment count
	
	; store new address in eax and return
	mov eax, ebx
proc_end




;---------------------------------------------------------
; Check the reference values in the object array currently
; pointed to by the scan pointer.
;
; locals: index, max
;---------------------------------------------------------
proc_begin collector_check_object_array, 2
	mov eax, [collector_scan]					; eax = scan
	mov eax, [eax + 4*array_length]				; eax = length
	add eax, array_data							; eax = max
	mov local_2, eax							; max = eax
	mov eax, array_data							; eax = index of first element
	mov local_1, eax							; index = eax
	
	; check the next value
.check:											; eax contains index
	cmp eax, local_2
	jge .return
	proc collector_check, eax
	inc dword local_1
	mov eax, local_1
	jmp .check
proc_end


;---------------------------------------------------------
; Check the reference values in the instance object
; currently pointed to by the scan pointer.
;
; locals: index, max, firstFlag
;---------------------------------------------------------
proc_begin collector_check_instance, 3
	mov eax, [collector_scan]								; eax = scan
	mov eax, [eax + 4*object_type]							; eax = type
	mov eax, [eax + 4*type_instance_map]					; eax = map
	mov ebx, [eax + 4*array_length]							; ebx = max
	mov local_2, ebx										; max = ebx
	add eax, 4*array_data									; eax = address of first flag
	mov local_3, eax										; firstFlag = eax
	mov ecx, 0												; ecx = 0
	mov local_1, ecx										; index = ecx
	
	; check the next field
.check:														; ecx has index
	cmp ecx, local_2
	jge .return
	mov eax, local_3										; eax = firstFlag
	add eax, ecx											; eax = address of next flag
	mov byte al, [eax]										; al = flag
	cmp al, value_true
	jne .next
	add ecx, object_fields									; ecx = index of field
	proc collector_check, ecx
	
	; increment index
.next:
	inc dword local_1										; index++
	mov ecx, local_1										; ecx = index
	jmp .check
proc_end



;---------------------------------------------------------
; Check the reference values in the static fields object
; currently pointed to by the scan pointer.
;
; locals: index, max, firstFlag
;---------------------------------------------------------
proc_begin collector_check_static_fields, 3
	proc collector_check, statics_map
	
	; set locals
	mov eax, [collector_scan]								; eax = scan
	mov eax, [eax + 4*statics_map]							; eax = map
	mov ebx, [eax + 4*array_length]							; ebx = max
	mov local_2, ebx										; max = ebx
	add eax, 4*array_data									; eax = address of first flag
	mov local_3, eax										; firstFlag = eax
	mov ecx, 0												; ecx = 0
	mov local_1, ecx										; index = ecx
	
	; check the next field
.check:														; ecx has index
	cmp ecx, local_2
	jge .return
	mov eax, local_3										; eax = firstFlag
	add eax, ecx											; eax = address of next flag
	mov byte al, [eax]										; al = flag
	cmp al, value_true
	jne .next
	add ecx, statics_fields									; ecx = index of field
	proc collector_check, ecx
	
	; increment index
.next:
	inc dword local_1										; index++
	mov ecx, local_1										; ecx = index
	jmp .check
proc_end




;---------------------------------------------------------
; Check the reference values in the stack frame object
; currently pointed to by the scan pointer.
; 
; locals: index, max, nextLocal
;---------------------------------------------------------
proc_begin collector_check_frame, 3
	proc collector_check, frame_return_frame
	proc collector_check, frame_method
	
	; set locals
	mov eax, [collector_scan]								; eax = scan
	mov eax, [eax + 4]										; eax = numWords
	mov local_2, eax										; max = eax
	mov eax, frame_locals									; eax = index of first local within frame
	mov local_1, eax										; index = eax
	mov ebx, 4*frame_locals									; ebx = offset of first local in bytes
	add ebx, [collector_scan]								; ebx = address of first local
	mov local_3, ebx										; nextLocal = ebx
	
	; check the next tagged value
.check:														; eax has index, ebx has nextLocal
	cmp eax, local_2
	jge .return
	mov ebx, [ebx + 4]										; ebx = reference flag
	cmp ebx, value_true
	jne .next
	proc collector_check, eax

	; increment index and nextLocal
.next:
	add dword local_1, 2									; index += 2
	add dword local_3, 8									; nextLocal += 8
	mov eax, local_1										; eax = index
	mov ebx, local_3										; ebx = nextLocal
	jmp .check
proc_end


;---------------------------------------------------------
; Check the pointer at the specified 32-bit word offset
; from the current scan object. Evacuate the object
; referenced by the pointer if it has not already been
; evacuated. Update the pointer to point to the new
; location of the target object. Leave null pointer values
; unchanged.
;
; args: offset
;
; locals: address
;---------------------------------------------------------
proc_begin collector_check, 1
	; retrieve the pointer value
	mov eax, arg_1											; eax = offset
	shl eax, 2												; eax = offset in bytes
	add eax, [collector_scan]								; eax = address of pointer
	mov local_1, eax										; address = eax
	mov ebx, [eax]											; ebx = value of pointer
	
	; ignore null values
	cmp ebx, value_null
	je .return
	
	; if already evacuated, just update pointer
	mov ecx, [ebx]											; ecx = header
	cmp ecx, header_forward
	jne .evacuate
	mov ecx, [ebx + 4]										; ecx = new location
	mov [eax], ecx											; update the pointer to new location
	jmp .return
	
	; evacuate before updating pointer
.evacuate:
	proc collector_evacuate, ebx							; eax = new location
	mov ebx, local_1										; ebx = address of pointer
	mov [ebx], eax											; update the pointer to new location
proc_end


