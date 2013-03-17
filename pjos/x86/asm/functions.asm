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
; Macros for procedure calls and declarations
;---------------------------------------------------------



;---------------------------------------------------------
; Procedure declaration. This header is a c-style
; function header as described in the NASM manual.
;---------------------------------------------------------
%macro proc_begin 2.nolist ; <name> <num locals>
%1:
	push ebp
	mov ebp, esp
%if %2 > 0
	sub esp, %2 * 4		; leave room for local variables
%endif
%endmacro


;---------------------------------------------------------
; Return from procedure.
;---------------------------------------------------------
%macro proc_end 0.nolist
.return:
	leave
	ret
%endmacro

;---------------------------------------------------------
; Procedure calls, from zero to four arguments
;---------------------------------------------------------
%macro proc 1.nolist ; <name>
	call %1
%endmacro

%macro proc 2.nolist ; <name> <arg1>
	push dword %2
	call %1
	add esp, 4
%endmacro

%macro proc 3.nolist ; <name> <arg1> <arg2>
	push dword %3
	push dword %2
	call %1
	add esp, 8
%endmacro

%macro proc 4.nolist ; <name> <arg1> <arg2> <arg3>
	push dword %4
	push dword %3
	push dword %2
	call %1
	add esp, 12
%endmacro

%macro proc 5.nolist ; <name> <arg1> <arg2> <arg3> <arg4>
	push dword %5
	push dword %4
	push dword %3
	push dword %2
	call %1
	add esp, 16
%endmacro

%macro proc 6.nolist ; <name> <arg1> <arg2> <arg3> <arg4> <arg5>
	push dword %6
	push dword %5
	push dword %4
	push dword %3
	push dword %2
	call %1
	add esp, 20
%endmacro




;---------------------------------------------------------
; Macros to access arguments and local variables
;---------------------------------------------------------
%define arg_1 [ebp+8]
%define arg_2 [ebp+12]
%define arg_3 [ebp+16]
%define arg_4 [ebp+20]
%define arg_5 [ebp+24]

%define local_1 [ebp-4]
%define local_2 [ebp-8]
%define local_3 [ebp-12]
%define local_4 [ebp-16]
%define local_5 [ebp-20]
%define local_6 [ebp-24]
%define local_7 [ebp-28]







;---------------------------------------------------------
; Copy the value on top of the stack into a register.
;---------------------------------------------------------
%macro peek_reg 1.nolist ; <reg>
	mov %1, [esi]						; reg = value
%endmacro

;---------------------------------------------------------
; Peek macros for specific registers.
;---------------------------------------------------------
%macro peek_eax 0.nolist
	peek_reg eax
%endmacro

%macro peek_ebx 0.nolist
	peek_reg ebx
%endmacro

%macro peek_ecx 0.nolist
	peek_reg ecx
%endmacro

%macro peek_edx 0.nolist
	peek_reg edx
%endmacro




;---------------------------------------------------------
; Copy the reference flag on top of the stack into a
; register.
;---------------------------------------------------------
%macro indicate_reg 1.nolist ; <reg>
	mov %1, [esi + 4]					; reg = true/false
%endmacro

;---------------------------------------------------------
; Indicate macros for specific registers.
;---------------------------------------------------------
%macro indicate_eax 0.nolist
	indicate_reg eax
%endmacro

%macro indicate_ebx 0.nolist
	indicate_reg ebx
%endmacro

%macro indicate_ecx 0.nolist
	indicate_reg ecx
%endmacro

%macro indicate_edx 0.nolist
	indicate_reg edx
%endmacro

;---------------------------------------------------------
; Copy the value and reference flags on top of the stack
; into the specified registers
;---------------------------------------------------------
%macro peek_indicate 2.nolist ; <value-reg> <flag-reg>
	mov %1, [esi]						; reg = value
	mov %2, [esi + 4]					; reg = flag
%endmacro

;---------------------------------------------------------
; Pop the value and reference flags off the stack and
; into the specified registers
;---------------------------------------------------------
%macro pop_indicate 2.nolist ; <value-reg> <flag-reg>
	peek_indicate %1, %2
	mov dword [esi + 4], value_false	; stack slot now unused, flag as data
	add esi, 8							; update stack register
%endmacro


