######################################################################
# Dp9 Validation test Makefile
# 
# CONFD_DIR has to be defined
######################################################################
include ../include.mk

usage:
	@echo "make all      Build all (java/fxs) and setup cdb"
	@echo "make clean    Remove all built and intermediary files"
	@echo "make debug    Start ConfD daemon forground"
	@echo "make test     Validaton tests against confd"

all:	clean stop setup

test: setup 
	./dp_test.sh


javagen: java/src/com/tailf/test/dp9/namespaces/smp.java

java/src/com/tailf/test/dp9/namespaces/smp.java: smp.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.dp9.namespaces \
	--emit-java java/src/com/tailf/test/dp9/namespaces/smp.java smp.fxs
setup:	ssh-keydir $(CDB_DIR) smp.fxs javagen ant_compile

SimpleWithTrans.class: smp.fxs smp.java SimpleWithTrans.java Server.class \
                       SimpleTransCb.class SimpleDataCb.class test.class \
                       TestCase.class  ssh-keydir \
                        $(CDB_DIR)


clean:	iclean ant_clean
	-rm -rf java/src/com/tailf/test/dp9/namespaces/smp.java
	-rm -rf java/*.prop
	-rm -rf java/*.DB

debug: all
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 