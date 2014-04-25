include ../include.mk

CSRC = action.c
CSRC2 = rpcrequest.c
HDRS = math2.h 
OBJS = $(CSRC:.c=.o)
OBJS2 = $(CSRC2:.c=.o)
CFLAGS = -g -Wall -I$(CONFD_DIR)/include

all:	config.fxs math2.fxs testaction.fxs mathrpc.fxs cs.fxs javagen ant_compile \
        ssh-keydir $(CDB_DIR) 

test: cdbclean all stop
	./dp_test.sh


%.h: mathrpc.yang
	$(CONFDC) --emit-h $@ $<

%.o:	%.c 
	$(CC) -c $(CFLAGS)  $<

$(OBJS): $(CSRC) $(HDRS)

action:	$(OBJS)
	$(CC) $(OBJS) $(LIBS) $(LDFLAGS) -o $@

rpcrequest: $(OBJS2)
	$(CC) $(OBJS2) $(LIBS) $(LDFLAGS)-o $@

debug: all
	${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd -i --ignore-initial-validation 


javagen: java/src/com/tailf/test/dp/action/namespaces/config.java java/src/com/tailf/test/dp/action/namespaces/testaction.java java/src/com/tailf/test/dp/action/namespaces/mathrpc.java java/src/com/tailf/test/dp/action/namespaces/cs.java

java/src/com/tailf/test/dp/action/namespaces/%.java: %.fxs
	$(CONFDC) \
	--java-disable-prefix \
	--java-package com.tailf.test.dp.action.namespaces \
	--emit-java java/src/com/tailf/test/dp/action/namespaces/$*.java $*.fxs


# ant_compile: 
# 	(cd java && ant compile)

ant_run:
	(cd java && ant run)
# ant_clean:
# 	(cd java && ant clean)

clean:	iclean ant_clean
	-rm -rf config.java
	-rm -rf java/src/com/tailf/test/dp/action/namespaces/*.java