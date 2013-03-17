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
; Virtual machine constants
;---------------------------------------------------------

; Access constants from java spec
acc_public								equ 0x1
acc_private								equ 0x2
acc_protected							equ 0x4
acc_static								equ 0x8
acc_final								equ 0x10
acc_super								equ 0x20
acc_synchronized						equ 0x20
acc_volatile							equ 0x40
acc_transient							equ 0x80
acc_native								equ 0x100
acc_interface							equ 0x200
acc_abstract							equ 0x400
acc_strict								equ 0x800

; Array constants
array_length            equ 5
array_data              equ 6

; Constant object layout
constant_first							equ 6
constant_second							equ 7

; Core object layout
core_next								equ 5
core_priority							equ 6
core_running							equ 7
core_sleeping							equ 8
core_idle								equ 9
core_notifier							equ 10
core_thread_run_method					equ 11
core_resolve_method						equ 12
core_throw_null_pointer					equ 13
core_throw_class_cast					equ 14
core_throw_array_index					equ 15
core_throw_arithmetic					equ 16
core_architecture_type					equ 17
core_statics_type						equ 18
core_lock_type							equ 19
core_arrays								equ 20 - 4

; Entry object layout
entry_id								equ 5
entry_name								equ 6
entry_descriptor						equ 7
entry_classname							equ 8
entry_flags								equ 9
entry_owner								equ 10

; Field object layout
field_index								equ 11
field_reference_flag					equ 12
field_size								equ 13
field_constant_value					equ 14

; Frame object layout
frame_return_frame						equ 5
frame_method							equ 6
frame_return_pc							equ 7
frame_pc								equ 8
frame_sp								equ 9
frame_locals							equ 10

; object headers
header_forward							equ 0xF0000000
header_instance							equ 0x0B000000
header_data_array						equ 0xDA000000
header_object_array						equ 0x0A000000
header_static_fields					equ 0x5C000000
header_stack_frame						equ 0x5F000000

; Runtime constant pool entry ids
id_type									equ 1
id_unresolved_type						equ 2
id_field								equ 3
id_unresolved_field						equ 4
id_method								equ 5
id_unresolved_method					equ 6
id_integer_constant						equ 7
id_long_constant						equ 8
id_string_constant						equ 9

; Lock layout
lock_owner								equ 5
lock_count								equ 6
lock_lock_head							equ 7
lock_lock_tail							equ 8
lock_wait_head							equ 9
lock_wait_tail							equ 10

; magic ids for runtime methods
magic_runtime_core_debug				equ 1
magic_runtime_core_execute_static		equ 2
magic_runtime_core_get					equ 3
magic_runtime_core_get_type				equ 4
magic_runtime_frame_current				equ 5
magic_runtime_idle_sleep				equ 6
magic_runtime_statics_create			equ 7
magic_runtime_thread_current			equ 8
magic_runtime_thread_sleep				equ 9
magic_runtime_thread_start_hook			equ 10
magic_runtime_thread_suspend			equ 11
magic_runtime_thread_resume				equ 12

; magic ids for java methods
magic_java_object_get_class				equ 13
magic_java_object_notify				equ 14
magic_java_object_notify_all			equ 15
magic_java_object_wait					equ 16
magic_java_system_time					equ 17
magic_java_system_hashcode				equ 18
magic_java_system_set_err				equ 19
magic_java_system_set_out				equ 20
magic_java_system_set_in				equ 21

; magic ids for x86-specific methods
magic_x86_write_to_console				equ 22
magic_x86_next_interrupt				equ 23
magic_x86_in							equ 24
magic_x86_out							equ 25
magic_x86_read_dma						equ 26

; Method object layout
method_pool								equ 11
method_max_stack						equ 12
method_max_locals						equ 13
method_arg_count						equ 14
method_code								equ 15
method_exceptions						equ 16
method_line_numbers						equ 17
method_magic							equ 18

; Object layout
object_header							equ 0
object_size								equ 1
object_hashcode							equ 2
object_lock								equ 3
object_type								equ 4
object_fields							equ 5

; the number of instructions to execute for a thread
; before moving to the next
slice_max								equ 10

; statics layout
statics_map								equ 5
statics_fields							equ 6

; string layout
string_chars							equ 5
string_first							equ 6
string_last								equ 7
string_length							equ 8
string_hashcode							equ 9

; system class static fields
system_in								equ 0
system_out								equ 1
system_err								equ 2

; array types
t_boolean								equ 4
t_char									equ 5
t_float									equ 6
t_double								equ 7
t_byte									equ 8
t_short									equ 9
t_int									equ 10
t_long									equ 11

; Thread object layout
thread_thread							equ 5
thread_frame							equ 6
thread_lock								equ 7
thread_lock_count						equ 8
thread_next_lock						equ 9
thread_prev_lock						equ 10
thread_next_running						equ 11
thread_prev_running						equ 12
thread_next_sleeping					equ 13
thread_prev_sleeping					equ 14
thread_wakeup							equ 15
; thread_wakeup takes two entries		equ 16
thread_name								equ 17
thread_priority							equ 18
thread_started							equ 19
thread_suspended						equ 20

; Type layout
type_id									equ 5
type_peer								equ 6
type_statics							equ 7
type_name								equ 8
type_code								equ 9
type_flags								equ 10
type_pool								equ 11
type_super_name							equ 12
type_super_type							equ 13
type_interface_names					equ 14
type_interface_types					equ 15
type_methods							equ 16
type_fields								equ 17
type_instance_field_count				equ 18
type_static_field_count					equ 19
type_source								equ 20
type_linked								equ 21
type_component_name						equ 22
type_component_type						equ 23
type_width								equ 24
type_primitive							equ 25
type_array_type							equ 26
type_instance_map						equ 27
type_static_map							equ 28

; values
value_null								equ 0x0
value_true								equ 0x1
value_false								equ 0x0

