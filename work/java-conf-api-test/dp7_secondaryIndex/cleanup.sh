#!/bin/sh

. ../include.sh

kill -9 `ps -eo pid,args | sort | grep -i java | grep -i SimpleWithTrans | awk '{print $1}'` || true

exit $res
