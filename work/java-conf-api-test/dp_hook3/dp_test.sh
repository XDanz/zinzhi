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
java -classpath ${CLASSPATH}:. SimpleHook  > ./test.log 2>&1 &

#exit 0
sleep 2

{ ${CONFDCLI} << EOF;
configure
set servers server tata
commit
show servers
exit
EOF
} | grep "server bar"
if [ $? != 0 ]; then
    fail 'no bar'
fi


{ ${CONFDCLI} << EOF;
configure
set servers flag 1
commit
show servers
EOF
} | egrep 'server bar'

if [ $? = 0 ]; then
    fail 'bar still exists'
fi




${CONFD} --stop


