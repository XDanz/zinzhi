# ./src Make file compiles all necessary src files to ../objs directory
CC = g++
CFLAGS = -Wall -std=c++11 -I../includes
SRC = $(wildcard *.cpp)
OBJS = $(SRC:%.cpp=../objs/%.o)

all: build

build: $(OBJS)

tt:
	@echo "SRC=$(SRC)"
	@echo "objs=\"$(OBJS)\""


# $@ -- references to .o file, $< referneces the .cpp file
../objs/%.o: %.cpp ../includes/%.h
	$(CC) -c $< -I../includes -o $@ $(CFLAGS)

clean:
	rm -f objs/*.o
