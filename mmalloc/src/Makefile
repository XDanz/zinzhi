CC = gcc
CFLAGS = -Wall -std=gnu99 -Wdeclaration-after-statement

OBJS = mm.o memlib.o

all: $(OBJS) ../lib/libmmalloc.a

debug: clean
	$(MAKE) "CFLAGS+= -g -gdwarf-2 -g3 -DDEBUG" all


%.o: %.c ../include/%.h
	$(CC) -I../include -c -o $@ $< $(CFLAGS)

../lib/libmmalloc.a: $(OBJS)
	ar rcs ../lib/libmmalloc.a $(OBJS)

clean:
	rm -f *~ *.o *.a
