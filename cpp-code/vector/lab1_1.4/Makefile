SRC=vector_run0.cpp vector.cpp
OBJS=$(SRC:%.cpp=%.o)

all: vector_run0

include ../include.mk

vector_run0: vector.o vector_run0.o vector.h
	$(CC) -o $@ $(OBJS)

vector: vector.o vector.h
	$(CC) $< -o $@

test:
	./vector_run0



