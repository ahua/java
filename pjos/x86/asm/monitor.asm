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
; monitorenter instruction
;---------------------------------------------------------
proc_begin monitor_monitorenter, 0
	peek_eax												; eax = object
	proc monitor_get_lock, eax								; eax = object lock
	cmp eax, value_null
	je .return												; roll back because gc has been done
	
	; gc can't interrupt now
	pop_ebx													; ebx = object
	instruction_offset 1
	proc monitor_acquire, [reg_thread], eax, 1				; monitor_acquire(thread, lock, increment)
proc_end





;---------------------------------------------------------
; monitorexit instruction
;---------------------------------------------------------
proc_begin monitor_monitorexit, 0
	; assume that the current running thread owns the monitor
	; assume lock was created when monitorenter was called...
	pop_eax													; eax = object
	mov ebx, [eax + 4*object_lock]							; ebx = lock
	proc monitor_unlock, ebx
	instruction_offset 1
proc_end



;---------------------------------------------------------
; Increment the specified lock count by the given amount
;
; args: lock, amount
;---------------------------------------------------------
proc_begin monitor_increment, 0
	mov eax, arg_1							; eax = address of lock
	mov ebx, arg_2							; ebx = amount
	add [eax + 4*lock_count], ebx			; lock.count += amount
proc_end




;---------------------------------------------------------
; Unlock the given lock
;
; args: lock
;---------------------------------------------------------
proc_begin monitor_unlock, 0
	mov eax, arg_1							; eax = address of lock
	add eax, 4*lock_count					; eax = address of count
	
	; just decrement count if higher than 1
	cmp dword [eax], 1
	jle .relinquish
	dec dword [eax]
	jmp .return

	; relinquish this lock
.relinquish:
	proc monitor_relinquish, arg_1
proc_end




;---------------------------------------------------------
; Relinquish the given lock
;
; args: lock
;---------------------------------------------------------
proc_begin monitor_relinquish, 0
	; set ownership to next in queue...
	mov eax, arg_1											; eax = lock
	mov ebx, [eax + 4*lock_lock_head]						; ebx = thread (head of queue)
	cmp ebx, value_null
	je .nothing_waiting
	
	; move thread at head of queue to owner
	mov ecx, [ebx + 4*thread_lock_count]					; ecx = count
	mov [eax + 4*lock_owner], ebx							; lock.owner = thread
	mov [eax + 4*lock_count], ecx							; lock.count = count
	
	; next thread in line becomes head
	mov edx, [ebx + 4*thread_next_lock]						; edx = next
	mov [eax + 4*lock_lock_head], edx						; lock.lockHead = next
	cmp edx, value_null
	jne .prev
	mov dword [eax + 4*lock_lock_tail], value_null			; if (next == null) lock.lockTail = null...
	jmp .dequeue
.prev:
	mov dword [edx + 4*thread_prev_lock], value_null		; ...else next.prevLock = null
	
	; thread is now no longer in queue
.dequeue:
	mov dword [ebx + 4*thread_next_lock], value_null		; thread.nextLock = null
	mov dword [ebx + 4*thread_lock], value_null				; thread.lock = null
	
	; schedule the new owner
	proc threads_schedule, ebx
	jmp .return

	; ...unless there's nothing waiting
.nothing_waiting:
	mov dword [eax + 4*lock_owner], value_null				; set lock owner to null
	mov dword [eax + 4*lock_count], 0						; set lock count to zero
proc_end






;---------------------------------------------------------
; Find the lock of the given object and return it in eax.
; If necessary, create a new lock object.
;
; args: object
;---------------------------------------------------------
proc_begin monitor_get_lock, 0
	; check the lock field of the object
	mov eax, arg_1											; eax = object
	mov eax, [eax + 4*object_lock]							; eax = lock
	cmp eax, value_null
	jne .return
	
	; allocate a new lock object
	proc allocate_new, lock_wait_tail + 1, header_instance	; eax = lock
	cmp eax, value_null
	je .return												; gc was done, return null
	
	; initialise lock object
	mov ebx, [reg_core]										; ebx = core
	mov ebx, [ebx + 4*core_lock_type]						; ebx = lock type
	mov [eax + 4*object_type], ebx							; set type
	mov [eax + 4*object_hashcode], eax						; set hash code
	
	; rest of fields were set to null/zero by allcoation
	
	; point argument to its new lock
	mov ebx, arg_1											; ebx = object
	mov [ebx + 4*object_lock], eax							; object.lock = eax
