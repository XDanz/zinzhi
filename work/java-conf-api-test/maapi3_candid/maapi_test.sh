#!/bin/sh

. ../include.sh

echo  "Starting the daemon"

${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " --> OK"

echo "Starting Data Provider"
(cd java && ant startprovider & >/dev/null 2>&1)
sleep 3
echo " --> OK"

echo "Starting Test"
(cd java && ant test)
res=$?
sleep 3
echo " --> OK"

${CONFD} --stop
sleep 1

exit $res

#kill -9 `ps -eo pid,args | sort | grep -i java | grep -i startprovider | awk '{print $1}'` || true

#kill -9 `ps -eo pid,args | sort | grep -i java | grep -i com.tailf.test.maapi3.SimpleProvider | awk '{print $1}'` || true

exit $res
