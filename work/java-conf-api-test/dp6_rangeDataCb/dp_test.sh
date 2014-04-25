#!/bin/sh

. ../include.sh


killall action 2> /dev/null



echo -n "Starting the daemon"

${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd --ignore-initial-validation 
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " --> OK"

echo "Starting Data Provider 1"
#java -classpath ${CLASSPATH}:. SimpleWithTrans1 &
(cd java && ant startcb1 >/dev/null &) 
[ $? -eq 0 ] || exit 1
sleep 3
echo " --> OK"

echo "Starting Data Provider 2"
#java -classpath ${CLASSPATH}:. SimpleWithTrans2 &
(cd java && ant startcb2 >/dev/null &)
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

#kill -9 `ps -eo pid,args | sort | grep -i java | grep -i SimpleWithTrans1 | awk '{print $1}'` || true

#kill -9 `ps -eo pid,args | sort | grep -i java | grep -i SimpleWithTrans2 | awk '{print $1}'` || true

exit $res
