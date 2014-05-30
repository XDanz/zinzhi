# Change these
CC = g++
cflags = -std=c++11 -Wall
cxxtestpath = /usr/bin/
includes = /usr/include/cxxtest

testcode    = example_test.h

testcode.cpp: $(testcode) matrix.h
	python $(cxxtestpath)cxxtestgen.py --error-printer -o testcode.cpp $(testcode)

testcode.o: testcode.cpp
	$(CC) $(cflags) -I $(includes) -I ../../lab1_1.5 -c $<

testcode: testcode.o
	$(CC) -o testcode testcode.o

matrix.o: matrix.cpp matrix.h
	g++ $(cflags) -I  ../../lab1_1.5 -c $<

matrix: matrix.o
	g++ $(cflags) -o $@ $<


clean:
	touch testcode.cpp
	-rm test testcode.cpp testcode *.o matrix

