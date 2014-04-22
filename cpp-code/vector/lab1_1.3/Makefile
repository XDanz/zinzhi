CC=g++
CFLAGS=-std=c++11 -Wall
VALGRINDFLAGS=--tool=memcheck --leak-check=yes
VALGRIND=/opt/local/bin/valgrind
# These are the pattern matching rules. In addition to the automatic
# variables used here, the variable $* that matches whatever % stands for
# can be useful in special cases.
#
# $< - refers to the first prerequisite
# $@ - refers to the target

all: Data A

Data: Data.o

memcheck: Data
	$(VALGRIND) $(VALGRINDFLAGS) ./$<

A: A.o

%.o: %.cpp
	$(CC) $(CFLAGS) -c $< -o $@
%: %.cpp
	$(CC) $(CFLAGS) -o $@ $<


clean:
	rm -f *.o Data A

.PHONY: clean


