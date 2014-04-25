include ../include.mk


######################################################################

all: hst.fxs hst.java DbServer.class DbServerCLI.class \
	Host.class Iface.class HostDataCb.class IfaceDataCb.class \
	DbCb.class setup

test: stop cdbclean all
	./dp_run.sh	

setup:	$(CDB_DIR) ssh-keydir


######################################################################
clean: iclean
	rm -rf  hst.java

