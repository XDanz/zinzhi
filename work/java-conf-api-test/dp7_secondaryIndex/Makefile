include ../include.mk


######################################################################

all:    build

test:	stop build test_cases cleanup

#build:	smp.fxs smp.java  ant_compile \
        mib_fxs TAIL-F-TEST-MIB.bin aaa_cdb.fxs  \
	cdb ssh-keydir

build:	smp.fxs javagen  ant_compile \
        mib_fxs TAIL-F-TEST-MIB.bin  \
	cdb ssh-keydir

clean:	iclean
	rm -rf java/src/com/tailf/test/dp7/namespaces/smp.java snmpa.state

javagen: java/src/com/tailf/test/dp7/namespaces/smp.java

java/src/com/tailf/test/dp7/namespaces/smp.java: smp.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.dp7.namespaces \
	--emit-java java/src/com/tailf/test/dp7/namespaces/smp.java smp.fxs

ant_compile:
	(cd java && ant compile)

debug: build
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation


######################################################################

TAIL-F-TEST-MIB.bin: TAIL-F-TEST-MIB.xfuncs TAIL-F-TEST-MIB.mib
	$(CONFDC) -c TAIL-F-TEST-MIB.mib TAIL-F-TEST-MIB.xfuncs

mib_fxs:
	cp -p $(CONFD_DIR)/etc/confd/snmp/SNMP*.fxs .
	cp -p $(CONFD_DIR)/etc/confd/snmp/smi.fxs .


cdb:	$(CDB_DIR)
	cp community_init.xml $(CDB_DIR)
	cp vacm_init.xml $(CDB_DIR)


######################################################################
# SNMP walk

COMMON_SNMP_OPTS = -m ./TAIL-F-TEST-MIB.mib -c public -v2c localhost:4000 

snmpwalk:	
	snmpwalk $(COMMON_SNMP_OPTS) enterprises 

snmptable:
	snmptable -C i $(COMMON_SNMP_OPTS) serverTable


######################################################################
# Test Cases

test_cases:	t1 t2

# Test SNMP Table order with secondaryIndex="snmp"
# ------ Test dp7_secondaryIndex/t1 ------	
t1: 	
	$(CONFD) --stop  || true
	$(CONFD) -c confd.conf --addloadpath ${CONFD_DIR}/etc/confd
	(cd java && ant startcb >/dev/null &)
	sleep 3
	snmptable -C i $(COMMON_SNMP_OPTS) serverTable >t1.log
	cat t1.log
	diff t1.log t1.log.saved > t1.diff
	$(CONFD) --stop	

# ------ Test dp7_secondaryIndex/t2 ------
t2: 	
	$(CONFD) -c confd.conf  --addloadpath ${CONFD_DIR}/etc/confd
	(cd java && ant startcb >/dev/null &)
	sleep 3
	(cd java && ant test)
	$(CONFD) --stop	

cleanup:
