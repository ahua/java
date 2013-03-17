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
; ireturn instruction
;
; locals: value
;---------------------------------------------------------
proc_begin return_ireturn, 1
	; pop return value and save
	pop_eax									; eax = value
	mov local_1, eax						; value = eax
	
	; pop stack frame
	proc return_previous_frame
	
	; store return value
	mov eax, local_1						; eax = value
	push_data eax
proc_end

;---------------------------------------------------------
; lreturn instruction
;
; locals: high, low
;---------------------------------------------------------
proc_begin return_lreturn, 2
	; pop return value and save
	pop_long edx, eax						; edx:eax = value
	mov local_1, edx
	mov local_2, eax
	
	; pop stack frame
	proc return_previous_frame
	
	; store return value
	mov edx, local_1
	mov eax, local_2						; edx:eax = value
	push_long edx, eax
proc_end

;---------------------------------------------------------
; freturn instruction
;---------------------------------------------------------
proc_begin return_freturn, 0
	; display error message and hang
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg
	db 'return_freturn not implemented!!!'
.end
proc_end

;---------------------------------------------------------
; dreturn instruction
;---------------------------------------------------------
proc_begin return_dreturn, 0
	; display error message and hang
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg
	db 'return_dreturn not implemented!!!'
.end
proc_end


;---------------------------------------------------------
; areturn instruction
;
; locals: pointer
;---------------------------------------------------------
proc_begin return_areturn, 1
	; pop return value and save
	pop_eax									; eax = pointer
	mov local_1, eax						; pointer = eax
	
	; pop stack frame
	proc return_previous_frame
	
	; store return value
	mov eax, local_1						; eax = pointer
	push_pointer eax
proc_end



;---------------------------------------------------------
; return instruction
;---------------------------------------------------------
proc_begin return_return, 0
	proc return_previous_frame
proc_end


;---------------------------------------------------------
; Point the current thread to the previous frame
;---------------------------------------------------------
proc_begin return_previous_frame, 0
	; print some debug info
;	proc screen_print_string, .msg, .end-.msg
;	jmp .end
;.msg:
;	db 'Returning from method', 0xA
;.end:

	; check synchronization
	mov eax, [reg_method]									; eax = method
	mov eax, [eax + 4*entry_flags]							; eax = flags
	and eax, acc_synchronized
	cmp eax, 0
	je .previous
	
	; unlock monitor
	mov eax, [reg_frame]									; eax = frame
	mov eax, [eax + 4*object_lock]							; eax = lock
	proc monitor_unlock, eax

	; point thread to previous frame
.previous:
	mov eax, [reg_frame]									; eax = frame
	mov ebx, [eax + 4*frame_return_frame]					; ebx = previous
	mov edx, [reg_thread]									; edx = thread
	mov [edx + 4*thread_frame], ebx							; thread.frame = previous
	
	; thread may need to be unscheduled
	cmp ebx, value_null
	jne .pop_frame
	proc threads_unschedule, edx							; current thread has finished so unschedule it
	jmp .return

	; return to previous frame
.pop_frame:
	mov ecx, [eax + 4*frame_return_pc]						; ecx = return pc
	mov [ebx + 4*frame_pc], ecx								; previous.pc = return pc
	proc reg_load											; load registers with values from previous frame
proc_end






