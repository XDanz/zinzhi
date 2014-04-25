include ../include.mk


all:	smp.fxs smp.java  ssh-keydir $(CDB_DIR)
	$(JAVAC) -classpath $(CLASSPATH) *.java
	cp smp.xml $(CDB_DIR)

test: all 
	./dp_test.sh


clean:	iclean
	-rm -rf smp.java
