#!/bin/sh

. ../include.sh

fail() {
    ${CONFD} --stop
    echo "** $1"
    exit 1
}

# Do all test without starting/stopping confd and killing provider
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


#kill -9 `ps -eo pid,args | sort | grep -i java | grep -i SimpleHook | awk '{print $1}'` || true




