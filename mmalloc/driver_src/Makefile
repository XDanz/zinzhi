CC = gcc
CFLAGS = -Wall -Wdeclaration-after-statement
INCLUDE=../include
LIB=../lib/libmmalloc.a

OBJS = mdriver.o fsecs.o fcyc.o clock.o ftimer.o

all: $(OBJS)

%.o: %.c
	$(CC) -I$(INCLUDE) -c -o $@ $< $(CFLAGS)

mdriver: $(OBJS)
	$(CC) $(CFLAGS) -o mdriver $(OBJS) $(LIB)

clean:
	rm -f *~ *.o


