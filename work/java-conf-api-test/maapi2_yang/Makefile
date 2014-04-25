######################################################################
# Maapi2 test Makefile
# 
# CONFD_DIR has to be defined
######################################################################

usage:
	@echo "make all      Build all"
	@echo "make clean    Remove all built and intermediary files"
	@echo "make debug    Start ConfD daemon forground"
	@echo "make stop     Stop ConfD daemon "
	@echo "make test     Maapi tests against confd"


include ../include.mk


#all:	SimpleProvider.class 

#test: all 
#	./maapi_test.sh

javagen: java/src/com/tailf/test/maapi2/namespaces/smp.java

java/src/com/tailf/test/maapi2/namespaces/smp.java: smp.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.maapi2.namespaces \
	--emit-java java/src/com/tailf/test/maapi2/namespaces/smp.java smp.fxs

clean:	iclean
	-rm -rf java/src/com/tailf/test/maapi2/namespaces/smp.java

all:	setup stop

test: 	cdbclean all
	./maapi_test.sh

testj: setup ant_compile
	(cd java && ant test)

testj_nocand: test_nocand.class
	java -classpath $(CLASSPATH) test_nocand

setup: ssh-keydir javagen $(CDB_DIR) ant_compile


debug: setup
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 

debug2: setup
	${CONFD} -d -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 


startcb: setup
	(cd java && ant startcb)

