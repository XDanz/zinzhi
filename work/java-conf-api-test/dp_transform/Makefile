include ../include.mk


all:	ssh-keydir $(CDB_DIR) smp.fxs javagen
	(cd java && ant compile)
	cp smp.xml $(CDB_DIR)

test: all 
	./dp_test.sh


clean:	iclean
	-rm -rf smp.java
	-rm -rf java/src/com/tailf/test/dp/transform/namespaces/smp.java

javagen: java/src/com/tailf/test/dp/transform/namespaces/smp.java

java/src/com/tailf/test/dp/transform/namespaces/%.java: %.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.dp.transform.namespaces \
	--emit-java java/src/com/tailf/test/dp/transform/namespaces/$*.java $*.fxs

debug: all
	${CONFD} -i -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd

