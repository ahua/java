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
; Throw an exception by calling the core method at the
; specified offset.
;
; args: offset
;---------------------------------------------------------
proc_begin exception_throw, 0
    mov ebx, arg_1                              ; ebx = offset
    mov eax, [reg_core]                         ; eax = address of core object
    mov ebx, [eax + 4*ebx]                      ; ebx = address of method
    mov ecx, [eax + 4*object_type]              ; ecx = address of core type (target)
    proc invoke_execute, ecx, ebx, 0
proc_end








;---------------------------------------------------------
; athrow instruction
;
; locals: exception, exceptionType
;---------------------------------------------------------
proc_begin exception_athrow, 2
	; throw null pointer exception if exception is null
	pop_eax										; eax = exception
	mov local_1, eax							; exception = eax
	cmp eax, value_null
	jne .search
	proc exception_throw, core_throw_null_pointer
	jmp .return
	
	; search for the appropriate handler
.search:
	mov ebx, [eax + 4*object_type]				; ebx = exception type
	mov local_2, ebx							; exceptionType = ebx
	cmp dword [reg_frame], value_null
	je .exit

	; search next method
.next:
	; if a suitable handler is found just return
	proc exception_search_for_handler, local_1, local_2 ; eax = true/false
	cmp eax, value_true
	je .return
	
	; if the method was synchronized, need to unlock
	proc exception_unlock_if_synchronized
	
	; pop to previous frame and continue loop
	mov eax, [reg_frame]						; eax = frame
	mov eax, [eax + 4*frame_return_frame]		; eax = previous frame
	mov ebx, [reg_thread]						; ebx = thread
	mov [ebx + 4*thread_frame], eax				; set previous frame in thread
	
	; load registers if next frame is not null
	cmp eax, value_null
	je .exit
	proc reg_load
	jmp .next
	
	; No handler was found in any frame so print
	; an error message and hang
.exit:
	mov dword [reg_thread], value_null
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg
	db 'Uncaught exception - VM execution stopped'
.end
proc_end



;---------------------------------------------------------
; If the current method is synchronized, unlock
; the object whose monitor is held.
;---------------------------------------------------------
proc_begin exception_unlock_if_synchronized, 0
	mov eax, [reg_method]						; eax = method
	mov eax, [eax + 4*entry_flags]				; eax = flags
	and eax, acc_synchronized
	cmp eax, 0
	je .return									; return if not synchronized
	mov eax, [reg_frame]						; eax = frame
	mov eax, [eax + 4*object_lock]				; eax = lock
	cmp eax, value_null
	je .return									; return if lock is null
	proc monitor_unlock, eax					; unlock object
proc_end





;---------------------------------------------------------
; Search for an appropriate handler in the current method
; and return true (=1) in eax if found,
; return false (=0) otherwise.
;
; args: exception, exceptionType
;
; locals:	1. entry
;			2. limit
;			3. pc
;			4. startpc
;			5. endpc
;			6. handlerpc
;			7. catchtype
;---------------------------------------------------------
proc_begin exception_search_for_handler, 7
	; check that the exception table exists
	mov eax, [reg_method]						; eax = method
	mov eax, [eax + 4*method_exceptions]		; eax = exception table
	cmp eax, value_null
	je near .false
	
	; calculate the loop parameters
	mov ebx, [eax + 4*array_length]				; ebx = length
	shl ebx, 1									; ebx = 2*length
	add eax, 4*array_data						; eax = address of first entry
	mov local_1, eax							; entry = eax
	add ebx, eax								; ebx = entry + 2*length
	mov local_2, ebx							; limit = ebx
	
	; calculate pc value
	mov eax, edi								; eax = address of current instruction
	sub eax, [reg_code]							; eax = pc
	mov local_3, eax							; pc = eax
	
	; check next entry in the table
.next:
	mov eax, local_1							; eax = entry
	cmp eax, local_2
	jge near .false								; loop finished, return false
	
	; check that pc is in range
	movzx ebx, word [eax]						; ebx = startpc
	cmp local_3, ebx
	jl .inc										; pc >= startpc
	movzx ebx, word [eax + 2]					; ebx = endpc
	cmp local_3, ebx
	jge .inc									; pc < endpc
	
	; find out if the exception type matches the handler
	movzx ebx, word [eax + 6]					; ebx = catchtype
	cmp ebx, 0
	je .catch									; catchtype of 0 means catch all
	mov ecx, [reg_pool]							; ecx = address of first pool entry
	mov ecx, [ecx + 4*ebx]						; ecx = catchTypeEntry
	proc exception_is_subclass, arg_2, ecx		; eax = true/false
	cmp eax, value_false
	je .false
	
	; direct execution to this handler
.catch:
	; set new pc
	mov eax, local_1							; eax = entry
	movzx edi, word [eax + 4]					; edi = handlerpc
	add edi, [reg_code]							; edi = new pc value
	mov [reg_instruction], edi					; save pc value (probably not necessary)
	
	; clear operand stack of current frame
	mov esi, [reg_frame]						; esi = frame
	mov ebx, [esi + 4*object_size]				; ebx = frame size in words
	shl ebx, 2									; ebx = frame size in bytes
	add esi, ebx								; esi = top of stack (empty)
	mov [reg_stack], esi						; save stack value (probably not necessary)
	
	; push exception on stack and return true
	mov eax, arg_1								; eax = exception
	push_pointer eax
	mov eax, value_true
	jmp .return
	
	; increment entry and continue loop
.inc:
	add dword local_1, 8						; next entry - each entry is 8 bytes (4 shorts)
	jmp .next
	
	; return false
.false:
	mov eax, value_false
proc_end


;---------------------------------------------------------
; Return true in eax if type A represents a subclass
; of type B. Type B may be an unresolved entry instead
; of a valid type.
;
; args: typeA, typeB
;---------------------------------------------------------
proc_begin exception_is_subclass, 0
	; check if type B is unresolved
	mov ecx, value_null							; ecx = null
	mov eax, arg_2								; eax = B
	mov ebx, [eax + 4*entry_id]					; ebx = id
	cmp ebx, id_unresolved_type
	jne .check
	mov ecx, [eax + 4*entry_name]				; ecx = nameB
	
	; initialise loop, start with type A
.check:
	mov eax, arg_1								; eax = typeA

	; loop to check A and all its superclasses against B
.next:
	cmp eax, value_null
	je .false
	
	; check against type B
	cmp eax, arg_2
	je .true
	
	; check against nameB (if nameB is not null)
	cmp ecx, value_null
	je .continue
	mov ebx, [eax + 4*type_name]				; ebx = name of type
	cmp ebx, ecx
	je .true
	
	; continue loop
.continue:
	mov eax, [eax + 4*type_super_type]			; eax = super type
	jmp .next
	
	; return values
.true:
	mov eax, value_true
	jmp .return
.false
	mov eax, value_false
proc_end

