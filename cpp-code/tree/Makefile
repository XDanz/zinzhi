# ./SEC Make file compiles all necessary src files to ../objs directory
CC = g++

INCLUDE=include
CFLAGS = -Wall -std=c++11 -I$(INCLUDE)
SRC = $(wildcard src/*.cpp)
OBJS = $(SRC:src/%.cpp=objs/%.o)

tt:
	@echo "SRC=$(SRC)"
	@echo "OBJ=$(OBJS)"

all: build

build: $(OBJS)

TAGS:
	find . -name \*.h -print -o -name \*.cpp -print | etags -

# $@ -- references to .o file, $< referneces the .cpp file
objs/%.o: src/%.cpp 
	$(CC) -c $< -I$(INCLUDE) -o $@ $(CFLAGS) -g -gdwarf-2

clean:
	rm -f objs/*.o
