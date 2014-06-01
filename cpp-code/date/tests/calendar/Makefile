# tests/calendar --
CC = g++
CXXGEN = cxxtestgen
CXXGENFLAGS = --error-printer -o
CFLAGS = -Wall -std=c++11
INCLUDE = ../../includes
CXX_INCLUDE = /Users/dtc/cxxtest/cxxtest-4.3
SRC = $(wildcard *.cpp)
OBJS = ../../objs/date.o ../../objs/gregorian.o ../../objs/kattistime.o ../../objs/julian.o

build: test_run calendar_test

calendar_test: calendar_test.o
	$(CC) $(CFLAGS) $< $(OBJS) -o $@ 

test_run: $(OBJS) test.o
	$(CC) $(CFLAGS) $(OBJS) test.o -o $@

test: build
	./test_run

cxxtest: calendar_test
	./calendar_test

calendar_test.cpp: calendar.cpp
	$(CXXGEN) $(CXXGENFLAGS) $@ $< 

calendar_test.o: calendar_test.cpp
	$(CC) $(CFLAGS) -I$(CXX_INCLUDE) -I$(INCLUDE) -c $<

# ../../objs/%.o:
# 	make -C ../../src all
# $@ -- references to .o file (target), $< referneces the .cpp 
#	(prerequisit) file
%.o: %.cpp ../../includes/calendar.h
	$(CC) -I$(INCLUDE) -c $< $(CFLAGS)



clean: 
	rm -rf *.o *~ test_run calendar_test calendar_test.cpp
