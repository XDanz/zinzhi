INCLUDE		 = -I$(CSAPP_DIR)/include
CSAPPLIB	 = $(CSAPP_DIR)/src/csapp.o
LIBS 		+= -lpthread
CFLAGS		 = -Wall -g $(INCLUDE)

CC 		 = gcc

%.o: %.c
	$(CC) -c -o $@ $< $(CFLAGS)
