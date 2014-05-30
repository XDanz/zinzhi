# Change these
cxxtestpath = /usr/bin/
includes = /usr/include/cxxtest
testcode    = example_test.h

testcode.cpp: $(testcode)
	python $(cxxtestpath)cxxtestgen.py --error-printer -o testcode.cpp $(testcode)

testcode.o: testcode.cpp
	g++ -I $(includes) -c $<

runtest0%: testcode.o
	g++ -I $(cxxtestpath) -o runtest0$* testcode.o Matrix0$*.o

clean:
	touch runtest01 testcode.cpp
	rm runtest0* testcode.cpp

