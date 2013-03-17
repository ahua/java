# Compile the test platform for pjos

CFLAGS= -g

OBJECTS= \
    array.o \
    bit.o \
    collector.o \
    control.o \
    convert.o \
    exceptions.o \
    distributor.o \
    field.o \
    instance.o \
    interpreter.o \
    invoke.o \
    load.o \
    math.o \
    memory.o \
    monitor.o \
    return.o \
    stack.o \
    store.o \
    switch.o \
    test.o \
    threads.o

all : test

clean :
	rm test $(OBJECTS)

test : $(OBJECTS)
	cc -g -o test $(OBJECTS)

$(OBJECTS) : interpreter.h



