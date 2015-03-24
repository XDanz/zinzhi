CC = gcc
CFLAGS = -Wall -Wdeclaration-after-statement -O2

OBJS = mdriver.o mm.o memlib.o fsecs.o fcyc.o clock.o ftimer.o

MM_OBJS = mm.o memlib.o test.o

INTSRC = interactive.c mm.c memlib.c

SIMPLESRC = test.c mm.c memlib.c

INTOBJ = $(INTSRC:.c=.o)

SIMPLEOBJ = $(SIMPLESRC:.c=.o)

TESTOBJS = mm.o memlib.o test1.o

SIMPLETEST = test.o

mdriver: $(OBJS)
	$(CC) $(CFLAGS) -o mdriver $(OBJS)

mm: $(MM_OBJS)
	$(CC) $(CFLAGS) -o mmalloc $(MM_OBJS)

all: mm mdriver

rundan: 
	./mdriver -f short_dan.rep

run: mm
	./mmalloc

mdriver.o: mdriver.c fsecs.h fcyc.h clock.h memlib.h config.h mm.h

memlib.o: memlib.c memlib.h

mm.o: mm.c mm.h memlib.h

fsecs.o: fsecs.c fsecs.h config.h

fcyc.o: fcyc.c fcyc.h

ftimer.o: ftimer.c ftimer.h config.h

clock.o: clock.c clock.h

test.o: test.c

test1.o: test1.c test1.h

$(INTOBJ): $(INTSRC)

$(SIMPLEOBJ): $(SIMPLESRC)

interact: $(INTOBJ) memlib.h mm.h
	$(CC) $(INTOBJ) $(CFLAGS) -o $@

test: $(SIMPLEOBJ) memlib.h mm.h
	$(CC) $(SIMPLEOBJ) $(CFLAGS) -o $@

clean:
	rm -f *~ *.o mdriver a.out

test1: $(TESTOBJS)
	$(CC) $(CFLAGS) -o test1 $(TESTOBJS)



