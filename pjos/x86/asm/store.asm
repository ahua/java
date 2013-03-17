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
; istore instruction
;---------------------------------------------------------
proc_begin store_istore, 0
	instruction_byte_code 1							; eax = local index
	shl eax, 3										; eax = local index in bytes
	add eax, [reg_locals]							; eax = address of local
	pop_ebx											; ebx = value
	mov [eax], ebx									; store value
	mov dword [eax + 4], value_false				; clear flag
	instruction_offset 2
proc_end



;---------------------------------------------------------
; lstore instruction
;---------------------------------------------------------
proc_begin store_lstore, 0
	instruction_byte_code 1							; eax = local index
	shl eax, 3										; eax = local index in bytes
	add eax, [reg_locals]							; eax = address of local
	pop_long ecx, ebx								; ecx:ebx = value
	mov dword [eax], ecx							; store high bytes
	mov dword [eax + 4], value_false				; clear flag
	mov dword [eax + 8], ebx						; store low bytes
	mov dword [eax + 12], value_false				; clear flag
	instruction_offset 2
proc_end




error_message store_fstore, 'store_fstore'
error_message store_dstore, 'store_dstore'



;---------------------------------------------------------
; astore instruction
;---------------------------------------------------------
proc_begin store_astore, 0
	instruction_byte_code 1							; eax = local index
	shl eax, 3										; eax = local index in bytes (8 bytes per local)
	add eax, [reg_locals]							; eax = address of local
	pop_indicate ecx, edx							; ecx = value, edx = flag
	mov [eax], ecx									; store value
	mov [eax + 4], edx								; store flag
	instruction_offset 2
proc_end




;---------------------------------------------------------
; Create a procedure for a 32 bit xstore_x instruction
;
; args: prefix, index
;---------------------------------------------------------
%macro store_xstore_x_32 2.nolist
proc_begin store_%1store_%2, 0
	pop_eax										; eax = value
	mov ebx, [reg_locals]						; ebx = address of first local
	add ebx, 8*%2								; ebx = address of local
	mov [ebx], eax								; store value
	mov dword [ebx + 4], value_false			; clear flag
	instruction_offset 1
proc_end
%endmacro

;---------------------------------------------------------
; istore_x instructions
;---------------------------------------------------------
store_xstore_x_32 i, 0
store_xstore_x_32 i, 1
store_xstore_x_32 i, 2
store_xstore_x_32 i, 3



;---------------------------------------------------------
; Create a procedure for a 64 bit xstore_x instruction
;
; args: prefix, index
;---------------------------------------------------------
%macro store_xstore_x_64 2.nolist
proc_begin store_%1store_%2, 0
	pop_long edx, eax							; edx:eax = value
	mov ebx, [reg_locals]						; ebx = address of first local
	add ebx, 8*%2								; ebx = address of local
	mov dword [ebx], edx						; store high bytes
	mov dword [ebx + 4], value_false			; clear high flag
	mov dword [ebx + 8], eax					; store low bytes
	mov dword [ebx + 12], value_false			; clear low flag
	instruction_offset 1
proc_end
%endmacro

;---------------------------------------------------------
; lstore_x instructions
;---------------------------------------------------------
store_xstore_x_64 l, 0
store_xstore_x_64 l, 1
store_xstore_x_64 l, 2
store_xstore_x_64 l, 3



;---------------------------------------------------------
; fstore_x instructions
;---------------------------------------------------------
store_xstore_x_32 f, 0
store_xstore_x_32 f, 1
store_xstore_x_32 f, 2
store_xstore_x_32 f, 3



;---------------------------------------------------------
; dstore_x instructions
;---------------------------------------------------------
store_xstore_x_64 d, 0
store_xstore_x_64 d, 1
store_xstore_x_64 d, 2
store_xstore_x_64 d, 3




;---------------------------------------------------------
; Create a procedure for an astore_x instruction
;
; args: index
;---------------------------------------------------------
%macro store_astore_x 1.nolist
proc_begin store_astore_%1, 0
	pop_indicate eax, ebx							; eax = value, ebx = flag
	mov ecx, [reg_locals]							; ecx = address of first local
	add ecx, 8*%1									; ecx = address of local
	mov [ecx], eax									; store value
	mov [ecx + 4], ebx								; store flag
	instruction_offset 1
proc_end
%endmacro

;---------------------------------------------------------
; astore_x instructions
;---------------------------------------------------------
store_astore_x 0
store_astore_x 1
store_astore_x 2
store_astore_x 3





