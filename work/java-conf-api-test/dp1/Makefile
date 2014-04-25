######################################################################
# Dp1 test Makefile
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

setup:	ssh-keydir $(CDB_DIR) smp.fxs javagen ant_compile cleanjavadb

javagen: java/src/com/tailf/test/dp1/namespaces/smp.java

java/src/com/tailf/test/dp1/namespaces/smp.java: smp.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.dp1.namespaces \
	--emit-java java/src/com/tailf/test/dp1/namespaces/smp.java smp.fxs

clean:	iclean ant_clean cleanjavadb
	rm -rf java/src/com/tailf/test/dp1/namespaces/smp.java

cleanjavadb:
	rm -rf java/running.DB

debug: setup
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 

# ant_compile: smp.java
# 	(cd java && ant compile)

ant_clean:
	(cd java && ant clean)