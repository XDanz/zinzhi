#!/bin/sh

. ../include.sh

echo "Starting ConfD \n"


${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd --ignore-initial-validation 

[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " ConfD Started --> OK"

echo  "Starting Data Provider"

(cd java && ant startcb >/dev/null &)
[ $? -eq 0 ] || exit 1
sleep 3
echo " --> OK"

echo -n "Starting Dp Validation Tests "

(cd java && ant test)
[ $? -eq 0 ] || exit 1
sleep 3
echo " --> OK"
res=$?

${CONFD} --stop
sleep 1

# kill -9 `ps -eo pid,args | sort | grep -i java | grep -i startcb | awk '{print $1}'` || true

# kill -9 `ps -eo pid,args | sort | grep -i java | grep -i com.tailf.test.dp2.SimpleWithTrans | awk '{print $1}'` || true

exit $res
