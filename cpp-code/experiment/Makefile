# tests/test1 --
CC = g++
CXXFLAGS = -Wall -std=c++11
INCLUDES=includes
SRC = $(wildcard src/*.cpp)
#OBJS = $(SRC:src/%.cpp=objs/%.o)
  # ../../objs/date.o ../../objs/gregorian.o ../../objs/julian.o test.o
# OBJS+= ../../objs/kattistime.o
tt:
	echo "$(OBJS)"

all: $(OBJS)


test2: objs/test2.o
	echo "DEBUG=${DEBUG}"
	@echo $(OBJS)
	$(CC) $(OBJS) -o $@

debug: CXXFLAGS += -DDEBUG -g -gdwarf
debug: test

# $@ -- references to .o file, $< referneces the .cpp file
objs/%.o: $(SRC)
	$(CC) -c $< -o $@ $(CXXFLAGS) -I$(INCLUDES)

test: objs/test.o
	$(CC) -o $@ $< $(CXXFLAGS)

%.o: %.cpp
	echo "dollar-star:" $*;
	$(CC) -c $< $(CFLAGS)

clean: 
	rm -rf *.o *~
	rm -f objs/*.o
