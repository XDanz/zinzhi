######################################################################
# Maapi1 Yang test Makefile
# 
# CONFD_DIR has to be defined (source env.sh)
#
#
# To debug manually:
# 0.) Start confd in fg
#   > make debug
# In another terminal:
# 1.) Start callback 
# >cd java && ant startcb
# In another terminal:
# 2.) Start action   
# >./action
#
# 3.) cd java && ant test (-Dfrom=<start> -Dto=<end>)
#  -Dfrom=<start> test starts from 0 
#  -Dto=<to>  to (exclusive)
#
# for example:
#  > ant test -Dfrom=0 -Dto=3 will run test 0 to 2
#
######################################################################

usage:
	@echo "make all      Build all"
	@echo "make clean    Remove all built and intermediary files"
	@echo "make debug    Start ConfD daemon in foreground"
	@echo "make stop     Stop ConfD daemon "
	@echo "make test     Maapi tests against confd"

include ../include.mk

JAVA_PACKAGE = com.tailf.test.maapi1
JDIR = $(shell echo $(JAVA_PACKAGE) | sed 's/\./\//g')
NS  = namespaces

SRC = $(wildcard yang/*.yang)
FXS = $(SRC:yang/%.yang=./load-dir/%.fxs)

JNSFLAGS = --java-disable-prefix \
         --exclude-enums \
         --fail-on-warnings \
         --java-package $(JAVA_PACKAGE).$(NS) \
         --emit-java java/src/$(JDIR)/namespaces

CNSFLAGS = --emit-h c-code

CLASSPATH= java/src:$(LOG4J):$(JUNIT):$(JARFILE)

%.class:
	$(JAVAC) $(JFLAGS) -cp $(CLASSPATH) $*.java 

./load-dir/%.fxs:	yang/%.yang
	$(CONFDC) --fail-on-warnings --yangpath yang -c -o $@  $<
	$(CONFDC) $(JNSFLAGS)/$*.java $@
	$(CONFDC) $(CNSFLAGS)/$*.h $@

fxs: $(FXS)

c-code/%.h: yang/%.yang
	$(CONFDC) --emit-h $@ $<

# test.class: test.java mtest.class cs.class TestCase.class

# test_nocand.class: test_nocand.java mtest.class TestCase.class cs.class

all:	setup stop

test: 	cdbclean all
	./maapi_test.sh

testj: setup ant_compile
	(cd java && ant test)

gram:
	java -classpath $(CLASSPATH):java/build/classes com.tailf.conf.dbg.XPathAbrevGrammar

setup: ssh-keydir $(FXS) $(CDB_DIR) copy c-code ant_compile

c-code: 
	cd c-code; $(MAKE) all
copy:
	rm -rf confd-cdb/*.cdb
	rm -rf rollback/*
	cp rollback[0-1] rollback
	cp cfg-init/*.xml $(CDB_DIR)
	cp $(CONFD_DIR)/var/confd/cdb/aaa_init.xml $(CDB_DIR)

debug: setup
	${CONFD} -c ./confd_maapitest.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 

debug2: setup
	${CONFD} -d -c ./confd_maapitest.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 	

debug_nocand: setup
	${CONFD} -c ./confd_maapitest_nocand.conf -i

clean:	iclean
	-rm -rf java/src/com/tailf/test/maapi1/namespaces/*.java
	-rm load-dir/*.fxs || true
	 cd c-code; $(MAKE) clean
	 cd java; ant clean
	 -rm -rf logs/*

startcb:
	(cd java && ant startcb & >/dev/null 2>&1)
	./c-code/action -D &

