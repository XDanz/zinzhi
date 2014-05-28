CC=g++
CFLAGS=-std=c++11 -Wall
OBJS = $(shell ls *.cpp)
PROG = hello

# These are the pattern matching rules. In addition to the automatic
# variables used here, the variable $* that matches whatever % stands for
# can be useful in special cases.
#
# $< - refers to the first prerequisite
# $@ - refers to the target

all: $(PROG)
	echo "in all '$*'=" $*

%.o: %.cpp
	echo "'$*'=" $*
	$(CC) $(CFLAGS) -c $< -o $@

$(PROG): $(OBJS)
	echo "$*=" $*
	$(CC) $(CFLAGS) -o $@ $<

clean:
	rm -f .depend *.o hello

.PHONY: clean


