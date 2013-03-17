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
; invokeinterface instruction
;
; locals: target
;---------------------------------------------------------
proc_begin invoke_invokeinterface, 1
    ; get the method entry from the constant pool
    instruction_two_byte_codes 1                            ; eax = entry index
    proc resolve_entry, eax, id_method                      ; eax = method entry
    cmp eax, value_null
    je .return                                              ; abort if not resolved
    
    ; get a pointer to the object the method was called on
    mov ebx, [eax + 4*method_arg_count]                     ; ebx = argcount
    dec ebx                                                 ; ebx = argcount - 1
    mov edx, [esi + 8*ebx]                                  ; edx = object
    mov local_1, edx                                        ; target = edx

    ; check for null pointer
    cmp edx, value_null
    jne .lookup
    proc exception_throw, core_throw_null_pointer
    jmp .return                                             ; throw exception if object is null

    ; lookup the method
.lookup:
    ; eax = method
    proc invoke_lookup, [edx + 4*object_type], [eax + 4*entry_name], [eax + 4*entry_descriptor]

    ; execute the method
    proc invoke_execute, local_1, eax, 5
proc_end




;---------------------------------------------------------
; invokespecial instruction
;
; locals: target, method
;---------------------------------------------------------
proc_begin invoke_invokespecial, 2
    ; get the method entry from the constant pool
    instruction_two_byte_codes 1            ; eax = entry index
    proc resolve_entry, eax, id_method      ; eax = method entry
    cmp eax, value_null
    je .return                              ; abort if not resolved
    mov local_2, eax						; method = eax
    
    ; get a pointer to the object the method was called on
    mov ebx, [eax + 4*method_arg_count]     ; ebx = argcount
    dec ebx                                 ; ebx = argcount - 1
    mov edx, [esi + 8*ebx]                  ; edx = object
    mov local_1, edx						; target = edx

    ; check for null pointer
    cmp edx, value_null
    jne .execute
    proc exception_throw, core_throw_null_pointer
    jmp .return
    
    ; execute the method
.execute:
    proc invoke_execute, local_1, local_2, 3
proc_end



;---------------------------------------------------------
; invokevirtual instruction
;
; locals: target
;---------------------------------------------------------
proc_begin invoke_invokevirtual, 1
    ; get the method entry from the constant pool
    instruction_two_byte_codes 1                            ; eax = pool index
    proc resolve_entry, eax, id_method                      ; eax = method entry
    cmp eax, value_null
    je .return                                              ; rollback
    
    ; get a pointer to the object the method was called on
    mov ebx, [eax + 4*method_arg_count]                     ; ebx = argcount
    dec ebx                                                 ; ebx = argcount - 1
    mov edx, [esi + 8*ebx]                                  ; edx = object
    mov local_1, edx                                        ; target = edx

    ; check for null pointer
    cmp edx, value_null
    jne .lookup
    proc exception_throw, core_throw_null_pointer
    jmp .return

    ; lookup the method
.lookup:
    ; eax = method
    proc invoke_lookup, [edx + 4*object_type], [eax + 4*entry_name], [eax + 4*entry_descriptor]

    ; execute the method
    proc invoke_execute, local_1, eax, 3
proc_end



;---------------------------------------------------------
; invokestatic instruction
;---------------------------------------------------------
proc_begin invoke_invokestatic, 0
    ; get the method entry from the constant pool
    instruction_two_byte_codes 1            ; eax = entry index
    proc resolve_entry, eax, id_method      ; eax = method entry
    cmp eax, value_null
    je .return                              ; abort if not resolved

    ; execute the method
    mov edx, [eax + 4*entry_owner]          ; edx = owner (type of method)
    proc invoke_execute, edx, eax, 3
proc_end








;---------------------------------------------------------
; Lookup the method with the specified name and
; descriptor. The strings can be compared by reference
; because they have all been interned. Return the method
; address in eax.
;
; args: type, name, descriptor
;
; locals: currentType
;---------------------------------------------------------
proc_begin invoke_lookup, 1
    mov eax, arg_1                          ; eax = type
    mov local_1, eax                        ; currentType = eax

    ; check for null type
.check
    cmp eax, value_null
    jne .find
    proc invoke_lookup_error, arg_1, arg_2, arg_3
    jmp .return
    

    ; find the method in the current type
