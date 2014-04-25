include ../include.mk


all:	smp.fxs smp.java 
	$(JAVAC) -classpath $(CLASSPATH) *.java

test: all 
	java -classpath $(CLASSPATH):. CtlTest


clean:	iclean
	-rm -rf smp.java
