CC=g++
CFLAGS=-std=c++11 -Wall

# These are the pattern matching rules. In addition to the automatic
# variables used here, the variable $* that matches whatever % stands for
# can be useful in special cases.
#
# $< - refers to the first prerequisite
# $@ - refers to the target

%.o: %.cpp
	$(CC) $(CFLAGS) -c $< -o $@
%: %.cpp
	$(CC) $(CFLAGS) -o $@ $<

clean:
	rm -f .depend *.o hello

.PHONY: clean