.find
    proc invoke_find, eax, arg_2, arg_3     ; eax = method (or null)

    ; if not found, check super type
    cmp eax, 0
    jne .return
    mov eax, local_1
    mov eax, [eax + 4*type_super_type]      ; eax = super type of current
    mov local_1, eax                        ; currentType = eax
    jmp .check
proc_end




;---------------------------------------------------------
; A temporary means to print error information instead
; of throwing a NoSuchMethodException. This method hangs.
;
; args: type, name, descriptor
;---------------------------------------------------------
proc_begin invoke_lookup_error, 0
    proc screen_print_string, .msg1, .msg2-.msg1
    mov eax, arg_1                          ; eax = type
    mov eax, [eax + 4*type_name]            ; eax = address of type name
    proc screen_print_java_string, eax
    proc screen_print_string, .msg2, .msg3-.msg2
    proc screen_print_java_string, arg_2
    proc screen_print_string, .msg3, .end-.msg3
    proc screen_print_java_string, arg_3
    jmp $
.msg1
    db 'invoke_lookup: Need to throw NoSuchMethodException', screen_newline, 'type: '
.msg2
    db screen_newline, 'name: '
.msg3
    db screen_newline, 'descriptor: '
.end
proc_end






;---------------------------------------------------------
; Find the method matching the given name and descriptor
; by searching through the method array of the given type.
; Return the method address in eax. If no method is found,
; return null (zero) in eax.
;
; args: type, name, descriptor
;
; locals: last
;---------------------------------------------------------
proc_begin invoke_find, 1
    ; get address of method array
    mov ebx, arg_1                          ; ebx = type
    mov ebx, [ebx + 4*type_methods]         ; ebx = method array
    mov ecx, [ebx + 4*array_length]         ; ecx = array length
    add ebx, 4*array_data                   ; ebx = address of first element
    shl ecx, 2                              ; ecx = length in bytes
    add ecx, ebx                            ; ecx = address after last element
    
    ; check the next method entry
.check:                                     ; ebx contains address of next method
    cmp ebx, ecx                            ; check for end of loop
    je .finished

    ; check name
    mov eax, [ebx]                          ; eax = method
    mov edx, [eax + 4*entry_name]           ; edx = name
    cmp edx, arg_2                          ; compare names
    jne .next
    mov edx, [eax + 4*entry_descriptor]     ; edx = descriptor
    cmp edx, arg_3                          ; compare descriptors
    je .return                              ; eax is returned if descriptors match

    ; move to next method in array
.next:
    add ebx, 4                              ; ebx contains address of next method in array
    jmp .check

    ; method wasn't found so return null
.finished:
    mov eax, value_null
proc_end




;---------------------------------------------------------
; Execute a method. Arguments are on the stack. If
; the method is executed successfully return 1 in eax,
; otherwise return 0. The target parameter is the class
; for static methods, the object for instance methods.
;
; args: target, method, pcOffset
;
; locals:   1 lock
;           2 numDataWords
;           3 newFrameAddress
;           4 argCount
;           5 flags
;---------------------------------------------------------
proc_begin invoke_execute, 5
    ; set argument count and flags
    mov eax, arg_2							; eax = method
    mov ebx, [eax + 4*method_arg_count]		; ebx = arg count
    mov local_4, ebx						; argCount = ebx
    mov ecx, [eax + 4*entry_flags]			; ecx = flags
    mov local_5, ecx						; flags = ecx

    ; check if the method is native
    and ecx, acc_native
    cmp ecx, 0
    je .synch								; jump if not native

    ; execute native method
    proc invoke_native, arg_2, arg_3, eax	; (method, pcOffset, argCount)
    jmp .true

    ; check for synchronization
.synch:
    mov dword local_1, value_null			; lock = null
    mov eax, local_5						; eax = flags
    and eax, acc_synchronized
    cmp eax, 0
    je .size
    
    ; get the lock of the object at the address in eax
    mov eax, arg_1							; eax = target
    proc monitor_get_lock, eax				; eax = lock
    mov local_1, eax						; lock = eax
    cmp eax, value_null
    je near .false							; rollback if lock null, gc has been done
    
    ; acquire the lock
    proc monitor_acquire, [reg_thread], eax, 0	; eax = true if acquired, false if waiting
    cmp eax, value_true
    jne near .false							; rollback if waiting for lock

    ; calculate size of stack frame
.size:
    mov eax, arg_2							; eax = method
    mov ebx, [eax + 4*method_max_stack]		; ebx = maxStack
    add ebx, [eax + 4*method_max_locals]	; ebx = maxStack + maxLocals
    shl ebx, 1								; ebx = 2*maxStack + 2*max_locals
    add ebx , frame_locals					; ebx += frame_locals
    mov local_2, ebx						; numDataWords = ebx

    ; allocate space for new frame
    proc allocate_new, ebx, header_stack_frame      ; eax now contains address of new frame

    ; if the new frame address is null, gc has been done so roll back
    cmp eax, value_null
    je near .return
    mov local_3, eax						; newFrameAddress = eax

    ; if the lock is not null, increment the lock count
    mov eax, local_1						; eax = lock
    cmp eax, value_null
    je .init
    proc monitor_increment, eax, 1

    ; initialise the new frame
.init:
    mov ebx, local_3						; ebx = newFrameAddress
    mov [ebx + 4*object_hashcode], ebx		; set hash code
    mov ecx, local_1						; ecx = lock
    mov [ebx + 4*object_lock], ecx			; set lock
    mov ecx, [reg_frame]					; ecx = frame
    mov eax, [ecx + 4*object_type]			; eax = frame type
    mov [ebx + 4*object_type], eax			; set type
    mov [ebx + 4*frame_return_frame], ecx	; set return frame
    mov eax, arg_2							; eax = method
    mov [ebx + 4*frame_method], eax			; set method
    mov eax, edi							; eax = instruction pointer
    sub eax, [reg_code]						; eax = pc
    add eax, arg_3							; eax = pc + pcOffset
    mov [ebx + 4*frame_return_pc], eax		; set return pc
    ;pc is already 0
    mov eax, local_2						; eax = numWords
    shl eax, 2								; eax = numBytes
    mov [ebx + 4*frame_sp], eax				; set stack pointer

    ; pop arguments off stack and write to locals of new frame
    proc invoke_copy_args, local_3, local_4

    ; set current thread to point to new frame
    proc reg_save							; save register values
    mov ebx, local_3						; ebx = newFrameAddress
    mov eax, [reg_thread]					; eax = thread
    mov [eax + 4*thread_frame], ebx			; point current thread to new frame
    proc reg_load							; set registers for new frame
    jmp .true
    
    ; return false
.false:
    mov eax, value_false
    jmp .return

    ; return true
.true:
    mov eax, value_true
proc_end






;---------------------------------------------------------
; Pop the specified number of arguments off the stack
; of the current frame and put them in the local slots of
; the given frame.
;
; args: frame, argCount
;
; locals: count, localIndex
;---------------------------------------------------------
proc_begin invoke_copy_args, 2
    mov eax, arg_1								; eax = frame
    add eax, 4*frame_locals						; eax = address of first local
    mov ebx, arg_2								; ebx = argCount
    shl ebx, 3									; ebx = argCount in bytes
    add ebx, eax								; ebx = address after last local
    sub ebx, 8									; ebx = address of last local
    
    ; check if all the args have been copied
.check:
    cmp ebx, eax
    jl .return
    
    ; copy the next arg
    indicate_ecx								; ecx = reference flag
    pop_edx										; edx = arg
    mov [ebx], edx								; store value
    mov [ebx + 4], ecx							; set reference flag
    
    ; point to next local
    sub ebx, 8									; ebx = next local
    jmp .check
proc_end






;---------------------------------------------------------
; Execute a native method
;
; args: method, pcOffset, argcount
;---------------------------------------------------------
proc_begin invoke_native, 0
    ; check the magic method id
    mov eax, arg_1								; eax = method
    mov ebx, [eax + 4*method_magic]				; ebx = magic id
    cmp ebx, 0
    je near .not_magic							; 0 is non-magic
    
    ; retrieve the method location using the lookup table
    mov ecx, ebx								; ecx = table offset in words
    shl ecx, 2									; ecx = table offset in bytes
    add ecx, .lookup_table						; ecx = entry address
    mov ecx, [ecx]								; ecx = entry
    cmp ecx, 0x0
    je NEAR .invalid
    proc ecx, arg_2, arg_3, arg_1				; call the procedure
    jmp .return

; This table is used to map asm procedures to magic method ids
.lookup_table:
	.unused:								dd 0x0

	; runtime methods
	.magic_runtime_core_debug:				dd invoke_core_debug
	.magic_runtime_core_execute_static:		dd invoke_core_execute_static
	.magic_runtime_core_get:				dd invoke_core_get
	.magic_runtime_core_get_type:			dd invoke_core_get_type
	.magic_runtime_frame_current:			dd invoke_frame_current_frame
	.magic_runtime_idle_sleep:				dd invoke_idle_sleep
	.magic_runtime_statics_create:			dd invoke_statics_create
	.magic_runtime_thread_current:			dd invoke_thread_current
	.magic_runtime_thread_sleep:			dd invoke_thread_sleep
	.magic_runtime_thread_start_hook:		dd invoke_thread_start_hook
	.magic_runtime_thread_suspend:			dd invoke_thread_suspend
	.magic_runtime_thread_resume:			dd invoke_thread_resume

	; java API methods
	.magic_java_object_get_class:			dd invoke_object_get_class
	.magic_java_object_notify:				dd 0x0
	.magic_java_object_notify_all:			dd invoke_object_notify_all
	.magic_java_object_wait:                dd invoke_object_wait
	.magic_java_system_time:				dd invoke_system_time
	.magic_java_system_hashcode:			dd invoke_system_hashcode
	.magic_java_system_set_err:				dd invoke_system_set_err
	.magic_java_system_set_out:				dd invoke_system_set_out
	.magic_java_system_set_in:				dd invoke_system_set_in

	; platform specific methods
	.magic_x86_write_to_console:			dd invoke_write_to_console
	.magic_x86_next_interrupt:				dd invoke_next_interrupt
	.magic_x86_in:							dd invoke_io_in
	.magic_x86_out:							dd invoke_io_out
	.magic_x86_read_dma:					dd invoke_read_dma

    ; The magic id in ebx is not valid so print error message and hang
.invalid:
    mov [.temp], ebx
    proc screen_print_string, .msg1, .end1-.msg1
    proc screen_print_word, [.temp]
    jmp $
.temp:
    dd 0x0
.msg1:
    db 'Invalid magic method id: 0x'
.end1:
    
    ; Ordinary native methods (ie. non-magic) are not currently supported
    ; so just print error message and hang
.not_magic:
    proc screen_print_string, .msg2, .end2-.msg2
    jmp $
.msg2:
    db 'Native methods are not supported'
.end2:
proc_end




;---------------------------------------------------------
; Implementation of magic method:
; vm.runtime.Core.get()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_core_get, 0
    mov eax, [reg_core]                             ; eax = core
    push_pointer eax                                ; push pointer to core
    instruction_offset arg_1
proc_end



;---------------------------------------------------------
; Implementation of magic method:
; vm.runtime.Core.getType(Class)
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_core_get_type, 0
	pop_eax								; eax = class
	; type is first field
	mov eax, [eax + 4*object_fields]	; eax = type
	push_pointer eax
    instruction_offset arg_1
proc_end




;---------------------------------------------------------
; Implementation of magic method:
; vm.runtime.Core.executeStatic(Method)
;
; args: pcOffset, argCount, method
;
; locals: method
;---------------------------------------------------------
proc_begin invoke_core_execute_static, 1
    ; execute the method
    pop_eax                                         ; eax = method
    mov local_1, eax                                ; method = eax
    cmp eax, value_null
    je .is_null
    mov ebx, [eax + 4*entry_owner]                  ; ebx = method type
    proc invoke_execute, ebx, eax, arg_1            ; eax = true/false
    cmp eax, value_true
    je .return                                      ; execution was successful
    mov eax, local_1
    push_pointer eax                                ; push method back on stack
    jmp .return

    ; throw a null pointer exception
.is_null:
    proc exception_throw, core_throw_null_pointer
proc_end



;---------------------------------------------------------
; Implementation of magic method:
; vm.runtime.Core.debug(String)
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_core_debug, 0
    ; print the string
    pop_eax                                         ; eax = message string
    proc screen_print_java_string, eax              ; print the message
    proc screen_print_string, .msg, .end-.msg       ; print newline char 0xA
    
    ; offset and return
    instruction_offset arg_1
    jmp .return
    
    ; newline character as string
.msg:
    db 0xA
.end:
proc_end



;---------------------------------------------------------
; Implementation of magic method:
; org.pjos.common.runtime.Frame.currentFrame()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_frame_current_frame, 0
    mov eax, [reg_frame]                            ; eax = frame address
    push_pointer eax
    instruction_offset arg_1
