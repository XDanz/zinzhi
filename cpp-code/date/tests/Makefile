# ./tests Make file
CC = g++
CFLAGS = -Wall -std=c++11 -I../../includes
SRC = $(wildcard *.cpp)
DIRS = test1 experiment

all: build test

build:
	@echo " **** Building tests! **** ";
	@for i in $(DIRS); do \
		echo " XBuild in $${i}!"; \
		$(MAKE) -C ./$${i} test_run || exit 1; \
	done

test:
	@echo " **** Running test **** ";
	@for i in $(DIRS); do \
		echo "Run test in $${i}"; \
		$(MAKE) -C ./$${i} test || exit 1; \
	done

../../objs/%.o:
	make -C ../../src all
# $@ -- references to .o file, $< referneces the .cpp file

clean:
	@echo " **** Cleaning tests **** ";
	@for i in $(DIRS); do \
		echo "XX Cleaning $${i}"; \
		$(MAKE) -C ./$${i} clean || exit 1; \
	done
