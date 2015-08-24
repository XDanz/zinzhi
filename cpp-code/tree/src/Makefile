# ./SEC Make file compiles all necessary src files to ../objs directory
CC = g++

INCLUDE=../include
CFLAGS = -Wall -std=c++11 -I$(INCLUDE)
SRC = $(wildcard *.cpp)
OBJS = $(SRC:%.cpp=../objs/%.o)

all: build

build: $(OBJS)

TAGS:
	find . -name \*.h -print -o -name \*.cpp -print | etags -

# $@ -- references to .o file, $< referneces the .cpp file
../objs/%.o: %.cpp 
	$(CC) -c $< -I$(INCLUDE) -o $@ $(CFLAGS)

clean:
	rm -f ../objs/*.o
