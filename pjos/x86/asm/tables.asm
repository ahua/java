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
tables_interrupt_queue	equ 0x0
tables_idt_location		equ 0x400

;---------------------------------------------------------
; Globals
;---------------------------------------------------------
tables_interrupt_first: dd 0x0								; address of first interrupt code in queue (inclusive)
tables_interrupt_last: dd 0x0								; address of last interrupt code in queue (exclusive)
tables_interrupt_code: dd 0x0								; holds code of most recent interrupt

;---------------------------------------------------------
; Fill the IDT by copying the template entry into each
; slot. Then initialise the IDT register.
;---------------------------------------------------------
proc_begin tables_setup_idt, 0
	; fill IDT
	mov ecx, 256											; 256 entries in table
	mov edx, tables_idt_location							; edx = address of first entry

	; set ebx:eax to contain 8 byte entry
.next:
	mov ebx, tables_gdt_code - tables_gdt					; ebx = selector offset in gdt
	shl ebx, 16												; ebx contains selector in bits 31..16
	mov eax, 256
	sub eax, ecx											; eax = number of this entry
	shl eax, 4												; eax *= 16 (byte offset of handler in table)
	add eax, tables_handlers								; eax = address of handler
	mov bx, ax												; ebx = selector | handler 15..0
	mov ax, 0x8E00											; eax = handler 31..16 | P DPL 01110

	; store descriptor ebx:eax in table
	mov [edx + 4], eax										; last 4 bytes of entry
	mov [edx], ebx											; first 4 bytes of entry
	add edx, 8
	loop .next
	
	; load IDT register
	lidt [.address]
	
	; initialise PICs
	mov al, 0x11											; 0x11 = master/slave config, edge triggered, icw4 to be sent
	out 0x20, al											; set icw1 of master
	out 0xA0, al											; set icw1 of slave
	
	mov al, 0x20
	out 0x21, al											; set icw2 of master (idt offset of 0x20)
	mov al, 0x28
	out 0xA1, al											; set icw2 of slave (idt offset of 0x28)
	
	mov al, 0x4
	out 0x21, al											; set icw3 of master (ir line 2 connected to slave)
	mov al, 0x2
	out 0xA1, al											; set icw3 of slave (irq 1)
	
	mov al, 0x5
	out 0x21, al											; set icw4 of master (master|8086/88|manual EOI|no buf|no nest)
	mov al, 0x1
	out 0xA1, al											; set icw4 of slave (slave|8086/88|manual EOI|no buf|no nest)
	
	; enable all interrupts
	;mov al, ~0x3	; timer & keyboard enabled
	;out 0x21, al
	;mov al, ~0x0	; all masked
	;out 0xA1, al
	mov al, 0x0												; all interrupts unmasked
	out 0x21, al											; set mask of master
	out 0xA1, al											; set mask of slave
	
	jmp .return
.address:
	dw 256*8												; limit = 256 entries of 8 bytes each
	dd tables_idt_location									; base
proc_end



