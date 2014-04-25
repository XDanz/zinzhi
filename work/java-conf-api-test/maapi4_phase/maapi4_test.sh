#!/bin/sh

. ../include.sh


killall action


echo -n "Starting CONFD to Phase0..."

${CONFD} --addloadpath ${CONFD_DIR}/etc/confd -c ./confd_maapitest.conf --start-phase0
[ $? -eq 0 ] || exit 1
${CONFD} --wait-phase0
echo " ok"

ant run

res=$?

${CONFD} --stop
sleep 1

[ $res -gt 0 ] && exit $res


echo -n "Starting CONFD to Phase0..."

rm -rf confd-cdb/*.cdb
rm -rf rollback/*
cp rollback[0-1] rollback
cp mtest.xml ${CDB_DIR}
cp ${CONFD_DIR}/var/confd/cdb/aaa_init.xml ${CDB_DIR}

${CONFD} --addloadpath ${CONFD_DIR}/etc/confd -c ./confd_maapitest.conf --start-phase0
[ $? -eq 0 ] || exit 1
${CONFD} --wait-phase0
echo " ok"

ant run2

res=$?

${CONFD} --stop
sleep 1


echo -n "Starting CONFD ..."

[ $res -gt 0 ] && exit $res


${CONFD} --addloadpath ${CONFD_DIR}/etc/confd -c ./confd_maapitest.conf 
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo " ok"

ant run3

res=$?

${CONFD} --stop
sleep 1

exit $res

