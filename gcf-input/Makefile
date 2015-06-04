CC = gcc
CFLAGS = -Wall -std=gnu99 -Wdeclaration-after-statement
LIBS = $(shell mysql_config --libs)
CFLAGS += $(shell mysql_config --cflags)

SRC=$(wildcard ls src/*.c)
OBJS = $(SRC:src/%.c=bin/%.o)
# OBJS = filldb.o read_bdt.o read_bdui.o

all: filldb

test:
	echo $(OO)

debug: clean
	$(MAKE) "CFLAGS+= -g -gdwarf-2 -g3 -DDEBUG" all

bin/%.o: src/%.c
	$(CC) -c $< $(CFLAGS) -Iinclude -o $@

filldb: $(OBJS)
	$(CC) -o	 $@  $(OBJS) $(LIBS)

clean:
	rm -f *~ $(OBJS) filldb


