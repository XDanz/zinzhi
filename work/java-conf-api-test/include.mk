CONFD=$(CONFD_DIR)/bin/confd
CONFDC=$(CONFD_DIR)/bin/confdc
INCLUDE=$(CONFD_DIR)/include
VRSN=$(shell ls $(CONFD_DIR)/java/jar/conf-api-[0-9]*)
JARFILE=$(VRSN)
# JARFILE=$(CONFD_DIR)/java/jar/conf-api-4.0.0.jar

# We need specific paths to aspectjrt and log4j

LOG4J=$(CONFD_DIR)/java/jar/log4j-1.2.16.jar
JUNIT=$(CONFD_DIR)/java/jar/junit-4.8.2.jar

CLASSPATH=$(JARFILE):$(LOG4J):$(JUNIT):.

JAVAC=javac
JAVA=java
NETCONF_CONSOLE_TCP=$(CONFD_DIR)/bin/netconf-console-tcp
CDB_DIR   = ./confd-cdb 
LIBS = -lm -lpthread $(CONFD_DIR)/lib/libconfd.a

CC = cc
TAR = tar

ifeq ($(shell uname -s | tr '[:upper:]' '[:lower:]'),freebsd)
TAR = gtar
endif
ifeq ($(shell uname -s | tr '[:upper:]' '[:lower:]'),netbsd)
TAR = gtar
endif
ifeq ($(shell uname -s | tr '[:upper:]' '[:lower:]'),sunos)
TAR = gtar
CC = gcc
LIBS = -lm -lpthread -lsocket -lnsl $(CONFD_DIR)/lib/libconfd.a
endif

ssh-keydir:
	@ln -s $(CONFD_DIR)/etc/confd/ssh $@

$(CDB_DIR):
	@mkdir -p $(CDB_DIR) 2>/dev/null || true
	@cp $(CONFD_DIR)/var/confd/cdb/aaa_init.xml $(CDB_DIR)

aaa_cdb.fxs: aaa.xso
	@$(CONFD_DIR)/bin/confdc  -l -o $@  $<

aaa.xso: $(CONFD_DIR)/src/confd/aaa/aaa.cs
	@$(CONFDC) -c $<

%.class:	%.java
	$(JAVAC) -classpath $(CLASSPATH)  $<

%.fxs: %.yang
	$(CONFDC) -c -o $*.fxs  $<

%.java: %.fxs
	$(CONFDC) --emit-java $*.java $<


iclean:	
	@rm -rf *.o *.a *.xso *.fxs *.xsd *.log *.class *.db *.ccl \
		host.key host.cert *_proto.h running.invalid global.data \
                *.prep *.diff *.DB \
		aaa_cdb.* $(CDB_DIR) ssh-keydir etc

stop:
	@$(CONFD) --stop 2>&1 > /dev/null || true

cdbclean:
	@rm -rf $(CDB_DIR)
	@rm -rf *.prep *.DB  *.diff \
             running.invalid global.data *.db *.log


ant_compile:
	@(cd java && ant compile)

ant_clean:
	(cd java && ant clean)
