CC = gcc
CFLAGS = -Wall -std=gnu99 -Wdeclaration-after-statement
INCLUDE=../include
LIB=../lib/libmmalloc.a

SRC = $(shell ls *.c)
OBJS = $(SRC:.c=.o)
EXEC = $(OBJS:.o=)

all: $(OBJS)

debug:
	$(MAKE) "CFLAGS+= -g3" clean all

%.o: %.c
	$(CC) -I$(INCLUDE) -c -o $@ $< $(LIB) $(CFLAGS) 

%: %.o
	$(CC) -I$(INCLUDE) -o $@ $< $(LIB) $(CFLAGS) 

clean:
	rm -f *~ *.o $(EXEC)


