# tests/test3 --
CC = g++
CFLAGS = -Wall -std=c++11 -I../../includes
SRC = $(wildcard *.cpp)
OBJS = ../../objs/date.o ../../objs/gregorian.o ../../objs/kattistime.o ../../objs/julian.o cprog09lab22c.o

build: test_run

test_run: $(OBJS)
	$(CC) $(OBJS) -o $@

test:
	cat lab22c.in | ./test_run > utdata.txt
	# diff utdata.txt lab22c.out

# ../../objs/%.o:
# 	make -C ../../src all
# $@ -- references to .o file (target), $< referneces the .cpp file
%.o: %.cpp
	$(CC) -c $< $(CFLAGS)


clean: 
	rm -rf *.o *~ test_run utdata.txt
