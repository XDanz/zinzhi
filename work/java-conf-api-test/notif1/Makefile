include ../include.mk


all:	test.class cs.fxs cs.class $(CDB_DIR) aaa_cdb.fxs ssh-keydir

test: all stop
	./notif_test.sh

test.class: test.java cs.class TestCase.class TestMaapi.class

clean:	iclean
	-rm -rf cs.java
