#!/bin/sh

. ../include.sh

fail() {
    ${CONFD} --stop
    echo "** $1"
    exit 1
}


${CONFD} --stop > /dev/null 2>&1
${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd
[ $? -eq 0 ] || exit 1
${CONFD} --wait-started



echo "Starting Data Provider"
#java -classpath ${CLASSPATH}:. SimpleTransform  > /dev/null 2>&1 &
(cd java && ant startcb > /dev/null &)

#exit 0
sleep 10

{ ${CONFDCLI} << EOF;
show configuration servers
exit
EOF
} | grep "server www"
if [ $? != 0 ]; then
    fail 'no www'
fi

{ ${CONFDCLI} << EOF;
configure
set servers server abc ip 111.2.3.4 macaddr 00:12:3f:7d:b0:32 port 77 prefixmask 6 snmpref 2.1
commit
show servers server abc
EOF
} | grep '111.2.3.4'

if [ $? != 0 ]; then
    fail 'no abc'
fi


${CONFD} --stop

