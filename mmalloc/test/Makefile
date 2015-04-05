CC = gcc
CFLAGS = -Wall -Wdeclaration-after-statement
INCLUDE=../include
LIB=../lib/libmalloc.a

SRC = $(shell ls *.c)
OBJS = $(SRC:.c=.o)
EXEC = $(OBJS:.o=)

all: $(OBJS)

%.o: %.c
	$(CC) -I$(INCLUDE) -c -o $@ $< $(LIB) $(CFLAGS) 

%: %.o
	$(CC) -I$(INCLUDE) -o $@ $< $(LIB) $(CFLAGS) 

clean:
	rm -f *~ *.o $(EXEC)


