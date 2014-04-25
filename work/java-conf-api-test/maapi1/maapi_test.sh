#!/bin/sh

. ../include.sh


killall action


echo -n "starting the daemon with candidate..."

${CONFD} -c ./confd_maapitest.conf --addloadpath ${CONFD_DIR}/etc
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " ok"

./action -D &


java -classpath ${CLASSPATH}:. test

res=$?

${CONFD} --stop
sleep 1

[ $res -gt 0 ] && exit $res


echo -n "starting the daemon without candidate..."

rm -rf confd-cdb/*.cdb
rm -rf rollback/*
cp rollback[0-1] rollback
cp mtest.xml ${CDB_DIR}
cp ${CONFD_DIR}/var/confd/cdb/aaa_init.xml ${CDB_DIR}

${CONFD} -c ./confd_maapitest_nocand.conf 
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " ok"

./action -D &

java -classpath ${CLASSPATH}:. test_nocand

res=$?

${CONFD} --stop
sleep 1

exit $res

