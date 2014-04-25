######################################################################
# Dp6 Validation test Makefile
# 
# CONFD_DIR has to be defined
######################################################################

usage:
	@echo "make all      Build all (java/fxs) and setup cdb"
	@echo "make clean    Remove all built and intermediary files"
	@echo "make debug    Start ConfD daemon forground"
	@echo "make test     Validaton tests against confd"

include ../include.mk

all: clean stop setup

javagen: java/src/com/tailf/test/dp6/namespaces/smp.java

java/src/com/tailf/test/dp6/namespaces/smp.java: smp.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.dp6.namespaces \
	--emit-java java/src/com/tailf/test/dp6/namespaces/smp.java smp.fxs


test: setup
	./dp_test.sh

setup:	ssh-keydir $(CDB_DIR) smp.fxs javagen ant_compile

clean:	iclean ant_clean
	-rm -rf java/src/com/tailf/test/dp6/namespaces/smp.java
	-rm -rf java/*.DB

debug: all
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 