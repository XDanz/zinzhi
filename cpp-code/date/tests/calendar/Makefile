# tests/test1 --
CC = g++
CFLAGS = -Wall -std=c++11 -I../../includes
SRC = $(wildcard *.cpp)
OBJS = ../../objs/date.o ../../objs/gregorian.o ../../objs/kattistime.o ../../objs/julian.o test.o

build: test_run

test_run: $(OBJS) 
	$(CC) $(OBJS) -o $@

test:
	./test_run

# ../../objs/%.o:
# 	make -C ../../src all
# $@ -- references to .o file (target), $< referneces the .cpp 
#	(prerequisit) file
%.o: %.cpp ../../includes/calendar.h
	$(CC) -c $< $(CFLAGS)


clean: 
	rm -rf *.o *~ test_run
