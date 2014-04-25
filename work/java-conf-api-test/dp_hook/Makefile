include ../include.mk


all:	ssh-keydir $(CDB_DIR) smp.fxs javagen
	(cd java && ant compile)
	cp smp.xml $(CDB_DIR)

test: all 
	./dp_test.sh


clean:	iclean
	(cd java && ant clean)
	-rm -rf java/src/com/tailf/test/dphook1/namespaces/smp.java


javagen: java/src/com/tailf/test/dphook1/namespaces/smp.java

java/src/com/tailf/test/dphook1/namespaces/smp.java: smp.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.dphook1.namespaces \
	--emit-java java/src/com/tailf/test/dphook1/namespaces/smp.java smp.fxs

debug: all
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation


