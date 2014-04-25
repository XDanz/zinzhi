include ../../include.mk

CFLAGS = -g -Wall -I$(CONFD_DIR)/include

EXC = action save_config test17 testoref

all: $(EXC)

action: action.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

save_config: save_config.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

test17: test17.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

testoref: testoref.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

clean:
	rm -f $(EXC) *.o *.h
