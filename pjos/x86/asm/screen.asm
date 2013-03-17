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
; Constants
;---------------------------------------------------------
screen_video_memory		equ 0xb8000
screen_video_memory_end	equ screen_video_memory + (80 * 25 * 2)
screen_newline			equ 0xa							; java '\n' (UNIX newline char)
screen_backspace        equ 0x8                         ; ascii BS character
screen_line				equ 80 * 2						; 80 characters (1 byte ascii char, 1 attribute byte)
screen_total			equ 80 * 25						; total number of chars



;---------------------------------------------------------
; Holds current cursor location
;---------------------------------------------------------
screen_pos:
	dd screen_video_memory





;---------------------------------------------------------
; Print a string to the screen
;
; args: string, length
;
; locals: count
;---------------------------------------------------------
proc_begin screen_print_string, 1
	mov dword local_1, 0			; count = 0

	; check if done
.next:
	mov ecx, local_1				; ecx = count
	cmp ecx, arg_2
	je .return						; if (count == length) return

	; print the next character
	mov eax, arg_1					; eax = string
	mov al, [eax+ecx]				; al = next char
	and eax, 0xFF					; eax = next char
	proc screen_print_char, eax

	; increment the count
	inc dword local_1
	jmp .next
proc_end


;---------------------------------------------------------
; Print a java string to the screen. Takes only the lower
; 8 bits of the java unicode characters and prints their
; ascii value.
;
; args: string
;
; locals: next, to
;---------------------------------------------------------
proc_begin screen_print_java_string, 2
	; get address of char array
	mov eax, arg_1
	mov ebx, [eax + 4*string_chars]							; ebx = char array
	add ebx, 4*array_data									; ebx = address of first char in array
	
	; get address of last char (exclusive)
	mov ecx, [eax + 4*string_last]							; ecx = last index
	shl ecx, 1												; ecx = last index in bytes
	add ecx, ebx											; ecx = address of last char
	mov local_2, ecx										; to = ecx
	
	; get address of first char (inclusive)
	mov edx, [eax + 4*string_first]							; edx = first index
	shl edx, 1												; edx = byte offset of first char
	add edx, ebx											; edx = address of first char
	mov local_1, edx										; next = edx
	
	; check if finished
.check:														; edx contains address of next char
	cmp edx, local_2
	jge .return												; if next >= to return
	
	; print the next character
	mov ax, [edx]
	and eax, 0x000000FF										; eax = ascii char
	proc screen_print_char, eax
	
	; move to next character
	add dword local_1, 2									; next += 2
	mov edx, local_1
	jmp .check
proc_end



;---------------------------------------------------------
; Print a hex representation of the given byte value
;
; args: byte
;---------------------------------------------------------
proc_begin screen_print_byte, 0
	; print first character
	mov eax, arg_1
	and eax, 0xFF								; clear rest of eax
	shr eax, 4									; eax = offset of first char
	add eax, .chars								; eax = address of first char
	mov al, [eax]								; al contains char to print
	proc screen_print_char, eax
	
	; print second character
	mov eax, arg_1
	and eax, 0xF								; eax = offset of second char
	add eax, .chars								; eax = address of second char
	mov al, [eax]								; al contains char to print
	proc screen_print_char, eax
	jmp .return
.chars:
	db '0123456789ABCDEF'
proc_end



;---------------------------------------------------------
; Print a hex representation of the given word value
;
; args: word
;
; locals: count
;---------------------------------------------------------
proc_begin screen_print_word, 1
	mov ecx, 3
	mov local_1, ecx					; count = 3

	; print next byte
.next:
	cmp ecx, 0
	jl .return							; while (count > 0) ...
	mov eax, arg_1
	shl cl, 3							; cl = number of bits to shift
	shr eax, cl
	and eax, 0xFF
	proc screen_print_byte, eax
	dec dword local_1					; count--
	mov ecx, local_1
	jmp .next
proc_end





;---------------------------------------------------------
; Print an ascii character to the screen
;
; args: char
;---------------------------------------------------------
proc_begin screen_print_char, 0
	mov al, arg_1								; al = char
	mov ah, 0x7									; ah = grey colour
	mov ecx, [screen_pos]						; ecx = current screen position

    ; handle a backspace char(0x8)
    cmp al, screen_backspace
    jne .newline
    cmp ecx, screen_video_memory
    je .newline                                 ; can't backspace if already at top left!
    sub ecx, 2                                  ; point to previous char
    mov al, ' '                                 ; al = space
    mov [ecx], ax                               ; erase previous char
    mov [screen_pos], ecx                       ; update cursor position
    jmp .return

	; handle a newline char (0xa)
.newline:
	cmp al, screen_newline
	jne .char

	; set edx to the number of chars on the current line
	mov eax, ecx
	sub eax, screen_video_memory				; eax now contains offset within video mem
	mov edx, 0x0
	mov ebx, screen_line
	div ebx										; eax = edx:eax / 160, (edx has remainder)

	; update screen pos
	add ecx, screen_line
	sub ecx, edx
	mov [screen_pos], ecx
	jmp .scroll

	; draw the char into video memory and update position
.char:
	mov [ecx], ax
	add ecx, 2
	mov [screen_pos], ecx

	; scroll screen contents up by one line if necessary
.scroll:
	cmp ecx, screen_video_memory_end
	jl .return
	sub ecx, screen_line
	mov [screen_pos], ecx
	call screen_scroll
proc_end



						
						

;---------------------------------------------------------
; Clear the screen
;---------------------------------------------------------
proc_begin screen_clear, 0
	mov ebx, screen_video_memory
	mov al, ' '									; space character
	mov ah, 0x7									; light grey colour
	mov ecx, screen_total						; number of chars
.next:
	mov [ebx], ax								; write character and colour
	add ebx, 2
	loop .next

	mov dword [screen_pos], screen_video_memory	; reset cursor pos

	; send VGA cursor off screen
	mov dx, 0x3D4								; VGA register
	mov al, 14									; function index
	out dx, al
	mov dx, 0x3D5
	mov al, bh									; most sig byte
	out dx, al

	mov dx, 0x3D4								; VGA register
	mov al, 15									; function index
	out dx, al
	mov dx, 0x3D5
	mov al, bl									; least sig byte
	out dx, al
proc_end





;---------------------------------------------------------
; Scroll the screen up by one line
;---------------------------------------------------------
proc_begin screen_scroll, 0
	; save instruction and stack registers
	mov [reg_instruction], edi
	mov [reg_stack], esi
	
	; mov each line up one
	cld
	mov edi, screen_video_memory
	mov esi, screen_video_memory + screen_line
	mov ecx, (screen_line * 24 / 4)				; 24 rows, each move does 4 bytes
	rep movsd
	
	; clear the last line
	mov edi, screen_video_memory_end - screen_line
	mov ah, 0x07								; light grey colour
	mov al, ' '									; space character
	mov ecx, screen_line / 2
	rep stosw
	
	; restore instruction and stack registers
	mov edi, [reg_instruction]
	mov esi, [reg_stack]
proc_end



		
			

