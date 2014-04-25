#!/bin/sh

. ../include.sh

mkdir -p /tmp/confd.cctest


echo "starting the daemon"

${CONFD} -c ./confd.conf 
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " ok"

(cd java && ant test)
res=$?
${CONFD} --stop
sleep 1
exit $res
