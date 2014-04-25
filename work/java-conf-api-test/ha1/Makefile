include ../include.mk


test: all 
	./ha_test.sh


all: smp.fxs ant_compile \
            ssh-keydir

javagen: java/src/com/tailf/test/dp6/namespaces/smp.java

java/src/com/tailf/test/ha1/namespaces/smp.java: smp.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.ha1.namespaces \
	--emit-java java/src/com/tailf/test/ha1/namespaces/smp.java smp.fxs

clean:	iclean ant_clean
	-rm -rf java/src/com/tailf/ha1/namespaces/smp.java node0/*.log node0/*.db node0/*.data \
	node0/confd-cdb/*.cdb node1/*.log node1/*.db node1/*.data \
	node1/confd-cdb/*.cdb node2/*.log node2/*.db node2/*.data \
	node2/confd-cdb/*.cdb

ha_stop:
	./ha_stop.sh


node_start0: all
	cd node0; \
	sname=node0 CONFD_IPC_PORT=4565 $(CONFD) \
	$${CONFD_FLAGS} \
		-c confd.conf --addloadpath $(CONFD_DIR)/etc/confd; 

node_start1: all
	cd node1; \
	sname=node1 CONFD_IPC_PORT=4575 $(CONFD) \
	$${CONFD_FLAGS} \
		-c confd.conf --addloadpath $(CONFD_DIR)/etc/confd; 

node_start2: all
	cd node2; \
	sname=node2 CONFD_IPC_PORT=4585 $(CONFD) \
	$${CONFD_FLAGS}	\
		-c confd.conf --addloadpath $(CONFD_DIR)/etc/confd;

