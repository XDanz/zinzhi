include ../include.mk

all: hello

hello: hello.o $(CSAPPLIB)
	$(CC) -o $@ $< $(LIBS)
