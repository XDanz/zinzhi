include ../include.mk


#test.class: test.java bulk.class TestCase.class

all:	setup stop	

test: 	cdbclean all
	./cdb_test.sh

testj: test.class 
	java -classpath $(CLASSPATH) test

setup: bulk.fxs aaa_cdb.fxs javagen ant_compile $(CDB_DIR) ssh-keydir 
	cp bulk_1.xml $(CDB_DIR)
	cp bulk_2.xml $(CDB_DIR)

debug: setup
	${CONFD} -c ./confd.conf -i

ant_compile:
	(cd java && ant compile)

ant_clean:
	(cd java && ant clean)

javagen:java/src/com/tailf/test/cdb3/namespaces/bulk.java

java/src/com/tailf/test/cdb3/namespaces/%.java: %.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.cdb3.namespaces \
	--emit-java java/src/com/tailf/test/cdb3/namespaces/$*.java $*.fxs

clean:	iclean ant_clean
	-rm -rf java/src/com/tailf/test/cdb3/namespaces/*.java *.h 