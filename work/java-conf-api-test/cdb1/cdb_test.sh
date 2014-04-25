#!/bin/sh

. ../include.sh

mkdir -p /tmp/confd.cctest

#CONFD_IPC_ACCESS_FILE=`pwd`/confd_secret_file.txt

#export CONFD_IPC_ACCESS_FILE

echo "starting the daemon"

${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd
[ $? -gt 0 ] && exit 1
${CONFD} --wait-started
echo " ok"

#java -classpath ${CLASSPATH}:. test
(cd java && ant test)

res=$?

${CONFD} --stop
sleep 1

exit $res
