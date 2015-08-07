# tests/test1 --
CC = g++
CFLAGS = -Wall -std=c++11 -I../../includes
SRC = $(wildcard *.cpp)
OBJS = ../../objs/date.o ../../objs/gregorian.o ../../objs/julian.o test.o
OBJS+= ../../objs/kattistime.o
all: $(OBJS) test_run TAGS

test_run: $(OBJS)
	$(CC) $(OBJS) -o $@

test:
	./test_run

../../objs/%.o:
	make -C ../../src all
# $@ -- references to .o file, $< referneces the .cpp file
%.o: %.cpp
	$(CC) -c $< $(CFLAGS)
TAGS:
	find . -name \*.h -print -o -name \*.cpp -print | etags -

clean: 
	rm -rf *.o *~ test_run
