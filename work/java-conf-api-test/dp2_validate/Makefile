######################################################################
# Dp2 Validation test Makefile
# 
# CONFD_DIR has to be defined
######################################################################

include ../include.mk


usage:
	@echo "make all      Build all (java/fxs) and setup cdb"
	@echo "make clean    Remove all built and intermediary files"
	@echo "make debug    Start ConfD daemon forground"
	@echo "make test     Validaton tests against confd"


all:	setup

test: cdbclean all stop
	./dp_test.sh

setup:	ssh-keydir $(CDB_DIR) ant_compile smp.fxs cleanjavadb

javagen: java/src/com/tailf/test/dp2/namespaces/smp.java

java/src/com/tailf/test/dp2/namespaces/smp.java: smp.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.dp2.namespaces \
	--emit-java java/src/com/tailf/test/dp2/namespaces/smp.java smp.fxs

clean:	iclean ant_clean cleanjavadb
	rm -rf 

cleanjavadb:
	rm -rf java/running.DB

debug: setup java/src/com/tailf/test/dp2/namespaces/smp.java
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 

ant_compile: javagen
	(cd java && ant compile)

ant_clean:
	(cd java && ant clean)