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
; Wake any sleeping threads that are due
;---------------------------------------------------------
proc_begin sleep_wake_sleeping_threads, 0
.next:
	mov ebx, [reg_core]						; ebx = core
	mov ebx, [ebx + 4*core_sleeping]		; ebx = asleep
	cmp ebx, value_null
	je .return
	
	; load wakeup time for sleeping thread
	mov edx, [ebx + 4*thread_wakeup]
	mov eax, [ebx + 4*thread_wakeup + 4]	; edx:eax = wakeup
	cmp edx, [reg_timehigh]
	jg .return
	jl .wake
	cmp eax, [reg_timelow]
	jg .return

	; wake up the thread
.wake:
	proc sleep_wake_thread, ebx
	jmp .next
proc_end





;---------------------------------------------------------
; Wake up the given thread
;
; args: thread
;
; locals: lock
;---------------------------------------------------------
proc_begin sleep_wake_thread, 1
	proc sleep_remove_from_sleep_queue, arg_1
	proc threads_schedule, arg_1
	
	; if thread was put to sleep by wait method it
	; will need to reacquire the lock
	mov eax, arg_1									; eax = thread
	mov ebx, [eax + 4*thread_lock]					; ebx = lock
	cmp ebx, value_null
	je .return
	mov local_1, ebx								; lock = ebx
	proc monitor_remove_from_wait_queue, arg_1, eax	; (thread, lock)
	proc monitor_acquire, arg_1, local_1, 0			; (thread, lock, increment)
proc_end





;---------------------------------------------------------
; Add the current thread to the sleep queue for the
; specified number of milliseconds.
;
; args: timeH, timeL
;
; locals: sleeping
;---------------------------------------------------------
proc_begin sleep_add_to_sleep_queue, 1
	; disable interrupts
	cli

	; find out when the thread should wake up
	mov edx, [reg_timehigh]
	mov eax, [reg_timelow]						; edx:eax = systime
	add eax, arg_2
	adc edx, arg_1								; edx:eax = waketime
	
	; set thread wakeup time
	mov ebx, [reg_thread]						; ebx = thread
	mov [ebx + 4*thread_wakeup], edx
	mov [ebx + 4*thread_wakeup + 4], eax		; thread.wakeup = waketime
	
	; get the first thread in the sleep queue
	mov ecx, [reg_core]							; ecx = core
	mov ecx, [ecx + 4*core_sleeping]			; ecx = sleeping
	
	; the sleep queue may be empty...
	cmp ecx, value_null
	jne .nonempty
	mov ecx, [reg_core]							; ecx = core
	mov [ecx + 4*core_sleeping], ebx			; core.sleeping = thread
	mov dword [ebx + 4*thread_next_sleeping], value_null
	mov dword [ebx + 4*thread_prev_sleeping], value_null
	jmp .enable

	; ...or the new thread needs to be inserted at the front...
.nonempty:
	cmp [ecx + 4*thread_wakeup], edx
	jl .further
	jg .front
	cmp [ecx + 4*thread_wakeup + 4], eax
	jg .further
.front:											; wakeup < sleeping.wakeup
	mov [ebx + 4*thread_next_sleeping], ecx		; thread -> sleeping
	mov dword [ebx + 4*thread_prev_sleeping], value_null ; null <- thread
	mov [ecx + 4*thread_prev_sleeping], ebx		; thread <- sleeping
	mov eax, [reg_core]
	mov [ecx + 4*thread_prev_sleeping], ebx		; core -> thread
	jmp .enable
	
	; ...or need to find the two threads in the queue between which
	; the new thread will be inserted.
.further:
	mov local_1, ecx							; sleeping = ecx
	mov ecx, [ecx + 4*thread_next_sleeping]		; ecx = next
	cmp ecx, value_null
	je .insert									; end of the queue reached
	cmp [ecx + 4*thread_wakeup], edx
	jl .insert
	jg .further
	cmp [ecx + 4*thread_wakeup + 4], eax
	jg .further
	
	; insert the thread in the queue
.insert:
	cmp ecx, value_null
	je .isnull
	mov [ecx + 4*thread_prev_sleeping], ebx		; thread <- next
.isnull:
	mov [ebx + 4*thread_next_sleeping], ecx		; thread -> next
	mov ecx, local_1							; ecx = sleeping
	mov [ebx + 4*thread_prev_sleeping], ecx		; sleeping <- thread
	mov [ecx + 4*thread_next_sleeping], ebx		; sleeping -> thread

	; enable interrupts
.enable:
	sti
proc_end



;---------------------------------------------------------
; Remove the specified thread from the sleep queue
;
; args: thread
;---------------------------------------------------------
proc_begin sleep_remove_from_sleep_queue, 0
	; disable interrupts
	cli

	mov eax, arg_1									; eax = thread
	mov ebx, [eax + 4*thread_prev_sleeping]			; ebx = prev
	mov ecx, [eax + 4*thread_next_sleeping]			; ecx = next
	mov edx, [reg_core]								; edx = core
	
	; if the thread is at the head of the queue...
	cmp eax, [edx + 4*core_sleeping]
	jne .remove
	mov [edx + 4*core_sleeping], ecx
	cmp ecx, value_null
	je .remove
	mov dword [ecx + 4*thread_prev_sleeping], value_null
	
	; was prev <-> thread <-> next, now prev <-> next
.remove:
	cmp ecx, value_null
	je .prev
	mov [ecx + 4*thread_prev_sleeping], ebx			; prev <- next
.prev:
	cmp ebx, value_null
	je .clear
	mov [ebx + 4*thread_next_sleeping], ecx			; prev -> next
	
	; thread is no longer in sleep queue
.clear:
	mov dword [eax + 4*thread_next_sleeping], value_null
	mov dword [eax + 4*thread_prev_sleeping], value_null
	mov dword [eax + 4*thread_wakeup], 0
	mov dword [eax + 4*thread_wakeup + 4], 0		; thread.wakeup = 0

	; enable interrupts
.enable:
	sti
proc_end





;---------------------------------------------------------
; Return true in eax if the specified thread is currently
; sleeping. A thread is sleeping if its wakeup time is
; any value other than zero.
;
; args: thread
;---------------------------------------------------------
proc_begin sleep_is_sleeping, 0
	; get wakeup value
	proc sleep_get_wakeup, arg_1							; edx:eax = wakeup
	
	; if (wakeup == 0x0) return false
	cmp edx, 0x0
	jne .true
	cmp eax, 0x0
	jne .true
	mov eax, value_false
	jmp .return
	
	; else return true
.true:
	mov eax, value_true
proc_end






;---------------------------------------------------------
; Return the wakeup time for the specified thread as a
; 64-bit signed integer in edx:eax.
;
; args: thread
;---------------------------------------------------------
proc_begin sleep_get_wakeup, 0
	mov ebx, arg_1											; ebx = thread
	mov edx, [ebx + 4*thread_wakeup]						; edx = high bytes
	mov eax, [ebx + 4*thread_wakeup + 4]					; eax = low bytes
proc_end




