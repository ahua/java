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
; iinc instruction
;
; locals: index
;---------------------------------------------------------
proc_begin locals_iinc, 1
	mov ecx, [reg_locals]							; ecx = address of first local
	instruction_byte_code 1							; eax = index of local
	shl eax, 3										; eax = index of local in bytes (8 bytes per entry)
	add ecx, eax									; ecx = address of local
	instruction_sign_byte_code 2					; eax = amount to increment
	add [ecx], eax
	instruction_offset 3
proc_end