proc_end







;---------------------------------------------------------
; The given thread wishes to acquire the specified lock
; with the specified increment. If the thread must wait
; in the queue it is unscheduled and 0 is returned in eax,
; otherwise the lock is acquired, the count incremented
; and 1 is returned in eax.
;
; args: thread, lock, increment
;---------------------------------------------------------
proc_begin monitor_acquire, 0
	mov eax, arg_1											; eax = thread
	mov ebx, arg_2											; ebx = lock
	mov ecx, arg_3											; ecx = increment
	mov edx, [ebx + 4*lock_owner]							; edx = lock.owner
	
	; if lock currently has no owner...
	cmp edx, value_null
	jne .has_owner
	mov [ebx + 4*lock_owner], eax							; lock.owner = thread
	mov [ebx + 4*lock_count], ecx							; lock.count = increment
	jmp .true												; return true

	; ...the current thread already holds the lock...
.has_owner:
	cmp edx, eax											; compare owner and thread
	jne .must_wait											; if thread is owner...
	add [ebx + 4*lock_count], ecx							; lock.count += increment
	jmp .true												; return true
	
	; ...the current thread has to wait in line for the lock
.must_wait:
	mov [eax + 4*thread_lock_count], ecx					; thread.lockCount = increment
	proc monitor_add_to_lock_queue, eax, ebx				; (thread, lock)
	proc threads_unschedule, arg_1
	mov eax, value_false									; return false
	jmp .return
	
	; return true
.true:
	mov eax, value_true
proc_end





;---------------------------------------------------------
; Add the given thread to the lock queue for the specified
; lock
;
; args: thread, lock
;---------------------------------------------------------
proc_begin monitor_add_to_lock_queue, 0
	; either the queue is empty...
	mov eax, arg_2											; eax = lock
	mov ebx, [eax + 4*lock_lock_tail]						; ebx = lockTail
	mov ecx, arg_1											; ecx = thread
	cmp ebx, value_null
	jne .append
	mov [eax + 4*lock_lock_head], ecx						; thread is now only entry in queue
	jmp .new_tail
	
	; ...or the thread can be appended
.append:
	mov [ebx + 4*thread_next_lock], ecx						; lockTail -> thread
	mov [ecx + 4*thread_prev_lock], ebx						; lockTail <- thread

	; either way the thread becomes the new tail
.new_tail:
	mov [eax + 4*lock_lock_tail], ecx						; lock.lockTail = thread
	mov [ecx + 4*thread_lock], eax							; thread.lock = lock
proc_end








