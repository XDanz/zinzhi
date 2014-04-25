
include ../include.mk


test:	istop all setup test_cases

all:	notif.fxs  notif.java  Notifier.class CmdReader.class setup

setup:	$(CDB_DIR) aaa_cdb.fxs ssh-keydir 

clean:	iclean

istop:	stop
	@killall netconf-console-tcp 2> /dev/null  || true

query:
	$(CONFD_DIR)/bin/netconf-console-tcp --get -x netconf

subscribe:
	$(CONFD_DIR)/bin/netconf-console-tcp -s raw sub.xml

replay:
	$(CONFD_DIR)/bin/netconf-console-tcp -s raw replay.xml

######################################################################
# Test Cases
#

test_cases:   t1


# test subscribe on stream
t1:  istop setup
	$(CONFD) -c confd.conf
	$(JAVA) -classpath $(CLASSPATH) Notifier s i s d s y s u s q &
	$(CONFD_DIR)/bin/netconf-console-tcp -s raw sub.xml > t1.log &
	sleep 15
	$(CONFD) --stop			
	cat t1.log
	grep linkDown t1.log > tmp
	test -s tmp	
	grep linkUp t1.log > tmp
	test -s tmp	
	# OK


	#----- Testing Replay Notification Streams  -----
t2:  istop
	$(CONFD) -c confd.conf
	$(JAVA) -classpath $(CLASSPATH) Notifier s i s d s y s u s q &
	$(CONFD_DIR)/bin/netconf-console-tcp -s raw replay.xml > t2.log &
	sleep 15
	$(CONFD) --stop			
	cat t2.log
	grep linkDown t2.log > tmp
	test -s tmp	
	grep linkUp t2.log > tmp
	test -s tmp	
	grep replayComplete t2.log > tmp
	test -s tmp	
	# OK

