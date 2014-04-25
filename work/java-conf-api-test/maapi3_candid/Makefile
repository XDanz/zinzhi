include ../include.mk

all:	setup stop

javagen: java/src/com/tailf/test/maapi3/namespaces/smp.java

java/src/com/tailf/test/maapi3/namespaces/smp.java: smp.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.maapi3.namespaces \
	--emit-java java/src/com/tailf/test/maapi3/namespaces/smp.java smp.fxs

clean:	iclean
	-rm -rf java/src/com/tailf/test/maapi3/namespaces/smp.java

test: 	cdbclean all
		./maapi_test.sh

testj: setup ant_compile
	(cd java && ant test)

setup: ssh-keydir javagen $(CDB_DIR) copy ant_compile

copy:
	rm -rf confd-cdb/*.cdb
	cp $(CONFD_DIR)/var/confd/cdb/aaa_init.xml $(CDB_DIR)

debug: setup
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 

debug2: setup
	${CONFD} -d -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 


ant_provider: setup
	(cd java && ant startcb)