proc_end




;---------------------------------------------------------
; Implementation of magic method:
; Object.getClass()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_object_get_class, 0
    pop_eax                                         ; eax = object address
    mov eax, [eax + 4*object_type]                  ; eax = type address
    mov eax, [eax + 4*type_peer]                    ; eax = class address
    push_pointer eax
    instruction_offset arg_1
proc_end




;---------------------------------------------------------
; Implementation of magic method:
; Object.notify()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_object_notify, 0
    pop_eax                                         ; eax = object
    instruction_offset arg_1
    proc monitor_notify, eax                        ; notify(object)
proc_end


;---------------------------------------------------------
; Implementation of magic method:
; Object.notifyAll()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_object_notify_all, 0
    pop_eax                                         ; eax = object
    instruction_offset arg_1
    proc monitor_notify_all, eax                    ; notifyAll(object)
proc_end


;---------------------------------------------------------
; Implementation of magic method:
; Object.wait(long, int)
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_object_wait, 0
    pop_ebx                                         ; ebx = nanos
    pop_long edx, ecx                               ; edx:ecx = millis
    pop_eax                                         ; eax = object
    instruction_offset arg_1                        ; can't abort
    proc monitor_wait, eax, edx, ecx                ; wait(obj, msHigh, msLow)
proc_end




;---------------------------------------------------------
; Return system time in milliseconds
; System.currentTimeMillis()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_system_time, 0
	mov edx, [reg_timehigh]
	mov eax, [reg_timelow]							; edx:eax = time
	push_long edx, eax
	instruction_offset arg_1
proc_end



;---------------------------------------------------------
; Implementation of magic method:
; System.identityHashCode(Object)
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_system_hashcode, 0
    pop_eax                                                 ; eax = object
    mov eax, [eax + 4*object_hashcode]                      ; eax = hashcode
    push_data eax
    instruction_offset arg_1
proc_end




;---------------------------------------------------------
; Implementation of magic method:
; System.setOut(PrintStream)
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_system_set_out, 0
    pop_eax                                                 ; eax = argument
    mov ebx, arg_3                                          ; ebx = method
    mov ebx, [ebx + 4*entry_owner]                          ; ebx = type
    mov ebx, [ebx + 4*type_statics]                         ; ebx = statics
    mov [ebx + 4*statics_fields + 4*system_out], eax        ; set field
    instruction_offset arg_1
proc_end

;---------------------------------------------------------
; Implementation of magic method:
; System.setErr(PrintStream)
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_system_set_err, 0
    pop_eax                                                 ; eax = argument (PrintStream)
    mov ebx, arg_3                                          ; ebx = method
    mov ebx, [ebx + 4*entry_owner]                          ; ebx = type
    mov ebx, [ebx + 4*type_statics]                         ; ebx = statics
    mov [ebx + 4*statics_fields + 4*system_err], eax        ; set the static field
    instruction_offset arg_1
proc_end


;---------------------------------------------------------
; Implementation of magic method:
; System.setIn(InputStream)
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_system_set_in, 0
    pop_eax                                                 ; eax = arg
    mov ebx, arg_3                                          ; ebx = method
    mov ebx, [ebx + 4*entry_owner]                          ; ebx = type
    mov ebx, [ebx + 4*type_statics]                         ; ebx = statics
    mov [ebx + 4*statics_fields + 4*system_in], eax         ; set the static field
    instruction_offset arg_1
proc_end




;---------------------------------------------------------
; Implementation of magic method:
; vm.runtime.Thread.currentThread()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_thread_current, 0
    mov eax, [reg_thread]                                   ; eax = current thread
    push_pointer eax
    instruction_offset arg_1
proc_end

;---------------------------------------------------------
; Implementation of magic method:
; vm.runtime.Thread.startHook()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_thread_start_hook, 0
    proc threads_start_new_thread, arg_1
proc_end

;---------------------------------------------------------
; Implementation of magic method:
; vm.runtime.Thread.suspend()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_thread_suspend, 0
    pop_eax                                                 ; eax = thread
    instruction_offset arg_1
    proc threads_suspend, eax
proc_end


;---------------------------------------------------------
; Implementation of magic method:
; vm.runtime.Thread.resume()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_thread_resume, 0
    pop_eax										; eax = thread
    instruction_offset arg_1
    proc threads_resume, eax
