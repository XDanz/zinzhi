#!/bin/sh

. ../include.sh


killall action


echo  "starting the daemon with candidate..."

${CONFD} -c ./confd_maapitest.conf --addloadpath ${CONFD_DIR}/etc/confd --ignore-initial-validation 
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " ok"

echo "starting providers..."
./action -D &

(cd java && ant startcb & >/dev/null 2>&1) 
echo " ok"
sleep 2

echo  "starting Tests..."
(cd java && ant test)

res=$?
(cd java && ant extra)
res2=$?

${CONFD} --stop
sleep 1

[ $res -gt 0 ] && exit $res

[ $res2 -gt 0 ] && exit $res2

exit $res

