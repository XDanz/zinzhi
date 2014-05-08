
JAVA_HOME=/usr/lib/jvm/default-java
JNI_LIBS=$(JAVA_HOME)/include
FLAGS=-shared -Wl -f 

Alias.o: Alias.c
	gcc -c -I$(JNI_LIBS) $@

alias.so: Alias.c add_alias2.c
	gcc -I$(JNI_LIBS) -o libalias.so -fPIC -shared Alias.c add_alias2.c 
