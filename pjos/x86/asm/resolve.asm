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
; Retrieve the constant pool entry at the given index.
; If the entry id doesn't match the id type supplied,
; call the resolve method of the Core class and return
; null.
;
; args: index, id
;---------------------------------------------------------
proc_begin resolve_entry, 0
    ; load constant pool entry
    mov eax, [reg_pool]                     ; eax = address of first pool entry
    mov ebx, arg_1                          ; ebx = index
    mov eax, [eax + 4*ebx]                  ; eax = entry
    
    ; check entry id
    mov ebx, [eax + 4*entry_id]             ; ebx = id
    cmp ebx, arg_2
    je .return
    
    ; call the resolve method of the Core class
    mov eax, [reg_core]                     ; eax = address of core object
    mov ebx, [eax + 4*core_resolve_method]  ; ebx = address of resolve method
    mov eax, [eax + 4*object_type]          ; eax = address of core type (target)
    proc invoke_execute, eax, ebx, 0
    
    ; return null
    mov eax, value_null
proc_end



