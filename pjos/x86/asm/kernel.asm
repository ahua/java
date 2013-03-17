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



BITS 32

;---------------------------------------------------------
; Constants
;---------------------------------------------------------
multiboot_magic			equ		0x1BADB002
multiboot_flags			equ		0x00010002				; load info + use header
kernel_size				equ		100 * 1024				; 100kb kernel
space_a					equ		0x00100000 + kernel_size
space_size				equ		0x00400000				; 2 x 4MB spaces
space_b					equ		space_a + space_size


;---------------------------------------------------------
; Defines macros
;---------------------------------------------------------
%include "functions.asm"


;---------------------------------------------------------
; Multiboot header
;---------------------------------------------------------

	org 0x100000

kernel_header:
	dd multiboot_magic
	dd multiboot_flags
	dd 0 - multiboot_magic - multiboot_flags
	dd kernel_header
	dd kernel_header				; load
	dd kernel_end					; load end
	dd kernel_end					; bss end
	dd kernel_entry					; entry


;---------------------------------------------------------
; Execution starts here
;---------------------------------------------------------
kernel_entry:
	; set up stack
	mov esp, 0x90000
	mov ebp, 0x90000

	; clear screen and display a welcome string
	proc screen_clear
	proc screen_print_string, .msg, .end-.msg
	
	; set up GDT
	proc tables_setup_gdt
	
	; set up IDT at 0x400 (256 entries each 8 bytes) by
	; copying the template entry into each slot
	proc tables_setup_idt
	
	; enable interrupts
	sti
	
	; start vm execution
	jmp kernel_execute

.msg:
	db 'PJOS x86 Implementation', 0xA
.end:


;---------------------------------------------------------
; Execute bytecode indefinitely
;---------------------------------------------------------
kernel_execute:
	; load registers ready for first instruction
	mov eax, [reg_core]					; eax = core
	mov eax, [eax + 4*core_running]		; eax = initial thread
	mov [reg_thread], eax				; set thread register
	proc reg_load

	; execute the next instruction
.next:
	movzx eax, byte [edi]				; eax = bytecode instruction
	mov ebx, distributor				; ebx = address of instruction table
	proc [ebx + 4*eax]					; call proc for instruction
	
	; check if the current thread has been unscheduled
	cmp dword [reg_thread], value_null
	je .yield

    ; check if it is time to yield
    cmp dword [.flag], 0x1
    jne .next
	
	; schedule the next thread
.yield:
	proc reg_save
	proc sleep_wake_sleeping_threads
	proc threads_schedule_next_thread
	mov dword [.flag], 0x0				; clear yield flag
	jmp .next

    ; this value is set by timer interrupt if a thread yield is required
.flag:
    dd 0x0
	

;---------------------------------------------------------
; Include files
;---------------------------------------------------------
%include "allocate.asm"
%include "array.asm"
%include "bit.asm"
%include "cast.asm"
%include "collector.asm"
%include "const.asm"
%include "constants.asm"
%include "control.asm"
%include "convert.asm"
%include "distributor.asm"
%include "exception.asm"
%include "field.asm"
%include "invoke.asm"
%include "instance.asm"
%include "load.asm"
%include "locals.asm"
%include "math.asm"
%include "monitor.asm"
%include "reg.asm"
%include "resolve.asm"
%include "return.asm"
%include "screen.asm"
%include "sleep.asm"
%include "stack.asm"
%include "store.asm"
%include "switch.asm"
%include "tables.asm"
%include "threads.asm"


;---------------------------------------------------------
; Internal error, no recovery possible
;---------------------------------------------------------
kernel_error:
	; display error string
	proc screen_print_string, .msg, .end-.msg

	; load current instruction into eax
	movzx eax, byte [edi]
	proc screen_print_byte, eax

	; hang
	jmp $
.msg:
	db ': System Error. Next Instruction is 0x'
.end:

;---------------------------------------------------------
; Fatal exception occured (number in eax)
;---------------------------------------------------------
kernel_death:
    ; save error code
    mov [.value], eax

    ; display error string
	proc screen_print_string, .msg, .end-.msg
    proc screen_print_byte, [.value]

    ; hang
    jmp $
.value:
    dd 0x0
.msg:
	db 0xa, 0xa, 'Fatal Exception 0x'
.end:
    


;---------------------------------------------------------
; Load the java image into the first space
;---------------------------------------------------------
times (kernel_size)-($-$$) db 0
incbin "memory.bin"


;---------------------------------------------------------
; End of kernel
;---------------------------------------------------------
kernel_end:




