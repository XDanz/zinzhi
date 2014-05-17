
JAVA_HOME=/usr/lib/jvm/default-java
JNI_LIBS=$(JAVA_HOME)/include
FLAGS=-shared -Wl -fPIC 

%.o: %.c
	gcc -c -fPIC -I$(JNI_LIBS) $<

alias.so: Alias.c add_alias2.c
	gcc -I$(JNI_LIBS) -o libalias.so -fPIC -shared Alias.c add_alias2.c 

libgarp.so: GArpSend.o
	gcc -o $@ $< $(FLAGS) -I$(JNI_LIBS)