proc_end


;---------------------------------------------------------
; Implementation of magic method:
; vm.runtime.Thread.sleep()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_thread_sleep, 0
    pop_ecx										; ecx = nanoseconds
    pop_long edx, eax							; edx:eax = milliseconds
    instruction_offset arg_1
    proc sleep_add_to_sleep_queue, edx, eax
    proc threads_unschedule, [reg_thread]
proc_end


;---------------------------------------------------------
; Implementation of magic method:
; X86Architecture.writeToConsole(I)
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_write_to_console, 0
    pop_eax										; eax = character
    and eax, 0xFF								; eax = ascii char value
    proc screen_print_char, eax
    instruction_offset arg_1
proc_end


;---------------------------------------------------------
; Implementation of magic method:
; Idle.sleep()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_idle_sleep, 0
    instruction_offset arg_1
proc_end



;---------------------------------------------------------
; Implementation of magic method:
; Statics.create(Type)
;
; args: pcOffset, argCount, method
;
; locals: type
;---------------------------------------------------------
proc_begin invoke_statics_create, 1
	pop_eax										; eax = type
	mov local_1, eax							; type = eax
	mov ebx, [eax + 4*type_static_field_count]	; ebx = num static fields
	add ebx, statics_fields						; ebx = num words

	; allocate space for statics object
	proc allocate_new, ebx, header_static_fields	; eax = address
	cmp eax, value_null
	jne .init
	mov eax, local_1							; eax = type
	push_pointer eax							; restore stack
	jmp .return									; rollback

	; initialise statics object
.init:
	mov [eax + 4*object_hashcode], eax			; set hash code
	mov ebx, [reg_core]							; ebx = core
	mov ebx, [ebx + 4*core_statics_type]		; ebx = type
	mov [eax + 4*object_type], ebx				; set type
	mov ecx, [ebx + 4*type_static_map]			; ecx = map
	mov [eax + 4*statics_map], ecx				; set map
	
	; set statics field in type object
	mov [ebx + 4*type_statics], eax
	instruction_offset arg_1
proc_end





;---------------------------------------------------------
; Implementation of magic method:
; X86Architecture.nextInterrupt()
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_next_interrupt, 0
    ; disable interrupts so following code will not be interrupted
    cli
    
    ; check if buffer is empty
    mov eax, [tables_interrupt_first]                       ; eax = first
    cmp eax, [tables_interrupt_last]
    jne .not_empty
    mov ebx, -1                                             ; ebx = -1
    jmp .enable
    
    ; extract the next code from the buffer
.not_empty:
    mov ebx, 0x0
    mov bl, [eax]                                           ; ebx = code
    
    ; update the first pointer
    inc eax                                                 ; eax++
    cmp eax, tables_idt_location
    jl .save_first
    mov eax, 0x0                                            ; wrap first
.save_first:
    mov [tables_interrupt_first], eax                       ; save first
    
    ; enable interrupts
.enable:
    sti
    
    ; push result code, offset pc and return
    push_data ebx                                           ; push result code
    instruction_offset arg_1
proc_end





;---------------------------------------------------------
; Implementation of magic method:
; X86Architecture.in(I)I
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_io_in, 0
    mov eax, 0                                              ; eax = 0
    pop_edx                                                 ; edx = port
    in al, dx                                               ; read value from port into eax
    push_data eax
    instruction_offset arg_1
proc_end



;---------------------------------------------------------
; Implementation of magic method:
; X86Architecture.out(II)V
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_io_out, 0
    pop_edx                                                 ; edx = port
    pop_eax                                                 ; eax = value
    out dx, al                                              ; write value to port
    instruction_offset arg_1
proc_end



;---------------------------------------------------------
; Implementation of magic method:
; X86Architecture.readDma([B)V
;
; args: pcOffset, argCount, method
;---------------------------------------------------------
proc_begin invoke_read_dma, 0
	pop_eax													; eax = byte array
	add eax, 4*array_data									; eax = destination address
	push esi												; save sp
	push edi												; save pc
	mov esi, 0x00											; copy from address 0x00
	mov edi, eax											; copy to byte array
	mov ecx, 128											; 512 bytes is 128 dwords
	cld														; clear direction flag
	rep movsd												; copy memory
	pop edi													; restore pc
	pop esi													; restore sp
    instruction_offset arg_1
proc_end

