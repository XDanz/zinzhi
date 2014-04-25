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
show configuration servers
exit
EOF
} | grep "server www"
if [ $? != 0 ]; then
    fail 'no www'
fi

#exit 0
{ ${CONFDCLI} << EOF;
configure
set servers server www port 1234
commit
show hval
EOF
} | egrep 'hval *2'

if [ $? != 0 ]; then
    fail 'no 2'
fi


{ ${CONFDCLI} << EOF;
configure
delete servers server www
show hval
EOF
} | grep 'hval *4'

if [ $? != 0 ]; then
    fail 'no 4'
fi



{ ${CONFDCLI} << EOF;
show configuration hval2
configure
set servers server www port 999
commit
show hval2
EOF
}



${CONFD} --stop


