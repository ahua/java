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
; i2l instruction
;---------------------------------------------------------
proc_begin convert_i2l, 0
	pop_eax							; eax = integer value
	cdq								; edx:eax = long value
	push_long edx, eax
	instruction_offset 1
proc_end



;---------------------------------------------------------
; i2f instruction
;---------------------------------------------------------
proc_begin convert_i2f, 0
	fild dword [esi]				; convert to float push on fp stack
	fstp dword [esi]				; store converted value on top of stack
	instruction_offset 1
proc_end





error_message convert_i2d, 'convert_i2d'




;---------------------------------------------------------
; l2i instruction
;---------------------------------------------------------
proc_begin convert_l2i, 0
	pop_long edx, eax				; edx:eax = long value
	push_data eax					; push low 32 bits
	instruction_offset 1
proc_end




error_message convert_l2f, 'convert_l2f'
error_message convert_l2d, 'convert_l2d'



;---------------------------------------------------------
; f2i instruction
;---------------------------------------------------------
proc_begin convert_f2i, 0
	fld dword [esi]					; push on fp stack
	fistp dword [esi]				; convert and store value on top of stack
	instruction_offset 1
proc_end





error_message convert_f2l, 'convert_f2l'
error_message convert_f2d, 'convert_f2d'
error_message convert_d2i, 'convert_d2i'
error_message convert_d2l, 'convert_d2l'
error_message convert_d2f, 'convert_d2f'





;---------------------------------------------------------
; i2b instruction
;---------------------------------------------------------
proc_begin convert_i2b, 0
    pop_eax                         ; eax = integer value
    movsx eax, al					; sign extend byte value
    push_data eax                   ; push value on stack
    instruction_offset 1
proc_end



;---------------------------------------------------------
; i2c instruction
;---------------------------------------------------------
proc_begin convert_i2c, 0
    pop_eax                         ; eax = integer value
    and eax, 0x0000ffff             ; convert to unsigned 16-bit value
    push_data eax                   ; push char on stack
    instruction_offset 1
proc_end



;---------------------------------------------------------
; i2s instruction
;---------------------------------------------------------
proc_begin convert_i2s, 0
    pop_eax                         ; eax = integer value
    movsx eax, ax					; sign extend short value
    push_data eax                   ; push value on stack
    instruction_offset 1
proc_end






	
