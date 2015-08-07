# tests/test1 --
CC = g++
CFLAGS = -Wall -std=c++11
INCLUDES=includes
SRC = $(wildcard src/*.cpp)
OBJS = $(SRC:src/%.cpp=objs/%.o)
  # ../../objs/date.o ../../objs/gregorian.o ../../objs/julian.o test.o
# OBJS+= ../../objs/kattistime.o
tt:
	echo "$(OBJS)"

all: $(OBJS)


test_run: $(OBJS)
	$(CC) $(OBJS) -o $@

# $@ -- references to .o file, $< referneces the .cpp file
objs/%.o: $(SRC)
	$(CC) -c $< -o $@ $(CFLAGS) -I$(INCLUDES)

test: objs/test.o
	$(CC) -o $@ $< $(CFLAGS)

%.o: %.cpp
	$(CC) -c $< $(CFLAGS)

clean: 
	rm -rf *.o *~