;---------------------------------------------------------
; Pop a value off the stack into a register.
;---------------------------------------------------------
%macro pop_reg 1.nolist ; <reg>
	mov %1, [esi]						; reg = value
	mov dword [esi + 4], value_false	; stack slot now unused, flag as data
	add esi, 8							; update stack register
%endmacro

;---------------------------------------------------------
; Pop macros for specific registers.
;---------------------------------------------------------
%macro pop_eax 0.nolist
	pop_reg eax
%endmacro

%macro pop_ebx 0.nolist
	pop_reg ebx
%endmacro

%macro pop_ecx 0.nolist
	pop_reg ecx
%endmacro

%macro pop_edx 0.nolist
	pop_reg edx
%endmacro




;---------------------------------------------------------
; Push a value and reference flag onto the stack
;---------------------------------------------------------
%macro push_value 2.nolist ; <value> <flag>
	sub esi, 8							; stack register now points to new entry
	mov dword [esi], %1					; set value
	mov dword [esi + 4], %2				; set reference flag
%endmacro


;---------------------------------------------------------
; Push a data value onto the stack
;---------------------------------------------------------
%macro push_data 1.nolist ; <value>
	push_value %1, value_false
%endmacro

;---------------------------------------------------------
; Push a pointer value onto the stack
;---------------------------------------------------------
%macro push_pointer 1.nolist ; <pointer>
	push_value %1, value_true
%endmacro

;---------------------------------------------------------
; Pop a long value into two registers
;---------------------------------------------------------
%macro pop_long 2.nolist ; <highreg> <lowreg>
	pop_reg %2							; pop low bits
	pop_reg %1							; pop high bits
%endmacro

;---------------------------------------------------------
; Push a long value onto the stack
;---------------------------------------------------------
%macro push_long 2.nolist ; <highreg> <lowreg>
	push_value %1, value_false			; push high bits
	push_value %2, value_false			; push low bits
%endmacro


;---------------------------------------------------------
; Negate a long value
;---------------------------------------------------------
%macro neg_long 2.nolist ; <highreg> <lowreg>
	not %1
	not %2
	add %2, 1
	adc %1, 0
%endmacro



;---------------------------------------------------------
; Read the 32-bit signed value at the specified offset
; from the current instruction into eax.
;---------------------------------------------------------
%macro instruction_sign_four_byte_codes 1.nolist ; <offset>
	movzx eax, byte [edi + %1]
	shl eax, 8
	mov al, [edi + %1 + 1]
	shl eax, 8
	mov al, [edi + %1 + 2]
	shl eax, 8
	mov al, [edi + %1 + 3]
%endmacro



;---------------------------------------------------------
; Read the 16-bit unsigned value at the specified offset
; from the current instruction into eax.
;---------------------------------------------------------
%macro instruction_two_byte_codes 1.nolist ; <offset>
    movzx eax, byte [edi + %1 + 1]  ; eax = second byte
    mov ah, [edi + %1]              ; ah = first byte, eax = value
%endmacro

;---------------------------------------------------------
; Read the 16-bit signed value at the specified offset
; from the current instruction into eax.
;---------------------------------------------------------
%macro instruction_sign_two_byte_codes 1.nolist ; <offset>
	instruction_two_byte_codes %1	; eax = unsigned value
	movsx eax, ax					; eax = signed value
%endmacro

;---------------------------------------------------------
; Read the 8-bit unsigned value at the specified offset
; from the current instruction into eax.
;---------------------------------------------------------
%macro instruction_byte_code 1.nolist ; <offset>
    movzx eax, byte [edi + %1]		; eax = value
%endmacro

;---------------------------------------------------------
; Read the 8-bit signed value at the specified offset
; from the current instruction into eax.
;---------------------------------------------------------
%macro instruction_sign_byte_code 1.nolist ; <offset>
	instruction_byte_code %1			; eax = unsigned value
	movsx eax, al						; eax = signed value
%endmacro





;---------------------------------------------------------
; Offset the instruction register by the specified amount
;---------------------------------------------------------
%macro instruction_offset 1.nolist ; <offset>
	add edi, %1
%endmacro





;---------------------------------------------------------
; Creates a procedure which just prints an error message
; and hangs.
;---------------------------------------------------------
%macro error_message 2.nolist
proc_begin %1, 0
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db %2, ' not implemented!!!'
.end:
proc_end
%endmacro




