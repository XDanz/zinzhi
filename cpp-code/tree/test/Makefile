# ./SEC Make file compiles all necessary src files to ../objs directory
CC = g++

INCLUDE=../include
CFLAGS = -Wall -std=c++11
SRC = $(wildcard *.cpp)
OBJS = ../objs/tree.o test.o

all: build

build: $(OBJS) 

TAGS:
	find . -name \*.h -print -o -name \*.cpp -print | etags -

# $@ -- references to .o file, $< referneces the .cpp file
%.o: %.cpp 
	$(CC) -c $< -I$(INCLUDE) -o $@ $(CFLAGS)

../objs/tree.o: ../src/tree.o
1	$(MAKE) -C ../src build

test: $(OBJS)
	$(CC) $(CFLAGS) $(OBJS) -o $@	

clean:
	rm -f ../objs/*.o
