CC=g++
CFLAGS=-std=c++11 -Wall

%.o: %.cpp
	@echo ">> compiling $<"
	$(CC) $(CFLAGS) -c $< 

.PHONY: clean

clean:
	@-rm -rf *.o
