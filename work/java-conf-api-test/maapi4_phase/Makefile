include ../include.mk

#CSRC = action.c
#HDRS = cs.h ncs.h
#OBJS = $(CSRC:.c=.o)
#CFLAGS = -g -Wall -I$(CONFD_DIR)/include

CONFD_FLAGS=--start-phase0

all:	setup stop ant_compile

test: 	cdbclean all 
	./maapi4_test.sh

testj:
	ant run

setup: $(CDB_DIR) copy

confd_wait:
	$(CONFD) --wait-phase0
start_confd: 
	$(CONFD) -c ./confd_maapitest.conf $(CONFD_FLAGS)

stop_confd:
	$(CONFD) --stop || true


#cs.java: cs.fxs
#	$(CONFDC) \
#	--java-disable-prefix \
#	--java-package com.tailf.test.maapi.phases.namespaces \
#	--emit-java src/com/tailf/test/maapi/phases/namespaces/cs.java cs.fxs
copy:
	rm -rf confd-cdb/*.cdb
	rm -rf rollback/*
	cp rollback[0-1] rollback

debug: setup
	${CONFD} --addloadpath $(CONFD)/etc/confd -c ./confd_maapitest.conf -i

clean:	iclean
	-rm -rf src/com/tailf/test/maapi/phases/cs.java src/com/tailf/maapi/phases/namespaces/mtest.java *.h cs.c ant clean

ant_compile: 
	ant compile

ant_run:
	ant run

ant_run2:
	ant run2


