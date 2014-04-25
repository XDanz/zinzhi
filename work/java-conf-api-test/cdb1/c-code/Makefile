include ../../include.mk

CFLAGS = -g -Wall -I$(CONFD_DIR)/include

EXC = exists0

all: $(EXC)

cdb2: cdb2.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

cdbl: cdbl.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

exists0: exists0.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

clean:
	rm -f $(EXC) *.o *.h
