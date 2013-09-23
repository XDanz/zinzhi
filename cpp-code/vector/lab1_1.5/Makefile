# SRC=$(wildcard *.cpp)
SRC=vector_run0.cpp
OBJS=$(SRC:%.cpp=%.o)

all: vector_run0

include ../include.mk

test: vector_run0
	./vector_run0

vector_run0.o: vector_run0.cpp vector.h
	@echo ">> compiling $< << "
	$(CC) $(CFLAGS) -c $<

vector_run0: vector_run0.o
	$(CC) $(CFLAGS) $(OBJS) -o $@

Vector.h:
	@echo ">> Vector.h Modified <<";


