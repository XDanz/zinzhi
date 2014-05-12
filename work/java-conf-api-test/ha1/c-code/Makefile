include ../../include.mk

CFLAGS = -g -Wall -I$(CONFD_DIR)/include

EXC = ha_ctrl

all: $(EXC)

ha_ctrl: ha_ctrl.o
	$(CC) $(LIBS) $(LDFLAGS) $< -o $@

clean:
	rm -f $(EXC) *.o *.h
