#!/bin/sh

. ../include.sh

echo -n "starting the daemon"

rm confd-cdb/*.cdb
${CONFD} -c ./confd.conf 
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " ok"

java -classpath ${CLASSPATH}:. test

res=$?

${CONFD} --stop
sleep 1

exit $res
