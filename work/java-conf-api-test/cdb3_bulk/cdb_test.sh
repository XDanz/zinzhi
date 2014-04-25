#!/bin/sh

. ../include.sh

mkdir -p /tmp/confd.cctest


echo -n "starting the daemon"

${CONFD} -c ./confd.conf 
[ $? -gt 0 ] && exit 1
${CONFD} --wait-started
echo " ok"

# java -classpath ${CLASSPATH}:. test
(cd java && ant test)
res=$?

${CONFD} --stop
sleep 1

exit $res
