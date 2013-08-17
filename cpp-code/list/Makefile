CC = g++

TESTS = test0

all: test0

test0: test0.o
	$(CC) $< -o $@
test1: test1.o
	$(CC) $< -o $@

test: all
	./test0
	./test1

run_test1: test1
	./test1
