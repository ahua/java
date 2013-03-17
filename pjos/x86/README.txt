PJOS implementation for x86
---------------------------

This directory contains the source files for the x86 architecture
implementation of PJOS. The file "floppy.zip" contains a fat formatted 1.4MB
floppy disk image with the GNU GRUB bootloader loaded into it. This is used as
a template by the build process to create a bootable floppy with the kernel
and java memory image for PJOS. The file "boot/grub/menu.lst" contains the
menu configuration that will be loaded into the floppy image.
