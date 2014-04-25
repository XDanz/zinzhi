#!/bin/sh

. ../include.sh

CONFD_IPC_ACCESS_FILE=`pwd`/confd_secret_file.txt
export CONFD_IPC_ACCESS_FILE


echo "Starting the daemon "

${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " --> OK"

echo "Starting Data Provider "

(cd java && ant startcb  >/dev/null &)
#[ $? -eq 0 ] || exit 1
echo " --> OK"
sleep 3

echo "Starting Test"
#echo `pwd`
(cd java && ant test)
#[ $? -eq 0 ] || exit 1
res=$?
sleep 3
echo " --> OK"
res=$?

${CONFD} --stop
sleep 1

exit $res
