CC=g++
CFLAGS=-std=c++11 -Wall
INCLUDE=/Users/dtc/cxxtest/cxxtest-4.3	
CXXTESTGEN=~/cxxtest/cxxtest-4.3/bin/cxxtestgen
CXXFLAGS=--error-printer
TESTS= MustFollowTest1.h
# g++ -o test_1.2b.out -I /info/cprog12/cxxtest/ 1.2b.cpp must_follow_a.o

all: test_1.2b

test_1.2b.cpp: test_must_follow_a.cpp
	$(CXXTESTGEN) $(CXXFLAGS) -o $@ $<

test_1.2b: test_1.2b.cpp must_follow_a.o
	$(CC) $(CFLAGS) -o $@ -I $(INCLUDE) $< must_follow_a.o

must_follow_a.o: must_follow_a.cpp must_follow_a.h
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	-rm test_1.2b.cpp test_1.2b *.o



