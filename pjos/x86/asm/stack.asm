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
; bipush instruction
;---------------------------------------------------------
proc_begin stack_bipush, 0
	instruction_sign_byte_code 1					; eax = signed byte value
	push_data eax									; push value
	instruction_offset 2
proc_end

;---------------------------------------------------------
; sipush instruction
;---------------------------------------------------------
proc_begin stack_sipush, 0
	instruction_sign_two_byte_codes 1				; eax = signed short value
	push_data eax									; push value
	instruction_offset 3
proc_end
	


;---------------------------------------------------------
; pop instruction
;---------------------------------------------------------
proc_begin stack_pop, 0
	pop_eax											; eax = popped value (ignored)
	instruction_offset 1
proc_end



;---------------------------------------------------------
; pop2 instruction
;---------------------------------------------------------
proc_begin stack_pop2, 0
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'pop2 not implemented!!!', 0xA
.end:
proc_end

;---------------------------------------------------------
; dup instruction
;---------------------------------------------------------
proc_begin stack_dup, 0
	peek_indicate eax, ebx									; eax = value1, ebx = flag1
	push_value eax, ebx										; push value1/flag1
	instruction_offset 1
proc_end



;---------------------------------------------------------
; dup_x1 instruction
;---------------------------------------------------------
proc_begin stack_dup_x1, 0
	pop_indicate eax, ebx									; eax = value1, ebx = flag1
	pop_indicate ecx, edx									; ecx = value2, ecx = flag2
	
	push_value eax, ebx										; push value1/flag1
	push_value ecx, edx										; push value2/flag2
	push_value eax, ebx										; push value1/flag1

	instruction_offset 1	
proc_end




;---------------------------------------------------------
; dup_x2 instruction
;---------------------------------------------------------
proc_begin stack_dup_x2, 0
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'dup_x2 not implemented!!!', 0xA
.end:
proc_end

;---------------------------------------------------------
; dup2 instruction
;---------------------------------------------------------
proc_begin stack_dup2, 0
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'dup2 not implemented!!!', 0xA
.end:
proc_end

;---------------------------------------------------------
; dup2_x1 instruction
;---------------------------------------------------------
proc_begin stack_dup2_x1, 0
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'dup2_x1 not implemented!!!', 0xA
.end:
proc_end

;---------------------------------------------------------
; dup2_x2 instruction
;---------------------------------------------------------
proc_begin stack_dup2_x2, 0
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'dup2_x2 not implemented!!!', 0xA
.end:
proc_end

;---------------------------------------------------------
; swap instruction
;---------------------------------------------------------
proc_begin stack_swap, 0
	proc screen_print_string, .msg, .end-.msg
	jmp $
.msg:
	db 'swap not implemented!!!', 0xA
.end:
proc_end















