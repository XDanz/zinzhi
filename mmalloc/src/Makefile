CC = gcc
CFLAGS = -Wall -Wdeclaration-after-statement

OBJS = mm.o memlib.o

all: $(OBJS) libmmalloc.a

%.o: %.c ../include/%.h
	$(CC) -I../include -c -o $@ $< $(CFLAGS)

libmmalloc.a: $(OBJS)
	ar rcs ../lib/libmalloc.a $(OBJS)

clean:
	rm -f *~ *.o


