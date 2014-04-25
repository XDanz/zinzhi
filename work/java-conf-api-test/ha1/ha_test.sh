#!/bin/sh

. ../include.sh



echo -n "Starting the daemon0"
cd node0
sname=node0
export sname
${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd
[ $? -eq 0 ] || exit 1
CONFD_IPC_PORT=4565
export CONFD_IPC_PORT
${CONFD} --wait-started
echo " --> OK"
cd ..

echo -n "Starting the daemon1"
cd node1
sname=node1
${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd
[ $? -eq 0 ] || exit 1
CONFD_IPC_PORT=4575
${CONFD} --wait-started
echo " --> OK"
cd ..

echo -n "Starting the daemon2"
cd node2
sname=node2
${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd
[ $? -eq 0 ] || exit 1
CONFD_IPC_PORT=4585
${CONFD} --wait-started
echo " --> OK"
cd ..


echo "Starting Test"
#java -classpath ${CLASSPATH}:. test
(cd java && ant test)
[ $? -eq 0 ] || exit 1
sleep 3
echo " --> OK"
res=$?

sh ha_stop.sh

sleep 1

exit $res
