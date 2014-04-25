include ../include.mk

CSRC = action.c
HDRS = cs.h
OBJS = $(CSRC:.c=.o)
CFLAGS = -g -Wall -I$(CONFD_DIR)/include


%.h: %.fxs
	$(CONFDC) --emit-h $*.h  $<


%.o:	%.c 
	$(CC) -c $(CFLAGS)  $<

$(OBJS): $(CSRC) $(HDRS)

action:	$(OBJS)
	$(CC) $(OBJS) $(LIBS) $(LDFLAGS)-o $@

test.class: test.java mtest.class cs.class TestCase.class

test_nocand.class: test_nocand.java mtest.class TestCase.class cs.class

all:	test.class test_nocand.class setup stop

test: 	cdbclean all
	./maapi_test.sh

testj: test.class 
	java -classpath $(CLASSPATH) test

testj_nocand: test_nocand.class
	java -classpath $(CLASSPATH) test_nocand

setup: ssh-keydir mtest.fxs cs.fxs mtest.class cs.class action $(CDB_DIR) copy

copy:
	rm -rf confd-cdb/*.cdb
	rm -rf rollback/*
	cp rollback[0-1] rollback
	cp mtest.xml $(CDB_DIR)
	cp $(CONFD_DIR)/var/confd/cdb/aaa_init.xml $(CDB_DIR)

debug: setup
	${CONFD} -c ./confd_maapitest.conf -i

debug_nocand: setup
	${CONFD} -c ./confd_maapitest_nocand.conf -i


clean:	iclean
	-rm -rf cs.java mtest.java *.h cs.c
