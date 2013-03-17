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
; aconst_null instruction
;---------------------------------------------------------
proc_begin const_aconst_null, 0
	push_pointer value_null
	instruction_offset 1
proc_end


;---------------------------------------------------------
; iconst_m1 instruction
;---------------------------------------------------------
proc_begin const_iconst_m1, 0
	push_data -1
	instruction_offset 1
proc_end

;---------------------------------------------------------
; iconst_0 instruction
;---------------------------------------------------------
proc_begin const_iconst_0, 0
	push_data 0
	instruction_offset 1
proc_end

;---------------------------------------------------------
; iconst_1 instruction
;---------------------------------------------------------
proc_begin const_iconst_1, 0
	push_data 1
	instruction_offset 1
proc_end

;---------------------------------------------------------
; iconst_2 instruction
;---------------------------------------------------------
proc_begin const_iconst_2, 0
	push_data 2
	instruction_offset 1
proc_end

;---------------------------------------------------------
; iconst_3 instruction
;---------------------------------------------------------
proc_begin const_iconst_3, 0
	push_data 3
	instruction_offset 1
proc_end

;---------------------------------------------------------
; iconst_4 instruction
;---------------------------------------------------------
proc_begin const_iconst_4, 0
	push_data 4
	instruction_offset 1
proc_end

;---------------------------------------------------------
; iconst_5 instruction
;---------------------------------------------------------
proc_begin const_iconst_5, 0
	push_data 5
	instruction_offset 1
proc_end





;---------------------------------------------------------
; lconst_0 instruction
;---------------------------------------------------------
proc_begin const_lconst_0, 0
	push_long 0, 0
	instruction_offset 1
proc_end

;---------------------------------------------------------
; lconst_1 instruction
;---------------------------------------------------------
proc_begin const_lconst_1, 0
	push_long 0, 1
	instruction_offset 1
proc_end






;---------------------------------------------------------
; fconst_0 instruction
;---------------------------------------------------------
proc_begin const_fconst_0, 0
	push_data 0x0
	instruction_offset 1
proc_end

;---------------------------------------------------------
; fconst_1 instruction
;---------------------------------------------------------
proc_begin const_fconst_1, 0
	push_data 0x3F800000
	instruction_offset 1
proc_end

;---------------------------------------------------------
; fconst_2 instruction
;---------------------------------------------------------
proc_begin const_fconst_2, 0
	push_data 0x40000000
	instruction_offset 1
proc_end



error_message const_dconst_2, 'const_dconst_2'
error_message const_dconst_1, 'const_dconst_1'






	
