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
; Insert the given thread into the queue of running
; threads. Assume the given thread is not already
; scheduled. It will be inserted directly before the
; first thread in the queue.
;
; args: thread
;---------------------------------------------------------
proc_begin threads_schedule, 0
	; disable interrupts
	cli
	
	; if the thread is suspended, do nothing
	mov eax, arg_1											; eax = thread
	cmp dword [eax + 4*thread_suspended], value_true		; check suspended flag
	je .enable
	
	; if there are currently no threads in the running queue...
	mov ebx, [reg_core]										; ebx = core
	mov ecx, [ebx + 4*core_running]							; ecx = running
	cmp ecx, value_null
	jne .insert
	mov [ebx + 4*core_running], eax							; thread is now running
	mov [eax + 4*thread_next_running], eax
	mov [eax + 4*thread_prev_running], eax					; thread is now its own next and previous
	jmp .enable
	
	; was prev <-> running
	; now prev <-> thread <-> running
.insert:
	mov ebx, [ecx + 4*thread_prev_running]					; ebx = prev
	mov [eax + 4*thread_prev_running], ebx					; prev <- thread
	mov [ebx + 4*thread_next_running], eax					; prev -> thread
	mov [ecx + 4*thread_prev_running], eax					; thread <- running
	mov [eax + 4*thread_next_running], ecx					; thread -> running
	
	; enable interrupts
.enable:
	sti
proc_end




;---------------------------------------------------------
; Unschedule the specified thread by removing it from
; the queue of running threads. The idle thread should
; not be unscheduled!
;
; args: thread
;---------------------------------------------------------
proc_begin threads_unschedule, 0
	; disable interrupts
	cli

	; if the given thread is the only one in the queue...
	mov eax, arg_1											; eax = thread
	mov ebx, [eax + 4*thread_prev_running]					; ebx = previous
	mov ecx, [eax + 4*thread_next_running]					; ecx = next
	mov edx, [reg_core]										; edx = core
	cmp eax, ecx
	jne .remove
	mov dword [edx + 4*core_running], value_null			; store null in core
	jmp .clear

	; ...otherwise remove it from the queue
.remove:
	cmp eax, [edx + 4*core_running]							; is the thread currently running?
	jne .update
	mov [edx + 4*core_running], ecx							; store next thread in core

	; was previous <-> thread <-> next
	; now previous <-> next
.update:
	mov [ebx + 4*thread_next_running], ecx					; previous -> next
	mov [ecx + 4*thread_prev_running], ebx					; previous <- next

	; clear the next and previous fields of the thread
.clear:
	mov dword [eax + 4*thread_prev_running], value_null
	mov dword [eax + 4*thread_next_running], value_null
	
	; if the current thread is being unscheduled, set the registers accordingly
	cmp eax, [reg_thread]
	jne .enable
	proc reg_save
	mov dword [reg_thread], value_null
	
	; enable interrupts
.enable:
	sti
proc_end




;---------------------------------------------------------
; Schedule the next running thread
;---------------------------------------------------------
proc_begin threads_schedule_next_thread, 0
	; get the address of the next thread
	mov eax, [reg_core]										; eax = core
	mov ebx, [eax + 4*core_running]							; ebx = running
	cmp ebx, value_null
	jne .not_null
	
	; schedule the idle thread
	mov ebx, [eax + 4*core_idle]							; ebx = idle thread
	jmp .load

	; schedule the next thread
.not_null:
	mov ebx, [ebx + 4*thread_next_running]					; ebx = next thread
	mov [eax + 4*core_running], ebx							; set next thread in core

	; set virtual registers
.load:														; ebx contains thread to run
	mov [reg_thread], ebx									; set thread register
	proc reg_load											; set other registers
proc_end



;---------------------------------------------------------
; Start a new thread
;
; args: pcOffset
;
; locals: method
;---------------------------------------------------------
proc_begin threads_start_new_thread, 1
	; calculate required size for stack frame
	mov eax, [reg_core]										; eax = core
	mov eax, [eax + 4*core_thread_run_method]				; eax = method
	mov local_1, eax										; method = eax
	mov ebx, [eax + 4*method_max_stack]						; ebx = maxStack
	add ebx, [eax + 4*method_max_locals]					; ebx = maxStack + maxLocals
	shl ebx, 1												; ebx = 2 * (maxStack + maxLocals)
	add ebx, frame_locals									; ebx = number of words required for stack frame
	
	; allocate space for new frame
	proc allocate_new, ebx, header_stack_frame				; eax = new stack frame allocation
	cmp eax, value_null
	je .return												; abort because gc was run
	
	; initialise the new frame
	pop_ebx													; ebx = thread
	mov ecx, [reg_frame]									; ecx = current frame
	mov ecx, [ecx + 4*object_type]							; ecx = frame type
	mov [eax + 4*object_type], ecx							; set type
	; return frame already null
	mov ecx, local_1										; ecx = method
	mov [eax + 4*frame_method], ecx							; set method
	; pc already set to 0
	mov ecx, [eax + 4]										; ecx = numWords
	shl ecx, 2												; ecx = numBytes
	mov [eax + 4*frame_sp], ecx								; set stack pointer
	mov [eax + 4*frame_locals], ebx							; set thread instance
	mov dword [eax + 4*frame_locals + 4], value_true		; set reference flag for arg 0
	
	; set new frame in thread object and set started flag
	mov [ebx + 4*thread_frame], eax							; set new frame in thread
	mov dword [ebx + 4*thread_started], value_true			; set started flag
	
	; offset pc and schedule new thread
	instruction_offset arg_1
	proc threads_schedule, ebx
proc_end

;---------------------------------------------------------
; Suspend the specified thread
;
; args: thread
;---------------------------------------------------------
proc_begin threads_suspend, 0
	; disable interrupts
	cli
	
	; set the suspended flag
	mov eax, arg_1											; eax = thread
	mov dword [eax + 4*thread_suspended], value_true		; thread.suspended = true
	
	; unschedule thread if necessary
	cmp dword [eax + 4*thread_next_running], value_null
	je .enable
	proc threads_unschedule, eax
	
	; enable interrupts
.enable:
	sti
proc_end

;---------------------------------------------------------
; Resume the specified thread
;
; args: thread
;---------------------------------------------------------
proc_begin threads_resume, 0
	; disable interrupts
	cli
	
	; clear the suspended flag
	mov eax, arg_1											; eax = thread
	mov dword [eax + 4*thread_suspended], value_false		; thread.suspended = false
	
	; schedule thread if necessary
	cmp dword [eax + 4*thread_next_running], value_null
	jne .enable
	proc threads_schedule, eax								; if (thread.nextRunning == null) schedule(thread);
	
	; enable interrupts
.enable:
	sti
proc_end

