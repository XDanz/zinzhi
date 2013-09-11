TEAM = Tommy_o_Daniel
VERSION = 1
HANDINDIR = /info/DD2486/sysprog08/labbar/lab3/handin

CC=cc
CFLAGS =-Wall
SRC=$(wildcard *.c)
OBJ=$(SRC:%.c=%.o)

all: proxy
	touch proxy.log
test:
	@echo $(OBJ)
start: 
	./proxy 15213

%.o: %.c
	$(CC) $(CFLAGS) -c $<

proxy: $(OBJ)
	$(CC) $(CFLAGS) $(OBJ) -o $@

handin:
	cp proxy.c $(HANDINDIR)/$(TEAM)-$(VERSION)-proxy.c

clean:
	rm -f *~ *.o proxy core *.log

