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
; Virtual register contents
;
; core:			Holds the address of the core object.
;
; thread:		Holds the address of the current thread.
;
; frame:		Holds the address of the current frame.
;
; method:		Holds the address of the current method.
;
; code:			Holds the address of the first bytecode
;				instruction in the current method.
;
; instruction:	Holds the address of the current bytecode
;				instruction being executed.
;
; stack:		Holds the address of the 32-bit value
;				currently on top of the stack. If the
;				stack is empty holds the address of the
;				32-bit word directly after the last word
;				in the current stack frame.
;
; locals:		Holds the address of the local variable
;				with index 0 in the current stack frame.
;
; pool:			Holds the address of the first entry in
;				the constant pool of the current method.
;
; timehigh:		Holds the high 32 bits of system time.
; timelow:		Holds the low 32 bits of system time.
;				System time is a signed 64 bit integer
;				representing the number of milliseconds
;				since 01 Jan 1970.
;
; Notes:		The pc (program counter) can be calculated
;				as: pc = instruction - code
;				The pc is the byte offset of the current
;				bytecode instruction within the current
;				method.
;
;				The sp (stack pointer) can be calculated
;				as: sp = stack - frame
;				The sp is the byte offset of the 32-bit
;				word currently on top of the stack within
;				the current stack frame.
;
;				The registers edi and esi are used to hold
;				the instruction and stack virtual register
;				values.
;
;				Stack frames store the pc and sp values
;				because these are relative values and are
;				therefore still valid after garbage
;				collection.
;
;				All instructions must leave correct values
;				in the edi and esi registers. If an
;				instruction results in the current thread
;				being unscheduled, the value in the thread
;				register must be set to null.
;---------------------------------------------------------

;---------------------------------------------------------
; These memory locations hold virtual register values.
;---------------------------------------------------------
reg_core:				dd space_a
reg_thread:				dd 0x0
reg_frame:				dd 0x0
reg_method:				dd 0x0
reg_code:				dd 0x0
reg_instruction:		dd 0x0 ; edi register used where possible
reg_stack:				dd 0x0 ; esi register used where possible
reg_locals:				dd 0x0
reg_pool:				dd 0x0
reg_timehigh:			dd 0x0
reg_timelow:			dd 0x0

;---------------------------------------------------------
; Set the other registers based on the value in the
; thread register.
;---------------------------------------------------------
proc_begin reg_load, 0
	mov eax, [reg_thread]									; eax = thread
	mov eax, [eax + 4*thread_frame]							; eax = frame
	mov [reg_frame], eax									; set frame register
	mov ebx, [eax + 4*frame_method]							; ebx = method
	mov [reg_method], ebx									; set method register
	mov ecx, [ebx + 4*method_code]							; ecx = code
	add ecx, 4*array_data									; ecx = code data
	mov [reg_code], ecx										; set code register
	mov edi, [eax + 4*frame_pc]								; edi = pc
	add edi, ecx											; edi = instruction
	mov [reg_instruction], edi								; set instruction register
	mov esi, [eax + 4*frame_sp]								; esi = sp
	add esi, eax											; esi = stack
	mov [reg_stack], esi									; set stack register
	add eax, 4*frame_locals									; eax = locals
	mov [reg_locals], eax									; set locals register
	mov ebx, [ebx + 4*method_pool]							; ebx = pool
	add ebx, 4*array_data									; ebx = pool data
	mov [reg_pool], ebx										; set pool register
proc_end

;---------------------------------------------------------
; Save the pc and stack information to the current stack
; frame as relative values.
;---------------------------------------------------------
proc_begin reg_save, 0
	; save to virtual registers
	mov [reg_instruction], edi
	mov [reg_stack], esi
	
	; save to stack frame
	mov eax, edi											; eax = instruction
	sub eax, [reg_code]										; eax = pc
	mov ebx, [reg_frame]									; ebx = frame
	mov [ebx + 4*frame_pc], eax								; save pc to frame
	mov eax, esi											; eax = stack
	sub eax, ebx											; eax = stack offset
	mov [ebx + 4*frame_sp], eax								; save sp to frame
proc_end