;---------------------------------------------------------
; Called when the current thread wants to wait on the
; given object with the specified time limit.
;
; args: object, timeoutHigh, timeoutLow
;
; locals: lock
;---------------------------------------------------------
proc_begin monitor_wait, 1
	; assume thread is already owner of lock (don't need to call getLock)
	mov eax, arg_1											; eax = object
	mov eax, [eax + 4*object_lock]							; eax = lock
	mov local_1, eax
	
	; set the lock count in the thread
	mov ebx, [eax + 4*lock_count]							; ebx = lock.count
	mov ecx, [reg_thread]									; ecx = thread
	mov [ecx + 4*thread_lock_count], ebx					; thread.lock_count = count
	mov [ecx + 4*thread_lock], eax							; thread.lock = lock
	
	; sit in wait queue
	proc monitor_add_to_wait_queue, eax
	proc monitor_relinquish, local_1
	cmp dword arg_2, 0
	jg .sleep
	cmp dword arg_3, 0
	jle .unschedule
.sleep:
	proc sleep_add_to_sleep_queue, arg_2, arg_3
.unschedule:
	proc threads_unschedule, [reg_thread]
proc_end




;---------------------------------------------------------
; Add the current thread to the wait queue of the
; specified lock object.
;
; args: lock
;---------------------------------------------------------
proc_begin monitor_add_to_wait_queue, 0
	mov eax, arg_1											; eax = lock
	mov ebx, [eax + 4*lock_wait_tail]						; ebx = waitTail
	mov ecx, [reg_thread]									; ecx = thread
	
	; either the queue is empty...
	cmp ebx, value_null
	jne .append
	mov [eax + 4*lock_wait_head], ecx
	jmp .new_tail
	
	; ...or the thread can be appended
.append:
	mov [ebx + 4*thread_next_lock], ecx						; waitTail.nextLock = thread
	mov [ecx + 4*thread_prev_lock], ebx						; thread.prevLock = waitTail

	; either way the thread becomes the new tail
.new_tail:
	mov [eax + 4*lock_wait_tail], ecx						; lock.waitTail = thread
proc_end




;---------------------------------------------------------
; Notify all the threads waiting on the specified object.
;
; args: object
;
; locals: lock
;---------------------------------------------------------
proc_begin monitor_notify_all, 1
	proc monitor_get_lock, arg_1							; eax = lock
	mov local_1, eax										; lock = eax
	cmp eax, value_null
	jne .notify
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'notifyAll() called on unsynchronized object'
.end:
	
	; notify all the threads in the queue
.notify:
	mov ebx, [eax + 4*lock_wait_head]						; ebx = thread
	cmp ebx, value_null
	je .return
	proc monitor_notify_thread, ebx, local_1				; notifyThread(thread, lock)
	jmp .notify
proc_end



;---------------------------------------------------------
; Notify the first thread waiting on the specified object.
;
; args: object
;---------------------------------------------------------
proc_begin monitor_notify, 0
	proc monitor_get_lock, arg_1							; eax = lock
	cmp eax, value_null
	jne .notify
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'notify() called on unsynchronized object'
.end:
	
	; notify the first thread
.notify:
	mov ebx, [eax + 4*lock_wait_head]						; ebx = thread
	cmp ebx, value_null
	je .return
	proc monitor_notify_thread, ebx, eax					; notifyThread(thread, lock)
proc_end





;---------------------------------------------------------
; Remove the specified thread from the wait queue and from
; the sleep queue (if necessary) and append it to the lock
; queue.
;
; args: thread, lock
;---------------------------------------------------------
proc_begin monitor_notify_thread, 0
	proc monitor_remove_from_wait_queue, arg_1, arg_2	; (thread, lock)
	proc sleep_is_sleeping, arg_1						; eax = true/false
	cmp eax, value_true
	jne .queue
	proc sleep_remove_from_sleep_queue, arg_1			; (thread)
.queue:
	proc monitor_add_to_lock_queue, arg_1, arg_2		; (thread, lock)
proc_end



;---------------------------------------------------------
; Remove the specified thread from the wait queue of the
; given lock.
;
; args: thread, lock
;---------------------------------------------------------
proc_begin monitor_remove_from_wait_queue, 0
	; either thread is at the head of the wait queue...
	mov eax, arg_1										; eax = thread
	mov ebx, arg_2										; ebx = lock
	mov ecx, [eax + 4*thread_prev_lock]					; ecx = prev
	mov edx, [eax + 4*thread_next_lock]					; edx = next
	cmp eax, [ebx + 4*lock_wait_head]
	jne .at_tail
	mov [ebx + 4*lock_wait_head], edx					; lock.waitHead = next
	cmp edx, value_null
	jne .not_null
	mov dword [ebx + 4*lock_wait_tail], value_null		; lock.waitTail = null
	jmp .dequeue
.not_null:
	mov dword [edx + 4*thread_prev_lock], value_null	; next.prevLock = null
	jmp .dequeue
	
	; ...or at the tail...
.at_tail:
	cmp eax, [ebx + 4*lock_wait_tail]
	jne .middle
	mov [ebx + 4*lock_wait_tail], ecx					; lock.waitTail = prev
	mov dword [ecx + 4*thread_next_lock], value_null	; prev.nextLock = null
	jmp .dequeue
	
	; ...or somewhere in the middle
.middle:
	mov [ecx + 4*thread_next_lock], edx					; prev.nextLock = next
	mov [edx + 4*thread_prev_lock], ecx					; next.prevLock = prev

	; thread is no longer in wait queue
.dequeue:
	mov dword [eax + 4*thread_next_lock], value_null	; thread.nextLock = null
	mov dword [eax + 4*thread_prev_lock], value_null	; thread.prevLock = null
	mov dword [eax + 4*thread_lock], value_null			; thread.lock = null
proc_end
