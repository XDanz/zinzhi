include ../../include.mk
SRC=$(wildcard *.c)
CFLAGS = -g -Wall -I$(CONFD_DIR)/include

EXC = $(%.c: %)

t:

all: $(EXC)
	echo $(SRC)

cdb_get_mod: cdb_get_mod.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

subscr: subscr.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

twophase: twophase.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

cdb_get_moditer: cdb_get_moditer.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

%.o: %.c
	$(CC) -I$(INCLUDE) -c $< 
	@echo $@

clean:
	rm -f $(EXC) *.o *.h