;---------------------------------------------------------
; The generic interrupt handler code. This procedure
; maintains a queue of interrupt values that will be
; accessed by the interrupt handling thread implemented
; in java. This thread is resumed when an interrupt
; occurs.
;---------------------------------------------------------
tables_generic_handler:
	; save registers
	pusha

    ; retrieve interrupt code
    mov eax, [tables_interrupt_code]                        ; eax/al = code

    ; check for exceptions and faults (shouldn't happen)
    cmp eax, 32
    jl near kernel_death
    jg .append

    ; handle timer interrupt, set thread yield flag
    mov dword [kernel_execute.flag], 0x1                    ; set yield flag
    add dword [reg_timelow], 55								
    adc dword [reg_timehigh], 0								; 55ms every 1/18.5s
    jmp .ack
	
	; append interrupt code to queue
.append:
	mov ebx, [tables_interrupt_last]						; ebx = last
	mov [ebx], al											; save code in queue
	inc ebx													; ebx++
	cmp ebx, tables_idt_location
	jl .save_last
	mov ebx, 0x0											; wrap last
.save_last:
	mov [tables_interrupt_last], ebx						; save last
	
	; update first pointer if necessary
	mov ecx, [tables_interrupt_first]						; ecx = first
	cmp ecx, ebx
	jne .resume
	inc ecx
	cmp ecx, tables_idt_location
	jl .save_first
	mov ecx, 0x0											; wrap first
.save_first:
	mov [tables_interrupt_first], ecx						; save first
	
	; resume the interrupt handler thread if suspended
.resume:
	mov eax, [reg_core]										; eax = core
	mov eax, [eax + 4*core_notifier]						; eax = notifier
	cmp dword [eax + 4*thread_suspended], value_true
	jne .ack
	proc threads_resume, eax								; resume notifier if suspended

	; send EOI (end of interrupt)
	; (This is necessary because PICs are in manual mode, not automatic)
.ack:
	mov al, 0x20
	out 0x20, al											; send EOI to master
	out 0xA0, al											; send EOI to slave
	
	; restore registers
.restore:
	popa
	
	; back to interrupted code
	iret








;---------------------------------------------------------
; Used to generate code to handle an interrupt. The id
; is pushed on the stack before calling the generic
; handler routine.
;---------------------------------------------------------
%macro tables_handler 1.nolist
.entry%1
	mov dword [tables_interrupt_code], %1
	jmp tables_generic_handler
	times .entry%1-$+16 db 0
%endmacro



			

;---------------------------------------------------------
; Load the GDT register with the address of the GDT
;---------------------------------------------------------
proc_begin tables_setup_gdt, 0
	lgdt [.address]
	jmp .return
.address:
	dw 3*8						; limit = 3 entries of 8 bytes each
	dd tables_gdt				; base = 32-bit address of gdt data
proc_end


;---------------------------------------------------------
; The GDT (Global Descriptor Table)
;---------------------------------------------------------
tables_gdt:
			; first entry is null
			dw 0x0000, 0x0000
			dw 0x0000, 0x0000
			
			; 4 Gigabyte code segment at address 0x00
tables_gdt_code:
			dw 0xFFFF					; limit 0..15
			dw 0x0000					; base 0..15
			db 0x00						; base 16..23
			db 0x9a						; access
			db 0xCF						; granularity, limit 16..19
			db 0x00						; base 24..31
			
			; 4 Gigabyte data segment at address 0x00
tables_gdt_data:
			dw 0xFFFF					; limit 0..15
			dw 0x0000					; base 0..15
			db 0x00						; base 16..23
			db 0x92						; access
			db 0xCF						; granularity, limit 16..19
			db 0x00						; base 24..31
			



;---------------------------------------------------------
; The table of interrupt handlers
;---------------------------------------------------------
tables_handlers:
	tables_handler 0
	tables_handler 1
	tables_handler 2
	tables_handler 3
	tables_handler 4
	tables_handler 5
	tables_handler 6
	tables_handler 7
	tables_handler 8
	tables_handler 9
	tables_handler 10
	tables_handler 11
	tables_handler 12
	tables_handler 13
	tables_handler 14
	tables_handler 15
	tables_handler 16
	tables_handler 17
	tables_handler 18
	tables_handler 19
	tables_handler 20
	tables_handler 21
	tables_handler 22
	tables_handler 23
	tables_handler 24
	tables_handler 25
	tables_handler 26
	tables_handler 27
	tables_handler 28
	tables_handler 29
	tables_handler 30
	tables_handler 31
	tables_handler 32
	tables_handler 33
	tables_handler 34
	tables_handler 35
	tables_handler 36
	tables_handler 37
	tables_handler 38
	tables_handler 39
	tables_handler 40
	tables_handler 41
	tables_handler 42
	tables_handler 43
	tables_handler 44
	tables_handler 45
	tables_handler 46
	tables_handler 47
	tables_handler 48
	tables_handler 49
	tables_handler 50
	tables_handler 51
	tables_handler 52
	tables_handler 53
	tables_handler 54
	tables_handler 55
	tables_handler 56
	tables_handler 57
	tables_handler 58
	tables_handler 59
	tables_handler 60
	tables_handler 61
	tables_handler 62
	tables_handler 63
	tables_handler 64
	tables_handler 65
	tables_handler 66
	tables_handler 67
	tables_handler 68
	tables_handler 69
	tables_handler 70
	tables_handler 71
	tables_handler 72
	tables_handler 73
	tables_handler 74
	tables_handler 75
	tables_handler 76
	tables_handler 77
	tables_handler 78
	tables_handler 79
	tables_handler 80
	tables_handler 81
	tables_handler 82
	tables_handler 83
	tables_handler 84
	tables_handler 85
	tables_handler 86
	tables_handler 87
	tables_handler 88
	tables_handler 89
	tables_handler 90
	tables_handler 91
	tables_handler 92
	tables_handler 93
	tables_handler 94
	tables_handler 95
	tables_handler 96
	tables_handler 97
	tables_handler 98
	tables_handler 99
	tables_handler 100
	tables_handler 101
	tables_handler 102
	tables_handler 103
	tables_handler 104
	tables_handler 105
	tables_handler 106
	tables_handler 107
	tables_handler 108
	tables_handler 109
	tables_handler 110
	tables_handler 111
	tables_handler 112
	tables_handler 113
	tables_handler 114
	tables_handler 115
	tables_handler 116
	tables_handler 117
	tables_handler 118
	tables_handler 119
	tables_handler 120
	tables_handler 121
	tables_handler 122
	tables_handler 123
	tables_handler 124
	tables_handler 125
	tables_handler 126
	tables_handler 127
	tables_handler 128
	tables_handler 129
	tables_handler 130
	tables_handler 131
	tables_handler 132
	tables_handler 133
	tables_handler 134
	tables_handler 135
	tables_handler 136
	tables_handler 137
	tables_handler 138
	tables_handler 139
	tables_handler 140
	tables_handler 141
	tables_handler 142
	tables_handler 143
	tables_handler 144
	tables_handler 145
	tables_handler 146
	tables_handler 147
	tables_handler 148
	tables_handler 149
	tables_handler 150
	tables_handler 151
	tables_handler 152
	tables_handler 153
	tables_handler 154
	tables_handler 155
	tables_handler 156
	tables_handler 157
	tables_handler 158
	tables_handler 159
	tables_handler 160
	tables_handler 161
	tables_handler 162
	tables_handler 163
	tables_handler 164
	tables_handler 165
	tables_handler 166
	tables_handler 167
	tables_handler 168
	tables_handler 169
	tables_handler 170
	tables_handler 171
	tables_handler 172
	tables_handler 173
	tables_handler 174
	tables_handler 175
	tables_handler 176
	tables_handler 177
	tables_handler 178
	tables_handler 179
	tables_handler 180
	tables_handler 181
	tables_handler 182
	tables_handler 183
	tables_handler 184
	tables_handler 185
	tables_handler 186
	tables_handler 187
	tables_handler 188
	tables_handler 189
	tables_handler 190
	tables_handler 191
	tables_handler 192
	tables_handler 193
	tables_handler 194
	tables_handler 195
	tables_handler 196
	tables_handler 197
	tables_handler 198
	tables_handler 199
	tables_handler 200
	tables_handler 201
	tables_handler 202
	tables_handler 203
	tables_handler 204
	tables_handler 205
	tables_handler 206
	tables_handler 207
	tables_handler 208
	tables_handler 209
	tables_handler 210
	tables_handler 211
	tables_handler 212
	tables_handler 213
	tables_handler 214
	tables_handler 215
	tables_handler 216
	tables_handler 217
	tables_handler 218
	tables_handler 219
	tables_handler 220
	tables_handler 221
	tables_handler 222
	tables_handler 223
	tables_handler 224
	tables_handler 225
	tables_handler 226
	tables_handler 227
	tables_handler 228
	tables_handler 229
	tables_handler 230
	tables_handler 231
	tables_handler 232
	tables_handler 233
	tables_handler 234
	tables_handler 235
	tables_handler 236
	tables_handler 237
	tables_handler 238
	tables_handler 239
	tables_handler 240
	tables_handler 241
	tables_handler 242
	tables_handler 243
	tables_handler 244
	tables_handler 245
	tables_handler 246
	tables_handler 247
	tables_handler 248
	tables_handler 249
	tables_handler 250
	tables_handler 251
	tables_handler 252
	tables_handler 253
	tables_handler 254
	tables_handler 255








