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
; arraylength instruction
;---------------------------------------------------------
proc_begin array_arraylength, 0
    ; get the array object, check not null
    pop_eax                                         ; eax = array
    cmp eax, value_null
    jne .not_null
    proc exception_throw, core_throw_null_pointer
    jmp .return
    
    ; get the length and return
.not_null:
    mov eax, [eax + 4*array_length]                 ; eax = length
    push_data eax                                   ; push length
    instruction_offset 1
proc_end





;---------------------------------------------------------
; Performs the initial part of an array load operation
; including bounds checking. State after execution:
;   eax: address of first element
;   ebx: index
; If the index is invalid or the array is null an
; exception is thrown and execution jumps to the next
; .return label.
;---------------------------------------------------------
%macro array_load_init 0.nolist
    pop_ebx								; ebx = index
    pop_eax								; eax = array
    
    ; if array == null, throw null pointer exception
    cmp eax, value_null
    jne .not_null
    proc exception_throw, core_throw_null_pointer
    jmp .return
    
    ; if index < 0 or index >= length, throw index out of bounds exception
.not_null:
    cmp ebx, 0
    jl .index_bad						; index must be >= 0
    mov ecx, [eax + 4*array_length]		; ecx = length
    cmp ebx, ecx
    jl .index_ok
.index_bad:
    proc exception_throw, core_throw_array_index
    jmp .return
.index_ok:
    add eax, 4*array_data				; eax = address of first element
%endmacro




;---------------------------------------------------------
; iaload instruction
;---------------------------------------------------------
proc_begin array_iaload, 0
    array_load_init				; eax = address of first element, ebx = index
    mov eax, [eax + 4*ebx]		; eax = element
    push_data eax				; push element on stack
    instruction_offset 1
proc_end



error_message array_laload, 'array_laload'
error_message array_faload, 'array_faload'
error_message array_eaload, 'array_eaload'

;---------------------------------------------------------
; aaload instruction
;---------------------------------------------------------
proc_begin array_aaload, 0
    array_load_init				; eax = address of first element, ebx = index
    mov eax, [eax + 4*ebx]		; eax = element
    push_pointer eax			; push element on stack
    instruction_offset 1
proc_end


;---------------------------------------------------------
; baload instruction
;---------------------------------------------------------
proc_begin array_baload, 0
	array_load_init				; eax = address of first element, ebx = index
	movsx eax, byte [eax + ebx]	; eax = signed byte value
	push_data eax				; push value on stack
	instruction_offset 1
proc_end




;---------------------------------------------------------
; caload instruction
;---------------------------------------------------------
proc_begin array_caload, 0
    array_load_init				; eax = address of first element, ebx = index
    movzx eax, word [eax + 2*ebx]	; eax = unsigned char value
    push_data eax					; push char on stack
    instruction_offset 1
proc_end




;---------------------------------------------------------
; saload instruction
;---------------------------------------------------------
proc_begin array_saload, 0
    array_load_init				; eax = address of first element, ebx = index
    movsx eax, word [eax + 2*ebx]		; eax = signed short value
    push_data eax						; push value on stack
    instruction_offset 1
proc_end




;---------------------------------------------------------
; Performs the initial part of an array store operation
; including bounds checking. State after execution:
;   eax: array
;   ebx: index
;   edx: value to be stored
; If the index is invalid or the array is null an
; exception is thrown and execution jumps to the next
; .return label.
;---------------------------------------------------------
%macro array_store_init_32 0.nolist
    pop_edx						; edx = value
    array_load_init				; eax = address of first element, ebx = index
%endmacro







;---------------------------------------------------------
; iastore instruction
;---------------------------------------------------------
proc_begin array_iastore, 0
    ; eax = address of first element, ebx = index, edx = value
    array_store_init_32
    mov [eax + 4*ebx], edx
    instruction_offset 1
proc_end




error_message array_lastore, 'array_lastore'
error_message array_fastore, 'array_fastore'
error_message array_dastore, 'array_dastore'






;---------------------------------------------------------
; aastore instruction
;---------------------------------------------------------
proc_begin array_aastore, 0
    ; eax = address of first element, ebx = index, edx = value
    array_store_init_32
    mov [eax + 4*ebx], edx				; store value
    instruction_offset 1
proc_end





;---------------------------------------------------------
; bastore instruction
;---------------------------------------------------------
proc_begin array_bastore, 0
    ; eax = address of first element, ebx = index, edx = value
    array_store_init_32
    mov [eax + ebx], dl
    instruction_offset 1
proc_end




;---------------------------------------------------------
; castore instruction
;---------------------------------------------------------
proc_begin array_castore, 0
    ; eax = address of first element, ebx = index, edx = value
    array_store_init_32
    mov [eax + 2*ebx], dx				; store value
    instruction_offset 1
proc_end




;---------------------------------------------------------
; sastore instruction
;---------------------------------------------------------
proc_begin array_sastore, 0
    ; eax = address of first element, ebx = index, edx = value
    array_store_init_32
    mov [eax + 2*ebx], dx				; store value
    instruction_offset 1
proc_end





