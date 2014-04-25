
JAVA_PACKAGE = com.tailf.test.cdb2
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

all: setup

include ../include.mk

classpath:
	@echo $(CLASSPATH)

%.class:
	$(JAVAC) $(JFLAGS) -cp $(CLASSPATH) $*.java 

./load-dir/%.fxs:	yang/%.yang
	$(CONFDC) --fail-on-warnings --yangpath yang -c -o $@  $<
	$(CONFDC) $(JNSFLAGS)/$*.java $@
	$(CONFDC) $(CNSFLAGS)/$*.h $@

fxs: $(FXS)

c-code/%.h: yang/%.yang
	$(CONFDC) --emit-h $@ $<
INCLUDE		 = -I$(CONFD_DIR)/include
CFLAGS		 = -Wall -g $(INCLUDE)

all:	setup stop

test: 	cdbclean all
	./cdb_test.sh

setup: cdbclean $(FXS) ssh-keydir ant_compile $(CDB_DIR)


clean:	iclean
	-rm -rf java/src/com/tailf/test/cdb2/cs.java cs.c 
	-rm -rf $(FXS)
	$(MAKE)	ant_clean
	cd c-code; $(MAKE) clean

debug: 
	$(CONFD) -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i


java/src/com/tailf/test/cdb2/namespaces/%.java: %.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.cdb2.namespaces \
	--emit-java java/src/com/tailf/test/cdb2/namespaces/$*.java $*.fxs

%.o: %.c
	$(CC) -c -o $@ $< $(CFLAGS)
subscr: 
	$(CC) -o $@ $(CFLAGS) 