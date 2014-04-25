include ../include.mk

JAVA_PACKAGE = com.tailf.test.cdb1
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

CFLAGS = -g -Wall -I$(CONFD_DIR)/include


c-code/%.h: yang/%.yang
	$(CONFDC) --emit-h $@ $<

c-code: 
	cd c-code; $(MAKE) all

./load-dir/%.fxs:	yang/%.yang
	$(CONFDC) --fail-on-warnings --yangpath yang -c -o $@  $<
	$(CONFDC) $(JNSFLAGS)/$*.java $@
	$(CONFDC) $(CNSFLAGS)/$*.h $@

fxs: $(FXS)

%.h: %.yang
	$(CONFDC) --emit-h $@ $<

all:	setup stop	

test: 	cdbclean all
	./cdb_test.sh

testj: 
	(cd java && ant test)

setup: $(FXS) ant_compile \
	 $(CDB_DIR) ssh-keydir
	cp cfg-init/mtest.xml $(CDB_DIR)

debug: setup
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i


clean:	iclean
	(cd java && ant clean)
	-rm -rf java/src/com/tailf/test/cdb1/namespaces/*.java
	-rm load-dir/*.fxs
	cd c-code; $(MAKE) clean

