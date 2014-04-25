#!/bin/sh

. ../include.sh



echo -n "Starting the daemon"

${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " --> OK"

echo "Starting Data Provider"
#java -classpath ${CLASSPATH}:. SimpleWithTrans &
(cd java && ant startcb >/dev/null &)
[ $? -eq 0 ] || exit 1
sleep 3
echo " --> OK"

echo "Starting Test"
#java -classpath ${CLASSPATH}:. test
(cd java && ant test)
[ $? -eq 0 ] || exit 1
sleep 3
echo " --> OK"
res=$?

${CONFD} --stop
sleep 1
#kill -9 `ps -eo pid,args | sort | grep -i java | grep -i SimpleWithTrans | awk '{print $1}'` || true
exit $res
