#!/bin/sh

. ../include.sh

echo -n "Starting ConfD "
${CONFD} -c ./confd.conf --addloadpath ${CONFD_DIR}/etc/confd


[ $? -eq 0 ] || exit 1
${CONFD} --wait-started
echo -n " --> OK "

java -cp ${CLASSPATH} DbServer & 
[ $? -eq 0 ] || exit 1
sleep 3
echo " --> OK"

echo -n "Starting Dp Tests \n"

${NETCONF_CONSOLE_TCP} cmd-get-all.xml >test.log
${NETCONF_CONSOLE_TCP} cmd-create-woody.xml >> test.log
${NETCONF_CONSOLE_TCP} cmd-get-all.xml >>test.log
${NETCONF_CONSOLE_TCP} cmd-delete-woody.xml >> test.log
${NETCONF_CONSOLE_TCP} cmd-get-all.xml >>test.log 
diff test.log test.log.saved > test.diff

[ $? -eq 0 ] || exit 1
sleep 3
echo " --> OK"
res=$?

${CONFD} --stop	
sleep 1

#kill -9 `ps -eo pid,args | sort | grep -i java | grep -i DbServer | awk '{print $1}'` || true

exit $res
