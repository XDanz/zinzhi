# top Makefile 
CC = g++
CFLAGS = -Wall -std=c++11 -I../includes
SRC = $(wildcard src/*.cpp)
OBJS = $(SRC:src/%.cpp=objs/%.o)

build:
	@echo " XXX Compiling Sources XXX "
	$(MAKE) -C src build
	$(MAKE) -C tests build

.PHONY: build

test: build
	$(MAKE) -C tests test

.PHONY: test

tt:
	$(MAKE) -C src tt

TAGS:
	find . -name \*.h -print -o -name \*.cpp -print | etags -


# $@ -- references to .o file, $< referneces the .cpp file
%.o: %.cpp
	$(CC) -c -o $@ $< $(CFLAGS)

clean: 
	$(MAKE) -C src clean
	$(MAKE) -C tests clean
	@rm -f $(OBJS) || true
